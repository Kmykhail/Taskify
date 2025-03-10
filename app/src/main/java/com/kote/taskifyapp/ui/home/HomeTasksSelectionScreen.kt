package com.kote.taskifyapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.R
import com.kote.taskifyapp.data.Priority
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.ui.components.CustomCheckBox
import com.kote.taskifyapp.ui.components.TaskSummaryDefaultView
import com.kote.taskifyapp.ui.theme.TaskifyTheme

@Composable
fun HomeTasksSelectionScreen(
    viewModel: HomeViewModel,
    navigationBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedTasks by viewModel.groupedTasks.collectAsState()
    var isSelectedAll by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf(setOf<Int>()) }
    val tasks = groupedTasks.values.flatten()

    selectedTask = when (isSelectedAll) {
        true -> tasks.map { it.id }.toSet()
        false -> setOf()
    }

    val onToggleTaskSelection = { taskId: Int ->
        selectedTask = if (taskId in selectedTask) {
            selectedTask - taskId
        } else {
            selectedTask + taskId
        }
    }
    
    Scaffold(
        topBar = {
            TopBar(
                selectedTasksCount = selectedTask.size,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        },
        bottomBar = {
            BottomBar(
                updateSelectAll = { isSelectedAll = it },
                deleteSelectedTasks = {
                    viewModel.deleteSelectedTasks(selectedTask)
                    if (isSelectedAll || selectedTask.size == tasks.size) {
                        navigationBack()
                    }
                },
                selectedTasks = selectedTask.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp, start = 32.dp, end = 32.dp)
            )
        },
    ) { paddingValues ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 10.dp)
                .fillMaxSize()
        ) {
            item {
                ShowTasks(
                    tasks = tasks,
                    onToggleTaskSelection = onToggleTaskSelection,
                    onSelectedTask = {selectedTask.contains(it)}
                )
            }
        }
    }
}

@Composable
private fun ShowTasks(
    tasks: List<Task>,
    onToggleTaskSelection: (Int) -> Unit,
    onSelectedTask: (Int) -> Boolean
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column {
            tasks.forEach { task ->
                TaskSummaryDefaultView(
                    task = task,
                    checkBox = {
                        CustomCheckBox(
                            checked = onSelectedTask(task.id),
                            onCheckedChange = {onToggleTaskSelection(task.id)}
                        )
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(end = 16.dp)
                        .fillMaxWidth()
                        .clickable {
                            onToggleTaskSelection(task.id)
                        }
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    selectedTasksCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Text(
            text = "$selectedTasksCount Selected",
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
private fun BottomBar(
    updateSelectAll: (Boolean) -> Unit,
    deleteSelectedTasks: () -> Unit,
    selectedTasks: Boolean,
    modifier: Modifier = Modifier
) {
    var isSelectAll by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        TextButton(onClick = {
            isSelectAll = !isSelectAll
            updateSelectAll(isSelectAll)
        }) { Text(text = if (!isSelectAll) stringResource(R.string.select_all) else stringResource(R.string.cancel)) }
        IconButton(
            onClick = { deleteSelectedTasks() },
            enabled = selectedTasks,
        ) {
            Icon(
                Icons.Default.DeleteOutline,
                contentDescription = "Delete",
            )
        }
    }
}

@Preview
@Composable
fun ShowTasksPreview() {
    TaskifyTheme {
        ShowTasks(
            tasks = listOf(Task(
                id = 2,
                title = "Test2",
                description = "Description for Test2 task",
                date = 456L,
                priority = Priority.Low,
            )),
            onToggleTaskSelection = {},
            onSelectedTask = { false }
        )
    }
}