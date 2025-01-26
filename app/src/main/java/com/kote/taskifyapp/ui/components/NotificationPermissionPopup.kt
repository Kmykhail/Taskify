package com.kote.taskifyapp.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ShowNotificationDialog(
    ctx: Context,
    onDismissDialog: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissDialog(false) },
        title = { Text("Notification permission required") },
        text = {
            Text(text = "This app requires notification permission to function properly. Please enable it in settings)")
        },
        dismissButton = {
            Button(onClick = {
                onDismissDialog(false)
            }) { Text("Skip") }
        },
        confirmButton = {
            Button(onClick = {
                    onDismissDialog(false)
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", ctx.packageName, null)
                    }
                    ctx.startActivity(intent)
                }
            ) { Text("Settings") }
        }
    )
}