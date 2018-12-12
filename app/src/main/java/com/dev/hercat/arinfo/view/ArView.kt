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
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.displayMetrics


const val PIXELS_PER_DEGREE = 60

class ArView : ViewGroup {

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

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // init container size
        setMeasuredDimension(total, displayMetrics.heightPixels)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            if (child.visibility != View.GONE) {
                val rotation = adapter!!.getRotation(index)
                val top = displayMetrics.heightPixels / 2 - child.measuredHeight / 2
                val left = ((rotation + 60) * PIXELS_PER_DEGREE).toInt() - child.measuredWidth / 2
                val right = total - left - child.measuredWidth / 2
                val bottom = displayMetrics.heightPixels / 2 - child.measuredHeight / 2
                child.layout(left, top, right, bottom)
            }
        }

    }

    fun addAdapter(adapter: ArAdapter) {
        this.adapter = adapter
        this.adapter!!.registerDataSetObserver(DataObserver())
        this.adapter!!.notifyDataSetChanged()
    }

    fun updateOrientation(orientation: Double) {
        val scrollTO = ((orientation + 60) * PIXELS_PER_DEGREE).toInt()
        scrollTo(scrollTO, 0)
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
                            this@ArView.removeView(convertView)
                        }
                        val view = adapter!!.getView(index, views.getOrNull(index), null)
                        addArInfo(view, adapter!!.getRotation(index), index)
                        views.add(index, view)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
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

    private fun addArInfo(view: View, rotation: Double, index: Int) {
        addView(view, index)
    }

}

abstract class ArAdapter : BaseAdapter() {
    abstract fun getRotation(position: Int): Double
}