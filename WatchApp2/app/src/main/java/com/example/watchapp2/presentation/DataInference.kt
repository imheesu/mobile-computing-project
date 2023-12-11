package com.example.watchapp2.presentation

import android.content.Context
import android.util.Log
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream

class DataInference (context: Context) {
    private var module: Module? = null
    private var sensorDataList = arrayListOf<SensorData>()
    private var dataCount : Int = 0

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
//            Log.d("DataInference debug", "Replacing lastSensorData with newSensorData")
        } else {
            // if the new sensor data has a different timestamp as the last one, then add the new one to the list
            sensorDataList.add(sensorData)
            dataCount += 1
//            Log.d("DataInference debug", "Adding newSensorData to the list")
        }
//        Log.d("DataInference debug", "sensorData #$dataCount: $sensorData")
    }

    fun resetSensorDataList() {
        sensorDataList.clear()
    }

    fun runInference() : FloatArray? {
        // run inference every 40 data points
        if (dataCount >= 80 && dataCount % 40 == 0) {
            Log.d("DataInference debug", "Running inference at dataCount: $dataCount")
            // data to be fed into the model is the last 80 data points
            val inferenceData = sensorDataList.takeLast(80)
            // get the largest acc and gyro values
            val normalizationFactor = findNormalizationFactor(inferenceData)
            // normalize the data
            val normalizedData = inferenceData.map { it.normalize(normalizationFactor[0], normalizationFactor[1]) }
            Log.d("DataInference debug", "Check whether normalized data is out: ${normalizedData[0]}")
            // convert the data into a (1, 80, 6) tensor
            val inputData = normalizedData.flatMap { it.getAccelerometer().toList() + it.getGyroscope().toList() }.toFloatArray()
            val inputTensor = Tensor.fromBlob(inputData, longArrayOf(1, 80, 6))
            // run inference
            val outputTensor = module!!.forward(IValue.from(inputTensor)).toTensor()
            Log.d("DataInference debug", "outputTensor: ${outputTensor.dataAsFloatArray.contentToString()}")
            val outputArray = outputTensor.dataAsFloatArray
            // get the predicted posture index
            val predictedResult = findPredictedPosture(outputArray)
            Log.d("DataInference debug", "outputArray: ${outputArray.contentToString()}")
            Log.d("DataInference debug", "predictedPosture: ${predictedResult.contentToString()}")
            return predictedResult
        }
        return null
    }

    private fun findPredictedPosture(outputArray: FloatArray): FloatArray {
        // find the index of the max value and its value in the output array
        var maxIndex = 0
        var maxValue = 0.0f
        for (i in outputArray.indices) {
            if (outputArray[i] > maxValue) {
                maxIndex = i
                maxValue = outputArray[i]
            }
        }
        return floatArrayOf(maxIndex.toFloat(), maxValue)
    }

    private fun findNormalizationFactor(dataset: List<SensorData>): FloatArray {
        // find the largest acc and gyro values in the sensor data list
        var maxAcc = -100.0f
        var maxGyro = -100.0f
        for (sensorData in dataset) {
            val acc = sensorData.getAccelerometer().maxOrNull() ?: maxGyro
            val gyro = sensorData.getGyroscope().maxOrNull() ?: maxAcc
            if (acc > maxAcc) {
                maxAcc = acc
            }
            if (gyro > maxGyro) {
                maxGyro = gyro
            }
        }
        return floatArrayOf(maxAcc, maxGyro)
    }

}