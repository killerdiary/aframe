package com.hy.frame.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.hy.frame.R
import com.hy.frame.app.BaseDialog
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog
import com.hy.frame.widget.loopview.LoopWheelView
import java.util.*

/**
 * 时间选择
 * @author HeYan
 * @time 2017/7/31 15:06
 */
class DatePickerDialog(context: Context) : BaseDialog(context) {
    private var txtTitle: TextView? = null
    private var lwvYear: LoopWheelView? = null
    private var lwvMonth: LoopWheelView? = null
    private var lwvDay: LoopWheelView? = null
    private var btnCancel: Button? = null
    private var btnConfirm: Button? = null
    var minDate: Long = 0
    var maxDate: Long = 0
    var cal: Calendar? = null
    private var cacheCal: Calendar? = null
    private val strs = arrayOf("天", "一", "二", "三", "四", "五", "六")
    private var minYear = 0
    override fun getLayoutId(): Int = R.layout.dlg_date_picker

    override fun initWindow() {
        windowDeploy(WindowManager.LayoutParams.MATCH_PARENT.toFloat(), WindowManager.LayoutParams.WRAP_CONTENT.toFloat(), Gravity.BOTTOM)
        window!!.setWindowAnimations(R.style.animBottomInBottomOutStyle)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }

    override fun initView() {
        txtTitle = findViewById(R.id.date_picker_txtTitle)
        lwvYear = findViewById(R.id.date_picker_lwvYear)
        lwvMonth = findViewById(R.id.date_picker_lwvMonth)
        lwvDay = findViewById(R.id.date_picker_lwvDay)
        btnCancel = setOnClickListener(R.id.date_picker_btnCancel)
        btnConfirm = setOnClickListener(R.id.date_picker_btnConfirm)
    }

    override fun initData() {
        cacheCal = Calendar.getInstance(Locale.CHINESE)
        cacheCal?.clear()
        if (minDate != 0L) {
            cacheCal?.timeInMillis = minDate
        }
        minYear = cacheCal!!.get(Calendar.YEAR)
        var maxYear = minYear + 100
        if (maxDate != 0L) {
            cacheCal?.timeInMillis = maxDate
            val year = cacheCal!!.get(Calendar.YEAR)
            if (year >= minYear)
                maxYear = year
        }
        cacheCal?.timeInMillis = System.currentTimeMillis()
        val curYear = cacheCal!!.get(Calendar.YEAR)
        val curMonth = cacheCal!!.get(Calendar.MONTH)
        //val curDay = cacheCal!!.get(Calendar.DAY_OF_MONTH)
        val yearStrs: MutableList<String> = ArrayList()
        (minYear..maxYear).mapTo(yearStrs) { it.toString() }
        lwvYear?.setItems(yearStrs)
        val monthStrs: MutableList<String> = ArrayList()
        (1..12).mapTo(monthStrs) { String.format("%02d", it) }
        lwvMonth?.setItems(monthStrs)
        if (curYear in minYear..maxYear) {
            lwvYear?.setInitPosition(curYear - minYear)
            lwvMonth?.setInitPosition(curMonth)
        } else {
            lwvYear?.setInitPosition(0)
            lwvMonth?.setInitPosition(0)
            cacheCal?.clear()
        }
        val maxDayOfMonth = cacheCal!!.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayStrs: MutableList<String> = ArrayList()
        (1..maxDayOfMonth).mapTo(dayStrs) { String.format("%02d", it) }
        lwvDay?.setItems(dayStrs)
        if (curYear in minYear..maxYear) {
            lwvDay?.setInitPosition(cacheCal!!.get(Calendar.DAY_OF_MONTH) - 1)
        } else {
            lwvDay?.setInitPosition(0)
        }
        cal = Calendar.getInstance(Locale.CHINESE)
        onDayChange()
        //cal?.clear()
        //cal?.set(cacheCal!!.get(Calendar.YEAR), cacheCal!!.get(Calendar.MONTH), cacheCal!!.get(Calendar.DAY_OF_MONTH))
//        txtTitle?.text = context.resources.getString(R.string.date_picker_title, cal!!.get(Calendar.YEAR), cal!!.get(Calendar.MONTH) + 1, cal!!.get(Calendar.DAY_OF_MONTH), strs[cal!!.get(Calendar.DAY_OF_WEEK) - 1])
        lwvYear?.setListener { onYearOrMonthChange() }
        lwvMonth?.setListener { onYearOrMonthChange() }
        lwvDay?.setListener { onDayChange() }
    }

    private fun onYearOrMonthChange() {
        MyLog.e("onYearOrMonthChange")
        val cacheDay = cal!!.get(Calendar.DAY_OF_MONTH)
        cacheCal?.clear()
        cacheCal?.set(lwvYear!!.selectedItemStr.toInt(), lwvMonth!!.selectedItemStr.toInt() - 1, 1)
        val maxCacheDayOfMonth = cacheCal!!.getActualMaximum(Calendar.DAY_OF_MONTH)
        val maxDayOfMonth = cal!!.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (maxCacheDayOfMonth != maxDayOfMonth) {
            val dayStrs: MutableList<String> = ArrayList()
            (1..maxCacheDayOfMonth).mapTo(dayStrs) { String.format("%02d", it) }
            lwvDay?.setItems(dayStrs)
            if (cacheDay > maxCacheDayOfMonth)
                lwvDay?.setCurrentPosition(maxCacheDayOfMonth - 1)
        }
        onDayChange()
    }

    private fun onDayChange() {
        MyLog.e("onDayChange")
        cal?.clear()
        btnConfirm?.postDelayed({
            if (!lwvDay!!.selectedItemStr.isNullOrEmpty())
                cal?.set(lwvYear!!.selectedItemStr.toInt(), lwvMonth!!.selectedItemStr.toInt() - 1, lwvDay!!.selectedItemStr.toInt())
            txtTitle?.text = context.resources.getString(R.string.date_picker_title, cal!!.get(Calendar.YEAR), cal!!.get(Calendar.MONTH) + 1, cal!!.get(Calendar.DAY_OF_MONTH), strs[cal!!.get(Calendar.DAY_OF_WEEK) - 1])
            btnConfirm?.isEnabled = !(minDate != 0L && cal!!.timeInMillis < minDate || maxDate != 0L && cal!!.timeInMillis > maxDate)
        }, 300L)
    }

    override fun onViewClick(v: View) {
        dismiss()
        when (v.id) {
            R.id.date_picker_btnConfirm -> listener?.onDlgConfirm(this)
        }
    }

    fun getTimeMillis(): Long {
        return cal!!.timeInMillis
    }

    fun getSelectDate(): String {
        return HyUtil.getDateTime(getTimeMillis(), "yyyy-MM-dd")
    }
}