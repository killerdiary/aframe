package com.hy.frame.common;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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
    private View contentView;
    // private boolean custom;
    private BaseApplication app;
    protected Context context;
    private Toolbar toolbar;
    //private TextView txtTitle;
    private FrameLayout flyMain;
    private LoadCache loadCache;
    private int showCount;
    private boolean init;
    private MyHttpClient client;

    public boolean isInit() {
        return init;
    }

    public int getShowCount() {
        return showCount;
    }

    protected boolean isTranslucentStatus() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            BaseActivity act = (BaseActivity) getActivity();
            return act.isTranslucentStatus();
        }
        return false;
    }

    /**
     * 唯一布局ID
     *
     * @return
     */
    protected int initSingleLayoutId() {
        return 0;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!init) {
            init = true;
            initData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // MyLog.d(getClass(), "onCreateView");
        showCount++;
        if (contentView == null) {
            context = getActivity();
            app = (BaseApplication) getActivity().getApplication();
            View v = null;
            if (initSingleLayoutId() != 0) {
                View v1 = inflater.inflate(initSingleLayoutId(), container, false);
                flyMain = getView(v1, R.id.base_flyMain);
                if (flyMain == null) {
                    v = inflater.inflate(R.layout.act_base_fragment, container, false);
                    flyMain = getView(v, R.id.base_flyMain);
                    View.inflate(context, initSingleLayoutId(), flyMain);
                } else {
                    v = v1;
                }
            } else if (initLayoutId() != 0) {
                v = inflater.inflate(R.layout.act_base, container, false);
                flyMain = getView(v, R.id.base_flyMain);
                View.inflate(context, initLayoutId(), flyMain);
            } else {
                MyLog.INSTANCE.e(getClass(), "initLayoutId not call");
                return null;
            }
            toolbar = getView(v, R.id.head_toolBar);
            contentView = v;
            init = false;
            initToolbar();
            initView();
        }
        return contentView;
    }

    private void initToolbar() {
        if (toolbar == null) return;
        toolbar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //toolbar.setBackgroundResource(R.color.blue);
            int statusBarHeight = ((BaseActivity) getActivity()).getStatusBarHeight();
            if (isTranslucentStatus() && statusBarHeight > 0) {
                toolbar.setPadding(0, statusBarHeight, 0, 0);
                if (toolbar.getLayoutParams() != null)
                    toolbar.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.header_height) + statusBarHeight;
            }
        }
    }

    public BaseApplication getApp() {
        return app;
    }

    protected LoadCache getLoadCache() {
        return loadCache;
    }

    protected void setLoadCache(LoadCache loadCache) {
        this.loadCache = loadCache;
    }

    /**
     * 加载布局
     */
    protected boolean initLoadView() {
        if (flyMain == null) {
            MyLog.INSTANCE.e(getClass(), "Your layout must include 'FrameLayout',the ID must be 'base_flyMain'!");
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
        loadCache.setLlyLoad(getView(R.id.base_llyLoad));
        loadCache.setProLoading(getView(R.id.base_proLoading));
        loadCache.setImgMessage(getView(R.id.base_imgMessage));
        loadCache.setTxtMessage(getView(R.id.base_txtMessage));
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
        showNoData(msg, R.mipmap.img_hint_nodata);
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

    private boolean retry;//重试

    protected void allowRetry() {
        if (loadCache != null) {
            retry = true;
            loadCache.getLlyLoad().setOnClickListener(this);
        }
    }

    protected void onRetryRequest() {

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
    protected void setTitle(@StringRes int titleId) {
        setTitle(getString(titleId));

    }

    /**
     * 设置标题
     */
    protected void setTitle(CharSequence title) {
        if (toolbar.findViewById(R.id.head_vTitle) == null) {
            View v = View.inflate(context, R.layout.in_head_title, null);
            Toolbar.LayoutParams tlp = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
            if (tlp != null) {
                tlp.gravity = Gravity.CENTER;
            }
            toolbar.addView(v, tlp);
        }
        TextView txtTitle = getView(toolbar, R.id.head_vTitle);
        txtTitle.setText(title);
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

    protected void addHeaderRight(@DrawableRes int right, @IdRes int id) {
        View v = View.inflate(context, R.layout.in_head_right, null);
        ImageView img = getView(v, R.id.head_vRight);
        img.setId(id);
        TypedArray array = getActivity().getTheme().obtainStyledAttributes(new int[]{R.attr.appHeaderHeight});
        int width = array.getDimensionPixelSize(0, 0);
        array.recycle();
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(width, width);
        //params.setMargins(0, 0, width * (rightCount - 1), 0);
        params.gravity = Gravity.RIGHT;
        img.setLayoutParams(params);
        toolbar.addView(v);
        img.setOnClickListener(this);
        img.setImageResource(right);
    }

    protected void setHeaderRightTxt(@StringRes int right) {
        if (right > 0) {
            if (toolbar.findViewById(R.id.head_vRight) == null) {
                View v = View.inflate(context, R.layout.in_head_tright, toolbar);
                TextView txt = getView(v, R.id.head_vRight);
                txt.setOnClickListener(this);
                txt.setText(right);
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

    public int getHeaderHeight() {
        if (toolbar != null) {
            return toolbar.getHeight();
        }
        return 0;
    }

    protected View getHeaderTitle() {
        return toolbar.findViewById(R.id.head_vTitle);
    }

    protected View getHeaderLeft() {
        return toolbar.findViewById(R.id.head_vLeft);
    }

    protected View getHeaderRight() {
        return toolbar.findViewById(R.id.head_vRight);
    }

    protected View getMainView() {
        return flyMain;
    }

    /**
     * @see #startAct(Class, Bundle)
     */
    protected void startAct(Class<?> cls) {
        startAct(cls, null);
    }

    /**
     * @see #startAct(Intent, Class, Bundle)
     */
    protected void startAct(Class<?> cls, Bundle bundle) {
        startAct(null, cls, bundle);
    }

    /**
     * 启动Activity
     */
    protected void startAct(Intent intent, Class<?> cls, Bundle bundle) {
        if (intent == null)
            intent = new Intent();
        if (bundle != null)
            intent.putExtra(BaseActivity.BUNDLE, bundle);
        intent.putExtra(BaseActivity.LAST_ACT, this.getClass().getSimpleName());
        intent.setClass(getActivity(), cls);
        startActivity(intent);
    }

    public void startActForResult(Class<?> cls, int requestCode) {
        startActForResult(cls, null, requestCode);
    }

    public void startActForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(getActivity(), cls);
        if (bundle != null)
            intent.putExtra(BaseActivity.BUNDLE, bundle);
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
        if (HyUtil.Companion.isFastClick())
            return;
        if (v.getId() == R.id.head_vLeft)
            onLeftClick();
        else if (v.getId() == R.id.head_vRight)
            onRightClick();
        else if (v.getId() == R.id.base_llyLoad)
            onRetryRequest();
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
        return getView(contentView, id);
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
        if (contentView != null) contentView.findViewById(id).setOnClickListener(this);
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
