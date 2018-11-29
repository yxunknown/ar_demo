package com.dev.hercat.arinfo

const val PI = Math.PI

/**
 * line two gps coordinate and calculate the direction angel between the line and the North direction
 *
 * @param lat1 lat of point1
 * @param lng1 lng of point1
 * @param lat2 lat of point2
 * @param lng2 lng of point2
 * @return the direction angel
 */
fun rotation(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    var lat1 = lat1
    var lon1 = lon1
    var lat2 = lat2
    var lon2 = lon2
    //convert to radians
    lat1 = Math.toRadians(lat1)
    lat2 = Math.toRadians(lat2)
    lon1 = Math.toRadians(lon1)
    lon2 = Math.toRadians(lon2)
    val deltaFI = Math.log(Math.tan(lat2 / 2 + PI / 4) / Math.tan(lat1 / 2 + PI / 4))
    val deltaLON = Math.abs(lon1 - lon2) % 180
    val theta = Math.atan2(deltaLON, deltaFI)
    return if (lon2 - lon1 > 0) {
        Math.toDegrees(theta)
    } else {
        360 - Math.toDegrees(theta)
    }
}