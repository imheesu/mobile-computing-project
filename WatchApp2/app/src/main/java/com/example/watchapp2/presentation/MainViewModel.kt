package com.example.watchapp2.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var isStarted by mutableStateOf(false)
    var runningPosture by mutableStateOf("Running...")
    var statusDetail by mutableStateOf("0.0")
    var isVibrated by mutableStateOf(false)

    @SuppressLint("StaticFieldLeak")
    val context: Context = application.applicationContext
    private var dataInference: DataInference = DataInference(context)
    private var labels = arrayOf("Abnormal arm swing", "Abnormal stride", "Abnormal upper body", "Good posture")
    private var prevPredictedClass:Int? = null

    fun handleSensorData(accelData: FloatArray, gyroData: FloatArray, timestamp: Long) {
        val newSensorData = SensorData(timestamp, accelData, gyroData)
        dataInference.addSensorData(newSensorData)
        val result = dataInference.runInference()
        if (result != null) {
            val predictedClass = result[0].toInt()
            if (prevPredictedClass!=null && prevPredictedClass == predictedClass) {
                isVibrated = predictedClass != 3
                runningPosture = labels[predictedClass]
                // only represent the 3 significant digits
                val decimalFormat = "%.3f"
                statusDetail = decimalFormat.format(result[1])
            }
            prevPredictedClass = predictedClass
//            isVibrated = predictedClass != 3
//            runningPosture = labels[predictedClass]
//            // only represent the 3 significant digits
//            val decimalFormat = "%.3f"
//            statusDetail = decimalFormat.format(result[1])
        }
    }

    fun resetSensorDataList() {
        dataInference.resetSensorDataList()
    }
}