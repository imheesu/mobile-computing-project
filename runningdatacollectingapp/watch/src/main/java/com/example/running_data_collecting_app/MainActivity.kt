package com.example.running_data_collecting_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }

    private val viewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp(viewModel)
        }
    }

    override fun onPause() {
        super.onPause()
        messageClient.removeListener(viewModel)
        capabilityClient.removeListener(viewModel, CAPABILITY_PHONE_APP)
        viewModel.messageClient = null
    }

    override fun onResume() {
        super.onResume()

        messageClient.addListener(viewModel)
        capabilityClient.addListener(viewModel, CAPABILITY_PHONE_APP)
        viewModel.messageClient = messageClient

        lifecycleScope.launch {
            checkIfPhoneHasApp()
        }
    }

    private suspend fun checkIfPhoneHasApp() {
        Log.d(TAG, "checkIfPhoneHasApp()")

        try {
            val capabilityInfo = capabilityClient.getCapability(
                CAPABILITY_PHONE_APP,
                CapabilityClient.FILTER_ALL
            ).await()

            Log.d(TAG, "Capability request succeeded.")

            withContext(Dispatchers.Main) {
                // There should only ever be one phone in a node set (much less w/ the correct
                // capability), so I am just grabbing the first one (which should be the only one).
                viewModel.onCapabilityChanged(capabilityInfo)
            }
        } catch (cancellationException: CancellationException) {
            // Request was cancelled normally
        } catch (throwable: Throwable) {
            Log.d(TAG, "Capability request failed to return any results.")
        }
    }


    companion object {
        const val TAG = "MainMobileActivity"
        const val CAPABILITY_PHONE_APP = "phone_app"
    }
}

