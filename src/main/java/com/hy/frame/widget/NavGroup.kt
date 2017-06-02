package com.hy.frame.widget

import android.content.Context
import android.support.annotation.IdRes
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

class NavGroup : LinearLayout, View.OnClickListener {
    // holds the checked id; the selection is empty by default
    /**
     *
     * Returns the identifier of the selected NavView in this group.
     * Upon empty selection, the returned value is -1.

     * @return the unique id of the selected NavView in this group
     *
     * @attr ref android.R.styleable#RadioGroup_checkedButton
     *
     * @see .check
     * @see .clearCheck
     */
    var checkedNavViewId = -1
    // tracks children NavViews checked state
    private val mChildOnClickListener: View.OnClickListener? = null
    // when true, mOnCheckedChangeListener discards events
    private var mProtectFromCheckedChange = false
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null
    private var mPassThroughListener: PassThroughHierarchyChangeListener? = null

    /**
     * {@inheritDoc}
     */
    constructor(context: Context) : super(context) {
        orientation = LinearLayout.VERTICAL
        init()
    }

    /**
     * {@inheritDoc}
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        mPassThroughListener = PassThroughHierarchyChangeListener()
        super.setOnHierarchyChangeListener(mPassThroughListener)
    }

    /**
     * {@inheritDoc}
     */
    override fun setOnHierarchyChangeListener(listener: ViewGroup.OnHierarchyChangeListener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener!!.mOnHierarchyChangeListener = listener
    }

    /**
     * {@inheritDoc}
     */
    override fun onFinishInflate() {
        super.onFinishInflate()

        // checks the appropriate NavView as requested in the XML file
        if (checkedNavViewId != View.NO_ID) {
            mProtectFromCheckedChange = true
            setCheckedStateForView(checkedNavViewId, true)
            mProtectFromCheckedChange = false
            setCheckedId(checkedNavViewId)
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child is NavView) {
            val nav = child
            if (nav.isChecked) {
                mProtectFromCheckedChange = true
                if (checkedNavViewId != View.NO_ID) {
                    setCheckedStateForView(checkedNavViewId, false)
                }
                mProtectFromCheckedChange = false
                setCheckedId(nav.id)
            }
        }
        super.addView(child, index, params)
    }

    /**
     *
     * Sets the selection to the NavView whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking [.clearCheck].

     * @param id the unique id of the NavView to select in this group
     *
     * @see .getCheckedNavViewId
     * @see .clearCheck
     */
    fun check(@IdRes id: Int) {
        // don't even bother
        if (id != View.NO_ID && id == checkedNavViewId) {
            return
        }

        if (checkedNavViewId != View.NO_ID) {
            setCheckedStateForView(checkedNavViewId, false)
        }

        if (id != View.NO_ID) {
            setCheckedStateForView(id, true)
        }

        setCheckedId(id)
    }

    private fun setCheckedId(@IdRes id: Int) {
        checkedNavViewId = id
        if (mOnCheckedChangeListener != null) {
            val nav = findViewById(id) as NavView
            mOnCheckedChangeListener!!.onCheckedChanged(this, nav, checkedNavViewId)
        }
    }

    private fun setCheckedStateForView(viewId: Int, checked: Boolean) {
        val checkedView = findViewById(viewId)
        if (checkedView != null && checkedView is NavView) {
            checkedView.isChecked = checked
        }
    }

    /**
     *
     * Clears the selection. When the selection is cleared, no NavView
     * in this group is selected and [.getCheckedNavViewId] returns
     * null.

     * @see .check
     * @see .getCheckedNavViewId
     */
    fun clearCheck() {
        check(-1)
    }

    /**
     *
     * Register a callback to be invoked when the checked radio NavView in this group.

     * @param listener the callback to call on checked state change
     */
    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        mOnCheckedChangeListener = listener
    }

    /**
     *
     * Interface definition for a callback to be invoked when the checked NavView changed in this group.
     */
    interface OnCheckedChangeListener {
        /**
         *
         * Called when the checked NavView has changed. When the selection is cleared, checkedId is -1.

         * @param group     the group
         *
         * @param nav       the group in which the checked NavView has changed
         *
         * @param checkedId the unique identifier of the newly checked NavView
         */
        fun onCheckedChanged(group: NavGroup, nav: NavView, @IdRes checkedId: Int)
    }

    /**
     *
     * A pass-through listener acts upon the events and dispatches them
     * to another listener. This allows the table layout to set its own internal
     * hierarchy change listener without preventing the user to setup his.
     */
    private inner class PassThroughHierarchyChangeListener : ViewGroup.OnHierarchyChangeListener {
        internal var mOnHierarchyChangeListener: ViewGroup.OnHierarchyChangeListener? = null

        /**
         * {@inheritDoc}
         */
        override fun onChildViewAdded(parent: View, child: View) {
            if (parent === this@NavGroup && child is NavView) {
                val id = child.getId()
                // generates an id if it's missing
                if (id != View.NO_ID) {
                    //                    id = View.generateViewId();
                    //                    child.setId(id);

                }
                child.setOnClickListener(this@NavGroup)
            }

            mOnHierarchyChangeListener?.onChildViewAdded(parent, child)
        }

        /**
         * {@inheritDoc}
         */
        override fun onChildViewRemoved(parent: View, child: View) {
            if (parent === this@NavGroup && child is NavView) {
                child.setOnClickListener(null)
            }
            mOnHierarchyChangeListener?.onChildViewRemoved(parent, child)
        }
    }

    override fun onClick(v: View) {
        onChildClick(v)
    }

    fun setCheckedChildByPosition(position: Int) {
        val v = getChildAt(position)
        onChildClick(v)
    }

    fun setCheckedChildById(id: Int) {
        val v = findViewById(id)
        onChildClick(v)
    }

    private fun onChildClick(v: View) {
        if (v.id != View.NO_ID && v is NavView) {
            if (!v.isChecked) {
                val size = childCount
                for (i in 0..size - 1) {
                    val child = getChildAt(i)
                    if (v.getId() != child.id && child is NavView) {
                        child.isChecked = false
                    }
                }
                check(v.getId())
            }
        }
    }
}
