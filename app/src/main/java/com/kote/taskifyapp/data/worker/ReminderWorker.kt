package com.kote.taskifyapp.data.worker

import android.app.Notification
import android.app.NotificationChannel
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kote.taskifyapp.CHANNEL_ID
import com.kote.taskifyapp.KEY_DESCRIPTION
import com.kote.taskifyapp.KEY_TITLE
import com.kote.taskifyapp.MainActivity
import com.kote.taskifyapp.R

class ReminderWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE)
        val description = inputData.getString(KEY_DESCRIPTION)

        showNotification(title!!, description!!)
        return Result.success()
    }

    private fun showNotification(title: String, description: String) {
        Log.d("Debug", "showNotification $title, $description")
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Reminder Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                "Channel for task reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_android)
            .setContentTitle(title)
            .setContentText(description)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        Log.d("Debug", "Notification: $notification")
        notificationManager.notify(0, notification.build())
    }
}