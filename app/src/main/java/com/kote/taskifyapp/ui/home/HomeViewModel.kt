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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

enum class SortType {
    TITLE, DATE, PRIORITY
}

enum class GroupTasksType {
    ALL, TODAY, PLANNED, COMPLETED, IMPORTANT
}

data class TasksUiState(
    val groupTasksType: GroupTasksType = GroupTasksType.ALL,
    val sortType: SortType = SortType.DATE,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val workRepository: WorkManagerRepository
) : ViewModel() {
    private val _tasksUiState = MutableStateFlow(TasksUiState())
    val tasksUiState = _tasksUiState.asStateFlow()

    private val _groupedTasks = MutableStateFlow(emptyMap<String, List<Task>>())
    val groupedTask = _groupedTasks.asStateFlow()

    init {
        workRepository.scheduleDailyCheck()
        viewModelScope.launch {
            repository.allTask
                .combine(_tasksUiState) { tasks, uiState ->
                    val currentDate = LocalDate.now()
                    when (uiState.groupTasksType) {
                        GroupTasksType.ALL -> {
                            tasks.groupBy {
                                when {
                                    it.isCompleted -> "Completed"
                                    it.date != null && convertMillisToDate(it.date) < currentDate -> "Outdated"
                                    it.date == null -> "Not planned"
                                    else -> "Active"
                                }
                            }
                        }
                        GroupTasksType.TODAY -> {
                            tasks.filter { it.date != null && convertMillisToDate(it.date) <= currentDate }
                                .groupBy {
                                    when {
                                        it.isCompleted -> "Completed"
                                        convertMillisToDate(it.date!!) == currentDate -> "Today"
                                        else -> "Outdated"
                                    }
                                }
                        }
                        GroupTasksType.COMPLETED -> {
                            mapOf("Completed" to tasks.filter { it.isCompleted })
                        }
                        GroupTasksType.PLANNED -> {
                            mapOf("Planned" to tasks.filter {
                                it.date != null && !it.isCompleted && convertMillisToDate(it.date) >= currentDate
                            })
                        }
                        GroupTasksType.IMPORTANT -> {
                            mapOf("Important" to tasks.filter { it.isFavorite })
                        }
                    }
                }
                .collect { groupedTasks ->
                    _groupedTasks.value = groupedTasks
                }
        }
    }

    fun setSortType(sortType: SortType) { _tasksUiState.update { it.copy(sortType = sortType) }}

    fun setGroupTasksType(groupTasksType: GroupTasksType) {
        _tasksUiState.update { it.copy(groupTasksType = groupTasksType) }
    }

    fun deleteSelectedTasks(selectedTaskIds: Set<Int>) {
        viewModelScope.launch {
            repository.deleteSpecificTasks(selectedTaskIds)
        }
    }

    fun markAsCompleted(group: String, taskId: Int) {
        viewModelScope.launch {
            _groupedTasks.value[group]?.find { it.id == taskId }?.let {
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
    private fun convertMillisToDate(date: Long): LocalDate {
        return Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}
