package com.example.importantdays.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.importantdays.ImportantDaysApplication
import com.example.importantdays.service.SmsNotificationService
import com.example.importantdays.service.WeChatNotificationService
import com.example.importantdays.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val smsService = SmsNotificationService()
    private val weChatService = WeChatNotificationService()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val dayId = inputData.getLong("day_id", -1L)
            val title = inputData.getString("title") ?: return@withContext Result.failure()
            val date = inputData.getString("date") ?: return@withContext Result.failure()
            val notificationChannels = inputData.getStringArray("notification_channels") ?: arrayOf()

            val app = applicationContext as ImportantDaysApplication
            val repository = app.container.repository

            // Get the day details
            val day = repository.getDayById(dayId)
            if (day == null) {
                return@withContext Result.failure()
            }

            val message = "Reminder: $title is coming up on $date"

            // Send notifications based on enabled channels
            notificationChannels.forEach { channel ->
                when (channel) {
                    "APP", "SYSTEM" -> {
                        NotificationHelper.sendReminderNotification(
                            applicationContext,
                            title,
                            message
                        )
                    }
                    "SMS" -> {
                        // TODO: Get user's phone number from settings/profile
                        val phoneNumber = "" // Placeholder
                        if (phoneNumber.isNotEmpty()) {
                            smsService.sendNotification(phoneNumber, title, message)
                        } else {
                            Log.w("ReminderWorker", "SMS enabled but no phone number configured")
                        }
                    }
                    "WECHAT" -> {
                        // TODO: Get user's WeChat ID from settings/profile
                        val wechatId = "" // Placeholder
                        if (wechatId.isNotEmpty()) {
                            weChatService.sendNotification(wechatId, title, message)
                        } else {
                            Log.w("ReminderWorker", "WeChat enabled but no WeChat ID configured")
                        }
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("ReminderWorker", "Error sending reminder", e)
            Result.failure()
        }
    }
}
