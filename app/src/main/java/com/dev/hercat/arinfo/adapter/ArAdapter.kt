package com.dev.hercat.arinfo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dev.hercat.arinfo.R
import com.dev.hercat.arinfo.model.Point
import com.dev.hercat.arinfo.rotation
import com.dev.hercat.arinfo.view.ArAdapter

class ArAdapter(private val points: List<Point>,
                private val context: Context): ArAdapter() {

    private val layoutInflater = LayoutInflater.from(context)

    private var currentLocation = Point(0.0, 0.0, "")

    override fun getRotation(position: Int): Double {
        val point = points[position]
        return rotation(currentLocation.latitude, currentLocation.longitude, point.latitude, point.longitude)
    }

    override fun getCount() = points.size

    override fun getItem(position: Int) = points[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: layoutInflater.inflate(R.layout.ar_view, parent, false)
        val arInfo = view.findViewById<TextView>(R.id.ar_info)
        arInfo.text = points[position].name
        return view
    }

    fun updateCurrentLocation(location: Point) {
        this.currentLocation = location
        notifyDataSetChanged()
    }
}