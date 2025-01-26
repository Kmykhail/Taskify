package com.kote.taskifyapp.ui.components

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import com.kote.taskifyapp.data.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenTimerPicker(
    task: Task,
    updateTimerPicker: (Boolean) -> Unit,
    onSelectTime: (Int) -> Unit,
) {

    val timePickerState = rememberTimePickerState(
        is24Hour = true,
        initialHour = task.time?.div(60) ?: 0,
        initialMinute = task.time?.rem(60) ?: 0
    )

    AlertDialog(
        onDismissRequest = { updateTimerPicker(false) },
        dismissButton = {
            TextButton(onClick = { updateTimerPicker(false) }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    Log.d("Debug", "current time: ${timePickerState.hour}:${timePickerState.minute}")
                    onSelectTime(timePickerState.hour * 60 + timePickerState.minute)
                    updateTimerPicker(false)
                }
            ) { Text("OK") }
        },
        text = { TimePicker(state = timePickerState) }
    )
}