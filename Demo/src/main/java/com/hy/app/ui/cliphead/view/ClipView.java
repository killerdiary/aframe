package com.hy.app.ui.cliphead.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import com.hy.app.ui.cliphead.utils.UIUtils;


/**
 * 裁剪图片的View
 */
public class ClipView extends View {

    public final int VIEW_WIDTH = UIUtils.INSTANCE.getScreenWidth() / 2;
    public final int VIEW_HEIGHT = VIEW_WIDTH;


    private int width;
    private int height;

    public ClipView(Context context) {
        super(context);
    }

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 返回该控件在Activity中左上角的X坐标
     *
     * @return
     */
    public int getTopX() {
        return (width - VIEW_WIDTH) / 2;
    }

    /**
     * 返回该控件在Activity中左上角的Y坐标
     *
     * @return
     */
    public int getTopY() {
        return (height - VIEW_HEIGHT) / 2;
    }

    private int border;
    private int viewHeight;
    private int viewWidth;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = this.getWidth();
        height = this.getHeight();
        border = UIUtils.INSTANCE.dip2px(1);
        viewHeight = VIEW_HEIGHT + (border * 2);
        viewWidth = VIEW_WIDTH + (border * 2);

        Paint paint = new Paint();
        paint.setColor(0xaa000000);
        paint.setStyle(Style.FILL);

        // top
        canvas.drawRect(0, 0, width, (height - viewHeight) / 2, paint);
        // left
        canvas.drawRect(0, (height - viewHeight) / 2,
                (width - viewWidth) / 2, (height + viewHeight) / 2, paint);

        // right
        canvas.drawRect((width + viewWidth) / 2, (height - viewHeight) / 2, width,
                (height + viewHeight) / 2, paint);

        // bottom
        canvas.drawRect(0, (height + viewHeight) / 2, width, height, paint);

        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(border);
        paint.setColor(Color.WHITE);
        canvas.drawRect((width - viewWidth) / 2, (height - viewHeight) / 2, viewWidth + (width - viewWidth) / 2, viewHeight + (height - viewHeight) / 2, paint);
    }

}
