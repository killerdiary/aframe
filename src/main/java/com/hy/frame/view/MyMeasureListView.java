package com.hy.frame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyMeasureListView extends ListView {

    public MyMeasureListView(Context context) {
        super(context);
    }

    public MyMeasureListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMeasureListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}