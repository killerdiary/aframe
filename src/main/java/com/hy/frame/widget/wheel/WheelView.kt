/*
 *  Android Wheel Control.
 *  https://code.google.com/p/android-wheel/
 *  
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.hy.frame.widget.wheel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation
import android.os.Handler
import android.os.Message
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.widget.Scroller
import com.hy.frame.R
import com.hy.frame.adapter.IWheelAdapter
import java.util.*

/**
 * Numeric wheel view.

 * @author Yuri Kanivets
 */
class WheelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    // Wheel Values
    /**
     * Gets wheel adapter

     * @return the adapter
     */
    /**
     * Sets wheel adapter

     * @param adapter
     * *            the new wheel adapter
     */
    var adapter: IWheelAdapter? = null
        set(adapter) {
            field = adapter
            invalidateLayouts()
            invalidate()
        }
    private var currentItem = 0

    // Widths
    private var itemsWidth = 0
    private var labelWidth = 0

    // Count of visible items
    /**
     * Gets count of visible items

     * @return the count of visible items
     */
    /**
     * Sets count of visible items

     * @param count
     * *            the new count
     */
    var visibleItems = DEF_VISIBLE_ITEMS
        set(count) {
            field = count
            invalidate()
        }

    // Item height
    private var itemHeight = 0

    // Text paints
    private var itemsPaint: TextPaint? = null
    private var valuePaint: TextPaint? = null

    // Layouts
    private var itemsLayout: StaticLayout? = null
    private var labelLayout: StaticLayout? = null
    private var valueLayout: StaticLayout? = null

    // Label & background
    /**
     * Gets label

     * @return the label
     */
    /**
     * Sets label

     * @param newLabel
     * *            the label to set
     */
    var label: String? = null
        set(newLabel) {
            if (this.label == null || this.label != newLabel) {
                field = newLabel
                labelLayout = null
                invalidate()
            }
        }
    private var centerDrawable: Drawable? = null

    // Shadows drawables
    private var topShadow: GradientDrawable? = null
    private var bottomShadow: GradientDrawable? = null

    // Scrolling
    private var isScrollingPerformed: Boolean = false
    private var scrollingOffset: Int = 0

    // Scrolling animation
    private var gestureDetector: GestureDetector? = null
    private var scroller: Scroller? = null
    private var lastScrollY: Int = 0

    // Cyclic
    internal var isCyclic = false

    // Listeners
    private val changingListeners = LinkedList<OnWheelChangedListener>()
    private val scrollingListeners = LinkedList<OnWheelScrollListener>()

    init {
        initData(context)
    }

    /**
     * Initializes class data

     * @param context
     * *            the context
     */
    private fun initData(context: Context) {
        gestureDetector = GestureDetector(context, gestureListener)
        gestureDetector!!.setIsLongpressEnabled(false)

        scroller = Scroller(context)
    }

    /**
     * Set the the specified scrolling interpolator

     * @param interpolator
     * *            the interpolator
     */
    fun setInterpolator(interpolator: Interpolator) {
        scroller!!.forceFinished(true)
        scroller = Scroller(context, interpolator)
    }

    /**
     * Adds wheel changing listener

     * @param listener
     * *            the listener
     */
    fun addChangingListener(listener: OnWheelChangedListener) {
        changingListeners.add(listener)
    }

    /**
     * Removes wheel changing listener

     * @param listener
     * *            the listener
     */
    fun removeChangingListener(listener: OnWheelChangedListener) {
        changingListeners.remove(listener)
    }

    /**
     * Notifies changing listeners

     * @param oldValue
     * *            the old wheel value
     * *
     * @param newValue
     * *            the new wheel value
     */
    protected fun notifyChangingListeners(oldValue: Int, newValue: Int) {
        for (listener in changingListeners) {
            listener.onChanged(this, oldValue, newValue)
        }
    }

    /**
     * Adds wheel scrolling listener

     * @param listener
     * *            the listener
     */
    fun addScrollingListener(listener: OnWheelScrollListener) {
        scrollingListeners.add(listener)
    }

    /**
     * Removes wheel scrolling listener

     * @param listener
     * *            the listener
     */
    fun removeScrollingListener(listener: OnWheelScrollListener) {
        scrollingListeners.remove(listener)
    }

    /**
     * Notifies listeners about starting scrolling
     */
    protected fun notifyScrollingListenersAboutStart() {
        for (listener in scrollingListeners) {
            listener.onScrollingStarted(this)
        }
    }

    /**
     * Notifies listeners about ending scrolling
     */
    protected fun notifyScrollingListenersAboutEnd() {
        for (listener in scrollingListeners) {
            listener.onScrollingFinished(this)
        }
    }

    /**
     * Gets current value

     * @return the current value
     */
    fun getCurrentItem(): Int {
        return currentItem
    }

    /**
     * Sets the current item. Does nothing when index is wrong.

     * @param index
     * *            the item index
     * *
     * @param animated
     * *            the animation flag
     */
    @JvmOverloads fun setCurrentItem(index: Int, animated: Boolean = false) {
        var index = index
        if (this.adapter == null || this.adapter!!.itemsCount == 0) {
            return  // throw?
        }
        if (index < 0 || index >= this.adapter!!.itemsCount) {
            if (isCyclic) {
                while (index < 0) {
                    index += this.adapter!!.itemsCount
                }
                index %= this.adapter!!.itemsCount
            } else {
                return  // throw?
            }
        }
        if (index != currentItem) {
            if (animated) {
                scroll(index - currentItem, SCROLLING_DURATION)
            } else {
                invalidateLayouts()

                val old = currentItem
                currentItem = index

                notifyChangingListeners(old, currentItem)

                invalidate()
            }
        }
    }

    /**
     * Tests if wheel is cyclic. That means before the 1st item there is shown
     * the last one

     * @return true if wheel is cyclic
     */
    fun isCyclic(): Boolean {
        return isCyclic
    }

    /**
     * Set wheel cyclic flag

     * @param isCyclic
     * *            the flag to set
     */
    fun setCyclic(isCyclic: Boolean) {
        this.isCyclic = isCyclic

        invalidate()
        invalidateLayouts()
    }

    /**
     * Invalidates layouts
     */
    private fun invalidateLayouts() {
        itemsLayout = null
        valueLayout = null
        scrollingOffset = 0
    }

    /**
     * Initializes resources
     */
    private fun initResourcesIfNecessary() {
        if (itemsPaint == null) {
            itemsPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.FAKE_BOLD_TEXT_FLAG)
            // itemsPaint.density = getResources().getDisplayMetrics().density;
            itemsPaint!!.textSize = TEXT_SIZE.toFloat()
        }

        if (valuePaint == null) {
            valuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.FAKE_BOLD_TEXT_FLAG or Paint.DITHER_FLAG)
            // valuePaint.density = getResources().getDisplayMetrics().density;
            valuePaint!!.textSize = TEXT_SIZE.toFloat()
            valuePaint!!.setShadowLayer(0.1f, 0f, 0.1f, 0xFFC0C0C0.toInt())
        }

        if (centerDrawable == null) {
            centerDrawable = context.resources.getDrawable(
                    R.drawable.wheel_val)
        }

        if (topShadow == null) {
            topShadow = GradientDrawable(Orientation.TOP_BOTTOM,
                    SHADOWS_COLORS)
        }

        if (bottomShadow == null) {
            bottomShadow = GradientDrawable(Orientation.BOTTOM_TOP,
                    SHADOWS_COLORS)
        }

        setBackgroundResource(R.drawable.wheel_bg)
    }

    /**
     * Calculates desired height for layout

     * @param layout
     * *            the source layout
     * *
     * @return the desired layout height
     */
    private fun getDesiredHeight(layout: Layout?): Int {
        if (layout == null) {
            return 0
        }

        var desired = getItemHeight() * this.visibleItems - ITEM_OFFSET * 2
        -ADDITIONAL_ITEM_HEIGHT

        // Check against our minimum height
        desired = Math.max(desired, suggestedMinimumHeight)

        return desired
    }

    /**
     * Returns text item by index

     * @param index
     * *            the item index
     * *
     * @return the item or null
     */
    private fun getTextItem(index: Int): String? {
        var index = index
        if (this.adapter == null || this.adapter!!.itemsCount == 0) {
            return null
        }
        val count = this.adapter!!.itemsCount
        if ((index < 0 || index >= count) && !isCyclic) {
            return null
        } else {
            while (index < 0) {
                index = count + index
            }
        }

        index %= count
        return this.adapter!!.getItem(index)
    }

    /**
     * Builds text depending on current value

     * @param useCurrentValue
     * *
     * @return the text
     */
    private fun buildText(useCurrentValue: Boolean): String {
        val itemsText = StringBuilder()
        val addItems = this.visibleItems / 2 + 1

        for (i in currentItem - addItems..currentItem + addItems) {
            if (useCurrentValue || i != currentItem) {
                val text = getTextItem(i)
                if (text != null) {
                    itemsText.append(text)
                }
            }
            if (i < currentItem + addItems) {
                itemsText.append("\n")
            }
        }

        return itemsText.toString()
    }

    /**
     * Returns the max item length that can be present

     * @return the max length
     */
    private val maxTextLength: Int
        get() {
            val adapter = adapter ?: return 0

            val adapterLength = adapter.maximumLength
            if (adapterLength > 0) {
                return adapterLength
            }

            var maxText: String? = null
            val addItems = this.visibleItems / 2
            for (i in Math.max(currentItem - addItems, 0)..Math.min(
                    currentItem + this.visibleItems, adapter.itemsCount) - 1) {
                val text = adapter.getItem(i)
                if (text != null && (maxText == null || maxText.length < text.length)) {
                    maxText = text
                }
            }

            return if (maxText != null) maxText.length else 0
        }

    /**
     * Returns height of wheel item

     * @return the item height
     */
    private fun getItemHeight(): Int {
        if (itemHeight != 0) {
            return itemHeight
        } else if (itemsLayout != null && itemsLayout!!.lineCount > 2) {
            itemHeight = itemsLayout!!.getLineTop(2) - itemsLayout!!.getLineTop(1)
            return itemHeight
        }

        return height / this.visibleItems
    }

    /**
     * Calculates control width and creates text layouts

     * @param widthSize
     * *            the input layout width
     * *
     * @param mode
     * *            the layout mode
     * *
     * @return the calculated control width
     */
    private fun calculateLayoutWidth(widthSize: Int, mode: Int): Int {
        initResourcesIfNecessary()

        var width = widthSize

        val maxLength = maxTextLength
        if (maxLength > 0) {
            val textWidth = Math.ceil(Layout.getDesiredWidth("0",
                    itemsPaint).toDouble()).toFloat()
            itemsWidth = (maxLength * textWidth).toInt()
        } else {
            itemsWidth = 0
        }
        itemsWidth += ADDITIONAL_ITEMS_SPACE // make it some more

        labelWidth = 0
        if (this.label != null && this.label!!.length > 0) {
            labelWidth = Math.ceil(Layout.getDesiredWidth(this.label,
                    valuePaint).toDouble()).toInt()
        }

        var recalculate = false
        if (mode == View.MeasureSpec.EXACTLY) {
            width = widthSize
            recalculate = true
        } else {
            width = itemsWidth + labelWidth + 2 * PADDING
            if (labelWidth > 0) {
                width += LABEL_OFFSET
            }

            // Check against our minimum width
            width = Math.max(width, suggestedMinimumWidth)

            if (mode == View.MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize
                recalculate = true
            }
        }

        if (recalculate) {
            // recalculate width
            val pureWidth = width - LABEL_OFFSET - 2 * PADDING
            if (pureWidth <= 0) {
                labelWidth = 0
                itemsWidth = labelWidth
            }
            if (labelWidth > 0) {
                val newWidthItems = itemsWidth.toDouble() * pureWidth / (itemsWidth + labelWidth)
                itemsWidth = newWidthItems.toInt()
                labelWidth = pureWidth - itemsWidth
            } else {
                itemsWidth = pureWidth + LABEL_OFFSET // no label
            }
        }

        if (itemsWidth > 0) {
            createLayouts(itemsWidth, labelWidth)
        }

        return width
    }

    /**
     * Creates layouts

     * @param widthItems
     * *            width of items layout
     * *
     * @param widthLabel
     * *            width of label layout
     */
    private fun createLayouts(widthItems: Int, widthLabel: Int) {
        if (itemsLayout == null || itemsLayout!!.width > widthItems) {
            itemsLayout = StaticLayout(buildText(isScrollingPerformed),
                    itemsPaint, widthItems,
                    if (widthLabel > 0)
                        Layout.Alignment.ALIGN_OPPOSITE
                    else
                        Layout.Alignment.ALIGN_CENTER, 1f,
                    ADDITIONAL_ITEM_HEIGHT.toFloat(), false)
        } else {
            itemsLayout!!.increaseWidthTo(widthItems)
        }

        if (!isScrollingPerformed && (valueLayout == null || valueLayout!!.width > widthItems)) {
            val text = if (adapter != null)
                adapter!!.getItem(
                        currentItem)
            else
                null
            valueLayout = StaticLayout(text ?: "",
                    valuePaint, widthItems,
                    if (widthLabel > 0)
                        Layout.Alignment.ALIGN_OPPOSITE
                    else
                        Layout.Alignment.ALIGN_CENTER, 1f,
                    ADDITIONAL_ITEM_HEIGHT.toFloat(), false)
        } else if (isScrollingPerformed) {
            valueLayout = null
        } else {
            valueLayout!!.increaseWidthTo(widthItems)
        }

        if (widthLabel > 0) {
            if (labelLayout == null || labelLayout!!.width > widthLabel) {
                labelLayout = StaticLayout(this.label, valuePaint, widthLabel,
                        Layout.Alignment.ALIGN_NORMAL, 1f,
                        ADDITIONAL_ITEM_HEIGHT.toFloat(), false)
            } else {
                labelLayout!!.increaseWidthTo(widthLabel)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width = calculateLayoutWidth(widthSize, widthMode)

        var height: Int
        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize
        } else {
            height = getDesiredHeight(itemsLayout)

            if (heightMode == View.MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize)
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (itemsLayout == null) {
            if (itemsWidth == 0) {
                calculateLayoutWidth(width, View.MeasureSpec.EXACTLY)
            } else {
                createLayouts(itemsWidth, labelWidth)
            }
        }

        if (itemsWidth > 0) {
            canvas.save()
            // Skip padding space and hide a part of top and bottom items
            canvas.translate(PADDING.toFloat(), (-ITEM_OFFSET).toFloat())
            drawItems(canvas)
            drawValue(canvas)
            canvas.restore()
        }

        drawCenterRect(canvas)
        drawShadows(canvas)
    }

    /**
     * Draws shadows on top and bottom of control

     * @param canvas
     * *            the canvas for drawing
     */
    private fun drawShadows(canvas: Canvas) {
        topShadow!!.setBounds(0, 0, width, height / this.visibleItems)
        topShadow!!.draw(canvas)

        bottomShadow!!.setBounds(0, height - height / this.visibleItems,
                width, height)
        bottomShadow!!.draw(canvas)
    }

    /**
     * Draws value and label layout

     * @param canvas
     * *            the canvas for drawing
     */
    private fun drawValue(canvas: Canvas) {
        valuePaint!!.color = VALUE_TEXT_COLOR
        valuePaint!!.drawableState = drawableState

        val bounds = Rect()
        itemsLayout!!.getLineBounds(this.visibleItems / 2, bounds)

        // draw label
        if (labelLayout != null) {
            canvas.save()
            canvas.translate((itemsLayout!!.width + LABEL_OFFSET).toFloat(), bounds.top.toFloat())
            labelLayout!!.draw(canvas)
            canvas.restore()
        }

        // draw current value
        if (valueLayout != null) {
            canvas.save()
            canvas.translate(0f, (bounds.top + scrollingOffset).toFloat())
            valueLayout!!.draw(canvas)
            canvas.restore()
        }
    }

    /**
     * Draws items

     * @param canvas
     * *            the canvas for drawing
     */
    private fun drawItems(canvas: Canvas) {
        canvas.save()

        val top = itemsLayout!!.getLineTop(1)
        canvas.translate(0f, (-top + scrollingOffset).toFloat())

        itemsPaint!!.color = ITEMS_TEXT_COLOR
        itemsPaint!!.drawableState = drawableState
        itemsLayout!!.draw(canvas)

        canvas.restore()
    }

    /**
     * Draws rect for current value

     * @param canvas
     * *            the canvas for drawing
     */
    private fun drawCenterRect(canvas: Canvas) {
        val center = height / 2
        val offset = getItemHeight() / 2
        centerDrawable!!.setBounds(0, center - offset, width, center + offset)
        centerDrawable!!.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val adapter = adapter ?: return true

        if (!gestureDetector!!.onTouchEvent(event) && event.action == MotionEvent.ACTION_UP) {
            justify()
        }
        return true
    }

    /**
     * Scrolls the wheel

     * @param delta
     * *            the scrolling value
     */
    private fun doScroll(delta: Int) {
        scrollingOffset += delta

        var count = scrollingOffset / getItemHeight()
        var pos = currentItem - count
        if (isCyclic && this.adapter!!.itemsCount > 0) {
            // fix position by rotating
            while (pos < 0) {
                pos += this.adapter!!.itemsCount
            }
            pos %= this.adapter!!.itemsCount
        } else if (isScrollingPerformed) {
            //
            if (pos < 0) {
                count = currentItem
                pos = 0
            } else if (pos >= this.adapter!!.itemsCount) {
                count = currentItem - this.adapter!!.itemsCount + 1
                pos = this.adapter!!.itemsCount - 1
            }
        } else {
            // fix position
            pos = Math.max(pos, 0)
            pos = Math.min(pos, this.adapter!!.itemsCount - 1)
        }

        val offset = scrollingOffset
        if (pos != currentItem) {
            setCurrentItem(pos, false)
        } else {
            invalidate()
        }

        // update offset
        scrollingOffset = offset - count * getItemHeight()
        if (scrollingOffset > height) {
            scrollingOffset = scrollingOffset % height + height
        }
    }

    // gesture listener
    private val gestureListener = object : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            if (isScrollingPerformed) {
                scroller!!.forceFinished(true)
                clearMessages()
                return true
            }
            return false
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent,
                              distanceX: Float, distanceY: Float): Boolean {
            startScrolling()
            doScroll((-distanceY).toInt())
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                             velocityY: Float): Boolean {
            lastScrollY = currentItem * getItemHeight() + scrollingOffset
            val maxY = if (isCyclic)
                0x7FFFFFFF
            else
                adapter!!.itemsCount * getItemHeight()
            val minY = if (isCyclic) -maxY else 0
            scroller!!.fling(0, lastScrollY, 0, (-velocityY).toInt() / 2, 0, 0, minY,
                    maxY)
            setNextMessage(MESSAGE_SCROLL)
            return true
        }
    }

    // Messages
    private val MESSAGE_SCROLL = 0
    private val MESSAGE_JUSTIFY = 1

    /**
     * Set next message to queue. Clears queue before.

     * @param message
     * *            the message to set
     */
    private fun setNextMessage(message: Int) {
        clearMessages()
        animationHandler.sendEmptyMessage(message)
    }

    /**
     * Clears messages from queue
     */
    private fun clearMessages() {
        animationHandler.removeMessages(MESSAGE_SCROLL)
        animationHandler.removeMessages(MESSAGE_JUSTIFY)
    }

    // animation handler
    @SuppressLint("HandlerLeak")
    private val animationHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            scroller!!.computeScrollOffset()
            var currY = scroller!!.currY
            val delta = lastScrollY - currY
            lastScrollY = currY
            if (delta != 0) {
                doScroll(delta)
            }

            // scrolling is not finished when it comes to final Y
            // so, finish it manually
            if (Math.abs(currY - scroller!!.finalY) < MIN_DELTA_FOR_SCROLLING) {
                currY = scroller!!.finalY
                scroller!!.forceFinished(true)
            }
            if (!scroller!!.isFinished) {
                this.sendEmptyMessage(msg.what)
            } else if (msg.what == MESSAGE_SCROLL) {
                justify()
            } else {
                finishScrolling()
            }
        }
    }

    /**
     * Justifies wheel
     */
    private fun justify() {
        if (this.adapter == null) {
            return
        }

        lastScrollY = 0
        var offset = scrollingOffset
        val itemHeight = getItemHeight()
        val needToIncrease = if (offset > 0)
            currentItem < this.adapter!!
                    .itemsCount
        else
            currentItem > 0
        if ((isCyclic || needToIncrease) && Math.abs(offset.toFloat()) > itemHeight.toFloat() / 2) {
            if (offset < 0)
                offset += itemHeight + MIN_DELTA_FOR_SCROLLING
            else
                offset -= itemHeight + MIN_DELTA_FOR_SCROLLING
        }
        if (Math.abs(offset) > MIN_DELTA_FOR_SCROLLING) {
            scroller!!.startScroll(0, 0, 0, offset, SCROLLING_DURATION)
            setNextMessage(MESSAGE_JUSTIFY)
        } else {
            finishScrolling()
        }
    }

    /**
     * Starts scrolling
     */
    private fun startScrolling() {
        if (!isScrollingPerformed) {
            isScrollingPerformed = true
            notifyScrollingListenersAboutStart()
        }
    }

    /**
     * Finishes scrolling
     */
    internal fun finishScrolling() {
        if (isScrollingPerformed) {
            notifyScrollingListenersAboutEnd()
            isScrollingPerformed = false
        }
        invalidateLayouts()
        invalidate()
    }

    /**
     * Scroll the wheel

     * @param itemsToScroll
     * *            items to scroll
     * *
     * @param time
     * *            scrolling duration
     */
    fun scroll(itemsToScroll: Int, time: Int) {
        scroller!!.forceFinished(true)

        lastScrollY = scrollingOffset
        val offset = itemsToScroll * getItemHeight()

        scroller!!.startScroll(0, lastScrollY, 0, offset - lastScrollY, time)
        setNextMessage(MESSAGE_SCROLL)

        startScrolling()
    }

    companion object {
        /** Scrolling duration  */
        private val SCROLLING_DURATION = 400

        /** Minimum delta for scrolling  */
        private val MIN_DELTA_FOR_SCROLLING = 1

        /** Current value & label text color  */
        private val VALUE_TEXT_COLOR = 0xF0FF6347.toInt()

        /** Items text color  */
        private val ITEMS_TEXT_COLOR = 0xFF000000.toInt()

        /** Top and bottom shadows colors  */
        private val SHADOWS_COLORS = intArrayOf(0xFF111111.toInt(), 0x00AAAAAA, 0x00AAAAAA)

        /** Additional items height (is added to standard text item height)  */
        private val ADDITIONAL_ITEM_HEIGHT = 15

        /** Text size  */
        private val TEXT_SIZE = 24

        /** Top and bottom items offset (to hide that)  */
        private val ITEM_OFFSET = TEXT_SIZE / 5

        /** Additional width for items layout  */
        private val ADDITIONAL_ITEMS_SPACE = 10

        /** Label offset  */
        private val LABEL_OFFSET = 8

        /** Left and right padding value  */
        private val PADDING = 10

        /** Default count of visible items  */
        private val DEF_VISIBLE_ITEMS = 5
    }

}
/**
 * Sets the current item w/o animation. Does nothing when index is wrong.

 * @param index
 * *            the item index
 */
