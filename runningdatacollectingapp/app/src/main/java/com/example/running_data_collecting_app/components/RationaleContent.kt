import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@ExperimentalPermissionsApi
@Composable
fun RationaleContent(
    permissionState: MultiplePermissionsState,
    message: String,
    onPermissionRequest: () -> Unit
) {
    if (permissionState.shouldShowRationale) {
        // This composable will show only if we should show rationale
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(message)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                onPermissionRequest()
            }) {
                Text("Grant Permission")
            }
        }
    }
}
