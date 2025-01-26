package com.kote.taskifyapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kote.taskifyapp.DELAY_FOR_DELETE
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.data.repository.TaskRepository
import com.kote.taskifyapp.data.repository.WorkManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

enum class SortType {
    TITLE, DATE, PRIORITY
}

enum class TaskFilterType {
    ALL, TODAY, PLANNED, COMPLETED, IMPORTANT
}

data class TasksUiState(
    val taskFilterType: TaskFilterType = TaskFilterType.ALL,
    val sortType: SortType = SortType.DATE
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val workRepository: WorkManagerRepository
) : ViewModel() {
    private val _tasksUiState = MutableStateFlow(TasksUiState())
    val tasksUiState = _tasksUiState.asStateFlow()

    val tasks: StateFlow<List<Task>> = repository.allTask
        .combine(_tasksUiState) { tasks, uiState ->
            val filteredTasks = when (uiState.taskFilterType) {
                TaskFilterType.TODAY -> {
                    tasks.filter {
                        val taskDate = it.date?.let { it1 ->
                            Instant
                                .ofEpochMilli(it1)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        taskDate != null && taskDate == LocalDate.now()
                    }
                }
                TaskFilterType.PLANNED -> tasks.filter { it.date != null && !it.isCompleted }
                TaskFilterType.COMPLETED -> tasks.filter { it.isCompleted }
                TaskFilterType.ALL -> tasks.filter { true }
                TaskFilterType.IMPORTANT -> tasks
            }
            when (uiState.sortType) {
                SortType.TITLE -> filteredTasks.sortedBy { it.title }
                SortType.DATE -> filteredTasks.sortedBy { it.date }
                SortType.PRIORITY -> filteredTasks.sortedBy { it.priority }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSortType(sortType: SortType) { _tasksUiState.update { it.copy(sortType = sortType) }}
    fun setFilterType(taskFilterType: TaskFilterType) { _tasksUiState.update { it.copy(taskFilterType = taskFilterType) } }


    fun deleteSelectedTasks(selectedTaskIds: Set<Int>) {
        viewModelScope.launch {
            repository.deleteSpecificTasks(selectedTaskIds)
        }
    }

    fun markAsCompleted(taskId: Int) {
        viewModelScope.launch {
            println("WTF taskID, $taskId")
            tasks.value.find { it.id == taskId}?.let {
                val updated = it.copy(
                    isCompleted = true,
                    deletionTime = System.currentTimeMillis() + DELAY_FOR_DELETE
                )
                repository.updateTask(updated)
                delay(400)

                workRepository.scheduleCompletedTask(taskId)
                workRepository.cancelAlarmNotification(taskId)
            }
        }
    }
}
