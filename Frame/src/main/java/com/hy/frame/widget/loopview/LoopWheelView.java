package com.hy.frame.widget.loopview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.hy.frame.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * LoopWheelView
 *
 * @author HeYan
 * @time 2017/7/26 15:42
 */
public class LoopWheelView extends View {

    private float scaleX = 1F;
//    private float scaleX = 1.05F;

    private static final int DEFAULT_TEXT_SIZE = 15;//sp
    private static final int DEFAULT_UNIT_TEXT_SIZE = 13;//sp
    private static final int DEFAULT_UNIT_PADDING = 4;//dp
    private static final float DEFAULT_LINE_SPACE = 2f;
    private static final int DEFAULT_VISIBIE_ITEMS = 9;

    public enum ACTION {
        CLICK, FLING, DAGGLE
    }

    private Context context;

    Handler handler;
    private GestureDetector flingGestureDetector;
    OnItemSelectedListener onItemSelectedListener;

    // Timer mTimer;
    ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mFuture;

    private Paint paintOuterText;
    private Paint paintCenterText;
    private Paint paintUnitText;
    private Paint paintIndicator;

    List<String> items;

    int textSize;
    int maxTextHeight;
    int maxUnitTextHeight;

    int outerTextColor;

    int centerTextColor;
    int dividerColor;
    int dividerHeight;

    int unitTextSize;
    int unitTextColor;
    int unitPadding;

    int gradientColor;
    Paint gradientTopPaint;
    Paint gradientBottomPaint;

    float lineSpacingMultiplier;
    boolean isLoop;

    int firstLineY;
    int secondLineY;

    int totalScrollY;
    int initPosition;
    private int selectedItem;
    int preCurrentIndex;
    int change;

    int itemsVisibleCount;

    String[] drawingStrings;

    int measuredHeight;
    int measuredWidth;

    int halfCircumference;
    int radius;

    private int mOffset = 0;
    private float previousY;
    long startTime = 0;

    private Rect tempRect = new Rect();
    private Rect dividerRect = new Rect();

    private int paddingLeft, paddingRight;
    private String unitStr;

