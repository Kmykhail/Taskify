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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.ui.components.SidePanel
import com.kote.taskifyapp.ui.navigation.UserHomeScreens
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    userHomeScreens: MutableState<UserHomeScreens>,
    previousSelectedDate: MutableState<Long?>,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    onNavigateToSelectionScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedTasks by viewModel.groupedTask.collectAsState()
    val tasksUiState by viewModel.tasksUiState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        Log.d("Debug", "HomeScreen Entered")
        onDispose { Log.d("Debug", "HomeScreen Exited") }
    }

    if (tasksUiState == null) {
        CircularProgressIndicator()
    } else {
        SidePanel(
            drawerState = drawerState,
            scope = scope,
            selectedFilterType = tasksUiState!!.groupTasksType,
            onSelectedFilterType = viewModel::setGroupTasksType,
            gestureEnabled = userHomeScreens.value == UserHomeScreens.TASKS,
            modifier = modifier
        ) {
            Scaffold(
                topBar = {
                    if (userHomeScreens.value == UserHomeScreens.TASKS) {
                        HomeTopBar(
                            groupTasksType = tasksUiState!!.groupTasksType,
                            onSortChange = viewModel::setSortType,
                            onFiltrationChange = viewModel::setGroupTasksType,
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
                        onCalendarGroupChange = {viewModel.setGroupTasksType(GroupTasksType.ALL)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 24.dp, start = 32.dp, end = 32.dp)
                    )
                },
                floatingActionButton = {
                    if (userHomeScreens.value != UserHomeScreens.SETTINGS) {
                        FloatingActionButton(
                            onClick = { onNavigateToTaskDetails("", previousSelectedDate.value.toString()) },
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
                when (userHomeScreens.value) {
                    UserHomeScreens.TASKS -> {
                        HomeListView(
                            groupedTasks = groupedTasks,
                            onNavigateToTaskDetails = onNavigateToTaskDetails,
                            onNavigateToSelectionScreen = onNavigateToSelectionScreen,
                            markAsCompleted = viewModel::markAsCompleted,
                            groupTasksType = tasksUiState!!.groupTasksType,
                            paddingValues = paddingValues
                        )
                    }
                    UserHomeScreens.CALENDAR -> {
                        HomeCalendarView(
                            groupedTasks = groupedTasks,
                            previousSelectedDate = previousSelectedDate,
                            onNavigateToTaskDetails = onNavigateToTaskDetails,
                            markAsCompleted = viewModel::markAsCompleted,
                            paddingValues = paddingValues
                        )
                    }
                    UserHomeScreens.SETTINGS -> {
                        SettingsView(
                            cancelDailyTaskCheck= viewModel::cancelDailyCheck
                        )
                    }
                }
            }
        }
    }
}