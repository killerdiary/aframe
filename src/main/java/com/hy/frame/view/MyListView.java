package com.hy.frame.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.util.HyUtil;

/**
 * @title 自定义ListView(下拉刷新，点击查看更多)
 * @author heyan
 * @time 2013-6-27 下午2:59:52
 */
public class MyListView extends ListView implements OnScrollListener {

    private final static int FLAG_DONE = 0;// 完成
    private final static int FLAG_PULLING = 1;// 拉...
    private final static int FLAG_REFRESHING = 2;// 正在刷新
    private final static int FLAG_RELEASE = 3;// 请释放
    // private final static int FLAG_LOADING = 4;// 加载中
    private final static int RATIO = 3;// 移动的比例
    private LayoutInflater inflater;

    private LinearLayout headView;
    private ImageView imgHeadArrow;
    private ProgressBar proHead;
    private TextView txtHeadHint, txtHeadUpdateTime;
    private int headerHeight;

    private LinearLayout footView;
    private ImageView imgFootArrow;
    private ProgressBar proFoot;
    private TextView txtFootHint, txtFootUpdateTime;
    private int footerHeight;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    private boolean isRecored;
    private int startY;
    private int state;
    private boolean isBack;
    private OnRefreshListener pullDownRefreshListener;
    private OnRefreshListener pullUpRefreshListener;
    /**
     * 是否开启下拉刷新
     */
    private boolean pullDownRefresh;
    /**
     * 是否开启上拉加载更多
     */
    private boolean pullUpRefresh;

    /**
     * 是否滚动到顶部
     */
    private boolean scrollTop;
    /**
     * 是否滚动到底部
     */
    private boolean scrollBottom;
    /**
     * 滚动的方向
     */
    private int direction;
    private final static int TO_UP = 1;// 向上
    private final static int TO_DOWN = 2;// 向下

    public MyListView(Context context) {
        super(context);
        init(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化头部和底部
     * 
     * @param context
     */
    private void init(Context context) {
        // setCacheColorHint(android.R.color.transparent);
        inflater = LayoutInflater.from(context);
        initHeader();
        initFooter();
        initAnim();
        setOnScrollListener(this);
        state = FLAG_DONE;
        pullDownRefresh = false;
        pullUpRefresh = false;
    }

    private void initHeader() {
        headView = (LinearLayout) inflater.inflate(R.layout.in_lv_header, null);
        imgHeadArrow = HyUtil.getView(headView, R.id.lv_imgHeadArrow);
        proHead = HyUtil.getView(headView, R.id.lv_proHead);
        txtHeadHint = HyUtil.getView(headView, R.id.lv_txtHeadHint);
        txtHeadUpdateTime = HyUtil.getView(headView, R.id.lv_txtHeadUpdateTime);
        // measureView(headView);
        // headerHeight = headView.getMeasuredHeight();
        headerHeight = getResources().getDimensionPixelSize(R.dimen.lv_heigth);
        headView.setPadding(0, -1 * headerHeight, 0, 0);
        headView.invalidate();
        addHeaderView(headView, null, false);
    }

    private void initFooter() {
        footView = (LinearLayout) inflater.inflate(R.layout.in_lv_footer, null);
        imgFootArrow = HyUtil.getView(footView, R.id.lv_imgFootArrow);
        proFoot = HyUtil.getView(footView, R.id.lv_proFoot);
        txtFootHint = HyUtil.getView(footView, R.id.lv_txtFootHint);
        txtFootUpdateTime = HyUtil.getView(footView, R.id.lv_txtFootUpdateTime);
        // measureView(footView);
        // footerHeight = footView.getMeasuredHeight();
        footerHeight = getResources().getDimensionPixelSize(R.dimen.lv_heigth);
        footView.setPadding(0, 0, 0, -1 * headerHeight);
        footView.invalidate();
        addFooterView(footView, null, false);
    }

    private void initAnim() {
        animation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);
        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);
    }

