package android.webkit.safe

import android.text.TextUtils
import android.webkit.WebView

import com.hy.frame.util.MyLog

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.lang.reflect.Method
import java.util.HashMap

class JsCallJava(interfaceObj: Any, interfaceName: String) {
    private var mMethodsMap: HashMap<String, Method>? = null
    private var mInterfaceObj: Any? = null
    private var mInterfacedName: String? = null
    var preloadInterfaceJS: String? = null
        private set

    init {
        try {
            if (TextUtils.isEmpty(interfaceName)) {
                throw Exception("injected name can not be null")
            }
            mInterfaceObj = interfaceObj
            mInterfacedName = interfaceName
            mMethodsMap = HashMap<String, Method>()
            // getMethods会获得所有继承与非继承的方法
            val methods = mInterfaceObj!!.javaClass.methods
            // 拼接的js脚本可参照备份文件：./library/doc/injected.js
            val sb = StringBuilder("javascript:(function(b){console.log(\"")
            sb.append(mInterfacedName)
            sb.append(" init begin\");var a={queue:[],callback:function(){var d=Array.prototype.slice.call(arguments,0);var c=d.shift();var e=d.shift();this.queue[c].apply(this,d);if(!e){delete this.queue[c]}}};")
            for (method in methods) {
                val sign: String = genJavaMethodSign(method) ?: continue
                mMethodsMap!!.put(sign, method)
                sb.append(String.format("a.%s=", method.name))
            }
            sb.append("function(){var f=Array.prototype.slice.call(arguments,0);if(f.length<1){throw\"")
            sb.append(mInterfacedName)
            sb.append(" call error, message:miss method name\"}var e=[];for(var h=1;h<f.length;h++){var c=f[h];var j=typeof c;e[e.length]=j;if(j==\"function\"){var d=a.queue.length;a.queue[d]=c;f[h]=d}}var k = new Date().getTime();var l = f.shift();var m=prompt('")
            sb.append(MSG_PROMPT_HEADER)
            sb.append("'+JSON.stringify(")
            sb.append(promptMsgFormat("'$mInterfacedName'", "l", "e", "f"))
            sb.append("));console.log(\"invoke \"+l+\", time: \"+(new Date().getTime()-k));var g=JSON.parse(m);if(g.code!=200){throw\"")
            sb.append(mInterfacedName)
            sb.append(" call error, code:\"+g.code+\", message:\"+g.result}return g.result};Object.getOwnPropertyNames(a).forEach(function(d){var c=a[d];if(typeof c===\"function\"&&d!==\"callback\"){a[d]=function(){return c.apply(a,[d].concat(Array.prototype.slice.call(arguments,0)))}}});b.")
            sb.append(mInterfacedName)
            sb.append("=a;console.log(\"")
            sb.append(mInterfacedName)
            sb.append(" init end\")})(window)")
            preloadInterfaceJS = sb.toString()
            sb.setLength(0)
        } catch (e: Exception) {
            MyLog.e(TAG, "init js error:" + e.message)
        }

    }

    private fun genJavaMethodSign(method: Method): String? {
        var sign = method.name
        val argsTypes = method.parameterTypes
        for (ignoreMethod in IGNORE_UNSAFE_METHODS) {
            if (ignoreMethod == sign) {
                MyLog.w(TAG, "method($sign) is unsafe, will be pass")
                return null
            }
        }
        val len = argsTypes.size
        for (k in 0..len - 1) {
            val cls = argsTypes[k]
            if (cls == String::class.java) {
                sign += "_S"
            } else if (cls == Int::class.javaPrimitiveType ||
                    cls == Long::class.javaPrimitiveType ||
                    cls == Float::class.javaPrimitiveType ||
                    cls == Double::class.javaPrimitiveType) {
                sign += "_N"
            } else if (cls == Boolean::class.javaPrimitiveType) {
                sign += "_B"
            } else if (cls == JSONObject::class.java) {
                sign += "_O"
            } else if (cls == JsCallback::class.java) {
                sign += "_F"
            } else {
                sign += "_P"
            }
        }
        return sign
    }

