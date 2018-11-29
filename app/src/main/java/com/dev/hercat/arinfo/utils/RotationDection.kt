package com.dev.hercat.arinfo.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class RotationDection(val contenxt: Context) {

    private val mSensorManager = contenxt.getSystemService(Context.SENSOR_SERVICE) as SensorManager

}

class RotationSensorEventListener: SensorEventListener {
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {

    }
}

interface RotationListener {
    fun onRotationChanged(x: Float, y: Float, z: Float)
}