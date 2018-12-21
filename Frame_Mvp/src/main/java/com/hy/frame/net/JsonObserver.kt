package com.hy.frame.net

import com.google.gson.JsonObject
import com.hy.frame.bean.ResultInfo
import com.hy.frame.mvp.IMyHttpListener
import com.hy.frame.util.JsonUtil
import com.hy.frame.util.MyLog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * JsonObserver
 * @author
 * @time 18-10-25 下午12:02
 */
class JsonObserver<T>(requestCode: Int, listener: IMyHttpListener?, cls: Class<T>? = null, list: Boolean = false) : Observer<JsonObject> {
    private val result = ResultInfo(requestCode)
    private val mListener: IMyHttpListener? = listener
    private val mCls: Class<T>? = cls
    private val mList: Boolean = list

    override fun onComplete() {
        MyLog.d("onComplete")
    }

    override fun onSubscribe(d: Disposable) {
        //MyLog.d("onSubscribe$d")
    }

    override fun onNext(json: JsonObject) {
        MyLog.d("onNext$json")
        var flag = 0//0异常 1成功
        var msg: String? = null
        var obj: Any? = null
        try {
            if (json.has("code") && !json.get("code").isJsonNull) {
                flag = json.get("code").asInt
            }
            if (json.has("message") && !json.get("message").isJsonNull) {
                msg = json.get("message").asString
            }
            result.errorCode = flag
            result.msg = msg
            if (flag == 0) {
                if (json.has("result") && !json.get("result").isJsonNull) {
                    val data = json.get("result")
                    obj = when {
                        mCls == null -> data
                        mList -> JsonUtil.getListFromJson(data, mCls)
                        else -> JsonUtil.getObjectFromJson(data, mCls)
                    }
                }
                result.setObj(obj)
                mListener?.onRequestSuccess(result)
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MyLog.e("数据异常$e")
            result.errorCode = ResultInfo.CODE_ERROR_DECODE
            result.msg = "数据异常，请稍后重试"
        }
        mListener?.onRequestError(result)
    }

    override fun onError(e: Throwable) {
        MyLog.e("onError$e")
        result.errorCode = ResultInfo.CODE_ERROR_DEFAULT
        result.msg = "网络异常，请稍后重试"
        mListener?.onRequestError(result)
    }

//    interface ICallback {
//        fun onSuccess(obj: Any?, msg: String?)
//        fun onError(errorCode: Int, msg: String?)
//    }
}