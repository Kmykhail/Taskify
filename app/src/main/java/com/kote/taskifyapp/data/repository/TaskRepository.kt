package com.kote.taskifyapp.data.repository

import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.data.TaskDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    val allTask: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun getTaskById(taskId: Int): Task? = taskDao.getTaskById(taskId)
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    suspend fun deleteAllTasks() = taskDao.deleteAllTasks()
}