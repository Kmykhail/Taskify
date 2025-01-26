package com.kote.taskifyapp.ui.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToTaskDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val tasksUiState by viewModel.tasksUiState.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(
                filterType = tasksUiState.filterType,
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
        HomeListView(
            tasks = tasks,
            tasksUiState = tasksUiState,
            onNavigateToTaskDetails = onNavigateToTaskDetails,
            markAsCompleted = viewModel::markAsCompleted,
            paddingValues = paddingValues
        )
    }
}
