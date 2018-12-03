package com.dev.hercat.arinfo.activities

import android.app.Activity
import android.content.res.Resources
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.blankj.utilcode.util.PermissionUtils

interface PermissionCallback {
    fun result(granted: Boolean,
               permissionGranted: List<String>?,
               permissionDeniedForever: List<String>?,
               permissionDenied: List<String>?)
}
fun Activity.checkPermission(): Boolean {
    val permissions = PermissionUtils.getPermissions()
    return PermissionUtils.isGranted(*permissions.toTypedArray())
}

fun Activity.getPermission(callback: PermissionCallback) {
    PermissionUtils.permission(*PermissionUtils.getPermissions().toTypedArray())
            .rationale {shouldRequest ->
                shouldRequest.again(true)}
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: MutableList<String>?) {
                    callback.result(granted = true,
                            permissionGranted = permissionsGranted,
                            permissionDeniedForever = null,
                            permissionDenied = null)
                }

                override fun onDenied(permissionsDeniedForever: MutableList<String>?, permissionsDenied: MutableList<String>?) {
                    callback.result(granted = false,
                            permissionGranted = null,
                            permissionDeniedForever = permissionsDeniedForever,
                            permissionDenied = permissionsDenied)
                }
            })
            .request()
}



val Activity.locationClientConfiguratiion: AMapLocationClientOption
    get() {
        val configuration = AMapLocationClientOption()
        //选择定位场景 运动模式
        configuration.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Sport
        //定位模式 高精度模式
        configuration.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //定位间隔 1000ms
        configuration.interval = 5000
        //需要地址描述
        configuration.isNeedAddress = true
        //定位超时 30s
        configuration.httpTimeOut = 30000
        configuration.isOnceLocation = false
        return configuration
    }
