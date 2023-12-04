package com.example.watchapp2.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel()  {
    var isStarted by mutableStateOf(false)
    var runningPosture by mutableStateOf("undefined")
    var statusDetail by mutableStateOf("")
    // create an arrayList to store the data
    private var sensorDataList = arrayListOf<SensorData>()

    fun handleSensorData(accelData: FloatArray, gyroData: FloatArray, timestamp: Long) {
        val newSensorData = SensorData(timestamp, accelData, gyroData)
        // check if the new sensor data has the same timestamp as the last one
        if (sensorDataList.size > 0 && newSensorData.getTimestamp() == sensorDataList.last().getTimestamp()) {
            // if the new sensor data has the same timestamp as the last one, then replace the last one with the new one
            sensorDataList[sensorDataList.size - 1] = newSensorData
        } else {
            // if the new sensor data has a different timestamp as the last one, then add the new one to the list
            sensorDataList.add(newSensorData)
        }
    }

    fun resetSensorDataList() {
        sensorDataList.clear()
    }
}