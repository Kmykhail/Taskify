package com.kote.taskifyapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    private val _sortType = MutableStateFlow(SortType.DATE)

    val tasks: StateFlow<List<Task>> = repository.allTask
        .combine(_sortType) { tasks, sortType ->
            when (sortType) {
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

    fun setSortType(sortType: SortType) { _sortType.value = sortType }

    fun markAsCompleted(taskId: Int) {
        viewModelScope.launch {
            tasks.value.find { it.id == taskId }?.let {
                val updated = it.copy(isCompleted = true)
                repository.updateTask(updated)
            }
        }
    }
}