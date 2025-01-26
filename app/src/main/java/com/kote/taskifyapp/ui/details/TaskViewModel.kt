package com.kote.taskifyapp.ui.details

import android.util.Log
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
            val selectedDate: String? = savedStateHandle["date"]

            repository.getAllTasksDesc().collect{ tasks ->
                if (!taskId.isNullOrEmpty()) {
                    tasks.firstOrNull{ it.id == taskId.toInt() }?.let { _taskState.value = it }
                } else {
                    val futureTaskId = if (tasks.isNotEmpty()) tasks[0].id + 1 else 1
                    _taskState.update { it.copy(id = futureTaskId) }
                }

                if (!selectedDate.isNullOrEmpty()) updateTaskDate(selectedDate.toLong())
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
                cancelNotificationWork(_taskState.value.id, _taskState.value.title)
                cancelCompletedTask(_taskState.value.id)
                repository.deleteTask(_taskState.value)
            }
        }
    }

    private fun cancelNotificationWork(id: Int, title: String?) {
        workRepository.cancelAlarmNotification(id, title)
    }

    private fun cancelCompletedTask(id: Int) {
        workRepository.cancelCompletedTask(id)
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
            if (time == null) {
                cancelNotificationWork(_taskState.value.id, _taskState.value.title)
            }
        }
    }

    fun updateReminderType(newType: ReminderType) {
        if (_taskState.value.reminderType != newType) {
            _taskState.update { it.copy(reminderType = newType) }
            if (newType == ReminderType.None) {
                cancelNotificationWork(_taskState.value.id, taskState.value.title)
            }
        }
    }

    fun updateTaskPriority(priority: Priority) {
        _taskState.update { it.copy(priority = priority) }
    }
}