package com.hy.demo2.ui

import android.content.pm.ActivityInfo
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hy.demo2.R
import com.hy.demo2.app.BasePresenterActivity
import com.hy.demo2.bean.TestBean
import com.hy.demo2.model.ApiContract
import com.hy.demo2.presenter.LaunchPresenter
import com.hy.frame.adapter.IAdapterListener


/**
 * title MVP模式
 * author heyan
 * time 18-12-5 上午11:01
 * desc 只做参考
 */
class LaunchActivity : BasePresenterActivity<LaunchPresenter>(), ApiContract.IView, IAdapterListener<MutableList<String>> {

    //private var adapter: TabAdapter? = null

    override fun buildPresenter(): LaunchPresenter? = LaunchPresenter(getCurContext(), this)

    override fun getScreenOrientation(): Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    override fun isSingleLayout(): Boolean = false

    private var rcyList: RecyclerView? = null
    private var data: TestBean? = null

    override fun getLayoutId(): Int = R.layout.v_test1


    override fun initView() {
        //rcyList = findViewById(R.id.launch_rcyList)
    }

    override fun initData() {
//        window.setBackgroundDrawable(null)
//        if (rxTimer == null)
//            rxTimer = RxTimerUtil()
//        rxTimer?.timer(TIME_LAUNCH, this)
        setTitle(R.string.appName)
        getTemplateControl()?.showLoading()
        getPresenter()?.requestData()
    }

    override fun updateUI(data: TestBean) {
//        this.data = data
//        if (adapter == null) {
//            adapter = TabAdapter(getCurContext(), data.button, this)
//            rcyList?.adapter = adapter
//        } else
//            adapter?.refresh(data.button)
    }

    override fun updateUI(datas: MutableList<TestBean>) {
    }

    override fun onViewClick(v: View) {

    }

    override fun onViewClick(v: View, item: MutableList<String>, position: Int) {
//        startAct(
//            WebActivity::class.java,
//            WebActivity.newArguments(item[1], null, this.data?.ua)
//        )
    }


    private fun doNext() {
//        startAct(
//            WebActivity::class.java,
//            WebActivity.newArguments(URL_INDEX)
//        )
//        finish()
    }

    override fun onBackPressed() {
        //屏蔽返回键
        getCurApp().exit()
    }

    companion object {
        //启动页时间
        const val TIME_LAUNCH = 100L
        // const val URL_INDEX = "http://39.130.160.196:9500/index.html"
//        const val URL_INDEX = "http://192.168.88.77:8000/index.html"
    }
}