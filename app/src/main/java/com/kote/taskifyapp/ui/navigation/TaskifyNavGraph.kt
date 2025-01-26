package com.kote.taskifyapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kote.taskifyapp.ui.details.TaskScreen
import com.kote.taskifyapp.ui.details.TaskViewModel
import com.kote.taskifyapp.ui.home.HomeViewModel
import com.kote.taskifyapp.ui.home.HomeScreen
import com.kote.taskifyapp.ui.home.HomeTasksSelectionScreen

@Composable
fun TaskifyNavGraph(
    navController: NavHostController,

    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable(route = "home") {
            HomeScreen(
                viewModel = hiltViewModel<HomeViewModel>(),
                onNavigateToTaskDetails = { taskId ->
                    navController.navigate("details/$taskId")
                },
                onNavigateToSelectionScreen = { navController.navigate("home_tasks_selections") },
                modifier = modifier
            )
        }
        composable(route = "home_tasks_selections") {
            HomeTasksSelectionScreen(
                viewModel = hiltViewModel<HomeViewModel>(),
                navigationBack = {navController.popBackStack()},
                modifier = modifier

            )
        }
        composable(
            route = "details/{taskId}?",
            arguments = listOf(navArgument("taskId") {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) {
            TaskScreen(
                viewModel = hiltViewModel<TaskViewModel>(),
                navigateBack = { navController.popBackStack()},
                modifier = modifier
            )
        }
    }
}