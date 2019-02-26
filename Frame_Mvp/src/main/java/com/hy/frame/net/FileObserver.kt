package com.hy.frame.net

import android.os.Handler
import android.os.Looper
import com.hy.frame.bean.DownFile
import com.hy.frame.util.MyLog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import java.io.*

/**
 * title 文件下载 非主线程
 * author heyan
 * time 18-12-4 上午11:23
 * desc 非断点
 */
open class FileObserver(private val listener: ICallback?, private val savePath: String?, private val isNeedProgress: Boolean = false) : Observer<ResponseBody> {

    private var disposable: Disposable? = null

    private val dFile = DownFile()

    override fun onComplete() {
        MyLog.d(javaClass, "onComplete")
    }

    override fun onSubscribe(d: Disposable) {
        //MyLog.e("onSubscribe$d")
        this.disposable = d
    }

    override fun onNext(response: ResponseBody) {
        MyLog.d(javaClass, "onNext")
        //val contentType = response.contentType().toString()
        if (savePath == null) {
            onRequestError(0, "未定义文件存储路径")
            return
        }
        MyLog.d(javaClass, "savePath=$savePath")
        dFile.filePath = savePath
        dFile.state = DownFile.STATUS_START
        val file = File(savePath)
        val tempFile = File(savePath + "c")
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            val fileReader = ByteArray(1024)
            val fileSize: Long = response.contentLength()
            var downloadSize: Long = 0
            if (isNeedProgress) {
                dFile.state = DownFile.STATUS_PROGRESS
                dFile.fileSize = fileSize
            }
            inputStream = response.byteStream()
            outputStream = FileOutputStream(tempFile)
            var read: Int
            while (true) {
                read = inputStream?.read(fileReader) ?: -1
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
                downloadSize += read
                MyLog.d(javaClass, "file download:$downloadSize/$fileSize read=$read")
                if (isNeedProgress) {
                    dFile.downloadSize = downloadSize
                    onRequestSuccess(DownFile.STATUS_PROGRESS, dFile)
                }
            }
            outputStream.flush()
            if (isNeedProgress) {
                dFile.state = DownFile.STATUS_SUCCESS
            }
            tempFile.renameTo(file)
            MyLog.d(javaClass, "file download:$downloadSize/$fileSize success")
            onRequestSuccess(DownFile.STATUS_SUCCESS, dFile)
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
        onRequestError(0, "网络异常，请稍后重试")
    }

    override fun onError(e: Throwable) {
        MyLog.e(javaClass, "onError$e")
        onRequestError(0, "网络异常，请稍后重试")
    }

    private fun onRequestError(code: Int, msg: String?) {
        MyLog.d(javaClass, "onRequestError")
        Handler(Looper.getMainLooper()).post {
            listener?.onError(code, msg)
        }
        this.disposable?.dispose()
    }

    private fun onRequestSuccess(status: Int, file: DownFile) {
        Handler(Looper.getMainLooper()).post {
            listener?.onSuccess(file, null)
        }
        if (status == DownFile.STATUS_SUCCESS)
            this.disposable?.dispose()
    }

    interface ICallback {
        fun onSuccess(obj: DownFile, msg: String?)
        fun onError(errorCode: Int, msg: String?)
    }
    
}