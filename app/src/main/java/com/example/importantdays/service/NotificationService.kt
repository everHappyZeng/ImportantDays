package com.example.importantdays.service

/**
 * Interface for external notification services (SMS, WeChat, etc.)
 * Implement this interface when backend services are ready
 */
interface NotificationService {
    /**
     * Send a notification through the service
     * @param recipient The recipient identifier (phone number, WeChat ID, etc.)
     * @param title The notification title
     * @param message The notification message
     * @return true if sent successfully, false otherwise
     */
    suspend fun sendNotification(recipient: String, title: String, message: String): Boolean
}
