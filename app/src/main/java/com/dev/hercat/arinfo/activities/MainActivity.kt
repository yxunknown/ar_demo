package com.dev.hercat.arinfo.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ListView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.dev.hercat.arinfo.R
import com.dev.hercat.arinfo.adapter.ArAdapter
import com.dev.hercat.arinfo.model.Point
import com.dev.hercat.arinfo.view.ArView
import com.github.nisrulz.sensey.Sensey
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.text.DecimalFormat
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), AnkoLogger {
    val TAG = "ACTIVITY_MAIN"

    private val points = listOf(
            Point(106.553696,29.519781, "怡丰花园"),
            Point(106.552645,29.520938, "海韵豪园"),
            Point(106.555332,29.521228, "重庆第六人民医院"),
            Point(106.553385,29.521307, "鲜鲜肥肠饭"),
            Point(106.552752,29.518464, "怡丰中学"))

    private lateinit var adapter: ArAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = ArAdapter(points, this)
        arView.addAdapter(adapter)
        initLocationClient()
        initSensor()
    }

    private fun initLocationClient() {
        if (!checkPermission()) {
            getPermission(object : PermissionCallback {
                override fun result(granted: Boolean, permissionGranted: List<String>?, permissionDeniedForever: List<String>?, permissionDenied: List<String>?) {
                }
            })
        }

        val mLocationClient = AMapLocationClient(applicationContext)

        mLocationClient.setLocationOption(locationClientConfiguratiion)

        mLocationClient.setLocationListener {aMapLocation ->
            if (aMapLocation is AMapLocation) {
                if (aMapLocation.errorCode == 0) {
                    //get location success
                    tvLocation.text = resources.getString(R.string.location_desc,
                            aMapLocation.address)
                    adapter.updateCurrentLocation(Point(aMapLocation.longitude, aMapLocation.latitude, ""))
                } else {
                    Log.e(TAG, """
                        location error, error code: ${aMapLocation.errorCode},
                        error info: ${aMapLocation.errorInfo}
                    """.trimIndent())
                }
            } else {
                Log.e(TAG, "获取定位失败")
            }
        }
        mLocationClient.startLocation()
    }

    private fun initSensor() {
        val rotationCache = mutableListOf<Float>()
        Sensey.getInstance().startRotationAngleDetection { x: Float, y: Float, z: Float ->
            val numberFormat = DecimalFormat("0.00")
            if (rotationCache.size < 11) {
                rotationCache.add(x)
            } else {
                val r = rotationCache.sorted()[5]
                rotationCache.clear()
                val orientation = if (r >= 0) r else 360 + r
                arView.updateOrientation(orientation.toDouble())
                tvRotation.text = numberFormat.format(orientation)
            }
        }

    }

    override fun onResume() {
        Log.i(TAG, "onResume")
        super.onResume()
        cameraPreviewer.start()


    }
    override fun onPause() {
        Log.i(TAG, "onPause")
        super.onPause()
        cameraPreviewer.stop()
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        super.onDestroy()
        cameraPreviewer.destroy()
        Sensey.getInstance().stop()
    }
}
