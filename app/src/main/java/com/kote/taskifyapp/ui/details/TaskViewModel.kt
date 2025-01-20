package com.kote.taskifyapp.ui.details

import androidx.compose.foundation.MutatePriority
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kote.taskifyapp.data.Priority
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository
) : ViewModel() {
    private val taskId: String? = savedStateHandle["taskId"]

    private val _taskState = MutableStateFlow<Task>(Task())
    val taskState : StateFlow<Task> = _taskState

    init {
        viewModelScope.launch {
            if (!taskId.isNullOrEmpty()) {
                repository.getAllTasks().collect{ tasks ->
                    tasks.firstOrNull{ it.id== taskId.toInt() }?.let { _taskState.value = it }
                }
            }
        }
    }

    fun saveTask() {
        viewModelScope.launch {
            if (!taskId.isNullOrEmpty()) {
                repository.updateTask(_taskState.value)
            } else {
                repository.insertTask(_taskState.value)
            }
        }
    }

    fun getTaskPriorityColor(): Color {
        return when(_taskState.value.priority) {
            Priority.High -> Color.Red
            Priority.Medium -> Color.Yellow
            Priority.Low -> Color.Green
            Priority.NoPriority -> Color.Gray
        }
    }

    fun updateTaskTitle(title: String) {
        _taskState.update { it.copy(title = title) }
    }

    fun updateTaskDescription(description: String) {
        _taskState.update { it.copy(description = description) }
    }

    fun updateTaskDate(date: Long?) {
        date?.let {
            _taskState.update { it.copy(date = date) }
        }
    }

    fun updateTaskTime(time: Int?) {
        time?.let {
            _taskState.update { it.copy(time = time) }
        }
    }

    fun updateTaskPriority(priority: Priority) {
        _taskState.update { it.copy(priority = priority) }
    }
}