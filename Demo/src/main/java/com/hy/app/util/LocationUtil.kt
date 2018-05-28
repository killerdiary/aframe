package com.hy.app.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.hy.frame.util.FormatUtil
import com.hy.frame.util.MyLog
import com.hy.frame.util.PermissionUtil
import java.util.*

/**
 * 定位工具
 * author HeYan
 * time 2015/12/21 14:20
 */
class LocationUtil(val context: Context) {
    //声明AMapLocationClient类对象
    private var client: AMapLocationClient? = null
    private var listeners: MutableList<AMapLocationListener>? = null
    var lastLocation: AMapLocation? = null
        private set
    private var history: Boolean = false//复位
    private var isStarted: Boolean = false

    init {
        this.client = AMapLocationClient(context.applicationContext)
        this.client!!.setLocationListener { aMapLocation ->
            MyLog.d("定位.onLocationChanged")
            isStarted = false
            if (aMapLocation.errorCode == AMapLocation.LOCATION_SUCCESS)
                lastLocation = aMapLocation
            else
                aMapLocation.errorInfo = "定位失败"
            if (FormatUtil.isNoEmpty(listeners)) {
                for (listener in listeners!!) {
                    listener.onLocationChanged(aMapLocation)
                }
                listeners!!.clear()
            }
        }
    }

    //设置定位回调监听
    fun addListener(listener: AMapLocationListener) {
        if (listeners == null)
            listeners = ArrayList()
        listeners!!.add(listener)
    }

    fun removeListeners() {
        if (listeners != null)
            listeners!!.clear()
    }

    fun setHistory(history: Boolean) {
        this.history = history
    }

    fun startLocation(mLocationOption: AMapLocationClientOption?, act: Activity) {
        if (!PermissionUtil.requesLocationPermission(act)) {
            MyLog.e(javaClass, "您没有定位权限")
        }
        startLocation(mLocationOption)
    }

    /**
     * 启动定位
     * @param mLocationOption 为空使用默认配置
     */
    fun startLocation(mLocationOption: AMapLocationClientOption?, fragment: com.hy.frame.app.IBaseFragment) {
        if (!PermissionUtil.requesLocationPermission(fragment.getFragment().activity!!)) {
            MyLog.e(javaClass, "您没有定位权限")
        }
        startLocation(mLocationOption)
    }

    private fun startLocation(option: AMapLocationClientOption?) {
        var mLocationOption = option
        if (history && lastLocation != null) {
            if (FormatUtil.isNoEmpty(listeners)) {
                for (listener in listeners!!) {
                    listener.onLocationChanged(lastLocation)
                }
                listeners!!.clear()
            }
        }
        if (isStarted) {
            MyLog.d("有定位正在运行...")
            return
        }
        if (mLocationOption == null) {
            //初始化定位参数
            mLocationOption = AMapLocationClientOption()
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.isNeedAddress = true
            //设置是否只定位一次,默认为false
            mLocationOption.isOnceLocation = true
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.isWifiActiveScan = true
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.isMockEnable = false
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.interval = 2000
        }
        //给定位客户端对象设置定位参数
        client!!.setLocationOption(mLocationOption)
        isStarted = true
        //启动定位
        client!!.startLocation()
    }

    fun onDestroy() {
        if (client != null) {
            if (client!!.isStarted)
                client!!.stopLocation()
            client!!.onDestroy()
            client = null
            instance = null
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: LocationUtil? = null

        fun getInstance(context: Context, listener: AMapLocationListener?): LocationUtil {
            if (instance == null)
                instance = LocationUtil(context)
            if (listener != null)
                instance!!.addListener(listener)
            instance!!.setHistory(false)
            return instance!!
        }

        fun getInstance(context: Context, listener: AMapLocationListener?, history: Boolean): LocationUtil {
            if (instance == null)
                instance = LocationUtil(context)
            if (listener != null)
                instance!!.addListener(listener)
            instance!!.setHistory(history)
            return instance!!
        }
    }
}
