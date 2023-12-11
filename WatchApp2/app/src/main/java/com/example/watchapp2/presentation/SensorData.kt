package com.example.watchapp2.presentation

class SensorData(private var timestamp: Long, private var accelerometer: FloatArray, private var gyroscope: FloatArray) {

    fun getAccelerometer(): FloatArray {
        return accelerometer
    }

    fun getGyroscope(): FloatArray {
        return gyroscope
    }

    fun getTimestamp(): Long {
        return timestamp
    }

    fun normalize(acc: Float, gyro: Float): SensorData {
        // normalize the accelerometer and gyroscope data, using acc and gyro as the normalization factor
        val normalizedAcc = accelerometer.map { it / acc }.toFloatArray()
        val normalizedGyro = gyroscope.map { it / gyro }.toFloatArray()
        return SensorData(timestamp, normalizedAcc, normalizedGyro)
    }

    // return the data in the format of a string
    override fun toString(): String {
        return "acc: ${accelerometer.contentToString()}, gyro: ${gyroscope.contentToString()}"
    }
}