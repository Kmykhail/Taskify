package com.kote.taskifyapp.data.repository

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kote.taskifyapp.KEY_DESCRIPTION
import com.kote.taskifyapp.KEY_TITLE
import com.kote.taskifyapp.data.worker.ReminderWorker
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderManagerRepository @Inject constructor(
    private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleNotification(
        title: String?,
        description: String?,
        duration: Long
    ) {
        val currentTime = System.currentTimeMillis()
        val delay = duration - currentTime
        if (delay > 0) {
            val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            reminderRequest.setInputData(createInputData(title, description))
            reminderRequest.setInitialDelay(delay, TimeUnit.MILLISECONDS)

            Log.d("Debug", "Reminder Time (Millis): $duration, long: $duration, current: $currentTime")
            Log.d("Debug", "Reminder Date-time: ${Date(duration)}")
            Log.d("Debug", "delay in milliseconds: $delay")

            workManager.enqueue(reminderRequest.build())
        }
    }

    private fun createInputData(title: String?, description: String?): Data {
        val builder = Data.Builder()
        builder
            .putString(KEY_TITLE, title ?: "")
            .putString(KEY_DESCRIPTION, description ?: "")
        return builder.build()
    }
}