    public void onScroll(AbsListView arg0, int firstVisiableItem,
            int visibleItemCount, int totalItemCount) {
        if (state == FLAG_DONE) {
            if (firstVisiableItem == 0) {
                scrollTop = true;
            } else {
                scrollTop = false;
            }
            // MyLog.d("最后位置: " + getLastVisiblePosition() + " 总：" + totalItemCount);
            if (getLastVisiblePosition() == totalItemCount - 1) {
                scrollBottom = true;
            } else {
                scrollBottom = false;
            }
        }
    }

    public void onScrollStateChanged(AbsListView arg0, int arg1) {
    }

    public boolean onTouchEvent(MotionEvent event) {
        // 正在刷新
        if (pullDownRefresh || pullUpRefresh) {
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!(scrollTop || scrollBottom))
                    break;
                // MyLog.e("ACTION_DOWN");
                if (!isRecored) {
                    // 开始检测
                    isRecored = true;
                    startY = (int) event.getY();
                    direction = 0;
                    // MyLog.e("ACTION_DOWN 记录当前位置:" + startY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!(scrollTop || scrollBottom))
                    break;
                int tempY = (int) event.getY();
                // MyLog.e("ACTION_MOVE");
                if (!isRecored) {
                    isRecored = true;
                    startY = tempY;
                    direction = 0;
                    // MyLog.e("ACTION_MOVE 记录当前位置:" + startY);
                }
                // 检测开启，没有刷新，没有加载
                if (state != FLAG_REFRESHING && isRecored) {
                    // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
                    int distance = tempY - startY;
                    int abs = Math.abs(distance);
                    if (state == FLAG_DONE) {
                        if (distance != 0) {
                            if (distance > 0)
                                direction = TO_DOWN;
                            else
                                direction = TO_UP;
                            state = FLAG_PULLING;
                            changeViewByState();
                        }
                        // MyLog.e("ACTION_MOVE FLAG_DONE" + direction);
                    }
                    if (direction == TO_DOWN && !pullDownRefresh) {
                        state = FLAG_DONE;
                        break;
                    }
                    if (direction == TO_UP && !pullUpRefresh) {
                        state = FLAG_DONE;
                        break;
                    }
                    if (state == FLAG_PULLING) {
                        // MyLog.e("ACTION_MOVE FLAG_PULLING " + direction);
                        if (direction == TO_DOWN) {
                            if (distance / RATIO >= headerHeight) {
                                state = FLAG_RELEASE;
                                isBack = true;
                                changeViewByState();
                            } else if (distance <= 0) {
                                state = FLAG_DONE;
                                changeViewByState();
                            }
                            headView.setPadding(0, distance / RATIO
                                    - headerHeight, 0, 0);
                        } else if (direction == TO_UP) {
                            if (abs / RATIO >= footerHeight) {
                                state = FLAG_RELEASE;
                                isBack = true;
                                changeViewByState();
                            } else if (distance >= 0) {
                                state = FLAG_DONE;
                                changeViewByState();
                            }
                            footView.setPadding(0, 0, 0, abs / RATIO - 1
                                    * footerHeight);
                        }
                    }
                    // 可以松手去刷新了
                    if (state == FLAG_RELEASE) {
                        // MyLog.e("ACTION_MOVE 可以松手去刷新了");
                        if (direction == TO_DOWN) {
                            // setSelection(0);
                            // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                            if ((distance / RATIO < headerHeight)
                                    && distance > 0) {
                                state = FLAG_PULLING;
                                changeViewByState();
                            } else if (distance <= 0) {
                                state = FLAG_DONE;
                                changeViewByState();
                            }
                            headView.setPadding(0, distance / RATIO
                                    - headerHeight, 0, 0);
                        } else if (direction == TO_UP) {
                            if ((abs / RATIO < footerHeight) && distance < 0) {
                                state = FLAG_PULLING;
                                changeViewByState();
                            } else if (distance >= 0) {
                                state = FLAG_DONE;
                                changeViewByState();
                            }
                            footView.setPadding(0, 0, 0, abs / RATIO - 1
                                    * footerHeight);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!(scrollTop || scrollBottom))
                    break;
                if (state != FLAG_REFRESHING) {
                    if (state == FLAG_DONE) {
                    }
                    if (state == FLAG_PULLING) {
                        state = FLAG_DONE;
                        changeViewByState();
                    }
                    if (state == FLAG_RELEASE) {
                        state = FLAG_REFRESHING;
                        changeViewByState();
                        if (direction == TO_DOWN) {
                            if (pullDownRefreshListener != null) {
                                pullDownRefreshListener.onRefresh(this, false);
                            }
                        } else if (direction == TO_UP) {
                            if (pullUpRefreshListener != null) {
                                pullUpRefreshListener.onRefresh(this, false);
                            }
                        }

                    }
                }
                isRecored = false;
                isBack = false;
                break;

            }
        }
        return super.onTouchEvent(event);
    }

