package com.dev.hercat.arinfo.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.dev.hercat.arinfo.R
import com.github.nisrulz.sensey.Sensey
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val TAG = "ACTIVITY_MAIN"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        Sensey.getInstance().startRotationAngleDetection { x: Float, y: Float, z: Float ->
            //            println("$x $y $z")
            tvRotation.text = "${((x + z).toInt() + 360) % 360}"
        }

    }

    override fun onResume() {
        Log.i(TAG, "onResume")
        super.onResume()
//        cameraPreviewer.start()


    }
    override fun onPause() {
        Log.i(TAG, "onPause")
        super.onPause()
//        cameraPreviewer.stop()
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        super.onDestroy()
//        cameraPreviewer.destroy()
    }

    inner class LocationListener: AMapLocationListener {
        override fun onLocationChanged(aMapLocation: AMapLocation?) {

        }
    }
}
