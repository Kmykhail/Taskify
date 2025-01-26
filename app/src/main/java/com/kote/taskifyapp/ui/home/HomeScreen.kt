package com.kote.taskifyapp.ui.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.ui.components.CustomCalendarView
import com.kote.taskifyapp.ui.components.SidePanel
import com.kote.taskifyapp.ui.navigation.UserHomeScreens
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    userHomeScreens: UserHomeScreens,
    updateHomeScreens: (UserHomeScreens) -> Unit,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    onNavigateToSelectionScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val tasksUiState by viewModel.tasksUiState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDate: String? = null

    SidePanel(
        drawerState = drawerState,
        scope = scope,
        modifier = modifier
    ) {
        Scaffold(
            topBar = {
                if (userHomeScreens == UserHomeScreens.TASKS) {
                    HomeTopBar(
                        filterType = tasksUiState.filterType,
                        onSortChange = viewModel::setSortType,
                        onFiltrationChange = viewModel::setFilterType,
                        onSwitchView = {},
                        onOpenSidePanel = { scope.launch { drawerState.open() } },
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                    )
                }
            },
            bottomBar = {
                HomeBottomBar(
                    clickableScreen = userHomeScreens,
                    onClick = {updateHomeScreens(it)},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 24.dp, start = 32.dp, end = 32.dp)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onNavigateToTaskDetails("", selectedDate) },
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
            when (userHomeScreens) {
                UserHomeScreens.TASKS -> {
                    HomeListView(
                        tasks = tasks,
                        tasksUiState = tasksUiState,
                        onNavigateToTaskDetails = onNavigateToTaskDetails,
                        onNavigateToSelectionScreen = onNavigateToSelectionScreen,
                        markAsCompleted = viewModel::markAsCompleted,
                        paddingValues = paddingValues
                    )
                }
                UserHomeScreens.CALENDAR -> {
                    CustomCalendarView(
                        tasks = tasks,
                        tasksUiState = tasksUiState,
                        onSelectedDate = {selectedDate = it},
                        onNavigateToTaskDetails = onNavigateToTaskDetails,
                        markAsCompleted = viewModel::markAsCompleted,
                        paddingValues = paddingValues
                    )
                }
                UserHomeScreens.SETTINGS -> Unit
            }
        }
    }
}