package com.dev.hercat.arinfo.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ListView
import com.amap.api.fence.GeoFence
import com.amap.api.fence.GeoFenceClient
import com.amap.api.location.*
import com.amap.api.maps.AMap
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.model.Polygon
import com.amap.api.maps.model.PolygonOptions
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

    private val GEO_FENCE_ACTION = "con.dev.hercat.arinfo.broadcast"

    private val points = listOf(
            Point(106.553696,29.519781, "怡丰花园", "桃源路怡丰花园", R.drawable.yfhy),
            Point(106.552645,29.520938, "海韵豪园", "桃源路海韵豪园", R.drawable.hyhy),
            Point(106.555332,29.521228, "重庆第六人民医院", "大石路市六院", R.drawable.ly),
            Point(106.553385,29.521307, "鲜鲜肥肠饭", "五小区肥肠饭", R.drawable.xxfcf),
            Point(106.552752,29.518464, "怡丰中学", "怡丰实验中学", R.drawable.yfsyzx))

    private lateinit var adapter: ArAdapter

    private lateinit var map: AMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = ArAdapter(points, this)
        arView.addAdapter(adapter)
        initLocationClient()
        initSensor()
        map_view.onCreate(savedInstanceState)
        map = map_view.map
        initMap()
        drawArea()
    }

    /**
     * start location client
     */
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
                    adapter.updateCurrentLocation(Point(aMapLocation.longitude, aMapLocation.latitude, "", "", 0))

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

    /**
     * init orientation sensor
     */
    private fun initSensor() {
        val rotationCache = mutableListOf<Float>()
        Sensey.getInstance().startRotationAngleDetection { x: Float, _: Float, _: Float ->
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

    /**
     * init map layer
     */
    private fun initMap() {
        val mLocationStyle = MyLocationStyle()
        // set fetch location interval
        mLocationStyle.interval(500)
        mLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE)
        mLocationStyle.showMyLocation(true)
        map.myLocationStyle = mLocationStyle
        map.isMyLocationEnabled = true
    }

    /**
     * draw target argument reality area
     */
    private fun drawArea() {
        val latlngs = arrayOf(LatLng(29.519142, 106.553157),
                LatLng(29.518722,106.554734), LatLng(29.520076,106.554702),
                LatLng(29.520818,106.553237), LatLng(29.520505,106.552352))
        val polygonOptions = PolygonOptions()
        polygonOptions.add(*latlngs)
        polygonOptions.strokeWidth(5f)
                .strokeColor(Color.argb(50, 1, 1,1))
                .fillColor(Color.argb(50, 0, 0,255))
        map.addPolygon(polygonOptions)
    }


    /**
     * draw target argument reality fence
     */
    private fun createFence() {
        val mGeoFenceClient = GeoFenceClient(applicationContext)
        mGeoFenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN or GeoFenceClient.GEOFENCE_OUT)
        val points = listOf(DPoint(29.519142, 106.553157), DPoint(29.518722,106.554734),
                DPoint(29.520076,106.554702), DPoint(29.520818,106.553237), DPoint(29.520505,106.552352))
        // create fence
        mGeoFenceClient.addGeoFence(points, "YFHY001")
        // add fence create result callback
        mGeoFenceClient.setGeoFenceListener { _, errorCode, _ ->
            if (errorCode == GeoFence.ADDGEOFENCE_SUCCESS) {
                Log.i(TAG, "add fence success")
            } else {
                Log.i(TAG, "add fence failed")
            }
        }
        // create PendingIntent
        mGeoFenceClient.createPendingIntent(GEO_FENCE_ACTION)

        val fenceBroadCastReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == GEO_FENCE_ACTION) {
                    // parse result
                    val bundle = intent.extras
                    val status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS)
                    val fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID)
                    val customId = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID)
                    // the trigger fence
                    val fence = bundle.getParcelable(GeoFence.BUNDLE_KEY_FENCE) as GeoFence
                }
            }
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(GEO_FENCE_ACTION)
        // register broadcast receiver
        registerReceiver(fenceBroadCastReceiver, intentFilter)
    }

    override fun onResume() {
        Log.i(TAG, "onResume")
        super.onResume()
        cameraPreviewer.start()
        map_view.onResume()
    }
    override fun onPause() {
        Log.i(TAG, "onPause")
        super.onPause()
        cameraPreviewer.stop()
        map_view.onPause()
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        super.onDestroy()
        cameraPreviewer.destroy()
        Sensey.getInstance().stop()
        map_view.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        map_view.onSaveInstanceState(outState)
    }
}
