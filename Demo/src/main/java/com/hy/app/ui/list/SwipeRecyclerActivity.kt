package com.hy.app.ui.list

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MotionEvent
import android.view.View

import com.hy.app.R
import com.hy.app.adapter.SwipeRecyclerAdapter
import com.hy.app.common.BaseActivity
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.util.FormatUtil
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog
import com.hy.frame.widget.recycler.LinearItemDecoration

import java.util.ArrayList

/**
 * SwipeRecyclerActivity
 *
 * @author HeYan
 * @time 2017/5/11 10:07
 */
class SwipeRecyclerActivity : BaseActivity(), IAdapterListener<String>, RecyclerView.OnItemTouchListener {
    private var rcyList: RecyclerView? = null
    private var adapter: SwipeRecyclerAdapter? = null
    private var datas: MutableList<String>? = null
    private var handler: Handler? = null

    override fun getLayoutId(): Int {
        return R.layout.act_recycler
    }

    override fun initView() {
        initHeaderBack(R.string.list_swipe_recycler)
        rcyList = findViewById(R.id.recycler_rcyList)
        rcyList!!.layoutManager = MyLinearLayoutManager(getCurContext()!!)
        rcyList!!.addItemDecoration(LinearItemDecoration(rcyList!!, 1, resources.getColor(R.color.divider_gray)))
        rcyList!!.addOnItemTouchListener(this)
    }

    override fun initData() {
        showLoading()
        requestData()
    }


    private fun requestData() {
        datas = ArrayList()
        for (i in 1..19) {
            datas!!.add("测试" + i)
        }
        if (handler == null)
            handler = Handler()
        handler!!.postDelayed({ updateUI() }, 1000)
    }

    private fun updateUI() {
        if (FormatUtil.isEmpty(datas)) {
            showNoData()
            return
        }
        showCView()
        if (adapter == null) {
            adapter = SwipeRecyclerAdapter(getCurContext()!!, datas, this)
            rcyList!!.adapter = adapter
            val helper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
                private var isItemViewSwipeEnabled = true
                private val isLongPressDragEnabled = true
                private var isItemOpen = false
                private var viewHolder: RecyclerView.ViewHolder? = null

                override fun isItemViewSwipeEnabled(): Boolean {
                    MyLog.e("isItemViewSwipeEnabled")
                    if (isItemOpen && viewHolder != null) {
                        closeItemMenu()
                    }
                    return isItemViewSwipeEnabled
                }

                override fun isLongPressDragEnabled(): Boolean {
                    return isLongPressDragEnabled
                }

                override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                    return ItemTouchHelper.Callback.makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
                }

                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    MyLog.e("onMove")
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    MyLog.e("onSwiped")
                }

                override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int, target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
                    //super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                    MyLog.e("onMoved")
                }

                override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)
                    MyLog.e("clearView")
                }

                override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
                    MyLog.e("convertToAbsoluteDirection")
                    return super.convertToAbsoluteDirection(flags, layoutDirection)
                }

                override fun canDropOver(recyclerView: RecyclerView?, current: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                    MyLog.e("canDropOver")
                    return super.canDropOver(recyclerView, current, target)
                }

                override fun chooseDropTarget(selected: RecyclerView.ViewHolder, dropTargets: List<RecyclerView.ViewHolder>, curX: Int, curY: Int): RecyclerView.ViewHolder {
                    MyLog.e("chooseDropTarget")
                    return super.chooseDropTarget(selected, dropTargets, curX, curY)
                }

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    MyLog.e("onSelectedChanged")
                    super.onSelectedChanged(viewHolder, actionState)
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    MyLog.e("onChildDraw dX=$dX,dY=$dY,actionState=$actionState,isCurrentlyActive=$isCurrentlyActive")
                    if (!isItemViewSwipeEnabled || isItemOpen) return
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        if (isCurrentlyActive) {
                            if (dX <= -adapter!!.menuWidth) {
                                super.onChildDraw(c, recyclerView, viewHolder, (-adapter!!.menuWidth).toFloat(), dY, actionState, true)
                                openItemMenu(viewHolder)
                                return
                            }
                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, true)
                        } else if (dX < -adapter!!.menuWidth / 3 * 2) {
                            super.onChildDraw(c, recyclerView, viewHolder, (-adapter!!.menuWidth).toFloat(), dY, actionState, false)
                            openItemMenu(viewHolder)
                        } else {
                            super.onChildDraw(c, recyclerView, viewHolder, 0f, dY, actionState, false)
                        }
                    } else {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    }
                }

                override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    //                    if (dX > -adapter.getMenuWidth()) {
                    //                        //isItemViewSwipeEnabled = true;
                    //                        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    //                        if (dX > -adapter.getMenuWidth() / 2) {
                    //                            super.onChildDrawOver(c, recyclerView, viewHolder, -adapter.getMenuWidth(), dY, actionState, isCurrentlyActive);
                    //                            isItemViewSwipeEnabled = true;
                    //
                    //                        }
                    //                    } else {
                    //                        //isItemViewSwipeEnabled = false;
                    //                    }
                    super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }

                private fun openItemMenu(viewHolder: RecyclerView.ViewHolder) {
                    this.viewHolder = viewHolder
                    isItemViewSwipeEnabled = false
                    isItemOpen = true
                    val manager = rcyList!!.layoutManager as MyLinearLayoutManager
                    manager.setScrollEnabled(false)
                }

                private fun closeItemMenu() {
                    ItemTouchHelper.Callback.getDefaultUIUtil().clearView(viewHolder!!.itemView)
                    isItemOpen = false
                    isItemViewSwipeEnabled = true
                    viewHolder = null
                    val manager = rcyList!!.layoutManager as MyLinearLayoutManager
                    manager.setScrollEnabled(true)
                }
            })
            helper.attachToRecyclerView(rcyList)
        } else {
            adapter!!.refresh(datas)
        }
    }

    override fun onViewClick(v: View) {}


    override fun onViewClick(v: View, item: String, position: Int) {
        when (v.id) {
            R.id.swipe_i_btnDelete -> {

                datas!!.removeAt(position)
                adapter!!.notifyItemRemoved(position)
            }
        }//adapter.refresh(datas);
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    private inner class MyLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

        private var isScrollEnabled = true

        fun setScrollEnabled(flag: Boolean) {
            this.isScrollEnabled = flag
        }

        override fun canScrollVertically(): Boolean {
            return isScrollEnabled && super.canScrollVertically()
        }
    }
}
