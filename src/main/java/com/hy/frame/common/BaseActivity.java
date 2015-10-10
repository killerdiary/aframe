package com.hy.frame.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.bean.ThemeInfo;
import com.hy.frame.util.Constant;
import com.hy.frame.util.HyUtil;

;

/**
 * 父类Activity
 * 
 * @author HeYan
 * @time 2014-7-18 下午2:53:55
 */
public abstract class BaseActivity extends Activity implements android.view.View.OnClickListener {

    private BaseApplication app;
    protected Context context = this;
    private Class<?> lastAct;// 上一级 Activity
    private String lastSkipAct;// 跳转过来的Activity
    private TextView txtTitle, txtMessage;
    private RelativeLayout rlyHead, rlyMain;
    private ImageView imgMessage;
    private View loadView, contentView;
    private ProgressBar proLoading;
    private ThemeInfo theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
        initData();
    }

    private void init() {
        lastSkipAct = getIntent().getStringExtra(Constant.LAST_ACT);// 获取上一级Activity的Name
        app = (BaseApplication) getApplication();
        app.addActivity(this);
    }

    protected void initTheme(ThemeInfo theme) {
        this.theme = theme;
    }

    public BaseApplication getApp() {
        return app;
    }

    /**
     * 获取上一级的Activity名
     * 
     * @return
     */
    public String getLastSkipAct() {
        return lastSkipAct;
    }

    /**
     * 使用统一布局
     * 
     * @param layout
     *            内容布局(除标题外)
     */
    protected void customAct(int layout) {
        if (layout < 1)
            return;
        setContentView(R.layout.base);
        rlyHead = getView(R.id.rlyHead);
        txtTitle = getView(R.id.head_vTitle);
        rlyMain = getView(R.id.rlyMain);
        setTitlebarBackground();
        txtTitle.setTextColor(theme.getTitleColor());
        txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, HyUtil.floatToSpDimension(theme.getTitleSize(), context));
        txtTitle.getPaint().setFakeBoldText(theme.isTitleBold());
        rlyMain.setBackgroundColor(theme.getThemeBackground());
        contentView = getLayoutInflater().inflate(layout, null);
        if (loadView != null)
            resetLayout(loadView);
        else if (contentView != null)
            resetLayout(contentView);
    }

    @SuppressLint("NewApi")
    private void setTitlebarBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rlyHead.setBackground(theme.getDrawTitleBar());
        } else {
            rlyHead.setBackgroundDrawable(theme.getDrawTitleBar());
        }
    }

    /**
     * 使用统一布局,带加载布局<br/>
     * 
     * @see #customAct(int, int)
     */
    protected void customLoadAct(int layout) {
        loadView = getLayoutInflater().inflate(R.layout.loading_act, null);
        proLoading = getView(loadView, R.id.loading_proLoading);
        imgMessage = getView(loadView, R.id.loading_imgMessage);
        txtMessage = getView(loadView, R.id.loading_txtMessage);
        customAct(layout);
    }

    protected void showLoading() {
        showLoading(getString(R.string.loading));
    }

    protected void showLoading(String msg) {
        if (loadView != null) {
            resetLayout(loadView);
            proLoading.setVisibility(View.VISIBLE);
            imgMessage.setVisibility(View.GONE);
            txtMessage.setVisibility(View.VISIBLE);
            txtMessage.setText(msg);
        }
    }

    protected void showNetFail() {
        showNetFail(getString(R.string.hint_net_fail));
    }

    protected void showNetFail(String msg) {
        if (loadView != null) {
            resetLayout(loadView);
            proLoading.setVisibility(View.GONE);
            imgMessage.setVisibility(View.VISIBLE);
            txtMessage.setVisibility(View.VISIBLE);
            txtMessage.setText(msg);
            imgMessage.setImageResource(R.drawable.hint_net_fail);
        }
    }

    protected void showNoData() {
        showNoData(getString(R.string.hint_nodata));
    }

    protected void showNoData(String msg) {
        showNoData(msg, R.drawable.hint_nodata);
    }

    protected void showNoData(String msg, int drawId) {
        if (loadView != null) {
            resetLayout(loadView);
            proLoading.setVisibility(View.GONE);
            imgMessage.setVisibility(View.VISIBLE);
            txtMessage.setVisibility(View.VISIBLE);
            if (msg == null)
                txtMessage.setText(R.string.hint_nodata);
            else
                txtMessage.setText(msg);
            imgMessage.setImageResource(drawId);
        }
    }

    protected void showCView() {
        if (contentView != null)
            resetLayout(contentView);
    }

    /**
     * 设置标题
     */
    @Override
    public void setTitle(CharSequence title) {
        if (txtTitle != null)
            txtTitle.setText(title);
        else
            super.setTitle(title);
    }

    /**
     * 设置标题
     */
    @Override
    public void setTitle(int titleId) {
        if (txtTitle != null)
            txtTitle.setText(titleId);
        else
            super.setTitle(titleId);
    }

    protected void setHeaderLeft(int left) {
        if (left > 0) {
            if (rlyHead.findViewById(R.id.head_vLeft) == null) {
                View v = getLayoutInflater().inflate(R.layout.in_head_left, rlyHead);
                ImageView img = getView(v, R.id.head_vLeft);
                img.setOnClickListener(this);
                img.setImageResource(left);
            } else {
                ImageView img = getView(rlyHead, R.id.head_vLeft);
                img.setImageResource(left);
            }
        }
    }

    protected void setHeaderLeftTxt(int left) {
        if (left > 0) {
            if (rlyHead.findViewById(R.id.head_vLeft) == null) {
                View v = getLayoutInflater().inflate(R.layout.in_head_tleft, rlyHead);
                TextView txt = getView(v, R.id.head_vLeft);
                txt.setOnClickListener(this);
                txt.setText(left);
                txt.setTextColor(theme.getTitleColor());
            } else {
                TextView txt = getView(rlyHead, R.id.head_vLeft);
                txt.setText(left);
            }
        }
    }

    protected void setHeaderRight(int right) {
        if (right > 0) {
            if (rlyHead.findViewById(R.id.head_vRight) == null) {
                View v = getLayoutInflater().inflate(R.layout.in_head_right, rlyHead);
                ImageView img = getView(v, R.id.head_vRight);
                img.setOnClickListener(this);
                img.setImageResource(right);
            } else {
                ImageView img = getView(rlyHead, R.id.head_vRight);
                img.setImageResource(right);
            }
        }
    }

    protected void setHeaderRightTxt(int right) {
        if (right > 0) {
            if (rlyHead.findViewById(R.id.head_vRight) == null) {
                View v = getLayoutInflater().inflate(R.layout.in_head_tright, rlyHead);
                TextView txt = getView(v, R.id.head_vRight);
                txt.setOnClickListener(this);
                txt.setText(right);
                txt.setTextColor(theme.getTitleColor());
            } else {
                TextView txt = getView(rlyHead, R.id.head_vRight);
                txt.setText(right);
            }
        }
    }

    /**
     * 头部
     * 
     * @return
     */
    protected View getHeader() {
        return rlyHead;
    }

    protected View getHeaderRight() {
        return rlyHead.findViewById(R.id.head_vRight);
    }

    // /**
    // * 初始化布局(用customAct方法时使用)
    // *
    // * @param layout
    // */
    // protected View initLayout(int layout) {
    // rlyMain.removeAllViews();
    // return getLayoutInflater().inflate(layout, rlyMain);
    // }
    //
    /**
     * 初始化布局(用customAct方法时使用)
     * 
     * @param layout
     */
    private void resetLayout(View v) {
        rlyMain.removeAllViews();
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlyMain.addView(v, rlp);
    }

    protected View getMainView() {
        return rlyMain;
    }

    //
    // /**
    // * 重置布局(用customAct方法时使用)
    // *
    // * @param layout
    // */
    // protected void addLayout(int layout) {
    //
    // getLayoutInflater().inflate(layout, rlyMain);
    // }
    //
    // /**
    // * 重置布局(用customAct方法时使用)
    // *
    // * @param layout
    // */
    // protected void addLayout(View v) {
    // rlyMain.addView(v);
    // }

    protected void setLastAct(Class<?> cls) {
        this.lastAct = cls;
    }

    /**
     * 启动Activity
     */
    protected void startAct(Class<?> cls) {
        startAct(null, cls);
    }

    /**
     * 启动Activity
     */
    protected void startAct(Intent intent, Class<?> cls) {
        if (intent == null)
            intent = new Intent();
        intent.putExtra(Constant.LAST_ACT, this.getClass().getSimpleName());
        intent.setClass(this, cls);
        startActivity(intent);
    }

    /**
     * 启动Activity
     * 
     * @param cls
     */
    protected void startActExit(Class<?> cls) {
        startActExit(null, cls);
    }

    /**
     * 启动Activity
     * 
     * @param cls
     */
    protected void startActExit(Intent intent, Class<?> cls) {
        app.clear();
        startAct(intent, cls);
    }

    /**
     * 启动Activity并关闭当前Activity
     * 
     * @param cls
     */
    protected void startActFinish(Class<?> cls) {
        startAct(cls);
        super.finish();
    }

    protected void actFinish() {
        if (lastAct != null && !lastAct.getSimpleName().equals(lastSkipAct)) {
            startAct(lastAct);
        }
        super.finish();
    }

    protected void clearText(TextView tv) {
        tv.setText("");
    }

    protected void setText(TextView tv, String value) {
        if (value == null)
            value = "";
        tv.setText(value);
    }

    protected void setText(TextView tv, int value) {
        tv.setText(value);
    }

    protected String getStrings(Integer... ids) {
        if (ids.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int id : ids) {
                sb.append(getString(id));
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 初始化布局
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 控件点击事件
     */
    protected abstract void onViewClick(View v);

    @Override
    public void onClick(View v) {
        if (HyUtil.isFastClick())
            return;
        if (v.getId() == R.id.head_vLeft)
            onLeftClick();
        else if (v.getId() == R.id.head_vRight)
            onRightClick();
        else
            onViewClick(v);
    }

    /**
     * 获取 控件
     * 
     * @param view
     *            布局
     * @param id
     *            行布局中某个组件的id
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(View view, int id) {
        View v = view.findViewById(id);
        return (T) v;
    }

    /**
     * 获取 控件
     * 
     * @param view
     *            布局
     * @param id
     *            行布局中某个组件的id
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int id) {
        View v = findViewById(id);
        return (T) v;
    }

    /**
     * 获取并绑定点击
     * 
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T getViewAndClick(int id) {
        View v = findViewById(id);
        v.setOnClickListener(this);
        return (T) v;
    }

    /**
     * 获取当前布局中的控件
     * 
     * @param id
     *            行布局中某个组件的id
     * @return
     */
    public <T extends View> T getCView(int id) {
        return getView(contentView, id);
    }

    /**
     * 头-左边图标点击
     */
    protected void onLeftClick() {
        actFinish();
    }

    /**
     * 头-右边图标点击
     */
    protected void onRightClick() {

    }

}
