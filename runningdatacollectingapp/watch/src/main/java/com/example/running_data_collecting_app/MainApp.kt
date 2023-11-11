package com.example.running_data_collecting_app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.running_data_collecting_app.presentation.theme.RunningdatacollectingappTheme

@Composable
fun MainApp(viewModel: MainViewModel) {
    val context = LocalContext.current

    val hasPhoneApp = viewModel.hasPhoneApp
    val isStarted = viewModel.isStarted
    val isActivated = viewModel.isPhoneDataCollectingActivated

    val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {


                Sensor.TYPE_ACCELEROMETER -> {
                    val accelerometer = event.values
                    viewModel.handleAccelerometerData(accelerometer)
                }

                Sensor.TYPE_GYROSCOPE -> {
                    val gyroscope = event.values
                    viewModel.handleGyroscopeData(gyroscope)
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscopeSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val magneticSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (accelerometerSensor != null) {
            sensorManager.registerListener(
                sensorEventListener,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }


        if (gyroscopeSensor != null) {
            sensorManager.registerListener(
                sensorEventListener,
                gyroscopeSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }


        if (magneticSensor != null) {
            sensorManager.registerListener(
                sensorEventListener,
                magneticSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }


    RunningdatacollectingappTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
        ) {
            if (hasPhoneApp) {
                if (isStarted) {
                    Button(onClick = {
                        viewModel.isStarted = false
                    }) {
                        Text(text = "Stop")
                    }
                    Text(text = "#Accelerometer: ${viewModel.accelerometerCount}")
                    Text(text = "#Gyroscope: ${viewModel.gyroscopeCount}")
                } else {
                    Button(
                        enabled = isActivated,
                        onClick = { viewModel.isStarted = true }
                    ) {
                        Text(text = "Start")
                    }
                }
            } else {
                Text(
                    text = "Phone app not found.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

