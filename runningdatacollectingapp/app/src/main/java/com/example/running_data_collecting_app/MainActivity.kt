package com.example.running_data_collecting_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.running_data_collecting_app.ui.theme.RunningdatacollectingappTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    val capabilityClient by lazy { Wearable.getCapabilityClient(this) }
    val messageClient by lazy { Wearable.getMessageClient(this) }

    val viewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    // Initial request for devices with our capability, aka, our Wear app installed.
                    findWearDevicesWithApp()
                }
            }
        }

        setContent {
            RunningdatacollectingappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(viewModel)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        capabilityClient.removeListener(viewModel, CAPABILITY_WEAR_APP)
        messageClient.removeListener(viewModel)
    }

    override fun onResume() {
        Log.d(TAG, "onResume()")
        super.onResume()
        capabilityClient.addListener(viewModel, CAPABILITY_WEAR_APP)
        messageClient.addListener(viewModel)
        viewModel.messageClient = messageClient
    }


    private suspend fun findWearDevicesWithApp() {
        Log.d(TAG, "findWearDevicesWithApp()")

        try {
            val capabilityInfo = capabilityClient
                .getCapability(CAPABILITY_WEAR_APP, CapabilityClient.FILTER_ALL)
                .await()

            withContext(Dispatchers.Main) {
                Log.d(TAG, "Capability request succeeded.")
                viewModel.wearNode = capabilityInfo.nodes.firstOrNull()
                Log.d(TAG, "Capable Node: $viewModel.wearNode")
            }
        } catch (cancellationException: CancellationException) {
            // Request was cancelled normally
            throw cancellationException
        } catch (throwable: Throwable) {
            Log.d(TAG, "Capability request failed to return any results.")
        }
    }

    companion object {
        const val TAG = "MainMobileActivity"

        const val CAPABILITY_WEAR_APP = "wear_app"
    }
}
