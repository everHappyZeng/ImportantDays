package com.example.importantdays.service

import android.util.Log

/**
 * WeChat Official Account notification service implementation
 * TODO: Implement actual WeChat notification sending when backend is ready
 */
class WeChatNotificationService : NotificationService {

    override suspend fun sendNotification(recipient: String, title: String, message: String): Boolean {
        // TODO: Call backend API to send WeChat notification
        Log.d("WeChatNotificationService", "WeChat notification would be sent to $recipient: $title - $message")

        // Placeholder implementation
        // When backend is ready, implement like:
        // val response = apiClient.sendWeChatNotification(recipient, title, message)
        // return response.isSuccessful

        return false // Return false until implemented
    }
}