    /**
     * set text line space, must more than 1
     *
     * @param lineSpacingMultiplier
     */
    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        if (lineSpacingMultiplier > 1.0f) {
            this.lineSpacingMultiplier = lineSpacingMultiplier;
        }
    }

    /**
     * set outer text color
     *
     * @param centerTextColor
     */
    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
        paintCenterText.setColor(centerTextColor);
    }

    /**
     * set center text color
     *
     * @param outerTextColor
     */
    public void setOuterTextColor(int outerTextColor) {
        this.outerTextColor = outerTextColor;
        paintOuterText.setColor(outerTextColor);
    }

    /**
     * set divider color
     *
     * @param dividerColor
     */
    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        paintIndicator.setColor(dividerColor);
    }

    public LoopWheelView(Context context) {
        super(context);
        initLoopView(context, null);
    }

    public LoopWheelView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        initLoopView(context, attributeset);
    }

    public LoopWheelView(Context context, AttributeSet attributeset, int defStyleAttr) {
        super(context, attributeset, defStyleAttr);
        initLoopView(context, attributeset);
    }

    private void initLoopView(Context context, AttributeSet attributeset) {
        this.context = context;
        handler = new MessageHandler(this);
        flingGestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
        flingGestureDetector.setIsLongpressEnabled(false);
        TypedArray a = context.obtainStyledAttributes(attributeset, R.styleable.LoopWheelView);
        textSize = a.getDimensionPixelSize(R.styleable.LoopWheelView_lwvTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE, context.getResources().getDisplayMetrics()));
        lineSpacingMultiplier = a.getFloat(R.styleable.LoopWheelView_lwvLineSpace, DEFAULT_LINE_SPACE);
        centerTextColor = a.getColor(R.styleable.LoopWheelView_lwvCenterTextColor, Color.GRAY);
        outerTextColor = a.getColor(R.styleable.LoopWheelView_lwvOuterTextColor, centerTextColor);
        dividerColor = a.getColor(R.styleable.LoopWheelView_lwvDividerColor, Color.GRAY);
        dividerHeight = a.getDimensionPixelSize(R.styleable.LoopWheelView_lwvDividerHeight, 1);
        gradientColor = a.getColor(R.styleable.LoopWheelView_lwvGradientColor, 0);
        unitTextSize = a.getDimensionPixelSize(R.styleable.LoopWheelView_lwvUnitTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_UNIT_TEXT_SIZE, context.getResources().getDisplayMetrics()));
        unitTextColor = a.getColor(R.styleable.LoopWheelView_lwvUnitTextColor, centerTextColor);
        unitPadding = a.getDimensionPixelSize(R.styleable.LoopWheelView_lwvUnitPadding, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_UNIT_PADDING, context.getResources().getDisplayMetrics()));
        unitStr = a.getString(R.styleable.LoopWheelView_lwvUnitText);
        itemsVisibleCount = a.getInteger(R.styleable.LoopWheelView_lwvItemsVisibleCount, DEFAULT_VISIBIE_ITEMS);
        if (itemsVisibleCount % 2 == 0) {
            itemsVisibleCount = DEFAULT_VISIBIE_ITEMS;
        }
        isLoop = a.getBoolean(R.styleable.LoopWheelView_lwvIsLoop, true);
        a.recycle();

        drawingStrings = new String[itemsVisibleCount];

        totalScrollY = 0;
        initPosition = -1;

        initPaints();
        List<String> datas = new ArrayList<>();
        for (int i = 1; i < 60; i++) {
            datas.add(String.format("%02d", i));
        }
        setItems(datas);
    }

    /**
     * visible item count, must be odd number
     *
     * @param visibleNumber
     */
    public void setItemsVisibleCount(int visibleNumber) {
        if (visibleNumber % 2 == 0) {
            return;
        }
        if (visibleNumber != itemsVisibleCount) {
            itemsVisibleCount = visibleNumber;
            drawingStrings = new String[itemsVisibleCount];
        }
    }

    private void initPaints() {
        paintOuterText = new Paint();
        paintOuterText.setColor(outerTextColor);
        paintOuterText.setAntiAlias(true);
        paintOuterText.setTypeface(Typeface.MONOSPACE);
        paintOuterText.setTextSize(textSize);

        paintCenterText = new Paint();
        paintCenterText.setColor(centerTextColor);
        paintCenterText.setAntiAlias(true);
        paintCenterText.setTextScaleX(scaleX);
        paintCenterText.setTypeface(Typeface.MONOSPACE);
        paintCenterText.setTextSize(textSize);

        paintIndicator = new Paint();
        paintIndicator.setColor(dividerColor);
        paintIndicator.setAntiAlias(true);
        if (dividerHeight > 0)
            paintIndicator.setStrokeWidth(dividerHeight);

        paintUnitText = new Paint();
        paintUnitText.setColor(unitTextColor);
        paintUnitText.setAntiAlias(true);
        paintUnitText.setTypeface(Typeface.MONOSPACE);
        paintUnitText.setTextSize(unitTextSize);
    }

    private void remeasure() {
        if (items == null || items.isEmpty()) {
            return;
        }
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
        if (measuredWidth == 0 || measuredHeight == 0) {
            return;
        }
        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        measuredWidth = measuredWidth - paddingRight;
        maxTextHeight = getTextRect("\u661F\u671F", paintCenterText).height();
        maxUnitTextHeight = getTextRect("\u661F\u671F", paintUnitText).height();
        halfCircumference = (int) (measuredHeight * Math.PI / 2);
        maxTextHeight = (int) (halfCircumference / (lineSpacingMultiplier * (itemsVisibleCount - 1)));
        radius = measuredHeight / 2;
        firstLineY = (int) ((measuredHeight - lineSpacingMultiplier * maxTextHeight) / 2.0F);
        secondLineY = (int) ((measuredHeight + lineSpacingMultiplier * maxTextHeight) / 2.0F);
        dividerRect.left = paddingLeft;
        dividerRect.top = firstLineY;
        dividerRect.right = measuredWidth;
        dividerRect.bottom = secondLineY;
        if (initPosition == -1) {
            if (isLoop) {
                initPosition = (items.size() + 1) / 2;
            } else {
                initPosition = 0;
            }
        }
        preCurrentIndex = initPosition;
    }

    void smoothScroll(ACTION action) {
        cancelFuture();
        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
            float itemHeight = lineSpacingMultiplier * maxTextHeight;
            mOffset = (int) ((totalScrollY % itemHeight + itemHeight) % itemHeight);
            if ((float) mOffset > itemHeight / 2.0F) {
                mOffset = (int) (itemHeight - (float) mOffset);
            } else {
                mOffset = -mOffset;
            }
        }
        mFuture =
                mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, mOffset), 0, 10, TimeUnit.MILLISECONDS);
    }

    protected final void scrollBy(float velocityY) {
        cancelFuture();
        // change this number, can change fling speed
        int velocityFling = 10;
        mFuture = mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0, velocityFling,
                TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }

    /**
     * set not loop
     */
    public void setNotLoop() {
        isLoop = false;
    }

    /**
     * set text size in dp
     *
     * @param size
     */
    public final void setTextSize(float size) {
        if (size > 0.0F) {
            textSize = (int) (context.getResources().getDisplayMetrics().density * size);
            paintOuterText.setTextSize(textSize);
            paintCenterText.setTextSize(textSize);
        }
    }

    public final void setInitPosition(int initPosition) {
        if (initPosition < 0) {
            this.initPosition = 0;
        } else {
            if (items != null && items.size() > initPosition) {
                this.initPosition = initPosition;
            }
        }
    }

    public final void setListener(OnItemSelectedListener OnItemSelectedListener) {
        onItemSelectedListener = OnItemSelectedListener;
    }

    public final void setItems(List<String> items) {
        this.items = items;
        remeasure();
        invalidate();
    }

    public final int getSelectedItem() {
        return selectedItem;
    }

    public final String getSelectedItemStr() {
        int position = getSelectedItem();
        if (items != null && position < items.size())
            return items.get(getSelectedItem());
        return null;
    }

    //
    // protected final void scrollBy(float velocityY) {
    // Timer timer = new Timer();
    // mTimer = timer;
    // timer.schedule(new InertiaTimerTask(this, velocityY, timer), 0L, 20L);
    // }

    protected final void onItemSelected() {
        if (onItemSelectedListener != null) {
            postDelayed(new OnItemSelectedRunnable(this), 200L);
        }
    }

    /**
     * link https://github.com/weidongjian/androidWheelView/issues/10
     *
     * @param scaleX
     */
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    /**
     * set current item position
     *
     * @param position
     */
    public void setCurrentPosition(int position) {
        if (items == null || items.isEmpty()) {
            return;
        }
        int size = items.size();
        if (position >= 0 && position < size && position != selectedItem) {
            initPosition = position;
            totalScrollY = 0;
            mOffset = 0;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (items == null || items.isEmpty()) {
            return;
        }

        change = (int) (totalScrollY / (lineSpacingMultiplier * maxTextHeight));
        preCurrentIndex = initPosition + change % items.size();

        if (!isLoop) {
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0;
            }
            if (preCurrentIndex > items.size() - 1) {
                preCurrentIndex = items.size() - 1;
            }
        } else {
            if (preCurrentIndex < 0) {
                preCurrentIndex = items.size() + preCurrentIndex;
            }
            if (preCurrentIndex > items.size() - 1) {
                preCurrentIndex = preCurrentIndex - items.size();
            }
        }

        int j2 = (int) (totalScrollY % (lineSpacingMultiplier * maxTextHeight));
        // put value to drawingString
        int k1 = 0;
        while (k1 < itemsVisibleCount) {
            int l1 = preCurrentIndex - (itemsVisibleCount / 2 - k1);
            if (isLoop) {
                while (l1 < 0) {
                    l1 = l1 + items.size();
                }
                while (l1 > items.size() - 1) {
                    l1 = l1 - items.size();
                }
                drawingStrings[k1] = items.get(l1);
            } else if (l1 < 0) {
                drawingStrings[k1] = "";
            } else if (l1 > items.size() - 1) {
                drawingStrings[k1] = "";
            } else {
                drawingStrings[k1] = items.get(l1);
            }
            k1++;
        }
        if (dividerHeight > 0) {
            canvas.drawLine(paddingLeft, firstLineY, measuredWidth, firstLineY, paintIndicator);
            canvas.drawLine(paddingLeft, secondLineY, measuredWidth, secondLineY, paintIndicator);
        } else {
            canvas.drawRect(dividerRect, paintIndicator);
        }
        if (unitStr != null && unitStr.length() > 0)
            canvas.drawText(unitStr, measuredWidth / 2 + getTextRect(items.get(0), paintCenterText).width() / 2 + unitPadding, secondLineY - (secondLineY - firstLineY - maxUnitTextHeight) / 2, paintUnitText);
        //canvas.drawRect(new Rect(measuredWidth / 2, firstLineY, measuredWidth, secondLineY), paintIndicator);
        //canvas.drawRect(new Rect(measuredWidth / 2 + getTextRect(items.get(0), paintCenterText).width() / 2, firstLineY, measuredWidth, secondLineY), paintIndicator);

        int i = 0;
        while (i < itemsVisibleCount) {
            canvas.save();
            float itemHeight = maxTextHeight * lineSpacingMultiplier;
            double radian = ((itemHeight * i - j2) * Math.PI) / halfCircumference;
            if (radian >= Math.PI || radian <= 0) {
                canvas.restore();
            } else {
                int translateY = (int) (radius - Math.cos(radian) * radius - (Math.sin(radian) * maxTextHeight) / 2D);
                canvas.translate(0.0F, translateY);
                canvas.scale(1.0F, (float) Math.sin(radian));
                if (translateY <= firstLineY && maxTextHeight + translateY >= firstLineY) {
                    // first divider
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, firstLineY - translateY);
                    canvas.drawText(drawingStrings[i], getTextX(drawingStrings[i], paintOuterText),
                            maxTextHeight, paintOuterText);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, firstLineY - translateY, measuredWidth, (int) (itemHeight));
                    canvas.drawText(drawingStrings[i], getTextX(drawingStrings[i], paintCenterText),
                            maxTextHeight, paintCenterText);
                    canvas.restore();
                } else if (translateY <= secondLineY && maxTextHeight + translateY >= secondLineY) {
                    // second divider
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, secondLineY - translateY);
                    canvas.drawText(drawingStrings[i], getTextX(drawingStrings[i], paintCenterText),
                            maxTextHeight, paintCenterText);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, secondLineY - translateY, measuredWidth, (int) (itemHeight));
                    canvas.drawText(drawingStrings[i], getTextX(drawingStrings[i], paintOuterText),
                            maxTextHeight, paintOuterText);
                    canvas.restore();
                } else if (translateY >= firstLineY && maxTextHeight + translateY <= secondLineY) {
                    // center item
                    canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                    canvas.drawText(drawingStrings[i], getTextX(drawingStrings[i], paintCenterText),
                            maxTextHeight, paintCenterText);
                    selectedItem = items.indexOf(drawingStrings[i]);
                } else {
                    // other item
                    canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                    canvas.drawText(drawingStrings[i], getTextX(drawingStrings[i], paintOuterText),
                            maxTextHeight, paintOuterText);
                }
                canvas.restore();
            }
            i++;
        }
        if (gradientColor != 0) {
            if (gradientTopPaint == null) {
                gradientTopPaint = new Paint();
                gradientTopPaint.setAntiAlias(true);
                Shader mShader = new LinearGradient(0, 0, 0, firstLineY, ColorUtils.setAlphaComponent(gradientColor, 200), ColorUtils.setAlphaComponent(gradientColor, 100), Shader.TileMode.REPEAT);
                gradientTopPaint.setShader(mShader);
            }
            canvas.drawRect(0, 0, measuredWidth, firstLineY, gradientTopPaint);
            if (gradientBottomPaint == null) {
                gradientBottomPaint = new Paint();
                gradientBottomPaint.setAntiAlias(true);
                Shader mShader = new LinearGradient(0, secondLineY, 0, measuredHeight, ColorUtils.setAlphaComponent(gradientColor, 100), ColorUtils.setAlphaComponent(gradientColor, 200), Shader.TileMode.REPEAT);
                gradientBottomPaint.setShader(mShader);
            }
            canvas.drawRect(0, secondLineY, measuredWidth, measuredHeight, gradientBottomPaint);
        }
    }

    private Rect getTextRect(String str, Paint paint) {
        paint.getTextBounds(str, 0, str.length(), tempRect);
        return tempRect;
    }

    // text start drawing position
    private int getTextX(String str, Paint paint) {
        int textWidth = getTextRect(str, paint).width();
        textWidth *= scaleX;
        return (measuredWidth - paddingLeft - textWidth) / 2 + paddingLeft;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        remeasure();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = flingGestureDetector.onTouchEvent(event);
        float itemHeight = lineSpacingMultiplier * maxTextHeight;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                cancelFuture();
                previousY = event.getRawY();
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float dy = previousY - event.getRawY();
                previousY = event.getRawY();

                totalScrollY = (int) (totalScrollY + dy);

                if (!isLoop) {
                    float top = -initPosition * itemHeight;
                    float bottom = (items.size() - 1 - initPosition) * itemHeight;

                    if (totalScrollY < top) {
                        totalScrollY = (int) top;
                    } else if (totalScrollY > bottom) {
                        totalScrollY = (int) bottom;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                if (!eventConsumed) {
                    float y = event.getY();
                    double l = Math.acos((radius - y) / radius) * radius;
                    int circlePosition = (int) ((l + itemHeight / 2) / itemHeight);

                    float extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight;
                    mOffset = (int) ((circlePosition - itemsVisibleCount / 2) * itemHeight - extraOffset);

                    if ((System.currentTimeMillis() - startTime) > 120) {
                        smoothScroll(ACTION.DAGGLE);
                    } else {
                        smoothScroll(ACTION.CLICK);
                    }
                }
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }

        invalidate();
        return true;
    }
}
