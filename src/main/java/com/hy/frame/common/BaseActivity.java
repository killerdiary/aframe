package com.hy.frame.common;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.util.Constant;
import com.hy.frame.util.HyUtil;

;

/**
 * 父类Activity
 *
 * @author HeYan
 * @time 2014-7-18 下午2:53:55
 */
public abstract class BaseActivity extends AppCompatActivity implements android.view.View.OnClickListener, IBaseActivity {

    private BaseApplication app;
    protected Context context = this;
    private Class<?> lastAct;// 上一级 Activity
    private String lastSkipAct;// 跳转过来的Activity
    private Toolbar toolbar;
    private TextView txtTitle, txtMessage;
    private RelativeLayout rlyMain;
    private ImageView imgMessage;
    private View loadView, contentView;
    private ProgressBar proLoading;
    private boolean translucentStatus;

    public boolean isTranslucentStatus() {
        return translucentStatus;
    }

    public void setTranslucentStatus(boolean translucentStatus) {
        this.translucentStatus = translucentStatus;
    }

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
        int layout = initLayoutId();
        if (layout < 1)
            return;
        setContentView(R.layout.act_base);
        toolbar = getView(R.id.head_toolBar);
        toolbar.setTitle("");
        txtTitle = getView(R.id.head_vTitle);
        int statusBarHeight = getStatusBarHeight();
        if (translucentStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && statusBarHeight > 0) {
            toolbar.setPadding(0, statusBarHeight, 0, 0);
            //toolbar.setMinimumHeight();
        }
        setSupportActionBar(toolbar);
        rlyMain = getView(R.id.rlyMain);
        contentView = View.inflate(context, layout, null);
        if (contentView != null)
            resetLayout(contentView);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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


    //    @SuppressLint("NewApi")
//    private void setTitlebarBackground() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            rlyHead.setBackground(theme.getDrawTitleBar());
//        } else {
//            rlyHead.setBackgroundDrawable(theme.getDrawTitleBar());
//        }
//    }
    @Deprecated
    protected void showNavigation(int drawId) {
        if (drawId > 0)
            toolbar.setNavigationIcon(drawId);
        else
            toolbar.setNavigationIcon(null);
    }

    /**
     * 加载布局
     */
    private void initLoadView() {
        if (null == loadView) {
            loadView = View.inflate(context, R.layout.loading_act, null);
            proLoading = getView(loadView, R.id.loading_proLoading);
            imgMessage = getView(loadView, R.id.loading_imgMessage);
            txtMessage = getView(loadView, R.id.loading_txtMessage);
        }
    }

    protected void showLoading() {
        showLoading(getString(R.string.loading));
    }

    protected void showLoading(String msg) {
        initLoadView();
        resetLayout(loadView);
        proLoading.setVisibility(View.VISIBLE);
        imgMessage.setVisibility(View.GONE);
        txtMessage.setVisibility(View.VISIBLE);
        txtMessage.setText(msg);
    }

    protected void showNetFail() {
        showNetFail(getString(R.string.hint_net_fail));
    }

    protected void showNetFail(String msg) {
        initLoadView();
        resetLayout(loadView);
        proLoading.setVisibility(View.GONE);
        imgMessage.setVisibility(View.VISIBLE);
        txtMessage.setVisibility(View.VISIBLE);
        txtMessage.setText(msg);
        imgMessage.setImageResource(R.drawable.img_hint_net_fail);
    }

    protected void showNoData() {
        showNoData(getString(R.string.hint_nodata));
    }

    protected void showNoData(String msg) {
        showNoData(msg, R.drawable.img_hint_nodata);
    }

