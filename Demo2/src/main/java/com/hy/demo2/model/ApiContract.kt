package com.hy.demo2.model

import com.hy.demo2.bean.TestBean


/**
 * title 无
 * author heyan
 * time 19-2-22 下午5:13
 * desc 无
 */
interface ApiContract {
    interface IView : com.hy.frame.mvp.contract.ApiContract.IView {
        fun updateUI(data: TestBean)
        fun updateUI(datas: MutableList<TestBean>)
    }

    interface IModel : com.hy.frame.mvp.contract.ApiContract.IModel {

    }
}