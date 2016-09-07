package com.hy.frame.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.common.BaseDialog;

/**
 * 加载对话框
 * author HeYan
 * time 2015/12/11 18:11
 */
public class LoadingDialog extends BaseDialog {

    private TextView txtLoadMsg;
    private String loadMsg;

    public LoadingDialog(Context context) {
        this(context, null);
    }

    public LoadingDialog(Context context, String loadMsg) {
        super(context);
        this.loadMsg = loadMsg;
    }

    @Override
    protected int initLayoutId() {
        return R.layout.dlg_loading;
    }

    @Override
    protected void initWindow() {
        windowDeploy(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
    }

    @Override
    protected void initView() {
        txtLoadMsg = getView(R.id.loading_txtLoadMsg);
    }

    @Override
    protected void initData() {
        if (loadMsg != null)
            txtLoadMsg.setText(loadMsg);
    }

    @Override
    protected void onViewClick(View v) {

    }

    public void updateMsg(String msg) {
        if (txtLoadMsg != null) {
            txtLoadMsg.setText(msg);
        } else {
            loadMsg = msg;
        }
    }

}
