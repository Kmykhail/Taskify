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
import com.kote.taskifyapp.KEY_DESCRIPTION
import com.kote.taskifyapp.KEY_TITLE
import com.kote.taskifyapp.data.worker.ReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderManagerRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

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

            val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setConstraints(constraints)
                .setInputData(createInputData(title, description))
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            Log.d("Debug", "Reminder Time (Millis): $duration, long: $duration, current: $currentTime")
            Log.d("Debug", "Reminder Date-time: ${Date(duration)}")
            Log.d("Debug", "delay in milliseconds: $delay")

            workManager.enqueueUniqueWork(
                id.toString(),
                ExistingWorkPolicy.REPLACE,
                reminderRequest
            )

            val workInfos = workManager.getWorkInfosForUniqueWork(id.toString()).get()
            workInfos.forEach { workInfo ->
                Log.d("Debug", "WorkManager, State: ${workInfo.state}")
            }
        } else {
            Log.w("Warning", "Notification scheduled time is in the past")
        }
    }

    fun removeNotification(id: Int) {
        workManager.cancelUniqueWork(id.toString())
    }

    private fun createInputData(title: String?, description: String?): Data {
        val builder = Data.Builder()
        builder
            .putString(KEY_TITLE, title ?: "")
            .putString(KEY_DESCRIPTION, description ?: "")
        return builder.build()
    }
}