package com.dev.hercat.arinfo.view

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.Scroller
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.displayMetrics


const val PIXELS_PER_DEGREE = 60

class ArView : HorizontalScrollView {

    private val TAG = "AR_VIEW"

    var adapter: ArAdapter? = null
    val views = mutableListOf<View>()

    // device screen info
    private lateinit var displayMetrics: DisplayMetrics

    // child view container
    private val container: FrameLayout = FrameLayout(context)

    private val total = PIXELS_PER_DEGREE * 480

    constructor(context: Context) : super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        displayMetrics = context.displayMetrics
        val lp = ViewGroup.LayoutParams(total, displayMetrics.heightPixels)
        container.layoutParams = lp
        addView(container, lp)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // init container size
        container.measure(total, displayMetrics.heightPixels)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
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
                    for (index in 0 until adapter!!.count) {
                        val convertView = views.getOrNull(index)
                        if (convertView != null) {
                            convertView.invalidate()
                            container.removeView(convertView)
                        }
                        val view = adapter!!.getView(index, views.getOrNull(index), null)
                        addArInfo(view, adapter!!.getRotation(index), index)
                        views.add(index, view)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                container.invalidate()
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
        val lp = FrameLayout.LayoutParams(500, 100)
        lp.leftMargin = ((rotation + 60) * PIXELS_PER_DEGREE -  lp.width / 2).toInt()
        view.measure(500, 100)
        println(lp.leftMargin)
        lp.topMargin = displayMetrics.heightPixels / 2 - lp.height / 2
        lp.rightMargin = total - left - lp.width / 2
        view.layoutParams = lp
        container.addView(view)
    }
}

abstract class ArAdapter : BaseAdapter() {
    abstract fun getRotation(position: Int): Double
}