package com.hy.frame.view.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hy.frame.R;

/**
 * LoadMoreHolder
 *
 * @author HeYan LoadMoreHolder
 * @time 2016/5/27 16:35
 */
public class LoadMoreHolder extends RecyclerView.ViewHolder {
    public static final int STATE_LOAD_PREPARE = 0;
    public static final int STATE_LOAD_ING = 1;
    public static final int STATE_LOAD_COMPLETE = 2;
    ProgressBar proFoot;
    TextView txtFootHint;

    public LoadMoreHolder(View v) {
        super(v);
        proFoot = (ProgressBar) v.findViewById(R.id.footer_proFoot);
        txtFootHint = (TextView) v.findViewById(R.id.footer_txtFootHint);
    }

    public void onChangeState(int state){
        switch (state){
            case STATE_LOAD_ING:
                loading();
                break;
            case STATE_LOAD_COMPLETE:
                loadComplete();
                break;
            default:
                prepare();
                break;
        }
    }


    private void prepare() {
        proFoot.setVisibility(View.GONE);
        txtFootHint.setText(R.string.load_more);
    }

    private void loading() {
        proFoot.setVisibility(View.VISIBLE);
        txtFootHint.setText(R.string.loading);
    }

    private void loadComplete() {
        proFoot.setVisibility(View.GONE);
        txtFootHint.setText(R.string.load_complete);
    }
}
