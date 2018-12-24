package com.dev.hercat.arinfo.view

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.text.Layout
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
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

    // adapter hold ar info
    private var adapter: ArAdapter? = null

    // view collector
    private val views = mutableListOf<View>()

    // device screen info
    private lateinit var displayMetrics: DisplayMetrics

    // child view container
    private val container: FrameLayout = FrameLayout(context)

    // total width pixels of container
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
        // initialize the display metrics
        displayMetrics = context.displayMetrics
        // init width and height of container
        val lp = FrameLayout.LayoutParams(total, displayMetrics.heightPixels)
        container.layoutParams = lp
        // add container to this view group
        addView(container, lp)

        // hide scroll bar
        scrollBarSize = 0
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        // dispatch to child
        return false
    }


    /**
     * set ar adapter
     */
    fun addAdapter(adapter: ArAdapter) {
        if (this.adapter != null) {
            this.adapter = null
            // clear container
            container.removeAllViews()
            container.invalidate()
        }
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
                        // clear parent of convertView
                        if (convertView != null) {
                            convertView.invalidate()
                            container.removeView(convertView)
                        }
                        // get view from adapter
                        val view = adapter!!.getView(index, convertView, null)
                        // add view into container
                        addArInfo(view = view,
                                  rotation = adapter!!.getRotation(index),
                                  distance = adapter!!.getDistance(index),
                                  index = index)
                        // add view to view collector
                        if (views.size > index) {
                            views[index] = view
                        } else {
                            views.add(view)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // update ui
                container.invalidate()
            } else {
                Log.w(TAG, "data adapter is null")
            }
        }
        override fun onInvalidated() {
            super.onInvalidated()
        }
    }

    private fun addArInfo(view: View, rotation: Double, distance: Double, index: Int) {
        // init layout parameter of view
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // measure height and width of view
        view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // init left and right position of view based on rotation
        lp.leftMargin = ((rotation + 60) * PIXELS_PER_DEGREE -  lp.width / 2).toInt()
        lp.rightMargin = total - lp.leftMargin - lp.width
        // init top position based on distance
        val halfHeightOfScreen = displayMetrics.heightPixels / 2
        val marginAndOpacity = when {
            distance <= 10 -> 0.0 to 0.0
            distance <= 50 -> distance * 2 to distance * 0.0002
            distance <= 200 -> 80 + (distance - 50) * 2 / 3 to 0.1 + (distance - 50) / 1500
            distance <= 500 -> 180 + (distance - 200) / 3 to 0.2 + (distance - 200) / 1500
            distance <= 1000 -> 280 + (distance - 500) / 5 to 0.4 + (distance - 500) / 2500
            distance <= 2000 -> 380 + (distance - 1000) / 10 to 0.6 + (distance - 1000) / 5000
            else -> 100.0 to 0.9
        }
        lp.topMargin = halfHeightOfScreen - marginAndOpacity.first.toInt()
        view.layoutParams = lp
        // set opacity of view
        view.alpha = (1.0 - marginAndOpacity.second).toFloat()
        container.addView(view)
    }
}

abstract class ArAdapter : BaseAdapter() {
    abstract fun getRotation(position: Int): Double
    abstract fun getDistance(position: Int): Double
}