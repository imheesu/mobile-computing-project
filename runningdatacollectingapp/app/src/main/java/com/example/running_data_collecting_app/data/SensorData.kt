package com.example.running_data_collecting_app.data

data class InertialSensorData(
    val gyroscope: MutableList<String>,
    val accelerometer: MutableList<String>,
    val label: Label,
)