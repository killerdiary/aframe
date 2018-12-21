package com.hy.demo2.ui

import android.content.Intent
import android.view.View
import com.hy.demo2.R
import com.hy.frame.bean.MenuInfo


class TestActivity : MenuActivity() {

    override fun initData() {
        intent.putExtra(BUNDLE, MenuActivity.newArguments(R.xml.menu_test, R.string.menu_test))
        super.initData()
        getTemplateControl()?.getHeaderLeft()?.visibility = View.GONE
    }

    override fun onViewClick(v: View, item: MenuInfo, position: Int) {
        when (position + 1) {
            1 -> {
                //启动好玩乐园
                val intent = packageManager.getLaunchIntentForPackage("cn.rhax.tvhall.happy")
                intent?.putExtra("arg", "test")
                startAct(intent!!)
            }
            2 -> {
                //发送用户信息
                val intent = Intent("cn.rhax.tvhall.SC_AUTH")
                intent.putExtra("arg_uid", "test001")
                intent.putExtra("arg_token", "token001")
                sendBroadcast(intent)
            }
            3 -> {
                //发送鉴权结果-成功
                val intent = Intent("cn.rhax.tvhall.SC_AUTH")
                intent.putExtra("arg_auth", "Y")
                sendBroadcast(intent)
            }
            4 -> {
                //发送鉴权结果-失败
                val intent = Intent("cn.rhax.tvhall.SC_AUTH")
                intent.putExtra("arg_auth", "N")
                sendBroadcast(intent)
            }
            5 -> {
                //直接打开当前游戏
                val html = "http://182.140.237.75:8300/static/h5game/android/hqct/index.html"
                val intent = packageManager.getLaunchIntentForPackage("cn.rhax.tvhall.happy")
                intent?.putExtra("arg", "test")
                intent?.putExtra("arg_url", html)
                startAct(intent!!)
            }
            6 -> {
                //发送鉴权结果-成功
                val intent = packageManager.getLaunchIntentForPackage("cn.rhax.tvhall.happy")
                intent?.putExtra("arg", "test")
                intent?.putExtra("arg_auth", "Y")
                startAct(intent!!)
            }
            7 -> {
                //发送鉴权结果-失败
                val intent = packageManager.getLaunchIntentForPackage("cn.rhax.tvhall.happy")
                intent?.putExtra("arg", "test")
                intent?.putExtra("arg_auth", "N")
                startAct(intent!!)
            }
        }
    }

    private fun startAct(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
