package com.dev.hercat.arinfo.view

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.View
import android.widget.BaseAdapter
import android.widget.RelativeLayout

class ArView constructor(context: Context,
                         attr: AttributeSet? = null,
                         defStyleAttr: Int = 0) : RelativeLayout(context, attr, defStyleAttr) {
    var adapter: ArAdapter? = null
    val views = mutableListOf<View>()

    fun addAdapter(adapter: ArAdapter) {
        this.adapter = adapter
        this.adapter!!.notifyDataSetChanged()
        this.adapter!!.registerDataSetObserver(DataObserver())
    }

    inner class DataObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            this@ArView.removeAllViews()
            if (adapter!!.count <= views.size) {
                for (index in 0 until adapter!!.count) {
                    this@ArView.addView(adapter!!.getView(index, views[index], this@ArView))
                }
            } else {
                for (index in 0 until views.size) {
                    this@ArView.addView(adapter!!.getView(index, views[index], this@ArView))
                }
                for (index in views.size until adapter!!.count) {
                    val view = adapter!!.getView(index, null, this@ArView)
                    views.add(view)
                    this@ArView.addView(view)
                }
            }
        }

        override fun onInvalidated() {
            super.onInvalidated()
        }
    }

}

abstract class ArAdapter : BaseAdapter() {
    abstract fun getRotation(): Double
}