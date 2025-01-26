package com.kote.taskifyapp.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.ui.components.CustomTextField
import androidx.compose.runtime.setValue

@Composable
fun TaskScreen(
    viewModel: TaskViewModel,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val task by viewModel.taskState.collectAsState()

    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }

    var openDatTimeSheet by remember { mutableStateOf(false) }
    var openPrioritySelector by remember { mutableStateOf(false) }

    Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = Color.White)
    ) {
        CustomTextField(
            value = task.title ?: "",
            placeholder = "Name",
            onValueChange = viewModel::updateTaskTitle,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequester2.requestFocus() }),
            isSingleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester1)
        )
        CustomTextField(
            value = task.description ?: "",
            placeholder = "Description",
            onValueChange = viewModel::updateTaskDescription,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester2)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = {openDatTimeSheet = !openDatTimeSheet}) {
                Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = "Set day and time", tint = viewModel.getTaskCalendarColor())
            }
            Box {
                IconButton(onClick = {openPrioritySelector = !openPrioritySelector}) {
                    Icon(imageVector = Icons.Outlined.Flag, contentDescription = "Set priority", tint = viewModel.getTaskPriorityColor())
                }
                if (openPrioritySelector) {
                    PriorityMenu(onDismissRequest = {openPrioritySelector = it}, onPriorityChange = viewModel::updateTaskPriority)
                }
            }
            IconButton(
                onClick = {
                    viewModel.deleteTask()
                    navigateBack()
                },
                enabled = task.isCreated
            ) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete task")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    viewModel.saveTask()
                    navigateBack()
                },
                enabled = !task.title.isNullOrEmpty() || !task.description.isNullOrEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Create/update task"
                )
            }
        }
    }

    if (openDatTimeSheet) {
        ModalDateTimeSheet(
            task = task,
            onDateChange = viewModel::updateTaskDate,
            onTimeChange = viewModel::updateTaskTime,
            removeReminder = viewModel::cancelNotificationWork,
            onDismissRequest = {openDatTimeSheet = it}
        )
    }
}
