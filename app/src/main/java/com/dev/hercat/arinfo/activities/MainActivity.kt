package com.dev.hercat.arinfo.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.location.LocationClientOption.LocationMode
import com.blankj.utilcode.util.SnackbarUtils
import com.dev.hercat.arinfo.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {
    val TAG = "ACTIVITY_MAIN"

    private val mLocationClient = LocationClient(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initLocationClient()


    }

    private fun initLocationClient() {

        //<editor-fold desc="location client option">
        // set option
        val option = LocationClientOption()
        option.locationMode = LocationMode.Hight_Accuracy
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.coorType = "bd0911"
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.scanSpan = 5000
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.openGps = true
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.isLocationNotify = true
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.isIgnoreKillProcess = true
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.isIgnoreCacheException = false
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.wifiCacheTimeOut = 5 * 60 * 1000
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.enableSimulateGps = false
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setIsNeedAddress(true)



        mLocationClient.locOption = option
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

        //register location listener
        mLocationClient.registerLocationListener(BDLocationListener())

        //<editor-fold/>

        //check permission

        if (!checkPermission()) {
            getPermission(object : PermissionCallback {
                override fun result(granted: Boolean,
                                    permissionGranted: List<String>?,
                                    permissionDeniedForever: List<String>?,
                                    permissionDenied: List<String>?) {
                    if (granted) {
                        mLocationClient.start()
                    } else {
                        toast("""
                            以下权限被拒绝：
                            ${permissionDenied.toString()}
                            ${permissionDeniedForever.toString()}
                        """.trimIndent())
                    }
                }
            })
        } else {
            mLocationClient.start()
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
    }

    inner class BDLocationListener: BDAbstractLocationListener() {
        override fun onReceiveLocation(bdLocation: BDLocation?) {
            if (bdLocation is BDLocation) {
                val address =  bdLocation.addrStr
                val lat = bdLocation.latitude
                val lng = bdLocation.longitude
                val radius = bdLocation.radius
                val locType = bdLocation.locType
                tvLocation.text = """
                        address: $address
                        lat: $lat
                        lng: $lng
                        radius: $radius
                        locType: $locType
                        gps: ${bdLocation.gpsAccuracyStatus} ${bdLocation.gpsCheckStatus}
                    """.trimIndent()
            }
        }

        override fun onLocDiagnosticMessage(p0: Int, p1: Int, p2: String?) {
            super.onLocDiagnosticMessage(p0, p1, p2)
            tvLocation.text =  "p0 = [${p0}], p1 = [${p1}], p2 = [${p2}]"
        }

        override fun onConnectHotSpotMessage(p0: String?, p1: Int) {
            super.onConnectHotSpotMessage(p0, p1)
        }
    }
}
