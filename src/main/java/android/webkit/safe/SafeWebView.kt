package android.webkit.safe


import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.hy.frame.util.MyLog
import java.util.*

/**
 * Created by zhangguojun on 2015/6/21.
 *
 * Android4.2.2以下版本WebView有远程执行代码漏洞
 * 乌云上的介绍：http://www.wooyun.org/bugs/wooyun-2010-067676
 * 测试方法：让自己的WebView加载http://drops.wooyun.org/webview.html
 */
class SafeWebView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : WebView(context, attrs) {
    private var mJsCallJavas: MutableMap<String, JsCallJava>? = null
    private var mInjectJavaScripts: MutableMap<Int, String>? = null
    private var mWebChromeClient: SafeWebChromeClient? = null
    private var mWebViewClient: SafeWebViewClient? = null

    init {
        removeSearchBoxJavaBridge()

        // WebView跨源（加载本地文件）攻击分析：http://blogs.360.cn/360mobile/2014/09/22/webview%E8%B7%A8%E6%BA%90%E6%94%BB%E5%87%BB%E5%88%86%E6%9E%90/
        // 是否允许WebView使用File协议，移动版的Chrome默认禁止加载file协议的文件；
        settings.allowFileAccess = false
    }

    /**
     * 经过大量的测试，按照以下方式才能保证JS脚本100%注入成功：
     * 1、在第一次loadUrl之前注入JS（在addJavascriptInterface里面注入即可）；
     * 2、在webViewClient.onPageStarted中都注入JS；
     * 3、在webChromeClient.onProgressChanged中都注入JS，并且不能通过自检查（onJsPrompt里面判断）JS是否注入成功来减少注入JS的次数，因为网页中的JS可以同时打开多个url导致无法控制检查的准确性；
     * 4、注入的JS中已经在脚本（./library/doc/notRepeat.js）中检查注入的对象是否已经存在，避免注入对象被重新赋值导致网页引用该对象的方法时发生异常；

     */
    @Deprecated("Android4.2.2及以上版本的addJavascriptInterface方法已经解决了安全问题，如果不使用“网页能将JS函数传到Java层”功能，不建议使用该类，毕竟系统的JS注入效率才是最高的；")
    override fun addJavascriptInterface(interfaceObj: Any, interfaceName: String) {
        if (mJsCallJavas == null) {
            mJsCallJavas = HashMap<String, JsCallJava>()
        }
        mJsCallJavas!!.put(interfaceName, JsCallJava(interfaceObj, interfaceName))
        setClient()
        if (mJsCallJavas != null) {
            injectJavaScript()
            MyLog.d(TAG, "injectJavaScript, addJavascriptInterface.interfaceObj = $interfaceObj, interfaceName = $interfaceName")
        }
    }

    override fun setWebViewClient(client: WebViewClient) {
        if (client is SafeWebViewClient) {
            if (mJsCallJavas != null) {
                super.setWebViewClient(client)
            } else {
                mWebViewClient = client
            }
        } else {
            super.setWebViewClient(client)
        }
    }

    override fun setWebChromeClient(client: WebChromeClient) {
        if (client is SafeWebChromeClient) {
            if (mJsCallJavas != null) {
                super.setWebChromeClient(client)
            } else {
                mWebChromeClient = client
            }
        } else {
            super.setWebChromeClient(client)
        }
    }

    override fun destroy() {
        if (mJsCallJavas != null) {
            mJsCallJavas!!.clear()
        }
        if (mInjectJavaScripts != null) {
            mInjectJavaScripts!!.clear()
        }
        removeAllViews()
        //WebView中包含一个ZoomButtonsController，当使用web.getSettings().setBuiltInZoomControls(true);启用该设置后，用户一旦触摸屏幕，就会出现缩放控制图标。这个图标过上几秒会自动消失，但在3.0系统以上上，如果图标自动消失前退出当前Activity的话，就会发生ZoomButton找不到依附的Window而造成程序崩溃，解决办法很简单就是在Activity的ondestory方法中调用web.setVisibility(View.GONE);方法，手动将其隐藏，就不会崩溃了。在3.0一下系统上不会出现该崩溃问题，真是各种崩溃，防不胜防啊！
        visibility = View.GONE
        val parent = parent
        if (parent is ViewGroup) {
            val mWebViewContainer = getParent() as ViewGroup
            mWebViewContainer.removeAllViews()
        }
        releaseConfigCallback()
        super.destroy()
    }

    override fun loadUrl(url: String) {
        if (mJsCallJavas == null) {
            setClient()
        }
        super.loadUrl(url)
    }

