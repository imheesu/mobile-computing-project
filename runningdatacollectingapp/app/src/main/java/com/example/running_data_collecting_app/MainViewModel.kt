package com.example.running_data_collecting_app


import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.running_data_collecting_app.data.InertialSensorData
import com.example.running_data_collecting_app.data.InertialSensorDatabase
import com.example.running_data_collecting_app.data.InertialSensorRepository
import com.example.running_data_collecting_app.data.InertialSensorRepositoryImpl
import com.example.running_data_collecting_app.data.Label
import com.example.running_data_collecting_app.utils.LabelEvent
import com.example.running_data_collecting_app.utils.UiEvent
import com.example.running_data_collecting_app.utils.saveCsv
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object ServiceLocator {
    fun getRepository(app: Application): InertialSensorRepository {
        val db = Room.databaseBuilder(app, InertialSensorDatabase::class.java, "inertial_sensor_db")
            .build()

        runBlocking {
            launch {
                db.dao.insertLabel(Label("good_running_posture"))
                db.dao.insertLabel(Label("abnormal_arm_swing"))
                db.dao.insertLabel(Label("abnormal_stride"))
                db.dao.insertLabel(Label("abnormal_upper_body"))
            }
        }

        return InertialSensorRepositoryImpl(db.dao)
    }
}

@ExperimentalPermissionsApi
class MainViewModel(
    application: Application,
) : AndroidViewModel(application), MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    val repository = ServiceLocator.getRepository(application)
    val labels = repository.getLabels()

    var wearNode by mutableStateOf<Node?>(null)
    var messageClient by mutableStateOf<MessageClient?>(null)

    var selectedLabel by mutableStateOf<Label?>(null)
        private set
    var gender by mutableStateOf<String?>(null)
        private set
    var height by mutableStateOf<Number?>(null)
        private set
    val isActivatedButtonDisabled: Boolean
        get() = selectedLabel == null || gender == null || height == null
    var isActivated by mutableStateOf(false)
        private set
    var inertialSensorData by mutableStateOf<InertialSensorData?>(
        null
    )

    var accelerometerCount by mutableStateOf(0)
        private set
    var gyroscopeCount by mutableStateOf(0)
        private set


    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        Log.d(MainActivity.TAG, "onCapabilityChanged(): $capabilityInfo")
        wearNode = capabilityInfo.nodes.firstOrNull()
    }

    val sendMessageToWearable = { path: String, data: String? ->
        val client = messageClient
        val nodeID = wearNode?.id
        if (nodeID != null && client != null) {
            client.sendMessage(
                nodeID, path, data?.toByteArray()
            )
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            "/accelerometer" -> {
                inertialSensorData?.let {
                    it.accelerometer.add(messageEvent.data.toString(Charsets.UTF_8))
                    accelerometerCount++
                }
            }

            "/gyroscope" -> {
                inertialSensorData?.let {
                    it.gyroscope.add(messageEvent.data.toString(Charsets.UTF_8))
                    gyroscopeCount++
                }
            }

            else -> {
                Log.d(MainActivity.TAG, "onMessageReceived(): Unknown path: ${messageEvent.path}")
            }
        }
        val msg = messageEvent.data.toString(Charsets.UTF_8)
        Log.d(MainActivity.TAG, "onMessageReceived(): $msg")
    }


    val handleHeightChange = { height: String ->
        if (height.isNotEmpty()) {
            this.height = height.trim().toInt()
        } else {
            this.height = null
        }
    }

    val handleActivatedButtonClick = {
        isActivated = true
        inertialSensorData = InertialSensorData(
            label = selectedLabel!!,
            gyroscope = mutableListOf(),
            accelerometer = mutableListOf(),
        )
        sendMessageToWearable("/activate", null)

    }

    val handleResetButtonClick = {
        isActivated = false
        sendMessageToWearable("/deactivate", null)
        inertialSensorData = null
        accelerometerCount = 0
        gyroscopeCount = 0
    }

    val handleSaveButtonClick = { context: Context ->
        isActivated = false
        handleSaveCapturedData(context)
        sendMessageToWearable("/deactivate", null)
        inertialSensorData = null
    }

    val handleSaveCapturedData = { context: Context ->
        viewModelScope.launch {
            inertialSensorData?.let {
                val t = System.currentTimeMillis()

                fun save(
                    name: String,
                    data: MutableList<String>,
                ) {
                    val metadata =
                        listOf("gender", gender, "height", height.toString()).joinToString(",")
                    val header = listOf("count", "timestamp", "x", "y", "z").joinToString(",")
                    saveCsv(
                        context,
                        listOf(it.label.name, t.toString()),
                        name,
                        metadata + "\n" + header + "\n" + data.joinToString("\n"),
                    )
                }
                save("accelerometer", it.accelerometer)
                save("gyroscope", it.gyroscope)

                sendUiEvent(
                    UiEvent.ShowSnackBar(
                        "저장되었습니다.",
                    )
                )
            }
        }
    }


    fun onActivityEvent(e: LabelEvent) {
        when (e) {
            is LabelEvent.SelectLabel -> {
                selectedLabel = e.label
            }

            is LabelEvent.AddLabel -> {
                selectedLabel = e.label
                viewModelScope.launch {
                    repository.insertLabel(
                        e.label
                    )
                }
            }

            is LabelEvent.DeleteLabel -> {
                viewModelScope.launch {
                    repository.deleteLabel(
                        e.label
                    )
                }
                selectedLabel = null
            }

            else -> Unit
        }
    }

    fun onGenderEvent(e: LabelEvent) {
        when (e) {
            is LabelEvent.SelectLabel -> {
                gender = e.label.name
            }

            else -> Unit
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}

