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
import javax.inject.Inject

enum class SortType {
    TITLE, DATE, PRIORITY
}

enum class FilterType {
    SHOW_ACTIVE, SHOW_COMPLETED
}

data class TasksUiState(
    val filterType: FilterType = FilterType.SHOW_ACTIVE,
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
            when (uiState.sortType) {
                SortType.TITLE -> tasks.sortedBy { it.title }
                SortType.DATE -> tasks.sortedBy { it.date }
                SortType.PRIORITY -> tasks.sortedBy { it.priority }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSortType(sortType: SortType) { _tasksUiState.update { it.copy(sortType = sortType) }}
    fun setFilterType(filterType: FilterType) { _tasksUiState.update { it.copy(filterType = filterType) } }

    fun getNumberActiveTasks(): Int {
        return tasks.value.count { !it.isCompleted }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            tasks.value.forEach { task ->
                workRepository.cancelCompletedTask(task.id)
                workRepository.cancelAlarmNotification(task.id)
            }

            repository.deleteAllTasks()
        }
    }

    fun markAsCompleted(taskId: Int) {
        viewModelScope.launch {
            tasks.value.find { it.id == taskId}?.let {
                val updated = it.copy(
                    isCompleted = true,
                    deletionTime = System.currentTimeMillis() + DELAY_FOR_DELETE
                )
                repository.updateTask(updated)
                delay(400)

                workRepository.scheduleCompletedTask(taskId)
            }
        }
    }
}
