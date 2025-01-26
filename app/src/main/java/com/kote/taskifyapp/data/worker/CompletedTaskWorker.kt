package com.kote.taskifyapp.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kote.taskifyapp.TASK_ID
import com.kote.taskifyapp.data.TaskDatabase
import com.kote.taskifyapp.data.repository.TaskRepository

class CompletedTaskWorker(
    private val ctx: Context,
    params: WorkerParameters,
): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val taskId = inputData.getString(TASK_ID) ?: return Result.failure()

        var resultState = Result.failure()
        val taskRepository = TaskRepository(TaskDatabase.getDataBase(ctx).taskDao())
        taskRepository.getTaskById(taskId.toInt())?.let {
            if (it.isCompleted && it.deletionTime != null && it.deletionTime <= System.currentTimeMillis()) {
                taskRepository.deleteTask(it)
                resultState = Result.success()
            } else {
                Log.w("Warning", "task scheduled for deletion but not completed, id:${it.id}, title:${it.title}")
                resultState = Result.failure()
            }
        }

        return resultState
    }
}