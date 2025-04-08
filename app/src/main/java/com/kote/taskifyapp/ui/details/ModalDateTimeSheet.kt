@file:OptIn(ExperimentalMaterial3Api::class)

package com.kote.taskifyapp.ui.details

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import androidx.compose.material.icons.filled.AccessAlarms
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kote.taskifyapp.R
import com.kote.taskifyapp.data.ReminderType
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.ui.components.ShowPermissionDialog
import com.kote.taskifyapp.ui.components.ShowReminderDialog
import com.kote.taskifyapp.ui.components.TimerPickerDialog
import java.time.LocalDate
import java.time.ZoneOffset

@SuppressLint("InlinedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDateTimeSheet(
    task: Task,
    onDateChange: (Long?) -> Unit,
    onTimeChange: (Int?) -> Unit,
    onReminderChange: (ReminderType) -> Unit,
    onDismissRequest: (Boolean) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedDate by remember { mutableStateOf(task.date) }
    var selectedTime by remember { mutableStateOf(task.time) }
    var selectedReminderType by remember { mutableStateOf(task.reminderType) }

    val currentDateInMillis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    ModalBottomSheet(
        onDismissRequest = {onDismissRequest(false)},
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        TopSheetBar(
            onCloseClick = {
                onDismissRequest(false)
            },
            onApplyClick = {
                onDismissRequest(false)
                onDateChange(selectedDate)
                onTimeChange(selectedTime)
                onReminderChange(selectedReminderType)
            }
        )
        SheetContent(
            task = task,
            selectedTime = selectedTime,
            selectedReminderType = selectedReminderType,
            currentDateInMillis = currentDateInMillis,
            onDateChange = { selectedDate = it },
            onTimeChange = { selectedTime = it },
            onReminderChange = { selectedReminderType = it }
        )
    }
}

@Composable
private fun SheetContent(
    task: Task,
    selectedTime: Int?,
    selectedReminderType: ReminderType,
    currentDateInMillis: Long,
    onDateChange: (Long?) -> Unit,
    onTimeChange: (Int?) -> Unit,
    onReminderChange: (ReminderType) -> Unit
) {
    val context = LocalContext.current

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = task.date ?: currentDateInMillis, initialDisplayMode = DisplayMode.Picker)
    val timePickerState = rememberTimePickerState(initialHour = task.time?.div(60) ?: 0, initialMinute = task.time?.rem(60) ?: 0, is24Hour = true)

    var showTimePickerDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        onDateChange(datePickerState.selectedDateMillis)
    }

    if (showPermissionDialog) {
        ShowPermissionDialog(context, onDismissRequest = { showPermissionDialog = false })
    }

    if (showTimePickerDialog) {
        TimerPickerDialog(
            timePickerState = timePickerState,
            onDismissRequest = { showTimePickerDialog = false },
            onConfirmation = {
                onTimeChange(timePickerState.hour * 60 + timePickerState.minute)
                onReminderChange(ReminderType.OnTime)
                showTimePickerDialog = false
            }
        )
    }

    if (showReminderDialog) {
        ShowReminderDialog(
            reminderType = selectedReminderType,
            onConfirmation = {
                onReminderChange(it)
                showReminderDialog = false
            },
            onDismissRequest = { showReminderDialog = false }
        )
    }

    DatePicker(
        state = datePickerState,
        colors = DatePickerDefaults.colors(
            selectedDayContainerColor = if (datePickerState.selectedDateMillis != null && datePickerState.selectedDateMillis!! >=  currentDateInMillis) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            todayDateBorderColor = MaterialTheme.colorScheme.primary
        )
    )

    ReminderTimeControl(
        context = context,
        selectedTime = selectedTime,
        selectedReminderType = selectedReminderType,
        onRemoveTime = {
            onTimeChange(null)
            onReminderChange(ReminderType.None)
        },
        onRemoveReminder = { onReminderChange(ReminderType.None) },
        onShowTimePickerDialogChange = { showTimePickerDialog = true },
        onShowPermissionDialogChange = { showPermissionDialog = true },
        onShowReminderDialogChange = { showReminderDialog = true }
    )
}

@SuppressLint("InlinedApi")
@Composable
private fun ReminderTimeControl(
    context: Context,
    selectedTime: Int?,
    selectedReminderType: ReminderType,
    onRemoveTime: () -> Unit,
    onRemoveReminder: () -> Unit,
    onShowTimePickerDialogChange: () -> Unit,
    onShowPermissionDialogChange: () -> Unit,
    onShowReminderDialogChange: () -> Unit
) {
    var onPermissionGranted by remember { mutableStateOf<(() -> Unit)?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted?.invoke()
        } else {
            onShowPermissionDialogChange()
        }
    }

    fun handleClick(action: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> action()
            ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.POST_NOTIFICATIONS) -> onShowPermissionDialogChange()
            else -> {
                onPermissionGranted = action
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        ReminderRow(
            label = stringResource(R.string.time),
            icon = Icons.Default.AccessTime,
            isSelected = selectedTime != null,
            selectedText = selectedTime?.let { String.format("%02d:%02d", it / 60, it % 60) },
            onClick = { handleClick(onShowTimePickerDialogChange) },
            onRemove = selectedTime?.let { onRemoveTime }
        )

        ReminderRow(
            label = stringResource(R.string.reminder),
            icon = Icons.Default.AccessAlarms,
            isSelected = selectedReminderType == ReminderType.OnTime,
            selectedText = if (selectedReminderType == ReminderType.OnTime) stringResource(R.string.on_time) else null,
            onClick = { handleClick(onShowReminderDialogChange) },
            onRemove = if (selectedReminderType == ReminderType.OnTime) onRemoveReminder else null
        )
    }
}

@Composable
private fun ReminderRow(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedText: String?,
    onClick: () -> Unit,
    onRemove: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = label,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        selectedText?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 4.dp)
                )
                onRemove?.let { removeAction ->
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove $label",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { removeAction() }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopSheetBar(
    onCloseClick: () -> Unit,
    onApplyClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = onCloseClick) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onApplyClick) {
            Icon(imageVector = Icons.Default.Done, contentDescription = "Apply")
        }
    }
}