    protected void showNoData(String msg, int drawId) {
        initLoadView();
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
    public void setTitle(@StringRes int titleId) {
        setTitle(getString(titleId));
    }

    protected void hideHeader() {
        if (toolbar != null)
            toolbar.setVisibility(View.GONE);
    }

    protected void setHeaderLeft(@DrawableRes int left) {
        if (left > 0) {
            if (toolbar.findViewById(R.id.head_vLeft) == null) {
                View v = View.inflate(context, R.layout.in_head_left, toolbar);
                ImageView img = getView(v, R.id.head_vLeft);
                img.setOnClickListener(this);
                img.setImageResource(left);
            } else {
                ImageView img = getView(toolbar, R.id.head_vLeft);
                img.setImageResource(left);
            }
        }
    }

    protected void setHeaderLeftTxt(@StringRes int left) {
        if (left > 0) {
            if (toolbar.findViewById(R.id.head_vLeft) == null) {
                View v = View.inflate(context, R.layout.in_head_tleft, toolbar);
                TextView txt = getView(v, R.id.head_vLeft);
                txt.setOnClickListener(this);
                txt.setText(left);
                if (txtTitle != null)
                    txt.setTextColor(txtTitle.getTextColors());
            } else {
                TextView txt = getView(toolbar, R.id.head_vLeft);
                txt.setText(left);
            }
        }
    }

    protected void setHeaderRight(@DrawableRes int right) {
        if (right > 0) {
            if (toolbar.findViewById(R.id.head_vRight) == null) {
                View v = View.inflate(context, R.layout.in_head_right, toolbar);
                ImageView img = getView(v, R.id.head_vRight);
                img.setOnClickListener(this);
                img.setImageResource(right);
            } else {
                ImageView img = getView(toolbar, R.id.head_vRight);
                img.setImageResource(right);
            }
        }
    }

    protected void setHeaderRightTxt(@StringRes int right) {
        if (right > 0) {
            if (toolbar.findViewById(R.id.head_vRight) == null) {
                View v = View.inflate(context, R.layout.in_head_tright, toolbar);
                TextView txt = getView(v, R.id.head_vRight);
                txt.setOnClickListener(this);
                txt.setText(right);
                if (txtTitle != null)
                    txt.setTextColor(txtTitle.getTextColors());
            } else {
                TextView txt = getView(toolbar, R.id.head_vRight);
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
        return toolbar;
    }

    protected View getHeaderRight() {
        return toolbar.findViewById(R.id.head_vRight);
    }

    /**
     * 初始化布局(用customAct方法时使用)
     *
     * @param v
     */
    private void resetLayout(View v) {
        rlyMain.removeAllViews();
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlyMain.addView(v, rlp);
    }

    protected View getMainView() {
        return rlyMain;
    }

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
        finish();
    }

    protected void actFinish() {
        if (lastAct != null && !lastAct.getSimpleName().equals(lastSkipAct)) {
            startAct(lastAct);
        }
        finish();
    }

    @Override
    public void finish() {
        if (app != null) {
            app.remove(this);
        }
        super.finish();
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
     * @param view 布局
     * @param id   行布局中某个组件的id
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(View view, @IdRes int id) {
        return (T) view.findViewById(id);
    }

    /**
     * 获取 控件
     *
     * @param id 行布局中某个组件的id
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(@IdRes int id) {
        return (T) findViewById(id);
    }

    /**
     * 获取并绑定点击
     *
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T getViewAndClick(@IdRes int id) {
        T v = getView(id);
        v.setOnClickListener(this);
        return v;
    }

    protected void setOnClickListener(@IdRes int id) {
        findViewById(id).setOnClickListener(this);
    }

    /**
     * 获取当前布局中的控件
     *
     * @param id 行布局中某个组件的id
     * @return
     */
    public <T extends View> T getCView(@IdRes int id) {
        return getView(contentView, id);
    }

    /**
     * 获取当前布局中的控件
     *
     * @param id 行布局中某个组件的id
     * @return
     */
    public <T extends View> T getCViewAndClick(@IdRes int id) {
        T v = getView(contentView, id);
        v.setOnClickListener(this);
        return v;
    }

    /**
     * 头-左边图标点击
     */
    public void onLeftClick() {
        actFinish();
    }

    /**
     * 头-右边图标点击
     */
    public void onRightClick() {
    }

}
