package com.example.watchapp2.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.watchapp2.presentation.theme.AppTheme

@Composable
fun MainUI(viewModel: MainViewModel) {
    val isStarted = viewModel.isStarted
    val context = LocalContext.current

    // float array to store accelerometer data
    var accelerometerData = floatArrayOf(0f, 0f, 0f)
    var gyroscopeData = floatArrayOf(0f, 0f, 0f)

    // Vibrate the watch if the posture is abnormal
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (viewModel.isVibrated) {
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        viewModel.isVibrated = false
    }

    // Add sensor event listener
    val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent) {
            val timestamp = event.timestamp
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val accelerometer = event.values
                    accelerometerData = accelerometer
//                    Log.d("MainUI debug", "Accelerometer changed at $timestamp!")
                }

                Sensor.TYPE_GYROSCOPE -> {
                    val gyroscope = event.values
                    gyroscopeData = gyroscope
                }
            }
            viewModel.handleSensorData(accelerometerData, gyroscopeData, timestamp)

        }
    }

    // Register sensor event listener (if MainUI recomposes, it won't register again)
    DisposableEffect(isStarted) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscopeSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        Log.d("MainUI debug", "isStarted: $isStarted")
        if (isStarted) {
            if (accelerometerSensor != null) {
                sensorManager.registerListener(
                    sensorEventListener,
                    accelerometerSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }

            if (gyroscopeSensor != null) {
                sensorManager.registerListener(
                    sensorEventListener,
                    gyroscopeSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        } else {
            Log.d("MainUI debug", "Unregistering sensor event listener")
            sensorManager.unregisterListener(sensorEventListener, accelerometerSensor)
            sensorManager.unregisterListener(sensorEventListener, gyroscopeSensor)
            viewModel.resetSensorDataList()
        }

        // Cleanup: Unregister the listener when the LaunchedEffect is disposed
        onDispose {
            sensorManager.unregisterListener(sensorEventListener, accelerometerSensor)
            sensorManager.unregisterListener(sensorEventListener, gyroscopeSensor)
            viewModel.resetSensorDataList()
        }
    }




    AppTheme {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
        ) {
            if (isStarted) {
                Text(text = viewModel.runningPosture)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {viewModel.isStarted = false}) {
                    Text(text = "Stop")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Confidence: ${viewModel.statusDetail}")
            } else {
                Text(text = "Start running!")
                // add a space between text and button
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.isStarted = true }) {
                    Text(text = "Start")
                }
            }
        }
    }
}
