package com.hy.frame.bean;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hy.frame.R;

/**
 * LoadCache
 *
 * @author HeYan
 * @time 2017/5/23 10:51
 */
public class LoadCache {
    public LinearLayout llyLoad;
    public TextView txtMessage;
    public ImageView imgMessage;
    public ProgressBar proLoading;

    public void showLoading(String msg) {
        llyLoad.setVisibility(View.VISIBLE);
        proLoading.setVisibility(View.VISIBLE);
        imgMessage.setVisibility(View.GONE);
        txtMessage.setVisibility(View.VISIBLE);
        txtMessage.setText(msg);
    }

    public void showNoData(String msg, int drawId) {
        llyLoad.setVisibility(View.VISIBLE);
        proLoading.setVisibility(View.GONE);
        imgMessage.setVisibility(View.VISIBLE);
        txtMessage.setVisibility(View.VISIBLE);
        if (msg == null) txtMessage.setText(R.string.hint_nodata);
        else txtMessage.setText(msg);
        imgMessage.setImageResource(drawId);
    }
}