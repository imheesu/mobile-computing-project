package com.example.watchapp2.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.File
import java.io.FileOutputStream

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var isStarted by mutableStateOf(false)
    var runningPosture by mutableStateOf("undefined")
    var statusDetail by mutableStateOf("")

    @SuppressLint("StaticFieldLeak")
    val context: Context = application.applicationContext
    private var dataInference: DataInference = DataInference(context)

    fun handleSensorData(accelData: FloatArray, gyroData: FloatArray, timestamp: Long) {
        val newSensorData = SensorData(timestamp, accelData, gyroData)
        dataInference.addSensorData(newSensorData)
        dataInference.runInference()
    }

    fun resetSensorDataList() {
        dataInference.resetSensorDataList()
    }
}