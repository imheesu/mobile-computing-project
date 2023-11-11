package com.example.running_data_collecting_app


import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainViewModel(
    application: Application
) :
    AndroidViewModel(application),
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    var handHeldNode: Node? = null
    var messageClient by mutableStateOf<MessageClient?>(null)

    var hasPhoneApp by mutableStateOf(false)
        private set
    var isPhoneDataCollectingActivated by mutableStateOf(false)
        private set
    var isStarted by mutableStateOf(false)
    var accelerometerCount by mutableStateOf(0)
    var gyroscopeCount by mutableStateOf(0)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            "/activate" -> {
                isPhoneDataCollectingActivated = true
            }

            "/deactivate" -> {
                isPhoneDataCollectingActivated = false
                isStarted = false
                accelerometerCount = 0
                gyroscopeCount = 0
            }

            else -> {
                Log.d(MainActivity.TAG, "onMessageReceived(): Unknown path: ${messageEvent.path}")
            }
        }

        val msg = messageEvent.toString()
        Log.d(MainActivity.TAG, "onMessageReceived(): $msg")
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        Log.d(MainActivity.TAG, "onCapabilityChanged(): $capabilityInfo")
        handHeldNode = capabilityInfo.nodes.firstOrNull()
        handleCapabilityChanged()
    }

    val sendMessageToHandHeld = { path: String, data: String? ->
        val client = messageClient
        val nodeID = handHeldNode?.id
        Log.d("debug", "sendMessageToHandHeld(): $nodeID, $path, $data")
        if (nodeID != null && client != null) {
            client.sendMessage(
                nodeID, path, data?.toByteArray()
            )
        }
    }

    private fun handleCapabilityChanged() {
        if (handHeldNode != null) {
            Log.d(MainActivity.TAG, "Phone app found on node: $handHeldNode")
            hasPhoneApp = true


        } else {
            Log.d(MainActivity.TAG, "Phone app not found.")
            hasPhoneApp = false
        }
    }

    val handleGyroscopeData = { data: FloatArray ->
        if (isStarted) {
            val currTS = System.currentTimeMillis()
            val result = "${gyroscopeCount},${currTS},${data[0]},${data[1]},${data[2]}"
            gyroscopeCount += 1
            sendMessageToHandHeld("/gyroscope", result)
        }
    }

    val handleAccelerometerData = { data: FloatArray ->
        if (isStarted) {
            val currTS = System.currentTimeMillis()
            val result = "${accelerometerCount},${currTS},${data[0]},${data[1]},${data[2]}"
            accelerometerCount += 1
            sendMessageToHandHeld("/accelerometer", result)
        }
    }

}
