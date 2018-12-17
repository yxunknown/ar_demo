package com.dev.hercat.arinfo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.amap.api.location.CoordinateConverter
import com.amap.api.location.DPoint
import com.dev.hercat.arinfo.R
import com.dev.hercat.arinfo.model.Point
import com.dev.hercat.arinfo.rotation
import com.dev.hercat.arinfo.view.ArAdapter
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.text.DecimalFormat
import java.util.*

class ArAdapter(private val points: List<Point>,
                private val context: Context): ArAdapter() {

    private val layoutInflater = LayoutInflater.from(context)

    private var currentLocation = Point(0.0, 0.0, "", "", 0)

    override fun getRotation(position: Int): Double {
        return if (position in 0 until points.size) {
            val point = points[position]
            // calculate rotation between current location and target location
            rotation(currentLocation.latitude, currentLocation.longitude, point.latitude, point.longitude)
        } else {
            // position is out of index boundary
            0.0
        }
    }

    override fun getDistance(position: Int): Double {
        return if (position in 0 until points.size) {
            val point = points[position]
            val dp1 = DPoint(point.latitude, point.longitude)
            val dp2 = DPoint(currentLocation.latitude, currentLocation.longitude)
            // calculate distance between target location and current location
            CoordinateConverter.calculateLineDistance(dp1, dp2).toDouble()
        } else {
            // position is out of index boundary
            0.00
        }
    }

    /**
     * return view count
     */
    override fun getCount() = points.size

    /**
     * return data item at position
     */
    override fun getItem(position: Int) = points[position]

    /**
     * return unique id of item
     */
    override fun getItemId(position: Int) = position.toLong()

    /**
     * return view that display data
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: layoutInflater.inflate(R.layout.ar_view, parent, false)
        val arInfo = view.findViewById<TextView>(R.id.ar_info)
        val detail = view.findViewById<View>(R.id.detail)
        val desc = view.findViewById<TextView>(R.id.tv_detail_desc)
        val img = view.findViewById<ImageView>(R.id.iv_detail_img)
        val doubleFormator = DecimalFormat("0.000")
        val dim = getDistance(position)
        val distance = doubleFormator.format(dim)
        arInfo.text = "${points[position].name} $distance 米"
        desc.text = points[position].desc
        img.setImageResource(points[position].img)
        arInfo.onClick {
             val opacity = view.alpha
             val visiable = if (detail.visibility == View.GONE) {
                 view.alpha = 1.0f
                 View.VISIBLE
             } else {
                 view.alpha = opacity
                 View.GONE
             }
            detail.visibility = visiable
        }
        return view
    }

    /**
     * refresh current location, and notify data has changed, update ui
     */
    fun updateCurrentLocation(location: Point) {
        this.currentLocation = location
        notifyDataSetChanged()
    }
}