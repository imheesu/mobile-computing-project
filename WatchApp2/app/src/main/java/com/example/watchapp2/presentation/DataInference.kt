package com.example.watchapp2.presentation

import android.content.Context
import android.util.Log
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.File
import java.io.FileOutputStream

class DataInference (context: Context) {
    private var module: Module? = null
    private var sensorDataList = arrayListOf<SensorData>()

    init {
        if (module == null) {
            loadModel(context)
        }
    }

    private fun loadModel(context: Context) {
        val moduleFileAbsoluteFilePath = File(context.filesDir, "optimized_model.ptl").absolutePath
        Log.d("MainUI debug", "moduleFileAbsoluteFilePath: $moduleFileAbsoluteFilePath")
        val moduleFile = File(moduleFileAbsoluteFilePath)
        if (!moduleFile.exists()) {
            Log.d("MainUI debug", "Model not found, copying now")
            val moduleAssetName = "optimized_model.ptl"
            val assets = context.assets
            val buffer = assets.open(moduleAssetName).readBytes()
            val out = FileOutputStream(moduleFile)
            out.write(buffer)
            out.close()
        }
        try {
            module = LiteModuleLoader.load(moduleFileAbsoluteFilePath)
            Log.d("DataInference debug", "Model loaded")
        } catch (e: Exception) {
            Log.d("DataInference debug", "Model not loaded")
            e.printStackTrace()
        }
    }

    fun addSensorData(sensorData: SensorData) {
        // check if the new sensor data has the same timestamp as the last one
        if (sensorDataList.size > 0 && sensorData.getTimestamp() == sensorDataList.last().getTimestamp()) {
            // if the new sensor data has the same timestamp as the last one, then replace the last one with the new one
            sensorDataList[sensorDataList.size - 1] = sensorData
            Log.d("DataInference debug", "Replacing lastSensorData with newSensorData")
        } else {
            // if the new sensor data has a different timestamp as the last one, then add the new one to the list
            sensorDataList.add(sensorData)
            Log.d("DataInference debug", "Adding newSensorData to the list")
        }
    }

    fun resetSensorDataList() {
        sensorDataList.clear()
    }

    fun runInference() {

    }


}