    override fun loadUrl(url: String, additionalHttpHeaders: Map<String, String>) {
        if (mJsCallJavas == null) {
            setClient()
        }
        super.loadUrl(url, additionalHttpHeaders)
    }

    private fun setClient() {
        if (mWebChromeClient != null) {
            setWebChromeClient(mWebChromeClient!!)
            mWebChromeClient = null
        }
        if (mWebViewClient != null) {
            setWebViewClient(mWebViewClient!!)
            mWebViewClient = null
        }
    }

    /**
     * 添加并注入JavaScript脚本（和“addJavascriptInterface”注入对象的注入时机一致，100%能注入成功）；
     * 注意：为了做到能100%注入，需要在注入的js中自行判断对象是否已经存在（如：if (typeof(window.Android) = 'undefined')）；

     * @param javaScript
     */
    fun addInjectJavaScript(javaScript: String) {
        if (mInjectJavaScripts == null) {
            mInjectJavaScripts = HashMap<Int, String>()
        }
        mInjectJavaScripts!!.put(javaScript.hashCode(), javaScript)
        injectExtraJavaScript()
    }

    private fun injectJavaScript() {
        for ((key, value) in mJsCallJavas!!) {
            this.loadUrl(buildNotRepeatInjectJS(key, value.preloadInterfaceJS!!))
        }
    }

    private fun injectExtraJavaScript() {
        for ((_, value) in mInjectJavaScripts!!) {
            this.loadUrl(buildTryCatchInjectJS(value))
        }
    }

    /**
     * 构建一个“不会重复注入”的js脚本；

     * @param key
     *
     * @param js
     *
     * @return
     */
    fun buildNotRepeatInjectJS(key: String, js: String): String {
        val obj = String.format("__injectFlag_%1\$s__", key)
        val sb = StringBuilder()
        sb.append("javascript:try{(function(){if(window.")
        sb.append(obj)
        sb.append("){console.log('")
        sb.append(obj)
        sb.append(" has been injected');return;}window.")
        sb.append(obj)
        sb.append("=true;")
        sb.append(js)
        sb.append("}())}catch(e){console.warn(e)}")
        return sb.toString()
    }

    /**
     * 构建一个“带try catch”的js脚本；

     * @param js
     *
     * @return
     */
    fun buildTryCatchInjectJS(js: String): String {
        val sb = StringBuilder()
        sb.append("javascript:try{")
        sb.append(js)
        sb.append("}catch(e){console.warn(e)}")
        return sb.toString()
    }

