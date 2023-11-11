package com.example.watchapp.presentation


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

    private val _events = mutableStateListOf<Event>()
    var androidPhoneNodeWithApp: Node? = null

    var hasPhoneApp by mutableStateOf(false)
        private set

    /**
     * The list of events from the clients.
     */
    val events: List<Event> = _events

    override fun onMessageReceived(messageEvent: MessageEvent) {
        _events.add(
            Event(
                title = "Message",
                text = messageEvent.toString()
            )
        )
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        Log.d(MainActivity.TAG, "onCapabilityChanged(): $capabilityInfo")
        androidPhoneNodeWithApp = capabilityInfo.nodes.firstOrNull()
        handleCapabilityChanged()
    }

    public fun handleCapabilityChanged() {
        val androidPhoneNodeWithApp = androidPhoneNodeWithApp
        if (androidPhoneNodeWithApp != null) {
            Log.d(MainActivity.TAG, "Phone app found on node: $androidPhoneNodeWithApp")
            hasPhoneApp = true


        } else {
            Log.d(MainActivity.TAG, "Phone app not found.")
            hasPhoneApp = false
        }
    }


}

/**
 * A data holder describing a client event.
 */
data class Event(
    val title: String,
    val text: String
)