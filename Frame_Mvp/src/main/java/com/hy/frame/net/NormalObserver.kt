package com.hy.frame.net

import android.os.Handler
import android.os.Looper
import com.hy.frame.bean.ResultInfo
import com.hy.frame.mvp.IMyHttpListener
import com.hy.frame.util.MyLog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import java.io.*

/**
 * title 普通请求 1.String 2. Json 3. File 文件下载 非主线程
 * author heyan
 * time 18-12-4 上午11:23
 * desc 非断点
 */
open class NormalObserver(requestCode: Int, listener: IMyHttpListener?, private val savePath: String?, private val isNeedProgress: Boolean = false) : Observer<ResponseBody> {

    private val result = ResultInfo(requestCode)
    private val mListener: IMyHttpListener? = listener


    override fun onComplete() {
        MyLog.d(javaClass, "onComplete")
    }

    override fun onSubscribe(d: Disposable) {
        //MyLog.e("onSubscribe$d")
    }

    override fun onNext(response: ResponseBody) {
        val contentType = response.contentType().toString()
        if (savePath == null) {
            result.errorCode = ResultInfo.CODE_ERROR_DEFAULT
            result.msg = "未定义文件存储路径"
            onRequestError(result)
            return
        }
        MyLog.d(javaClass, "savePath=$savePath")
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        val file = File(savePath)
        try {
            val fileReader = ByteArray(1024)
            val fileSize: Long = response.contentLength()
            var downloadSize: Long = 0
            if (isNeedProgress) {
                result.putValue(FILE_STATE, STATUS_PROGRESS.toString())
                result.putValue(FILE_TYPE, contentType)
                result.putValue(FILE_SIZE, fileSize.toString())
            }
            inputStream = response.byteStream()
            outputStream = FileOutputStream(file)
            var read: Int
            while (true) {
                read = inputStream?.read(fileReader) ?: -1
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
                downloadSize += read
                MyLog.d(javaClass, "file download:$downloadSize/$fileSize")
                if (isNeedProgress) {
                    result.putValue(FILE_SIZE_DOWNLOAD, downloadSize.toString())
                    onRequestSuccess(result)
                }
            }
            outputStream.flush()
            if (isNeedProgress) {
                result.putValue(FILE_STATE, STATUS_SUCCESS.toString())
            }
            onRequestSuccess(result)
            return
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        onRequestError(result)
    }

    override fun onError(e: Throwable) {
        MyLog.e(javaClass, "onError$e")
        result.errorCode = ResultInfo.CODE_ERROR_NET
        result.msg = "网络异常，请稍后重试"
        onRequestError(result)
    }
    private fun onRequestError(result: ResultInfo){
        Handler(Looper.getMainLooper()).post {
            mListener?.onRequestError(result)
        }
    }
    private fun onRequestSuccess(result: ResultInfo){
        Handler(Looper.getMainLooper()).post {
            mListener?.onRequestSuccess(result)
        }
    }
    companion object {
        const val FILE_STATE = "file_state"
        const val FILE_TYPE = "file_type"
        const val FILE_SIZE = "file_size"
        const val FILE_SIZE_DOWNLOAD = "file_size_download"
        const val STATUS_START = 0
        const val STATUS_PROGRESS = 1
        const val STATUS_SUCCESS = 2
        const val STATUS_ERROR = -1
    }
}