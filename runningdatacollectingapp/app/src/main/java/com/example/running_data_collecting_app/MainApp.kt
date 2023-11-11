package com.example.running_data_collecting_app

import OpenSettingsContent
import RationaleContent
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.running_data_collecting_app.components.SelectLabel
import com.example.running_data_collecting_app.data.Label
import com.example.running_data_collecting_app.utils.UiEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.*

private val PERMISSIONS = listOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
)

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun MainApp(
    viewModel: MainViewModel
) {
    val context = LocalContext.current

    val labels = viewModel.labels.collectAsState(initial = emptyList())
    val selectedLabel = viewModel.selectedLabel

    val snackbarHostState = remember { SnackbarHostState() }
    var isPermissionAlreadyRequested by rememberSaveable { mutableStateOf(false) }
    val permissionState = rememberMultiplePermissionsState(permissions = PERMISSIONS)

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { e ->
            when (e) {
                is UiEvent.ShowSnackBar -> {
                    val job = launch {
                        snackbarHostState.showSnackbar(
                            message = e.message,
                            actionLabel = e.action,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                    delay(750)
                    job.cancel()
                }

                else -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Running Data Collecting") },
                modifier = Modifier.background(color = Color.Gray)
            )
        },
    ) {
        if (permissionState.allPermissionsGranted) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(it)
            ) {
                Column(modifier = Modifier.weight(1.3f)) {
                    SelectLabel(
                        title = "Activity",
                        labels = labels.value,
                        selectedLabel = selectedLabel,
                        onEvent = { e -> viewModel.onActivityEvent(e) }
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    SelectLabel(
                        title = "Gender",
                        labels = listOf(Label("Male"), Label("Female")),
                        selectedLabel = viewModel.gender?.let { Label(it) },
                        hasButtons = false,
                        onEvent = { e -> viewModel.onGenderEvent(e) }
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.height(56.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Height", fontSize = 18.sp, modifier = Modifier.padding(4.dp)
                        )
                        TextField(
                            value = viewModel.height?.let { it.toString() } ?: "",
                            onValueChange = viewModel.handleHeightChange,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        )
                    }
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Column {
                        Text(text = "#Accelerometer: ${viewModel.accelerometerCount}")
                        Text(text = "#Gyroscope: ${viewModel.gyroscopeCount}")
                    }

                }
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row {

                    if (viewModel.isActivated) {
                        Button(
                            shape = RoundedCornerShape(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                            onClick = { viewModel.handleSaveButtonClick(context) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Save",
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            shape = RoundedCornerShape(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            onClick = viewModel.handleResetButtonClick,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Reset",
                            )
                        }
                    } else {
                        Button(
                            shape = RoundedCornerShape(0.dp),
                            enabled = !viewModel.isActivatedButtonDisabled,
                            onClick = viewModel.handleActivatedButtonClick,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Text(text = "Activate")
                        }
                    }
                }
            }
        } else {
            if (!isPermissionAlreadyRequested && !permissionState.shouldShowRationale) {
                SideEffect {
                    permissionState.launchMultiplePermissionRequest()
                    isPermissionAlreadyRequested = true
                }
            } else if (permissionState.shouldShowRationale) {
                RationaleContent(
                    permissionState = permissionState,
                    message = "Permission is needed to labeling the inertial info"
                ) {
                    permissionState.launchMultiplePermissionRequest()
                }

            } else {
                OpenSettingsContent {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri: Uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }
            }
        }
    }

}
