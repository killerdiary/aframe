package com.hy.demo2.app

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.view.KeyEvent
import android.view.View
import com.hy.demo2.R
import com.hy.frame.mvp.IBasePresenter

abstract class BaseActivity : BasePresenterActivity<IBasePresenter>() {
    override fun buildPresenter(): IBasePresenter? = null


    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        var result = false
        if (keyCode in arrayOf(KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_CENTER)) {
            result = onKeyDpad(keyCode)
        }
        if (result)
            return true
        return super.onKeyUp(keyCode, event)
    }

    /**
     * 只包含方向和确定键
     */
    open fun onKeyDpad(keyCode: Int): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> onKeyDpadLeft()
            KeyEvent.KEYCODE_DPAD_UP -> onKeyDpadUp()
            KeyEvent.KEYCODE_DPAD_RIGHT -> onKeyDpadRight()
            KeyEvent.KEYCODE_DPAD_DOWN -> onKeyDpadDown()
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> onKeyDpadEnter()
            else -> false
        }
    }

    var selectId = View.NO_ID

//    private fun getCurrentFocus(): View? {
//        return if (activity != null && isAdded && selectId != View.NO_ID) {
//            findViewById(selectId)
//        } else null
//    }

    fun getNextFocusId(keyCode: Int): Int {
        val vFocus = currentFocus ?: return View.NO_ID
        var lastViewId = vFocus.id
        var nextFocusId = getNextFocusId(vFocus, keyCode)
        var vNext: View? = findViewById(nextFocusId)
        while (vNext != null && nextFocusId != View.NO_ID && nextFocusId != lastViewId && vNext.visibility != View.VISIBLE) {
            lastViewId = nextFocusId
            nextFocusId = getNextFocusId(vNext, keyCode)
            vNext = findViewById(nextFocusId)
        }
        if (vNext == null || nextFocusId == lastViewId || nextFocusId == View.NO_ID || vNext.visibility != View.VISIBLE)
            return View.NO_ID
        return nextFocusId
    }

    private fun getNextFocusId(v: View?, keyCode: Int): Int {
        if (v == null) return View.NO_ID
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> v.nextFocusLeftId
            KeyEvent.KEYCODE_DPAD_UP -> v.nextFocusUpId
            KeyEvent.KEYCODE_DPAD_RIGHT -> v.nextFocusRightId
            KeyEvent.KEYCODE_DPAD_DOWN -> v.nextFocusDownId
            else -> View.NO_ID
        }
    }

    open fun onKeyDpadLeft(): Boolean {
        val nextFocusId = getNextFocusId(KeyEvent.KEYCODE_DPAD_LEFT)
        if (nextFocusId == View.NO_ID)
            return false
        selectId = nextFocusId
        onfocusChange(nextFocusId)
        return true
    }


    open fun onKeyDpadUp(): Boolean {
        val nextFocusId = getNextFocusId(KeyEvent.KEYCODE_DPAD_UP)
        if (nextFocusId == View.NO_ID)
            return false
        selectId = nextFocusId
        onfocusChange(nextFocusId)
        return true
    }

    open fun onKeyDpadRight(): Boolean {
        val nextFocusId = getNextFocusId(KeyEvent.KEYCODE_DPAD_RIGHT)
        if (nextFocusId == View.NO_ID)
            return false
        selectId = nextFocusId
        onfocusChange(nextFocusId)
        return true
    }

    open fun onKeyDpadDown(): Boolean {
        val nextFocusId = getNextFocusId(KeyEvent.KEYCODE_DPAD_DOWN)
        if (nextFocusId == View.NO_ID)
            return false
        selectId = nextFocusId
        onfocusChange(nextFocusId)
        return true
    }

    open fun onKeyDpadEnter(): Boolean {
        return false
    }

    open fun onfocusChange(id: Int) {

    }
}