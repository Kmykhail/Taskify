package com.kote.taskifyapp.ui.home

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.ui.components.SidePanel
import com.kote.taskifyapp.ui.settings.SettingsViewModel
import com.kote.taskifyapp.util.convertLocalDateToMillis
import com.kote.taskifyapp.util.convertMillisToLocalDate
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    onNavigateToSelectionScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedTasks by homeViewModel.groupedTasks.collectAsState()
    val allCalendarTasks by homeViewModel.allCalendarTasks.collectAsState()
    val tasksUiState by homeViewModel.tasksUiState.collectAsState()
    val settingsUiState by settingsViewModel.settingsUiState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (tasksUiState == null) {
        CircularProgressIndicator()
    } else {
        SidePanel(
            drawerState = drawerState,
            scope = scope,
            selectedFilterType = tasksUiState!!.groupTasksType,
            onSelectedFilterType = { homeViewModel.setGroupTasksType(it) },
            gestureEnabled = tasksUiState!!.userHomeScreens == UserHomeScreens.TASKS,
            modifier = modifier
        ) {
            Scaffold(
                topBar = {
                    if (tasksUiState!!.userHomeScreens == UserHomeScreens.TASKS) {
                        HomeTopBar(
                            groupTasksType = tasksUiState!!.groupTasksType,
                            onSortChange = homeViewModel::setSortType,
                            onFiltrationChange = homeViewModel::setGroupTasksType,
                            onOpenSidePanel = { scope.launch { drawerState.open() } },
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                        )
                    }
                },
                bottomBar = {
                    HomeBottomBar(
                        userHomeScreen = tasksUiState!!.userHomeScreens,
                        onHomeScreenClick = { homeViewModel.setUserHomeScreens(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 24.dp, start = 32.dp, end = 32.dp)
                    )
                },
                floatingActionButton = {
                    if (homeViewModel.allowTaskCreation()) {
                        FloatingActionButton(
                            onClick = { onNavigateToTaskDetails("", homeViewModel.getDate()) },
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(52.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add task",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            ) { paddingValues ->
                when (tasksUiState!!.userHomeScreens) {
                    UserHomeScreens.TASKS -> {
                        HomeListView(
                            taskViewType = settingsUiState.taskViewType,
                            groupedTasks = groupedTasks,
                            onNavigateToTaskDetails = onNavigateToTaskDetails,
                            onNavigateToSelectionScreen = onNavigateToSelectionScreen,
                            markAsCompleted = homeViewModel::markAsCompleted,
                            groupTasksType = tasksUiState!!.groupTasksType,
                            paddingValues = paddingValues
                        )
                    }
                    UserHomeScreens.CALENDAR -> {
                        HomeCalendarView(
                            groupedTasks = allCalendarTasks,
                            selectedDate = convertMillisToLocalDate(tasksUiState!!.selectedDate!!),
                            setSelectedDate = { homeViewModel.setSelectedDay(convertLocalDateToMillis(it)) },
                            onNavigateToTaskDetails = onNavigateToTaskDetails,
                            markAsCompleted = homeViewModel::markAsCompleted,
                            paddingValues = paddingValues
                        )
                    }
                    UserHomeScreens.SETTINGS -> {
                        SettingsView(
                            settingsUiState = settingsUiState,
                            setSettings = settingsViewModel::setSettings,
                            modifier = modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}