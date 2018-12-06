package com.dev.hercat.arinfo.view

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.displayMetrics


const val PIXELS_PER_DEGREE = 60

class ArView : HorizontalScrollView {

    private val TAG = "AR_VIEW"

    var adapter: ArAdapter? = null
    val views = mutableListOf<View>()

    // device screen info
    private val displayMetrics = context.displayMetrics

    // child view container
    private val container: FrameLayout = FrameLayout(context)

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        val lp = ViewGroup.LayoutParams(PIXELS_PER_DEGREE * 480, ViewGroup.LayoutParams.MATCH_PARENT)
        container.layoutParams = lp
        addView(container, lp)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }

    fun addAdapter(adapter: ArAdapter) {
        this.adapter = adapter
        this.adapter!!.registerDataSetObserver(DataObserver())
        this.adapter!!.notifyDataSetChanged()
    }

    fun updateOrientation(orientation: Double) {
        val scrollTO = ((orientation + 60) * PIXELS_PER_DEGREE).toInt()
        smoothScrollTo(scrollTO, 0)
    }

    inner class DataObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            if (adapter != null) {
                try {
                    container.removeAllViews()
                    for (index in 0 until adapter!!.count) {
                        val convertView = views.getOrNull(index)
                        if (convertView != null) convertView.invalidate()
                        val view = adapter!!.getView(index, views.getOrNull(index), container)
                        addArInfo(view, adapter!!.getRotation(index), index)
                        views.add(index, view)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                invalidate()
            } else {
                Log.w(TAG, "data adapter is null")
            }
        }

        override fun onInvalidated() {
            super.onInvalidated()
        }
    }

    private fun addArInfo(view: View, rotation: Double, index: Int) {
        val lp = view.layoutParams as FrameLayout.LayoutParams
        lp.topMargin = (displayMetrics.heightPixels - view.height) / 2
        lp.marginStart = ((rotation + 60) * PIXELS_PER_DEGREE).toInt()
        lp.marginEnd = PIXELS_PER_DEGREE * 480 - lp.marginStart - lp.width
        view.layoutParams = lp
        container.addView(view, lp)
    }

}

abstract class ArAdapter : BaseAdapter() {
    abstract fun getRotation(position: Int): Double
}