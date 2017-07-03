package com.hy.frame.adapter

import android.content.Context
import android.support.annotation.IdRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * BaseRecyclerAdapter
 * @author HeYan
 * @time 2016/5/27 16:22
 */
abstract class BaseRecyclerAdapter<T> constructor(protected val context: Context, datas: List<T>?, listener: IAdapterListener<T>? = null) : RecyclerView.Adapter<BaseRecyclerAdapter<T>.BaseHolder>() {

    var datas: List<T>? = null
    protected var listener: IAdapterListener<T>? = null
    var headerCount: Int = 0

    init {
        this.datas = datas
        this.listener = listener
    }


    protected fun inflate(resId: Int): View {
        return LayoutInflater.from(context).inflate(resId, null)
    }

    private var gridCount: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        //if (viewType == TYPE_MORE)
        //    return new LoadMoreHolder(inflate(parent, R.layout.in_recycler_footer));
        val isGrid = (parent as RecyclerView).layoutManager is GridLayoutManager
        if (isGrid) {
            gridCount = (parent.layoutManager as GridLayoutManager).spanCount
            if (dividerHorizontalSize > 0) {
                parent.setPadding(dividerHorizontalSize, parent.getPaddingTop(), parent.getPaddingRight(), parent.getPaddingBottom())
            }
        }
        return createView(parent)
    }


    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        if (dividerHorizontalSize > 0 || dividerVerticalSize > 0 || topPadding > 0 || bottomPadding > 0) {
            val padding = dividerVerticalSize
            val left: Int = holder.itemView.paddingLeft
            var top: Int = 0
            var right: Int
            var bottom: Int
            right = holder.itemView.paddingRight
            bottom = holder.itemView.paddingBottom
            if (position == 0 && topPadding > 0) {
                top = topPadding
            } else if (dividerVerticalSize > 0) {
                top = padding
            }
            if (bottomPadding > 0) {
                bottom = 0
                if (gridCount <= 1 && itemCount - (position + 1) == 0) {
                    bottom = bottomPadding
                } else if (gridCount > 1) {
                    val curPosition = getCurPosition(position) + 1
                    val lastLinePosition: Int
                    val surplus = itemCount % gridCount
                    if (surplus == 0) {
                        lastLinePosition = itemCount - gridCount
                    } else {
                        lastLinePosition = itemCount - surplus
                    }
                    if (curPosition > lastLinePosition) {
                        bottom = bottomPadding
                    }
                }
            }
            if (gridCount > 1 && dividerHorizontalSize > 0) {
                right = dividerHorizontalSize
            }
            holder.itemView.setPadding(left, top, right, bottom)
        }
        bindViewData(holder, position)
    }

    private var dividerHorizontalSize: Int = 0
    private var dividerVerticalSize: Int = 0
    private var topPadding: Int = 0
    private var bottomPadding: Int = 0

    fun setDividerHorizontalSize(dividerHorizontalSize: Int) {
        this.dividerHorizontalSize = dividerHorizontalSize
    }

    fun setDividerVerticalSize(dividerVerticalSize: Int) {
        this.dividerVerticalSize = dividerVerticalSize
    }

    fun setTopPadding(topPadding: Int) {
        this.topPadding = topPadding
    }

    fun setBottomPadding(bottomPadding: Int) {
        this.bottomPadding = bottomPadding
    }

    /**
     * Cur True Position
     */
    fun getCurPosition(position: Int): Int {
        return position - headerCount
    }

    fun getItem(position: Int): T {
        return datas!![position]
    }

    fun refresh() {
        this.notifyDataSetChanged()
    }

    fun refresh(beans: List<T>?) {
        this.datas = beans
        this.notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return if (datas == null) 0 else datas!!.size
    }

    /**
     * create child View
     */
    abstract fun createView(parent: ViewGroup): BaseHolder

    /**
     * bind child data
     */
    abstract fun bindViewData(holder: RecyclerView.ViewHolder, position: Int)

    /**
     * 获取 控件
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> findViewById(@IdRes id: Int, parent: View?): T? {
        val view = parent?.findViewById<View>(id)
        return if(view ==null) null else view as T
    }

    /**
     * 获取并绑定点击
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    protected fun <V : View> setOnClickListener(@IdRes id: Int, parent: View?, listener: View.OnClickListener): V? {
        val view = findViewById<V>(id, parent)
        view?.setOnClickListener(listener) ?: return null
        return view
    }


    /**
     * BaseRecyclerAdapter
     * 自定义的ViewHolder，持有每个Item的的所有界面元素,static不是说只存在1个实例，而是可以访问外部类的静态变量，final修饰类则是不让该类继承

     * @author HeYan
     *
     * @time 2017/5/23 10:13
     */
    open inner class BaseHolder(v: View) : RecyclerView.ViewHolder(v) {

        init {
            if (v.layoutParams == null) {
                v.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    open inner class BaseClickHolder(v: View) : BaseHolder(v), View.OnClickListener {

        override fun onClick(v: View) {
            if (listener != null) {
                var position = adapterPosition
                if (position < 0) return
                position = getCurPosition(position)
                if (position < 0) return
                listener!!.onViewClick(v, getItem(position), position)
            }
        }

        fun setOnClickListener(v: View?) {
            v?.setOnClickListener(this)
        }

        protected fun <V : View> setOnClickListener(@IdRes id: Int): V? {
            return setOnClickListener(id, itemView, this)
        }
    }

    open inner class BaseLongClickHolder(v: View) : BaseClickHolder(v), View.OnLongClickListener {

        override fun onLongClick(v: View): Boolean {
            if (listener != null && listener is IAdapterLongListener<T>) {
                var position = adapterPosition
                if (position < 0) return false
                position = getCurPosition(position)
                if (position < 0) return false
                (listener as IAdapterLongListener<T>).onViewLongClick(v, getItem(position), position)
            }
            return false
        }

        fun setOnLongClickListener(v: View?) {
            v?.setOnLongClickListener(this)
        }

        protected fun <V : View> setOnLongClickListener(@IdRes id: Int): V? {
            val v = findViewById<V>(id, itemView)
            setOnLongClickListener(v)
            return v
        }
    }
}
