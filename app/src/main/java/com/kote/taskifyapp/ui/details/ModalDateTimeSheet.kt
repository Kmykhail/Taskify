package com.kote.taskifyapp.ui.details

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.ui.components.OpenTimerPicker
import com.kote.taskifyapp.ui.components.ShowPermissionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDateTimeSheet(
    task: Task,
    onDateChange: (Long?) -> Unit,
    onTimeChange: (Int?) -> Unit,
    removeReminder: (id: Int) -> Unit,
    onDismissRequest: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // datePicker
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = task.date ?: System.currentTimeMillis()
    )
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // timerPicker
    var openTimerPicker by remember { mutableStateOf(false) }
    var selectedTime: Int? = task.time
    val timePickerState = rememberTimePickerState(
        is24Hour = true,
        initialHour = task.time?.div(60) ?: 0,
        initialMinute = task.time?.rem(60) ?: 0
    )

    // notification permission
    val context = LocalContext.current
    val permission = Manifest.permission.POST_NOTIFICATIONS
    var showPermissionDialog by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openTimerPicker = !openTimerPicker
        } else {
            showPermissionDialog = true
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest(false) },
        sheetState = bottomSheetState,
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = {
                onDismissRequest(false)
                datePickerState.selectedDateMillis = System.currentTimeMillis()
                selectedTime = null
            }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                onDateChange(datePickerState.selectedDateMillis)
                onTimeChange(selectedTime)
                onDismissRequest(false)
            }) { Icon(imageVector = Icons.Default.Done, contentDescription = "Apply") }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DatePicker(state = datePickerState)
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color = Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when {
                                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                                    openTimerPicker = !openTimerPicker
                                }
                                ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission) -> {
                                    showPermissionDialog = true
                                }
                                else -> {
                                    permissionLauncher.launch(permission)
                                }
                            }
                        }
                ) {
                    Icon(imageVector = Icons.Default.Timelapse, contentDescription = "Duration Time")
                    Text("Time")
                    Spacer(modifier = Modifier.weight(1f))
                    if (selectedTime != null) {
                        Text(text = String.format("%02d:%02d", selectedTime!! / 60, selectedTime!!  % 60))
                        IconButton(onClick = {
                            onTimeChange(null)
                            removeReminder(task.id)
                        }) { Icon(imageVector = Icons.Default.Close, contentDescription = "Remove reminder") }
                    }
                }
            }
        }

        if (showPermissionDialog) {
            ShowPermissionDialog(ctx = context, onDismissDialog = {showPermissionDialog = it})
        }

        if (openTimerPicker) {
            OpenTimerPicker(timePickerState, updateTimerPicker = {openTimerPicker = it}, onSelectTime = {selectedTime = it})
        }
    }
}
