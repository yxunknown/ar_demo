package com.dev.hercat.arinfo.view

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import org.jetbrains.anko.displayMetrics

const val PIXELS_PER_DEGREE = 60

class ArView: HorizontalScrollView {

    private val TAG = "AR_VIEW"

    var adapter: ArAdapter? = null
    val views = mutableListOf<View>()

    // device screen info
    private val displayMetrics  = context.displayMetrics

    // child view container
    private val container: FrameLayout = FrameLayout(context).apply {
        val lp = ViewGroup.LayoutParams(PIXELS_PER_DEGREE * 480, displayMetrics.heightPixels)
        layoutParams = lp
    }

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (childCount == 0) {
            addView(container)
        }
    }

    fun addAdapter(adapter: ArAdapter) {
        this.adapter = adapter
        this.adapter!!.registerDataSetObserver(DataObserver())
        this.adapter!!.notifyDataSetChanged()
    }

    inner class DataObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            container.removeAllViews()
            if (adapter != null) {
                for (index in 0 until adapter!!.count) {
                    val view = adapter!!.getView(index, views.getOrNull(index), container)
                    views[index] = view
                    addArInfo(view, adapter!!.getRotation(index))
                }
                requestLayout()
            } else {
                Log.w(TAG, "data adapter is null")
            }
        }

        override fun onInvalidated() {
            super.onInvalidated()
        }
    }

    private fun addArInfo(view: View, rotation: Double) {
        val lp = FrameLayout.LayoutParams(view.width, view.height)
        lp.topMargin = (displayMetrics.heightPixels - view.height) / 2
        lp.marginStart = ((rotation + 60) * PIXELS_PER_DEGREE).toInt()
        view.layoutParams = lp
        container.addView(view)
    }

}

abstract class ArAdapter : BaseAdapter() {
    abstract fun getRotation(position: Int): Double
}