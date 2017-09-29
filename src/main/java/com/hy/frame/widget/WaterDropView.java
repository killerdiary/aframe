package com.hy.frame.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.hy.frame.R;

public class WaterDropView extends View {

    private Circle topCircle;
    private Circle bottomCircle;

    private Paint mPaint;
    private Path mPath;
    private float mMaxCircleRadius;//圆半径最大值
    private float mMinCircleRaidus;//圆半径最小值
    private final static int BACK_ANIM_DURATION = 180;
    private float strokeWidth = 0;//边线宽度

    public WaterDropView(Context context) {
        super(context);
        init(context, null);
    }

    public WaterDropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WaterDropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaterDropView, 0, 0);
        topCircle.setX(a.getDimensionPixelSize(R.styleable.WaterDropView_wdvTopCircleX, 0));
        topCircle.setY(a.getDimensionPixelSize(R.styleable.WaterDropView_wdvTopCircleY, 0));
        bottomCircle.setX(a.getDimensionPixelSize(R.styleable.WaterDropView_wdvBottomCircleX, 0));
        bottomCircle.setY(a.getDimensionPixelSize(R.styleable.WaterDropView_wdvBottomCircleY, 0));
        mPaint.setColor(a.getColor(R.styleable.WaterDropView_wdvWaterDropColor, Color.GRAY));
        mMaxCircleRadius = a.getDimensionPixelSize(R.styleable.WaterDropView_wdvMaxCircleRadius, 0);
        float percent = a.getFloat(R.styleable.WaterDropView_wdvPercent, 0);
        strokeWidth = a.getDimensionPixelSize(R.styleable.WaterDropView_wdvStrokeWidth, 0);
        topCircle.setRadius(mMaxCircleRadius);
        bottomCircle.setRadius(mMaxCircleRadius);
        topCircle.setX(strokeWidth + mMaxCircleRadius);
        topCircle.setY(strokeWidth + mMaxCircleRadius);
        bottomCircle.setX(strokeWidth + mMaxCircleRadius);
        bottomCircle.setY(strokeWidth + mMaxCircleRadius);
        mMinCircleRaidus = a.getDimensionPixelSize(R.styleable.WaterDropView_wdvMinCircleRadius, 0);
        if (mMinCircleRaidus > mMaxCircleRadius) {
            throw new IllegalStateException("Circle's MinRaidus should be equal or lesser than the MaxRadius");
        }
        a.recycle();
        if (percent > 0 && percent < 1)
            updateComleteState(percent);
    }

    private void init(Context context, AttributeSet attrs) {
        topCircle = new Circle();
        bottomCircle = new Circle();
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        parseAttrs(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //宽度：上圆和下圆的最大直径
        int width = (int) ((mMaxCircleRadius + strokeWidth) * 2);
        //高度：上圆半径 + 圆心距 + 下圆半径
        int height = (int) Math.ceil(bottomCircle.getY() + bottomCircle.getRadius() + strokeWidth * 2);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        makeBezierPath();
//        mPaint.setColor(Color.RED);
//        mPaint.setAlpha(200);
        canvas.drawPath(mPath, mPaint);
//        mPaint.setColor(Color.GRAY);
//        mPaint.setAlpha(50);
        canvas.drawCircle(topCircle.getX(), topCircle.getY(), topCircle.getRadius(), mPaint);
        canvas.drawCircle(bottomCircle.getX(), bottomCircle.getY(), bottomCircle.getRadius(), mPaint);
//        canvas.drawBitmap(arrowBitmap, topCircle.getX() - topCircle.getRadius(), topCircle.getY() - topCircle.getRadius(), mPaint);

        super.onDraw(canvas);
    }


    private void makeBezierPath() {
        mPath.reset();
        //获取两圆的两个切线形成的四个切点
        double angle = getAngle();
        float topX1 = (float) (topCircle.getX() - topCircle.getRadius() * Math.cos(angle));
        float topY1 = (float) (topCircle.getY() + topCircle.getRadius() * Math.sin(angle));

        float topX2 = (float) (topCircle.getX() + topCircle.getRadius() * Math.cos(angle));
        float topY2 = topY1;

        float bottomX1 = (float) (bottomCircle.getX() - bottomCircle.getRadius() * Math.cos(angle));
        float bottomY1 = (float) (bottomCircle.getY() + bottomCircle.getRadius() * Math.sin(angle));

        float bottomX2 = (float) (bottomCircle.getX() + bottomCircle.getRadius() * Math.cos(angle));
        float bottomY2 = bottomY1;

        mPath.moveTo(topCircle.getX(), topCircle.getY());

        mPath.lineTo(topX1, topY1);

        mPath.quadTo((bottomCircle.getX() - bottomCircle.getRadius()),
                (bottomCircle.getY() + topCircle.getY()) / 2,

                bottomX1,
                bottomY1);
        mPath.lineTo(bottomX2, bottomY2);

        mPath.quadTo((bottomCircle.getX() + bottomCircle.getRadius()),
                (bottomCircle.getY() + topY2) / 2,
                topX2,
                topY2);

        mPath.close();
    }

    /**
     * 获得两个圆切线与圆心连线的夹角
     *
     * @return
     */
    private double getAngle() {
        if (bottomCircle.getRadius() > topCircle.getRadius()) {
            throw new IllegalStateException("bottomCircle's radius must be less than the topCircle's");
        }
        return Math.asin((topCircle.getRadius() - bottomCircle.getRadius()) / (bottomCircle.getY() - topCircle.getY()));
    }

    /**
     * 创建回弹动画
     * 上圆半径减速恢复至最大半径
     * 下圆半径减速恢复至最大半径
     * 圆心距减速从最大值减到0(下圆Y从当前位置移动到上圆Y)。
     *
     * @return
     */
    public Animator createAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0).setDuration(BACK_ANIM_DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                updateComleteState((float) valueAnimator.getAnimatedValue());
            }
        });
        return valueAnimator;
    }

    /**
     * 完成的百分比
     *
     * @param percent between[0,1]
     */
    public void updateComleteState(float percent) {
        if (percent < 0 || percent > 1) {
            throw new IllegalStateException("completion percent should between 0 and 1!");
        }
        float top_r = (float) (mMaxCircleRadius - 0.25 * percent * mMaxCircleRadius);
        float bottom_r = (mMinCircleRaidus - mMaxCircleRadius) * percent + mMaxCircleRadius;
        float bottomCricleOffset = 2 * percent * mMaxCircleRadius;
        topCircle.setRadius(top_r);
        bottomCircle.setRadius(bottom_r);
        bottomCircle.setY(topCircle.getY() + bottomCricleOffset);
        requestLayout();
        postInvalidate();
    }

    public Circle getTopCircle() {
        return topCircle;
    }

    public Circle getBottomCircle() {
        return bottomCircle;
    }

    public void setIndicatorColor(int color) {
        mPaint.setColor(color);
    }

    public int getIndicatorColor() {
        return mPaint.getColor();
    }

    class Circle {
        private float x;//圆x坐标
        private float y;//圆y坐标
        private float radius;//圆半径
        private int color;//圆的颜色

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }

}
