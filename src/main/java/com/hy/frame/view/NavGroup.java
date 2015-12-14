package com.hy.frame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.hy.frame.R;

public class NavGroup extends LinearLayout implements View.OnClickListener {
    // holds the checked id; the selection is empty by default
    private int mCheckedId = -1;
    // tracks children NavViews checked state
    private View.OnClickListener mChildOnClickListener;
    // when true, mOnCheckedChangeListener discards events
    private boolean mProtectFromCheckedChange = false;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;

    /**
     * {@inheritDoc}
     */
    public NavGroup(Context context) {
        super(context);
        setOrientation(VERTICAL);
        init();
    }

    /**
     * {@inheritDoc}
     */
    public NavGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // checks the appropriate NavView as requested in the XML file
        if (mCheckedId != -1) {
            mProtectFromCheckedChange = true;
            setCheckedStateForView(mCheckedId, true);
            mProtectFromCheckedChange = false;
            setCheckedId(mCheckedId);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof NavGroup) {
            final NavView nav = (NavView) child;
            if (nav.isChecked()) {
                mProtectFromCheckedChange = true;
                if (mCheckedId != -1) {
                    setCheckedStateForView(mCheckedId, false);
                }
                mProtectFromCheckedChange = false;
                setCheckedId(nav.getId());
            }
        }
        super.addView(child, index, params);
    }

    /**
     * <p>Sets the selection to the NavView whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking {@link #clearCheck()}.</p>
     *
     * @param id the unique id of the NavView to select in this group
     * @see #getCheckedNavViewId()
     * @see #clearCheck()
     */
    public void check(@IdRes int id) {
        // don't even bother
        if (id != -1 && (id == mCheckedId)) {
            return;
        }

        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false);
        }

        if (id != -1) {
            setCheckedStateForView(id, true);
        }

        setCheckedId(id);
    }

    private void setCheckedId(@IdRes int id) {
        mCheckedId = id;
        if (mOnCheckedChangeListener != null) {
            NavView nav = (NavView) findViewById(id);
            mOnCheckedChangeListener.onCheckedChanged(this, nav, mCheckedId);
        }
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView != null && checkedView instanceof NavView) {
            ((NavView) checkedView).setChecked(checked);
        }
    }

    /**
     * <p>Returns the identifier of the selected NavView in this group.
     * Upon empty selection, the returned value is -1.</p>
     *
     * @return the unique id of the selected NavView in this group
     * @attr ref android.R.styleable#RadioGroup_checkedButton
     * @see #check(int)
     * @see #clearCheck()
     */
    @IdRes
    public int getCheckedNavViewId() {
        return mCheckedId;
    }

    /**
     * <p>Clears the selection. When the selection is cleared, no NavView
     * in this group is selected and {@link #getCheckedNavViewId()} returns
     * null.</p>
     *
     * @see #check(int)
     * @see #getCheckedNavViewId()
     */
    public void clearCheck() {
        check(-1);
    }

    /**
     * <p>Register a callback to be invoked when the checked radio NavView in this group.</p>
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * <p>Interface definition for a callback to be invoked when the checked NavView changed in this group.</p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>Called when the checked NavView has changed. When the selection is cleared, checkedId is -1.</p>
         *
         * @param group     the group
         * @param nav       the group in which the checked NavView has changed
         * @param checkedId the unique identifier of the newly checked NavView
         */
        public void onCheckedChanged(NavGroup group, NavView nav, @IdRes int checkedId);
    }

    /**
     * <p>A pass-through listener acts upon the events and dispatches them
     * to another listener. This allows the table layout to set its own internal
     * hierarchy change listener without preventing the user to setup his.</p>
     */
    private class PassThroughHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {
        private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        public void onChildViewAdded(View parent, View child) {
            if (parent == NavGroup.this && child instanceof NavView) {
                int id = child.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
//                    id = View.generateViewId();
//                    child.setId(id);
                }
                child.setOnClickListener(NavGroup.this);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onChildViewRemoved(View parent, View child) {
            if (parent == NavGroup.this && child instanceof NavView) {
                child.setOnClickListener(null);
            }
            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

    @Override
    public void onClick(View v) {
        onChildClick(v);
    }

    public void setCheckedChildByPosition(int position) {
        View v = getChildAt(position);
        onChildClick(v);
    }

    public void setCheckedChildById(int id) {
        View v = findViewById(id);
        onChildClick(v);
    }

    private void onChildClick(View v) {
        if (v.getId() != View.NO_ID && v instanceof NavView) {
            if (!((NavView) v).isChecked()) {
                int size = getChildCount();
                for (int i = 0; i < size; i++) {
                    View child = getChildAt(i);
                    if (v.getId() != child.getId()) {
                        ((NavView) child).setChecked(false);
                    }
                }
                check(v.getId());
            }
        }
    }
}
