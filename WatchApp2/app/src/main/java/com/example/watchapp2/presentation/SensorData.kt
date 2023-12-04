package com.example.watchapp2.presentation

class SensorData(private var timestamp: Long?, private var accelerometer: FloatArray?, private var gyroscope: FloatArray?) {

    fun getAccelerometer(): FloatArray? {
        return accelerometer
    }

    fun getGyroscope(): FloatArray? {
        return gyroscope
    }

    fun getTimestamp(): Long? {
        return timestamp
    }

    // return the data in the format of a string
    override fun toString(): String {
        return "acc: ${accelerometer?.contentToString()}, gyro: ${gyroscope?.contentToString()}"
    }
}