    private void changeViewByState() {
        if (direction == TO_DOWN) {
            switch (state) {
            case FLAG_RELEASE:
                imgHeadArrow.setVisibility(View.VISIBLE);
                proHead.setVisibility(View.GONE);
                txtHeadHint.setVisibility(View.VISIBLE);
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                imgHeadArrow.clearAnimation();
                // imgHeadArrow.setImageResource(R.drawable.refresh_arrow_get);
                imgHeadArrow.startAnimation(animation);
                txtHeadHint.setText("请释放 刷新");
                // Log.i("HyLog", "RELEASE_To_REFRESH 这是第  " + i++ + "步" + 12 +
                // "请释放 刷新");
                break;
            case FLAG_PULLING:
                proHead.setVisibility(View.GONE);
                txtHeadHint.setVisibility(View.VISIBLE);
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                imgHeadArrow.clearAnimation();
                imgHeadArrow.setVisibility(View.VISIBLE);
                if (isBack) {
                    isBack = false;
                    imgHeadArrow.clearAnimation();
                    imgHeadArrow.startAnimation(reverseAnimation);
                    txtHeadHint.setText("下拉刷新");
                } else {
                    txtHeadHint.setText("下拉刷新");
                }
                break;
            case FLAG_REFRESHING:
                headView.setPadding(0, 0, 0, 0);
                proHead.setVisibility(View.VISIBLE);
                imgHeadArrow.clearAnimation();
                imgHeadArrow.setVisibility(View.GONE);
                txtHeadHint.setText("正在加载中 ...");
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                break;
            case FLAG_DONE:
                headView.setPadding(0, -1 * headerHeight, 0, 0);
                proHead.setVisibility(View.GONE);
                imgHeadArrow.clearAnimation();
                imgHeadArrow.setImageResource(R.drawable.refresh_arrow_top);
                txtHeadHint.setText("已经加载完毕 ");
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                break;
            }

        } else if (direction == TO_UP) {
            switch (state) {
            case FLAG_RELEASE:
                imgFootArrow.setVisibility(View.VISIBLE);
                proFoot.setVisibility(View.GONE);
                txtFootHint.setVisibility(View.VISIBLE);
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                imgFootArrow.clearAnimation();
                // imgFootArrow.setImageResource(R.drawable.refresh_arrow_get);
                imgFootArrow.startAnimation(animation);
                txtFootHint.setText("请释放 加载更多");
                break;
            case FLAG_PULLING:
                proFoot.setVisibility(View.GONE);
                txtFootHint.setVisibility(View.VISIBLE);
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                imgFootArrow.clearAnimation();
                imgFootArrow.setVisibility(View.VISIBLE);
                if (isBack) {
                    isBack = false;
                    imgFootArrow.clearAnimation();
                    imgFootArrow.startAnimation(reverseAnimation);
                    txtFootHint.setText("上拉加载更多");
                } else {
                    txtFootHint.setText("上拉加载更多");
                }
                break;
            case FLAG_REFRESHING:
                // footView.setPadding(0, 0, 0, 0);
                proFoot.setVisibility(View.VISIBLE);
                imgFootArrow.clearAnimation();
                imgFootArrow.setVisibility(View.GONE);
                txtFootHint.setText("正在加载中 ...");
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                break;
            case FLAG_DONE:
                footView.setPadding(0, -1 * footerHeight, 0, 0);
                proFoot.setVisibility(View.GONE);
                imgFootArrow.clearAnimation();
                imgFootArrow.setImageResource(R.drawable.refresh_arrow_top);
                txtFootHint.setText("已经加载完毕 ");
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                break;
            }
        }

    }

