package com.example.mundopeliculas.ui.notifications

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NotificationsManager {
    private const val PREFS_NAME = "notifications_prefs"
    private const val KEY_NOTIFICATIONS = "app_notifications"
    private val gson = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getNotifications(context: Context): MutableList<AppNotification> {
        val json = getPrefs(context).getString(KEY_NOTIFICATIONS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<AppNotification>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addNotification(context: Context, title: String, message: String, type: String) {
        val notifications = getNotifications(context)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = sdf.format(Date())
        
        val newNotification = AppNotification(
            id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            title = title,
            message = message,
            time = currentTime,
            type = type
        )
        
        notifications.add(0, newNotification) // Add at the beginning
        
        // Limit to last 20 notifications
        val limitedList = if (notifications.size > 20) notifications.take(20) else notifications
        
        saveNotifications(context, limitedList)
    }

    fun removeNotification(context: Context, notificationId: Int) {
        val notifications = getNotifications(context)
        val removed = notifications.removeAll { it.id == notificationId }
        if (removed) {
            saveNotifications(context, notifications)
        }
    }

    fun clearAll(context: Context) {
        saveNotifications(context, emptyList())
    }

    private fun saveNotifications(context: Context, notifications: List<AppNotification>) {
        val json = gson.toJson(notifications)
        getPrefs(context).edit().putString(KEY_NOTIFICATIONS, json).apply()
    }
}