package com.dev.hercat.arinfo.application

import android.app.Application
import com.blankj.utilcode.util.Utils

class ARApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}