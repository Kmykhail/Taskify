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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortType {
    TITLE, DATE, PRIORITY
}

enum class FilterType {
    ALL, COMPLETED, INCOMPLETED
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val workRepository: WorkManagerRepository
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.DATE)
    private val _filterType = MutableStateFlow(FilterType.ALL)

    val tasks: StateFlow<List<Task>> = repository.allTask
        .combine(_sortType) { tasks, sortType ->
            when (sortType) {
                SortType.TITLE -> tasks.sortedBy { it.title }
                SortType.DATE -> tasks.sortedBy { it.date }
                SortType.PRIORITY -> tasks.sortedBy { it.priority }
            }
        }
        .combine(_filterType) { tasks, filterType ->
            when (filterType) {
                FilterType.ALL -> tasks
                FilterType.COMPLETED -> tasks.filter { it.isCompleted }
                FilterType.INCOMPLETED -> tasks.filter { !it.isCompleted }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSortType(sortType: SortType) { _sortType.value = sortType }
    fun setFilterType(filterType: FilterType) { _filterType.value = filterType }

    fun getNumberActiveTasks(): Int {
        return tasks.value.count { !it.isCompleted }
    }

    fun markAsCompleted(taskId: Int) {
        viewModelScope.launch {
            tasks.value.find { it.id == taskId }?.let {
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