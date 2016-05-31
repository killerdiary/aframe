package com.hy.frame.common;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.bean.LoadCache;
import com.hy.frame.util.HyUtil;
import com.hy.frame.util.MyLog;
import com.hy.http.MyHttpClient;

/**
 * 父类Fragment
 * author HeYan
 * time 2015/12/23 17:12
 */
public abstract class BaseFragment extends Fragment implements android.view.View.OnClickListener, IFragmentListener, IBaseActivity {
    // private boolean custom;
    private BaseApplication app;
    protected Context context;
    private Toolbar toolbar;
    private TextView txtTitle;
    private FrameLayout flyMain;
    private LoadCache loadCache;
    private int showCount;
    private boolean init;
    private MyHttpClient client;

    public void setInit(boolean init) {
        this.init = init;
    }

    public int getShowCount() {
        return showCount;
    }

    private boolean translucentStatus;

    public void setTranslucentStatus(boolean translucentStatus) {
        this.translucentStatus = translucentStatus;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!init) {
            init = true;
            initView();
            initData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // MyLog.d(getClass(), "onCreateView");
        showCount++;
        context = getActivity();
        app = (BaseApplication) getActivity().getApplication();
        // custom = true;
        boolean custumHeader = true;
        int layout = initLayoutId();
        View v = null;
        if (layout > 0) {
            v = inflater.inflate(layout, container, false);
            toolbar = getView(v, R.id.head_toolBar);
        }
        if (toolbar == null) {
            custumHeader = false;
            v = inflater.inflate(R.layout.act_base, container, false);
            toolbar = getView(v, R.id.head_toolBar);
        }
        toolbar = getView(v, R.id.head_toolBar);
        toolbar.setTitle("");
        int statusBarHeight = getStatusBarHeight();
        if (translucentStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && statusBarHeight > 0) {
            toolbar.setPadding(0, statusBarHeight, 0, 0);
        }
        txtTitle = getView(v, R.id.head_vTitle);
        flyMain = getView(v, R.id.base_flyMain);
        if (!custumHeader && flyMain != null && layout > 0) {
            View.inflate(context, layout, flyMain);
        }
        init = false;
        return v;
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
     * 加载布局
     */
    private boolean initLoadView() {
        if (flyMain == null) {
            MyLog.e(getClass(), "Your layout must include 'FrameLayout',the ID must be 'base_flyMain'!");
            return false;
        }
        if (loadCache != null) return true;
        View loadView = getView(R.id.base_llyLoad);
        //You need to add the layout
        if (loadView == null) {
            if (flyMain.getChildCount() > 0) {
                loadView = View.inflate(context, R.layout.in_loading, null);
                flyMain.addView(loadView, 0);
            } else
                View.inflate(context, R.layout.in_loading, flyMain);
        }
        loadCache = new LoadCache();
        loadCache.llyLoad = getView(R.id.base_llyLoad);
        loadCache.proLoading = getView(R.id.base_proLoading);
        loadCache.imgMessage = getView(R.id.base_imgMessage);
        loadCache.txtMessage = getView(R.id.base_txtMessage);
        return true;
    }

    protected void showLoading() {
        showLoading(getString(R.string.loading));
    }

    protected void showLoading(String msg) {
        if (initLoadView()) {
            int count = flyMain.getChildCount();
            for (int i = 0; i < count; i++) {
                View v = flyMain.getChildAt(i);
                if (i > 0) v.setVisibility(View.GONE);
            }
            loadCache.showLoading(msg);
        }
    }

    protected void showNoData() {
        showNoData(getString(R.string.hint_nodata));
    }

    protected void showNoData(String msg) {
        showNoData(msg, R.drawable.img_hint_nodata);
    }

    //R.drawable.img_hint_net_fail
    protected void showNoData(String msg, int drawId) {
        if (initLoadView()) {
            int count = flyMain.getChildCount();
            for (int i = 0; i < count; i++) {
                View v = flyMain.getChildAt(i);
                if (i > 0) v.setVisibility(View.GONE);
            }
            loadCache.showNoData(msg, drawId);
        }
    }

    /**
     * 显示内容View
     */
    protected void showCView() {
        if (initLoadView()) {
            int count = flyMain.getChildCount();
            for (int i = 0; i < count; i++) {
                View v = flyMain.getChildAt(i);
                if (i == 0) v.setVisibility(View.GONE);
                else v.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 设置标题
     */
    public void setTitle(@StringRes int titleId) {
        setTitle(getString(titleId));
    }

    /**
     * 设置标题
     */
    public void setTitle(CharSequence title) {
        if (txtTitle != null) txtTitle.setText(title);
    }

    public TextView getTitleText() {
        if (txtTitle != null)
            return txtTitle;
        return null;
    }

    protected void hideHeader() {
        if (toolbar != null) toolbar.setVisibility(View.GONE);
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
     */
    protected View getHeader() {
        return toolbar;
    }

    protected View getHeaderRight() {
        return toolbar.findViewById(R.id.head_vRight);
    }

    protected View getMainView() {
        return flyMain;
    }

    /**
     * @see #startAct(Intent, Class)
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
        intent.putExtra(BaseActivity.LAST_ACT, this.getClass().getSimpleName());
        intent.setClass(getActivity(), cls);
        startActivity(intent);
    }

    public void startActForResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra(BaseActivity.LAST_ACT, this.getClass().getSimpleName());
        startActivityForResult(intent, requestCode);
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
     * @param v  布局
     * @param id 行布局中某个组件的id
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(View v, @IdRes int id) {
        return (T) v.findViewById(id);
    }

    /**
     * 获取 控件
     *
     * @param id 行布局中某个组件的id
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(@IdRes int id) {
        return getView(getView(), id);
    }

    /**
     * 获取并绑定点击
     *
     * @param id id
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T getViewAndClick(@IdRes int id) {
        T v = getView(id);
        v.setOnClickListener(this);
        return v;
    }

    /**
     * 获取并绑定点击
     *
     * @param id id
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T getViewAndClick(View view, @IdRes int id) {
        T v = getView(view, id);
        v.setOnClickListener(this);
        return v;
    }

    protected void setOnClickListener(@IdRes int id) {
        if (getView() != null) getView().findViewById(id).setOnClickListener(this);
    }

    protected void setOnClickListener(View v, @IdRes int id) {
        v.findViewById(id).setOnClickListener(this);
    }

    /**
     * 头-左边图标点击
     */
    public void onLeftClick() {

    }

    /**
     * 头-右边图标点击
     */
    public void onRightClick() {
    }

    @Override
    public void onDestroy() {
        if (client != null) {
            client.onDestroy();
        }
        super.onDestroy();
    }

    protected void setClient(MyHttpClient client) {
        this.client = client;
    }

    protected MyHttpClient getClient() {
        return client;
    }
}
