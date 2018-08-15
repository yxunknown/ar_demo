package com.dev.hercat.arinfo.activities

import android.app.Activity
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