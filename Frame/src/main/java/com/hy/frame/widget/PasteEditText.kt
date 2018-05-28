package com.hy.frame.widget

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet

/**
 * BS_Studio
 * @author HeYan
 * @time 2017/5/18 16:59
 */
class PasteEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onTextContextMenuItem(id: Int): Boolean {
        if (id == ID_PASTE) {
            val result = super.onTextContextMenuItem(id)
            if (result && copyListener != null) {
                copyListener!!.onTextPaste()
            }
            return result
        }
        return super.onTextContextMenuItem(id)
    }

    private var copyListener: ICopyListener? = null

    fun setCopyListener(copyListener: ICopyListener) {
        this.copyListener = copyListener
    }

    interface ICopyListener {
        fun onTextPaste()
    }

    companion object {
        internal val ID_PASTE = android.R.id.paste
    }
}
