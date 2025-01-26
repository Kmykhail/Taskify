package com.kote.taskifyapp.ui.details

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kote.taskifyapp.data.Priority
import com.kote.taskifyapp.data.ReminderType
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.data.repository.WorkManagerRepository
import com.kote.taskifyapp.data.repository.TaskRepository
import com.kote.taskifyapp.util.calculateReminderTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository,
    private val workRepository: WorkManagerRepository
) : ViewModel() {

    private val _taskState = MutableStateFlow(Task())
    val taskState : StateFlow<Task> = _taskState

    init {
        viewModelScope.launch {
            val taskId: String? = savedStateHandle["taskId"]
            val dateString: String? = savedStateHandle["date"]

            repository.getAllTasksDesc().collect{ tasks ->
                if (!taskId.isNullOrEmpty()) {
                    tasks.firstOrNull{ it.id == taskId.toInt() }?.let { _taskState.value = it }
                } else {
                    val futureTaskId = if (tasks.isNotEmpty()) tasks[0].id + 1 else 1
                    _taskState.update { it.copy(id = futureTaskId) }
                }

                if (dateString != null) {
                    updateTaskDate(convertDateToMillis(dateString))
                }
            }
        }
    }

    fun restoreTask() {
        viewModelScope.launch {
            _taskState.update { it.copy(isCompleted = false, deletionTime = null) }
            repository.updateTask(_taskState.value)
            workRepository.cancelCompletedTask(_taskState.value.id)
        }
    }

    fun saveTask() {
        viewModelScope.launch {
            _taskState.value.run {
                if (date != null && time != null && reminderType == ReminderType.OnTime ) {
                    workRepository.scheduleAlarmNotification(id, title, description, calculateReminderTime(date, time))
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
        workRepository.cancelAlarmNotification(id)
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
            Log.d("Debug", "new date:$date")
            if (_taskState.value.date != date) {
                _taskState.update { it.copy(date = date) }
            }
        }
    }

    fun updateTaskTime(time: Int?) {
        if (_taskState.value.time != time) {
            _taskState.update { it.copy(time = time) }
        }
    }

    fun updateTaskPriority(priority: Priority) {
        _taskState.update { it.copy(priority = priority) }
    }

    private fun convertDateToMillis(dateString: String): Long {
        return LocalDate.parse(dateString).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
}