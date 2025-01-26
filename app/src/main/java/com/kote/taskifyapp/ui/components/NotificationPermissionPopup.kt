package com.kote.taskifyapp.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kote.taskifyapp.R

@Composable
fun ShowPermissionDialog(
    context: Context,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.alert_permission_title)) },
        text = { Text(stringResource(R.string.alert_permission_text)) },
        dismissButton = { Button(onClick = { onDismissRequest() }) { Text(stringResource(R.string.alert_permission_dismiss)) } },
        confirmButton = {
            Button(onClick = {
                    onDismissRequest()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            ) { Text(stringResource(R.string.alert_permission_confirm)) }
        }
    )
}