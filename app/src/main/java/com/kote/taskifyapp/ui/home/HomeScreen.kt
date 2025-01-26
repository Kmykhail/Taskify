package com.kote.taskifyapp.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.data.Task

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToTaskDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(
                onSortChange = viewModel::setSortType,
                onFiltrationChange = viewModel::setFilterType,
                onSwitchView = {},
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
            )
        },
        bottomBar = {
            HomeBottomBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp, start = 32.dp, end = 32.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToTaskDetails("") },
                shape = CircleShape,
                containerColor = Color(0xFF4872FB),
                contentColor = Color.White,
                modifier = Modifier
                    .size(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add task",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (tasks.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color = Color.White)
                ) {
                    // Head
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 2.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Today" // TODO
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = viewModel.getNumberActiveTasks().toString())
                        IconButton(
                            onClick = {} // TODO hide task list(Body)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Chevron down"
                            )
                        }
                    }

                    LazyColumn() {
                        items(tasks) { task ->
                            TaskItem(task, onNavigateToTaskDetails, viewModel::markAsCompleted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onNavigateToTaskDetails: (String) -> Unit,
    markAsCompleted: (Int) -> Unit
) {
    AnimatedVisibility(
        visible = true,
        exit = fadeOut() + shrinkVertically()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .padding(end = 16.dp)
                .fillMaxWidth()
                .clickable { onNavigateToTaskDetails(task.id.toString()) }
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { markAsCompleted(task.id) }
            )
            Text(text = task.title ?: "Untitled",)
            Spacer(modifier = Modifier.weight(1f))
            if (task.time != null) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = "Clock",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
