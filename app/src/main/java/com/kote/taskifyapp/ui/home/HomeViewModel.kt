package com.kote.taskifyapp.ui.home

import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kote.taskifyapp.DELAY_FOR_DELETE
import com.kote.taskifyapp.R
import com.kote.taskifyapp.data.SortType
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.data.repository.TaskRepository
import com.kote.taskifyapp.data.repository.UserPreferencesRepository
import com.kote.taskifyapp.data.repository.WorkManagerRepository
import com.kote.taskifyapp.util.convertLocalDateToMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

enum class UserHomeScreens {
    TASKS,
    CALENDAR,
    SETTINGS
}

enum class GroupTasksType {
    ALL,
    TODAY,
    PLANNED,
    COMPLETED;

    companion object {
        fun fromInt(value: Int) = enumValues<GroupTasksType>().getOrNull(value) ?: ALL
    }
}

data class TasksUiState(
    val groupTasksType: GroupTasksType,
    val sortType: SortType = SortType.Date,
    val userHomeScreens: UserHomeScreens = UserHomeScreens.TASKS,
    val selectedDate: Long? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val workRepository: WorkManagerRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _tasksUiState = MutableStateFlow<TasksUiState?>(null)
    val tasksUiState = _tasksUiState.asStateFlow()

    private val _groupedTasks = MutableStateFlow(emptyMap<Int, List<Task>>())
    val groupedTasks = _groupedTasks.asStateFlow()

    private val _allCalendarTasks = MutableStateFlow(emptyMap<Int, List<Task>>())
    val allCalendarTasks = _allCalendarTasks.asStateFlow()

    private val groupOrder = listOf(R.string.completed, R.string.overdue, R.string.not_planned, R.string.active, R.string.today, R.string.planned)
    private val today = LocalDate.now()

    init {
        workRepository.scheduleDailyCheckAlarm()

        viewModelScope.launch {
            val initialType = userPreferencesRepository.groupTaskTypeFlow.first()
            _tasksUiState.value = TasksUiState(groupTasksType = initialType)
            if (initialType == GroupTasksType.TODAY) {
                setSelectedDay(convertLocalDateToMillis(today))
            }
        }

        viewModelScope.launch {
            val currentDate = LocalDate.now()
            repository.getAllCalendarTasks()
                .map { tasks ->
                    tasks.groupBy {
                        when {
                            it.isCompleted -> R.string.completed
                            convertMillisToDate(it.date!!) < currentDate -> R.string.overdue
                            convertMillisToDate(it.date!!) == currentDate -> R.string.today
                            else -> R.string.planned
                        }
                    }
                }
                .collect { grouped ->
                    _allCalendarTasks.value = grouped
                }
        }

        viewModelScope.launch {
            repository.allTask.combine(_tasksUiState.filterNotNull()) { tasks, uiState ->
                val currentDate = LocalDate.now()
                val grouped = when (uiState.groupTasksType) {
                    GroupTasksType.ALL -> {
                        tasks.groupBy {
                            when {
                                it.isCompleted -> R.string.completed
                                it.date != null && convertMillisToDate(it.date) < currentDate -> R.string.overdue
                                it.date == null -> R.string.not_planned
                                else -> R.string.active
                            }
                        }
                    }
                    GroupTasksType.TODAY -> {
                        tasks.filter { it.date != null && convertMillisToDate(it.date) <= currentDate }
                            .groupBy {
                                when {
                                    it.isCompleted -> R.string.completed
                                    convertMillisToDate(it.date!!) == currentDate -> R.string.today
                                    else -> R.string.overdue
                                }
                            }
                    }
                    GroupTasksType.COMPLETED -> {
                        mapOf(R.string.completed to tasks.filter { it.isCompleted })
                    }
                    GroupTasksType.PLANNED -> {
                        mapOf(R.string.planned to tasks.filter {
                            it.date != null && !it.isCompleted && convertMillisToDate(it.date) >= currentDate
                        })
                    }
                }
                val sortedGroupedTasks = LinkedHashMap<Int, List<Task>>()
                for (group in groupOrder) {
                    grouped[group]?.let { taskList ->
                        sortedGroupedTasks[group] = when(uiState.sortType) {
                            SortType.Title -> taskList.sortedBy { it.title?.lowercase() }
                            SortType.Date -> taskList.sortedBy { it.date }
                            SortType.Priority -> taskList.sortedByDescending { it.priority }
                        }
                    }
                }
                sortedGroupedTasks
            }
            .collect { groupedTasks ->
                _groupedTasks.value = groupedTasks
            }
        }
    }

    fun cancelDailyCheck() {
        workRepository.cancelDailyCheckAlarm()
    }

    fun getDate() : String? {
        return if (_tasksUiState.value!!.userHomeScreens == UserHomeScreens.CALENDAR) {
            return _tasksUiState.value!!.selectedDate?.toString()
        } else if (_tasksUiState.value!!.userHomeScreens == UserHomeScreens.TASKS && _tasksUiState.value!!.groupTasksType == GroupTasksType.TODAY) {
            return convertLocalDateToMillis(today).toString()
        } else {
            null
        }
    }

    fun setSortType(sortType: SortType) { _tasksUiState.update { it?.copy(sortType = sortType) }}

    fun setGroupTasksType(newType: GroupTasksType) {
        _tasksUiState.update { it?.copy(groupTasksType = newType) }
        viewModelScope.launch { userPreferencesRepository.saveGroupTaskType(newType) }
    }

    fun setUserHomeScreens(homeScreen: UserHomeScreens) {
        if (homeScreen == UserHomeScreens.CALENDAR && _tasksUiState.value?.selectedDate == null) {
            setSelectedDay(convertLocalDateToMillis(today))
        }
        _tasksUiState.update { it?.copy(userHomeScreens = homeScreen) }
    }

    fun setSelectedDay(selectedDate: Long) {
        _tasksUiState.update { it?.copy(selectedDate = selectedDate) }
    }

    fun deleteSelectedTasks(selectedTaskIds: Set<Int>) {
        viewModelScope.launch {
            repository.deleteSpecificTasks(selectedTaskIds)
        }
    }

    fun markAsCompleted(group: Int, taskId: Int) {
        viewModelScope.launch {
            _groupedTasks.value[group]?.find { it.id == taskId }?.let {
                val updated = it.copy(
                    isCompleted = true,
                    deletionTime = System.currentTimeMillis() + DELAY_FOR_DELETE
                )
                repository.updateTask(updated)
                delay(400)

                workRepository.scheduleCompletedTask(it.id)
                workRepository.cancelAlarmNotification(it.id, it.title)
            }
        }
    }

    fun allowTaskCreation() : Boolean {
        return _tasksUiState.value?.userHomeScreens != UserHomeScreens.SETTINGS
    }

    private fun convertMillisToDate(date: Long): LocalDate {
        return Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}
