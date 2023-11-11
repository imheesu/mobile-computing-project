package com.example.watchapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.watchapp.presentation.theme.RunningdatacollectingappTheme

@Composable
fun MainApp(hasPhoneApp: Boolean, isStarted: Boolean, isDirty: Boolean) {
    RunningdatacollectingappTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            if (hasPhoneApp) {
                if (isStarted) {
                    Button(onClick = {}) {
                        Text(text = "Stop")
                    }

                } else {
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Start")
                    }
                    if (isDirty) {
                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "Reset")
                        }
                        Button(onClick = {}) {
                            Text(text = "Upload")
                        }
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

