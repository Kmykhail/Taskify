package com.kote.taskifyapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kote.taskifyapp.CLEANUP
import com.kote.taskifyapp.DELAY_FOR_DELETE
import com.kote.taskifyapp.KEY_DESCRIPTION
import com.kote.taskifyapp.KEY_TITLE
import com.kote.taskifyapp.NOTIFICATION
import com.kote.taskifyapp.TASK_ID
import com.kote.taskifyapp.data.workers.CompletedTaskWorker
import com.kote.taskifyapp.data.workers.NotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    // Notification start
    @SuppressLint("EnqueueWork")
    fun scheduleNotification(
        id: Int,
        title: String?,
        description: String?,
        duration: Long
    ) {
        val currentTime = System.currentTimeMillis()
        val delay = duration - currentTime

        if (delay > 0) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val reminderRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setConstraints(constraints)
                .setInputData(createNotificationInputData(title, description))
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            Log.d("Debug", "Reminder Time (Millis): $duration, long: $duration, current: $currentTime")
            Log.d("Debug", "Reminder Date-time: ${Date(duration)}")
            Log.d("Debug", "delay in milliseconds: $delay")

            workManager.enqueueUniqueWork(
                id.toString() + NOTIFICATION,
                ExistingWorkPolicy.REPLACE,
                reminderRequest
            )

            val workInfos = workManager.getWorkInfosForUniqueWork(id.toString() + NOTIFICATION).get()
            workInfos.forEach { workInfo ->
                Log.d("Debug", "WorkManager, notification state: ${workInfo.state}")
            }
        } else {
            Log.w("Warning", "Notification scheduled time is in the past")
        }
    }

    fun cancelNotification(id: Int) {
        workManager.cancelUniqueWork(id.toString() + NOTIFICATION)
    }

    private fun createNotificationInputData(title: String?, description: String?): Data {
        val builder = Data.Builder()
            .putString(KEY_TITLE, title ?: "")
            .putString(KEY_DESCRIPTION, description ?: "")
        return builder.build()
    }
    // Notification end

    // Cleanup start
    fun scheduleCompletedTask(id: Int) {
        Log.d("Debug",
            "WorkManager, `completed task` delay:$DELAY_FOR_DELETE, deletionTime:${System.currentTimeMillis() + DELAY_FOR_DELETE}")

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val cleanupRequest = OneTimeWorkRequestBuilder<CompletedTaskWorker>()
            .setConstraints(constraints)
            .setInputData(Data.Builder().putString(TASK_ID, id.toString()).build())
            .setInitialDelay(DELAY_FOR_DELETE, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniqueWork(
            id.toString() + CLEANUP,
            ExistingWorkPolicy.REPLACE,
            cleanupRequest
        )

        val workInfos = workManager.getWorkInfosForUniqueWork(id.toString() + CLEANUP).get()
        workInfos.forEach { workInfo ->
            Log.d("Debug", "WorkManager, `completed task` state: ${workInfo.state}")
        }
    }

    fun cancelCompletedTask(id: Int) {
        workManager.cancelUniqueWork(id.toString() + CLEANUP)
    }
    // Cleanup end
}


