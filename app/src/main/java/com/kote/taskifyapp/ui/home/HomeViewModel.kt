package com.kote.taskifyapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = repository.allTask.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

//    init {
//        if (!taskId.isNullOrEmpty()) {
////            _dummyTask.value = repository.getTaskById(taskId = taskId.toInt())
//            viewModelScope.launch {
//                tasks.collect { taskList ->
//                    Log.d("Debug", "taskId: ${taskId}, task size ${taskList.size}")
//                    taskList.forEach {
//                        if (it.id == taskId.toInt()) {
//                            _dummyTask.value = it
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun createTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }
}