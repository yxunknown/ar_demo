package com.dev.hercat.arinfo.application

import android.app.Application
import com.amap.api.location.AMapLocationClient
import com.autonavi.aps.amapapi.model.AMapLocationServer
import com.blankj.utilcode.util.Utils
import com.github.nisrulz.sensey.Sensey

class ARApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        Sensey.getInstance().init(this, Sensey.SAMPLING_PERIOD_FASTEST)
        AMapLocationClient.setApiKey("194da61bc8d18ee4c4ad82b8e05673c3")
    }
}