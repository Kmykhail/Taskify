package com.kote.taskifyapp.ui.details

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kote.taskifyapp.data.Priority
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.data.repository.WorkManagerRepository
import com.kote.taskifyapp.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository,
    private val workRepository: WorkManagerRepository
) : ViewModel() {

    private val _taskState = MutableStateFlow<Task>(Task())
    val taskState : StateFlow<Task> = _taskState

    init {
        viewModelScope.launch {
            val taskId: String? = savedStateHandle["taskId"]
            if (!taskId.isNullOrEmpty()) {
                repository.getAllTasks().collect{ tasks ->
                    tasks.firstOrNull{ it.id == taskId.toInt() }?.let { _taskState.value = it }
                }
            }
        }
    }

    fun saveTask() {
        viewModelScope.launch {
            _taskState.value.run {
                if (date != null && time != null) {
                    workRepository.scheduleNotification(id, title, description, calculateReminderTime(date, time))
                }
                if (isCreated) {
                    repository.updateTask(_taskState.value)
                } else {
                    _taskState.update { it.copy(isCreated = true) }
                    repository.insertTask(_taskState.value)
                }
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            if (_taskState.value.isCreated) {
                cancelNotificationWork(_taskState.value.id)
                cancelCompletedTask(_taskState.value.id)
                repository.deleteTask(_taskState.value)
            }
        }
    }

    fun cancelNotificationWork(id: Int) {
        workRepository.cancelNotification(id)
    }

    private fun cancelCompletedTask(id: Int) {
        workRepository.cancelCompletedTask(id)
    }

    fun getTaskPriorityColor(): Color {
        return when(_taskState.value.priority) {
            Priority.High -> Color.Red
            Priority.Medium -> Color.Yellow
            Priority.Low -> Color.Green
            Priority.NoPriority -> Color.Gray
        }
    }

    fun getTaskCalendarColor() : Color {
        return if (_taskState.value.date != null) Color(0xFF4872FB) else Color.Gray
    }

    fun updateTaskTitle(title: String) {
        _taskState.update { it.copy(title = title) }
    }

    fun updateTaskDescription(description: String) {
        _taskState.update { it.copy(description = description) }
    }

    fun updateTaskDate(date: Long?) {
        date?.let {
            if (_taskState.value.date != date) {
                _taskState.update { it.copy(date = date) }
            }
        }
    }

    fun updateTaskTime(time: Int?) {
        time?.let {
            if (_taskState.value.time != time) {
                _taskState.update { it.copy(time = time) }
            }
        }
    }

    fun updateTaskPriority(priority: Priority) {
        _taskState.update { it.copy(priority = priority) }
    }

    private fun calculateReminderTime(dateInMillis: Long, timeInMinutes: Int): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val timeInMillis = timeInMinutes * 60 * 1000L
        return calendar.timeInMillis + timeInMillis
    }
}