package com.kote.taskifyapp.ui.details

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kote.taskifyapp.data.Priority
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.data.repository.ReminderManagerRepository
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
    private val reminderRepository: ReminderManagerRepository
) : ViewModel() {
    private val taskId: String? = savedStateHandle["taskId"]

    private val _taskState = MutableStateFlow<Task>(Task())
    val taskState : StateFlow<Task> = _taskState

    private var isDateChosen = false
    private var isTimeChosen = false

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
            _taskState.value.run {
                Log.d("Debug", "check $isDateChosen, $isTimeChosen, $isCompleted")
                if (isDateChosen && isTimeChosen && !isCompleted) {
                    reminderRepository.scheduleNotification(
                        id,
                        title,
                        description,
                        calculateReminderTime(date!!, time!!)
                    )
                }
            }

            if (!taskId.isNullOrEmpty()) {
                repository.updateTask(_taskState.value)
            } else {
                repository.insertTask(_taskState.value)
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            if (!taskId.isNullOrEmpty()) {
                removeReminder(_taskState.value.id)
                repository.deleteTask(_taskState.value)
            }
        }
    }

    fun removeReminder(id: Int) {
        reminderRepository.removeNotification(id)
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
                isDateChosen = true
                _taskState.update { it.copy(date = date) }
            }
        }
    }

    fun updateTaskTime(time: Int?) {
        time?.let {
            if (_taskState.value.time != time) {
                isTimeChosen = true
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