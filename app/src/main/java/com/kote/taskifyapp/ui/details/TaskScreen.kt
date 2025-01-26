package com.kote.taskifyapp.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import com.kote.taskifyapp.data.Priority
import com.kote.taskifyapp.data.Task
import java.util.concurrent.TimeUnit

@Composable
fun TaskScreen(
    viewModel: TaskViewModel,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val task by viewModel.taskState.collectAsState()

    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }

    val openDatTimeSheet = remember { mutableStateOf(false) }
    val openPrioritySelector = remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color = Color.White)
    ) {
        CustomTextField(
            value = task.title ?: "",
            placeholder = "Name",
            onValueChange = viewModel::updateTaskTitle,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequester2.requestFocus() }),
            isSingleLine = true,
            readOnly = task.isCompleted,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester1)
        )
        Box(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .heightIn(min = 56.dp, max = (LocalConfiguration.current.screenHeightDp * 0.8).dp)
        ) {
            CustomTextField(
                value = task.description ?: "",
                placeholder = "Description",
                onValueChange = viewModel::updateTaskDescription,
                readOnly = task.isCompleted,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester2)
            )
        }
        if (task.isCompleted) {
            ShowCompletedBar(
                task = task,
                onRestoreClick = {
                    viewModel.restoreTask()
                    navigateBack()
                },
                onDeleteClick = {
                    viewModel.deleteTask()
                    navigateBack()
                }
            )
        } else {
            ShowIncompleteBar(
                task = task,
                openDatTimeSheet = openDatTimeSheet,
                openPrioritySelector = openPrioritySelector,
                getTaskCalendarColor = viewModel::getTaskCalendarColor,
                getTaskPriorityColor = viewModel::getTaskPriorityColor,
                updateTaskPriority = viewModel::updateTaskPriority,
                onDeleteTask = {
                    viewModel.deleteTask()
                    navigateBack()
                },
                onSaveTask = {
                    viewModel.saveTask()
                    navigateBack()
                }
            )
        }
    }

    if (openDatTimeSheet.value) {
        ModalDateTimeSheet(
            task = task,
            onDateChange = viewModel::updateTaskDate,
            onTimeChange = viewModel::updateTaskTime,
            removeReminder = viewModel::cancelNotificationWork,
            onDismissRequest = {openDatTimeSheet.value = it}
        )
    }
}

@Composable
private fun ShowCompletedBar(
    task: Task,
    onRestoreClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onRestoreClick,
            enabled = !task.title.isNullOrEmpty() || !task.description.isNullOrEmpty()
        ) { Icon(imageVector = Icons.Default.Restore, contentDescription = "Restore task") }
        IconButton(
            onClick = onDeleteClick,
            enabled = task.isCreated
        ) { Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete task") }
    }
    Text(
        text = "This task will be deleted in ${TimeUnit.MILLISECONDS.toDays(task.deletionTime!! - System.currentTimeMillis())} day",
        textAlign = TextAlign.Justify
    )
}

@Composable
private fun ShowIncompleteBar(
    task: Task,
    openDatTimeSheet: MutableState<Boolean>,
    openPrioritySelector: MutableState<Boolean>,
    getTaskCalendarColor: () -> Color,
    getTaskPriorityColor: () -> Color,
    updateTaskPriority: (Priority) -> Unit,
    onDeleteTask: () -> Unit,
    onSaveTask: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = {openDatTimeSheet.value = !openDatTimeSheet.value}) {
            Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = "Set day and time", tint = getTaskCalendarColor())
        }
        Box {
            IconButton(onClick = {openPrioritySelector.value = !openPrioritySelector.value}) {
                Icon(imageVector = Icons.Outlined.Flag, contentDescription = "Set priority", tint = getTaskPriorityColor())
            }
            if (openPrioritySelector.value) {
                PriorityMenu(onDismissRequest = {openPrioritySelector.value = it}, onPriorityChange = updateTaskPriority)
            }
        }
        IconButton(
            onClick = onDeleteTask,
            enabled = task.isCreated
        ) { Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete task") }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onSaveTask,
            enabled = !task.title.isNullOrEmpty() || !task.description.isNullOrEmpty()
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Create/update task"
            )
        }
    }
}