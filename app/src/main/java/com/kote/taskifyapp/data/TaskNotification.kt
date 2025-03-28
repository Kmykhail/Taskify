package com.kote.taskifyapp.data

import android.app.NotificationChannel
import android.content.Context
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kote.taskifyapp.util.CHANNEL_ID
import com.kote.taskifyapp.util.CHANNEL_NAME
import com.kote.taskifyapp.MainActivity
import com.kote.taskifyapp.R

class TaskNotification(
    private val ctx: Context,
) {
    private val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(taskId: Int, title: String, description: String) {
        createNotificationChannel()
        Log.d("Debug", "Show notification, task id: $taskId, title: $title, description: $description")

        val intent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(ctx, taskId, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_done_outline_24)
            .setContentTitle(title)
            .setContentText(description)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(taskId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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