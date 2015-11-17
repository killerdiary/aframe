package com.hy.frame.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.bean.ThemeInfo;
import com.hy.frame.util.Constant;
import com.hy.frame.util.HyUtil;

public abstract class BaseFragment extends Fragment implements android.view.View.OnClickListener, IFragmentListener {
    // private boolean custom;
    private BaseApplication app;
    protected Context context;
    private TextView txtTitle, txtMessage;
    private RelativeLayout rlyHead, rlyMain;
    private ImageView imgMessage;
    private View loadView, contentView;
    private ProgressBar proLoading;
    private int showCount;
    private boolean init;
    private ThemeInfo theme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // MyLog.d(getClass(), "onCreateView");
        showCount++;
        context = getActivity();
        app = (BaseApplication) getActivity().getApplication();
        // custom = true;
        View v = inflater.inflate(R.layout.base, container, false);
        rlyHead = getView(v, R.id.rlyHead);
        txtTitle = getView(v, R.id.head_vTitle);
        rlyMain = getView(v, R.id.rlyMain);
        init = false;
        return v;
    }

    protected void initTheme(ThemeInfo theme) {
        this.theme = theme;
    }

    protected void hideHeader() {
        if (rlyHead != null)
            rlyHead.setVisibility(View.GONE);
    }

    public BaseApplication getApp() {
        return app;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public int getShowCount() {
        return showCount;
    }

    public RelativeLayout getHeadLayout() {
        return rlyHead;
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

    /**
     * 使用统一布局
     *
     * @param layout 内容布局(除标题外)
     */
    protected void customAct(int layout) {
        if (theme != null) {
            setTitlebarBackground();
            txtTitle.setTextColor(theme.getTitleColor());
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, HyUtil.floatToSpDimension(theme.getTitleSize(), context));
            txtTitle.getPaint().setFakeBoldText(theme.isTitleBold());
            rlyMain.setBackgroundColor(theme.getThemeBackground());
        }
        contentView = getActivity().getLayoutInflater().inflate(layout, null);
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
     * @see #customAct(int)
     */
    protected void customLoadAct(int layout) {
        loadView = getActivity().getLayoutInflater().inflate(R.layout.loading_act, null);
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
            imgMessage.setImageResource(R.drawable.img_hint_net_fail);
        }
    }

    protected void showNoData() {
        showNoData(getString(R.string.hint_nodata));
    }

    protected void showNoData(String msg) {
        showNoData(msg, R.drawable.img_hint_nodata);
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
    protected void setTitle(CharSequence title) {
        if (txtTitle != null)
            txtTitle.setText(title);
    }

    /**
     * 设置标题
     */
    protected void setTitle(int titleId) {
        if (txtTitle != null)
            txtTitle.setText(titleId);
    }

    protected void setHeaderLeft(int left) {
        if (left > 0) {
            if (rlyHead.findViewById(R.id.head_vLeft) == null) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.in_head_left, rlyHead);
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
                View v = getActivity().getLayoutInflater().inflate(R.layout.in_head_tleft, rlyHead);
                TextView txt = getView(v, R.id.head_vLeft);
                txt.setOnClickListener(this);
                txt.setText(left);
                if (theme != null)
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
                View v = getActivity().getLayoutInflater().inflate(R.layout.in_head_right, rlyHead);
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
                View v = getActivity().getLayoutInflater().inflate(R.layout.in_head_tright, rlyHead);
                TextView txt = getView(v, R.id.head_vRight);
                txt.setOnClickListener(this);
                txt.setText(right);
                if (theme != null)
                    txt.setTextColor(theme.getTitleColor());
            } else {
                TextView txt = getView(rlyHead, R.id.head_vRight);
                txt.setText(right);
            }
        }
    }

    /**
     * 初始化布局(用customAct方法时使用)
     *
     * @param v 布局
     */
    private void resetLayout(View v) {
        rlyMain.removeAllViews();
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlyMain.addView(v, rlp);
    }

    protected View getMainView() {
        return rlyMain;
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
     * 初始化数据
     */
    public abstract void onStartData();

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
     * @param view 布局
     * @param id   行布局中某个组件的id
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(View view, int id) {
        View v = view.findViewById(id);
        return (T) v;
    }

    /**
     * 获取并绑定点击
     *
     * @param id 组件的id
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T getViewAndClick(View view, int id) {
        View v = view.findViewById(id);
        v.setOnClickListener(this);
        return (T) v;
    }

    /**
     * 获取 控件
     *
     * @param id 行布局中某个组件的id
     */
    public <T extends View> T getView(int id) {
        return getView(getView(), id);
    }

    /**
     * 获取并绑定点击
     *
     * @param id 组件的id
     */
    public <T extends View> T getViewAndClick(int id) {
        return getViewAndClick(getView(), id);
    }

    /**
     * 获取当前布局中的控件
     *
     * @param id 行布局中某个组件的id
     */
    public <T extends View> T getCView(int id) {
        return getView(contentView, id);
    }

    /**
     * 头-左边图标点击
     */
    protected void onLeftClick() {
    }

    /**
     * 头-右边图标点击
     */
    protected void onRightClick() {
    }

    /**
     * 启动Activity
     */
    protected void startAct(Class<?> cls) {

        Intent intent = new Intent();
        intent.putExtra(Constant.LAST_ACT, this.getClass().getSimpleName());
        intent.setClass(getActivity(), cls);
        startActivity(intent);
    }

    protected View getHeaderRight() {
        return rlyHead.findViewById(R.id.head_vRight);
    }

}