    fun call(webView: WebView, jsonObject: JSONObject?): String {
        var time: Long = 0
        if (MyLog.isLoggable) {
            time = android.os.SystemClock.uptimeMillis()
        }
        if (jsonObject != null) {
            try {
                val methodName = jsonObject.getString(KEY_METHOD)
                val argsTypes = jsonObject.getJSONArray(KEY_TYPES)
                val argsVals = jsonObject.getJSONArray(KEY_ARGS)
                var sign = methodName
                val len = argsTypes.length()
                val values = arrayOfNulls<Any>(len)
                var numIndex = 0
                var currType: String

                for (k in 0..len - 1) {
                    currType = argsTypes.optString(k)
                    if ("string" == currType) {
                        sign += "_S"
                        values[k] = if (argsVals.isNull(k)) null else argsVals.getString(k)
                    } else if ("number" == currType) {
                        sign += "_N"
                        numIndex = numIndex * 10 + k + 1
                    } else if ("boolean" == currType) {
                        sign += "_B"
                        values[k] = argsVals.getBoolean(k)
                    } else if ("object" == currType) {
                        sign += "_O"
                        values[k] = if (argsVals.isNull(k)) null else argsVals.getJSONObject(k)
                    } else if ("function" == currType) {
                        sign += "_F"
                        values[k] = JsCallback(webView, mInterfacedName, argsVals.getInt(k))
                    } else {
                        sign += "_P"
                    }
                }

                val currMethod = mMethodsMap!![sign] ?: return getReturn(jsonObject, 500, "not found method($sign) with valid parameters", time)

                // 方法匹配失败
                // 数字类型细分匹配
                if (numIndex > 0) {
                    val methodTypes = currMethod.parameterTypes
                    var currIndex: Int
                    var currCls: Class<*>
                    while (numIndex > 0) {
                        currIndex = numIndex - numIndex / 10 * 10 - 1
                        currCls = methodTypes[currIndex]
                        if (currCls == Int::class.javaPrimitiveType) {
                            values[currIndex] = argsVals.getInt(currIndex)
                        } else if (currCls == Long::class.javaPrimitiveType) {
                            //WARN: argsJson.getLong(k + defValue) will return a bigger incorrect number
                            values[currIndex] = java.lang.Long.parseLong(argsVals.getString(currIndex))
                        } else {
                            values[currIndex] = argsVals.getDouble(currIndex)
                        }
                        numIndex /= 10
                    }
                }

                return getReturn(jsonObject, 200, currMethod.invoke(mInterfaceObj, *values), time)
            } catch (e: Exception) {
                //优先返回详细的错误信息
                if (e.cause != null) {
                    return getReturn(jsonObject, 500, "method execute error:" + e.cause!!.message, time)
                }
                return getReturn(jsonObject, 500, "method execute error:" + e.message, time)
            }

        } else {
            return getReturn(jsonObject, 500, "call data empty", time)
        }
    }

    private fun getReturn(reqJson: JSONObject?, stateCode: Int, result: Any?, time: Long): String {
        var result = result
        val insertRes: String
        if (result == null) {
            insertRes = "null"
        } else if (result is String) {
            result = result.replace("\"", "\\\"")
            insertRes = "\"" + result.toString() + "\""
        } else { // 其他类型直接转换
            insertRes = result.toString()

            // 兼容：如果在解决WebView注入安全漏洞时，js注入采用的是XXX:function(){return prompt(...)}的形式，函数返回类型包括：void、int、boolean、String；
            // 在返回给网页（onJsPrompt方法中jsPromptResult.confirm）的时候强制返回的是String类型，所以在此将result的值加双引号兼容一下；
            // insertRes = "\"".concat(String.valueOf(result)).concat("\"");
        }
        val resStr = String.format(RETURN_RESULT_FORMAT, stateCode, insertRes)
        MyLog.d(TAG, "call time: " + (android.os.SystemClock.uptimeMillis() - time) + ", request: " + reqJson + ", result:" + resStr)
        return resStr
    }

    companion object {
        private val TAG = "JsCallJava"
        private val RETURN_RESULT_FORMAT = "{\"code\": %d, \"result\": %s}"
        private val MSG_PROMPT_HEADER = "SafeWebView:"
        private val KEY_OBJ = "obj"
        private val KEY_METHOD = "method"
        private val KEY_TYPES = "types"
        private val KEY_ARGS = "args"
        private val IGNORE_UNSAFE_METHODS = arrayOf("getClass", "hashCode", "notify", "notifyAll", "equals", "toString", "wait")

        private fun promptMsgFormat(`object`: String, method: String, types: String, args: String): String {
            val sb = StringBuilder()
            sb.append("{")
            sb.append(KEY_OBJ).append(":").append(`object`).append(",")
            sb.append(KEY_METHOD).append(":").append(method).append(",")
            sb.append(KEY_TYPES).append(":").append(types).append(",")
            sb.append(KEY_ARGS).append(":").append(args)
            sb.append("}")
            return sb.toString()
        }

        /**
         * 是否是“Java接口类中方法调用”的内部消息；

         * @param message
         * *
         * @return
         */
        internal fun isSafeWebViewCallMsg(message: String): Boolean {
            return message.startsWith(MSG_PROMPT_HEADER)
        }

        internal fun getMsgJSONObject(message: String): JSONObject {
            var message = message
            message = message.substring(MSG_PROMPT_HEADER.length)
            var jsonObject: JSONObject
            try {
                jsonObject = JSONObject(message)
            } catch (e: JSONException) {
                e.printStackTrace()
                jsonObject = JSONObject()
            }

            return jsonObject
        }

        internal fun getInterfacedName(jsonObject: JSONObject): String {
            return jsonObject.optString(KEY_OBJ)
        }
    }
}