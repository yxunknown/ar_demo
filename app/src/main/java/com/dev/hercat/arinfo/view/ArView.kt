package com.dev.hercat.arinfo.view

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.widget.BaseAdapter
import android.widget.RelativeLayout

class ArView constructor(context: Context,
                         attr: AttributeSet? = null,
                         defStyleAttr: Int = 0): RelativeLayout(context, attr, defStyleAttr) {
    var adapter: ArAdapter? = null

    fun addAdapter(adapter: ArAdapter) {
        this.adapter = adapter
        this.adapter!!.notifyDataSetChanged()
        this.adapter!!.registerDataSetObserver(DataObserver())
    }

    inner class DataObserver: DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            this@ArView.removeAllViews()
            if (adapter is ArAdapter) {
                for (i in 0 until adapter!!.count) {
                    this@ArView.addView(adapter!!.getView(i, null, this@ArView))
                }
            }
        }

        override fun onInvalidated() {
            super.onInvalidated()
        }
    }

}

abstract class ArAdapter: BaseAdapter() {
    abstract fun getRotation(): Double
}