    /**
     * 下拉刷新
     * 
     * @param refreshListener
     */
    public void setPullDownRefreshListener(OnRefreshListener refreshListener) {
        this.pullDownRefreshListener = refreshListener;
        pullDownRefresh = true;
    }

    /**
     * 上拉刷新
     * 
     * @param refreshListener
     */
    public void setPullUpRefreshListener(OnRefreshListener refreshListener) {
        this.pullUpRefreshListener = refreshListener;
        pullUpRefresh = true;
    }

    public interface OnRefreshListener {

        public void onRefresh(MyListView lv, boolean first);
    }

    public void onRefreshComplete() {
        state = FLAG_DONE;
        if (direction == TO_DOWN) {
            txtHeadUpdateTime.setText("上次更新: " + getNowTime());
        } else if (direction == TO_UP) {
            txtFootUpdateTime.setText("上次更新: " + getNowTime());
        }
        changeViewByState();
        // Log.i("HyLog", "onRefreshComplete() 被调用。。。");
    }

    // /**
    // * 手动调用
    // *
    // * @param first
    // */
    // public void onRefresh(boolean first) {
    // if (refreshListener != null) {
    // refreshListener.onRefresh(first);
    // // Log.i("HyLog", "onRefresh被调用，这是第  " + i++ + "步");
    // }
    // }

    // public void setShowMoreListener(android.view.View.OnClickListener listener) {
    // footView.setOnClickListener(listener);
    // }

    // public void showMoreClose(boolean state) {
    // if (state)
    // footView.setPadding(0, -1 * footerHeight, 0, 0);
    // else
    // footView.setPadding(0, 0, 0, 0);
    // }

    // private void measureView(View child) {
    // ViewGroup.LayoutParams p = child.getLayoutParams();
    // if (p == null) {
    // p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.match_parent, ViewGroup.LayoutParams.WRAP_CONTENT);
    // }
    // int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
    // int lpHeight = p.height;
    // int childHeightSpec;
    // if (lpHeight > 0) {
    // childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
    // } else {
    // childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    // }
    // child.measure(childWidthSpec, childHeightSpec);
    // }

    public void setAdapter(BaseAdapter adapter) {
        txtHeadUpdateTime.setText("更新时间：" + getNowTime());
        txtFootUpdateTime.setText("更新时间：" + getNowTime());
        super.setAdapter(adapter);
    }

    /**
     * 手动调用刷新
     */
    public void onRefresh() {
        state = FLAG_REFRESHING;
        direction = TO_DOWN;
        changeViewByState();
        if (pullDownRefreshListener != null) {
            pullDownRefreshListener.onRefresh(this, false);
        }
    }

    // public void setRefreshable(boolean b) {
    // this.isRefreshable = b;
    // }

    /**
     * 获取当前时间Date
     * 
     * @return 现在时间(Now)
     */
    public String getNowTime() {
        Date d = new Date(System.currentTimeMillis());
        // String type = "yyyy-MM-dd HH:mm:ss";
        String type = "HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(type, Locale.CHINA);
        return formatter.format(d);
    }

    public boolean isPullDownRefresh() {
        return pullDownRefresh;
    }

    public void setPullDownRefresh(boolean pullDownRefresh) {
        this.pullDownRefresh = pullDownRefresh;
    }

    public boolean isPullUpRefresh() {
        return pullUpRefresh;
    }

    public void setPullUpRefresh(boolean pullUpRefresh) {
        this.pullUpRefresh = pullUpRefresh;
    }

}