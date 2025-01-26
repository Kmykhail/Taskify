package com.kote.taskifyapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.ui.components.CustomCheckBox

@Composable
fun HomeTasksSelectionScreen(
    viewModel: HomeViewModel,
    navigationBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedTasks by viewModel.groupedTask.collectAsState()
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
        bottomBar = {
            BottomBar(
                { isSelectedAll = it },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            ShowTasks(
                tasks,
                onToggleTaskSelection,
                {selectedTask.contains(it)},
                modifier
            )
        }
    }
}

@Composable
private fun ShowTasks(
    tasks: List<Task>,
    onToggleTaskSelection: (Int) -> Unit,
    onSelectedTask: (Int) -> Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .background(color = Color.White)
            .clip(RoundedCornerShape(4.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 2.dp)
                .fillMaxWidth()
        ) {
            Text(text = "All", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = tasks.size.toString())
        }
        LazyColumn {
            items(tasks) { task ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .padding(end = 16.dp)
                        .fillMaxWidth()
                ) {
                    CustomCheckBox(
                        checked = onSelectedTask(task.id),
                        onCheckedChange = {onToggleTaskSelection(task.id)}
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = task.title ?: "Untitled")
                }
            }
        }
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
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextButton(onClick = {
            isSelectAll = !isSelectAll
            updateSelectAll(isSelectAll)
        }) { Text(text = if (!isSelectAll) "Select All" else "Cancel") }
        IconButton(
            onClick = { deleteSelectedTasks() },
            enabled = selectedTasks,
        ) {
            Icon(
                Icons.Default.DeleteOutline,
                "Delete",
                tint = if (selectedTasks) Color.Black else Color.Black.copy(alpha = 0.38f))
        }
    }
}