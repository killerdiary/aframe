package com.hy.demo2.presenter

import android.net.Uri
import com.hy.demo2.BuildConfig
import com.hy.demo2.bean.TestBean
import com.hy.demo2.model.ApiContract
import com.hy.frame.bean.DownFile
import com.hy.frame.mvp.BasePresenter
import com.hy.frame.net.FileObserver

import com.hy.frame.net.NormalObserver
import com.hy.frame.util.EncodeUtil
import com.hy.frame.util.MyLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream

/**
 * title 无
 * author heyan
 * time 19-2-22 下午4:46
 * desc 无
 */
class LaunchPresenter(context: android.content.Context, view: ApiContract.IView) : BasePresenter<ApiContract.IView, com.hy.frame.mvp.contract.ApiContract.IModel>(context, view, com.hy.frame.mvp.model.ApiModel()) {
    init {
        val manager = getModel().getRetrofitManager()
        manager?.setBaseUrl("http://www.baidu.com")
        manager?.setLoggable(BuildConfig.DEBUG)

    }

    /**
     * 普通请求
     */
    fun requestData() {
        getModel().get("/api/app.php", HashMap())
            ?.subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(NormalObserver<TestBean>().listener(object : NormalObserver.ICallback<TestBean> {
                override fun onSuccess(obj: TestBean?, msg: String?) {
                    MyLog.d(javaClass, "onSuccess")
                    if (obj == null) {
                        onError(0, msg)
                        return
                    }
                    getView().updateUI(obj)
                }

                override fun onError(errorCode: Int, msg: String?) {
                    getView().getTemplateControl()?.showNoData(msg ?: "ERROR")
                }

            }, TestBean::class.java))
    }

    /**
     * 列表请求
     */
    fun requestListData() {
        getModel().get("/api/apps.php", HashMap())
            ?.subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(NormalObserver<TestBean>().listListener(object : NormalObserver.ICallback<MutableList<TestBean>> {
                override fun onSuccess(obj: MutableList<TestBean>?, msg: String?) {
                    MyLog.d(javaClass, "onSuccess")
                    if (obj == null) {
                        onError(0, msg)
                        return
                    }
                    getView().updateUI(obj)
                }

                override fun onError(errorCode: Int, msg: String?) {
                    getView().getTemplateControl()?.showNoData(msg ?: "ERROR")
                }

            }, TestBean::class.java))
    }

    /**
     * 下载
     */
    fun downloadFile(url: String, ext: String) {
        val fileName = EncodeUtil.md5(url) + ext
        MyLog.d(javaClass, "fileName=$fileName")
        //1. .data格式 html zip
        //2. .dap格式 apk
        //判断本地压缩包是否存在
        val file = File(getView().getTemplateUI().getCurContext().filesDir, fileName)
        if (file.exists() && file.length() > 0) {
            //解压
            downloadComplete(file)
            return
        }
        val downPath = file.absolutePath
        when {
            url.startsWith("file:///android_asset/") -> //拷贝
                copyAsset(url, file)
            url.startsWith("file:///") -> //调用下载完成
                downloadComplete(File(Uri.parse(url).path))
            else -> getModel().download(url, HashMap())
                ?.subscribeOn(Schedulers.newThread())
                ?.observeOn(Schedulers.io())
                ?.subscribe(FileObserver(object : FileObserver.ICallback {
                    override fun onSuccess(obj: DownFile, msg: String?) {
                        if (obj.state == DownFile.STATUS_SUCCESS)
                            downloadComplete(file)
                    }

                    override fun onError(errorCode: Int, msg: String?) {
                        getView().getTemplateControl()?.showNoData(msg ?: "ERROR")
                    }

                }, downPath, true))
        }
    }

    /**
     * 文件拷贝asset
     */
    private fun copyAsset(url: String, file: File) {
        val prefix = "file:///android_asset/"
        if (!url.startsWith(prefix)) return
        val fileName = url.substring(prefix.length)
        try {
            val inputStream = getView().getTemplateUI().getCurContext().assets.open(fileName)
            if (file.exists())
                file.delete()
            val fos = FileOutputStream(file)  //如果文件不存在，FileOutputStream会自动创建文件
            val fileReader = ByteArray(1024)
            var read: Int
            while (true) {
                read = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                fos.write(fileReader, 0, read)
            }
            fos.flush() //刷新缓存区
            inputStream.close()
            fos.close()
            MyLog.d(javaClass, "文件拷贝完成 $file")
            downloadComplete(file)
        } catch (e: Exception) {
            e.printStackTrace()
            MyLog.e(javaClass, "copyAsset " + e.message)
        }
    }

    fun downloadComplete(file: File) {

    }
}