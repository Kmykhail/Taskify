package com.kote.taskifyapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kote.taskifyapp.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    val tasks = repository.getAllTasks().asLiveData()

    init {
        println("Hello from TaskViewModel ${tasks.value?.size ?: 0}")
    }
}