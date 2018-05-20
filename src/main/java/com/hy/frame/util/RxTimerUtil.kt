package com.hy.frame.util

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class RxTimerUtil {

    private var mDisposable: Disposable? = null

    /**
     * milliseconds毫秒后执行next操作
     *
     * @param milliseconds 毫秒
     * @param callback 回调
     */
    fun timer(milliseconds: Long, callback: ICallback?) {
        Observable.timer(milliseconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    override fun onSubscribe(disposable: Disposable) {
                        mDisposable = disposable
                    }

                    override fun onNext(number: Long) {
                        callback?.doNext(number)
                    }

                    override fun onError(e: Throwable) {
                        //取消订阅
                        cancel()
                    }

                    override fun onComplete() {
                        //取消订阅
                        cancel()
                    }
                })
    }


    /**
     * 每隔milliseconds毫秒后执行
     *
     * @param milliseconds
     * @param callback 回调
     */
    fun interval(milliseconds: Long, callback: ICallback?) {
        Observable.interval(milliseconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    override fun onSubscribe(disposable: Disposable) {
                        this@RxTimerUtil.mDisposable = disposable
                    }

                    override fun onNext(number: Long) {
                        callback?.doNext(number)
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })
    }


    /**
     * 取消订阅
     */
    fun cancel() {
        if (mDisposable != null && !mDisposable!!.isDisposed) {
            mDisposable!!.dispose()
        }
    }

    interface ICallback {
        fun doNext(milliseconds: Long)
    }
}