package com.hy.demo2.net

import com.google.gson.JsonParser
import com.hy.frame.bean.ResultInfo
import com.hy.frame.util.JsonUtil
import com.hy.frame.util.MyLog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody


/**
 * NormalObserver
 * @author
 * @time 18-10-25 下午12:02
 * @desc 如果需要去掉 公有code或者msg请继承重写onNext
 */
open class NormalObserver<T> : Observer<ResponseBody> {

    private var disposable: Disposable? = null
    private var mListener: ICallback<T>? = null
    private var mListListener: ICallback<MutableList<T>>? = null
    private var cls: Class<T>? = null

    fun listener(listener: ICallback<T>?, cls: Class<T>?): NormalObserver<T> {
        this.mListener = listener
        this.cls = cls
        return this
    }

    fun listListener(listener: ICallback<MutableList<T>>?, cls: Class<T>?): NormalObserver<T> {
        this.mListListener = listener
        this.cls = cls
        return this
    }

    override fun onComplete() {
        MyLog.d(javaClass, "onComplete")
    }

    override fun onSubscribe(d: Disposable) {
        this.disposable = d
    }

    override fun onNext(body: ResponseBody) {
        MyLog.d("onNext$body")
        var code: Int = 0//0异常 1成功
        var msg = ""
        try {
            // 1. 通用模板
//            val json = JsonParser().parse(body.string())
//            if (json.isJsonObject && mListener != null && cls != null) {
//                //直接转换
//                mListener?.onSuccess(JsonUtil.getObjectFromJson(json, cls!!), msg)
//            } else if (json.isJsonArray && mListListener != null && cls != null) {
//                //直接转换
//                mListListener?.onSuccess(JsonUtil.getListFromJson(json, cls!!), msg)
//            }
//2. 自定义模板
            val json = JsonParser().parse(body.string())
            if (json.isJsonObject) {
                val obj = json.asJsonObject

                if (obj.has("code")) {
                    code = obj.get("code").asInt
                }
                if (obj.has("msg")) {
                    msg = obj.get("msg").asString
                }
                if (code == 1) {
                    if (cls != null && obj.has("data")) {
                        if (mListListener != null) {
                            mListListener?.onSuccess(JsonUtil.getListFromJson(obj.get("data"), cls!!), msg)
                            this.disposable?.dispose()
                            return
                        } else if (mListener != null) {
                            mListener?.onSuccess(JsonUtil.getObjectFromJson(obj.get("data"), cls!!), msg)
                            this.disposable?.dispose()
                            return
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MyLog.e("数据异常$e")
            code = ResultInfo.CODE_ERROR_DECODE
            msg = "数据异常，请稍后重试"
        }
        onError(code, msg)
    }

    override fun onError(e: Throwable) {
        MyLog.e("onError$e")
        onError(ResultInfo.CODE_ERROR_DEFAULT, "网络异常，请稍后重试")
    }

    private fun onError(code: Int, msg: String?) {
        if (mListListener != null) {
            mListListener?.onError(code, msg)
        } else if (mListener != null) {
            mListener?.onError(code, msg)
        }
        this.disposable?.dispose()
    }

    interface ICallback<T> {
        fun onSuccess(obj: T?, msg: String?)
        fun onError(errorCode: Int, msg: String?)
    }
}