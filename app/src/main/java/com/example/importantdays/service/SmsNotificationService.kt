package com.example.importantdays.service

import android.util.Log

/**
 * SMS notification service implementation
 * TODO: Implement actual SMS sending when backend is ready
 */
class SmsNotificationService : NotificationService {

    override suspend fun sendNotification(recipient: String, title: String, message: String): Boolean {
        // TODO: Call backend API to send SMS
        Log.d("SmsNotificationService", "SMS would be sent to $recipient: $title - $message")

        // Placeholder implementation
        // When backend is ready, implement like:
        // val response = apiClient.sendSms(recipient, title, message)
        // return response.isSuccessful

        return false // Return false until implemented
    }
}
