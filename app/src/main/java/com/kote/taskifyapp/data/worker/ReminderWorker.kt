package com.kote.taskifyapp.data.worker

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kote.taskifyapp.CHANNEL_ID
import com.kote.taskifyapp.CHANNEL_NAME
import com.kote.taskifyapp.KEY_DESCRIPTION
import com.kote.taskifyapp.KEY_TITLE
import com.kote.taskifyapp.MainActivity
import com.kote.taskifyapp.R

class ReminderWorker(
    private val ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: "Reminder"
        val description = inputData.getString(KEY_DESCRIPTION) ?: "Reminder's description"

        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(0, createNotification(title, description))
            }
        }

        return Result.success()
    }

    private fun createNotification(title: String, description: String): Notification {
        createNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_android)
            .setContentTitle(title)
            .setContentText(description)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for task reminders"
            }
            notificationManager.createNotificationChannel(channel)
            Log.d("Debug", "Added notification channel")
        } else {
            Log.d("Debug", "No notification channel")
        }
    }
}