    /**
     * 如果没有使用addJavascriptInterface方法，不需要使用这个类；
     */
    inner class SafeWebViewClient : WebViewClient() {

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
            if (mJsCallJavas != null) {
                injectJavaScript()
                MyLog.d(TAG, "injectJavaScript, onPageStarted.url = " + view.url)
            }
            if (mInjectJavaScripts != null) {
                injectExtraJavaScript()
            }
            super.onPageStarted(view, url, favicon)
        }
    }

    /**
     * 如果没有使用addJavascriptInterface方法，不需要使用这个类；
     */
    inner class SafeWebChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (mJsCallJavas != null) {
                injectJavaScript()
                MyLog.d(TAG, "injectJavaScript, onProgressChanged.newProgress = " + newProgress + ", url = " + view.url)
            }
            if (mInjectJavaScripts != null) {
                injectExtraJavaScript()
            }
            super.onProgressChanged(view, newProgress)
        }

        override fun onJsPrompt(view: WebView, url: String, message: String, defaultValue: String, result: JsPromptResult): Boolean {
            if (mJsCallJavas != null && JsCallJava.isSafeWebViewCallMsg(message)) {
                val jsonObject = JsCallJava.getMsgJSONObject(message)
                val interfacedName = JsCallJava.getInterfacedName(jsonObject)
                if (interfacedName != null) {
                    val jsCallJava = mJsCallJavas!![interfacedName]
                    if (jsCallJava != null) {
                        result.confirm(jsCallJava.call(view, jsonObject))
                    }
                }
                return true
            } else {
                return super.onJsPrompt(view, url, message, defaultValue, result)
            }
        }
    }

    // 解决WebView内存泄漏问题；
    private fun releaseConfigCallback() {
        if (Build.VERSION.SDK_INT < 16) { // JELLY_BEAN
            try {
                var field = WebView::class.java.getDeclaredField("mWebViewCore")
                field = field.type.getDeclaredField("mBrowserFrame")
                field = field.type.getDeclaredField("sConfigCallback")
                field.isAccessible = true
                field.set(null, null)
            } catch (e: NoSuchFieldException) {
                if (MyLog.isLoggable) {
                    e.printStackTrace()
                }
            } catch (e: IllegalAccessException) {
                if (MyLog.isLoggable) {
                    e.printStackTrace()
                }
            }

        } else {
            try {
                val sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback")
                if (sConfigCallback != null) {
                    sConfigCallback.isAccessible = true
                    sConfigCallback.set(null, null)
                }
            } catch (e: NoSuchFieldException) {
                if (MyLog.isLoggable) {
                    e.printStackTrace()
                }
            } catch (e: ClassNotFoundException) {
                if (MyLog.isLoggable) {
                    e.printStackTrace()
                }
            } catch (e: IllegalAccessException) {
                if (MyLog.isLoggable) {
                    e.printStackTrace()
                }
            }

        }
    }

    /**
     * Android 4.4 KitKat 使用Chrome DevTools 远程调试WebView
     * WebView.setWebContentsDebuggingEnabled(true);
     * http://blog.csdn.net/t12x3456/article/details/14225235
     */
    @TargetApi(19)
    protected fun trySetWebDebuggEnabled() {
        if (MyLog.isLoggable && Build.VERSION.SDK_INT >= 19) {
            try {
                val clazz = WebView::class.java
                val method = clazz.getMethod("setWebContentsDebuggingEnabled", Boolean::class.javaPrimitiveType)
                method.invoke(null, true)
            } catch (e: Throwable) {
                if (MyLog.isLoggable) {
                    e.printStackTrace()
                }
            }

        }
    }

    /**
     * 解决Webview远程执行代码漏洞，避免被“getClass”方法恶意利用（在loadUrl之前调用，如：MyWebView(Context context, AttributeSet attrs)里面）；
     * 漏洞详解：http://drops.wooyun.org/papers/548
     *
     *
     * function execute(cmdArgs)
     * {
     * for (var obj in window) {
     * if ("getClass" in window[obj]) {
     * alert(obj);
     * return ?window[obj].getClass().forName("java.lang.Runtime")
     * .getMethod("getRuntime",null).invoke(null,null).exec(cmdArgs);
     * }
     * }
     * }

     * @return
     */
    @TargetApi(11)
    protected fun removeSearchBoxJavaBridge(): Boolean {
        try {
            if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 17) {
                val method = this.javaClass.getMethod("removeJavascriptInterface", String::class.java)
                method.invoke(this, "searchBoxJavaBridge_")
                return true
            }
        } catch (e: Exception) {
            if (MyLog.isLoggable) {
                e.printStackTrace()
            }
        }

        return false
    }

    /**
     * 解决Android4.2中开启了辅助模式后，LocalActivityManager控制的Activity与AccessibilityInjector不兼容导致的崩溃问题；
     * Caused by: java.lang.NullPointerException
     * at android.webkit.AccessibilityInjector$TextToSpeechWrapper$1.onInit(AccessibilityInjector.java:753)
     * ...
     * at android.webkit.WebSettingsClassic.setJavaScriptEnabled(WebSettingsClassic.java:1125)
     * 必须放在webSettings.setJavaScriptEnabled之前执行；
     */
    protected fun fixedAccessibilityInjectorException() {
        if (Build.VERSION.SDK_INT == 17) {
            try {
                val webViewProvider = WebView::class.java.getMethod("getWebViewProvider").invoke(this)
                val getAccessibilityInjector = webViewProvider.javaClass.getDeclaredMethod("getAccessibilityInjector")
                getAccessibilityInjector.isAccessible = true
                val accessibilityInjector = getAccessibilityInjector.invoke(webViewProvider)
                getAccessibilityInjector.isAccessible = false
                val mAccessibilityManagerField = accessibilityInjector.javaClass.getDeclaredField("mAccessibilityManager")
                mAccessibilityManagerField.isAccessible = true
                val mAccessibilityManager = mAccessibilityManagerField.get(accessibilityInjector)
                mAccessibilityManagerField.isAccessible = false
                val mIsEnabledField = mAccessibilityManager.javaClass.getDeclaredField("mIsEnabled")
                mIsEnabledField.isAccessible = true
                mIsEnabledField.set(mAccessibilityManager, false)
                mIsEnabledField.isAccessible = false
            } catch (e: Exception) {
                if (MyLog.isLoggable) {
                    e.printStackTrace()
                }
            }

        }
    }

    /**
     * 向网页更新Cookie，设置cookie后不需要页面刷新即可生效；
     */
    protected fun updateCookies(url: String, value: String) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setCookie(url, value)
    }

    companion object {
        private val TAG = "SafeWebView"
    }
}