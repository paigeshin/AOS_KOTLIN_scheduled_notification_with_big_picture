package com.paigesoftware.notificaitonwithbigimage

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class IntervalNotificationManager(
    private val context: Context
) {

    fun createNotification(hourOfDay: Int, minutes: Int, completion: () -> Unit) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minutes)
        val intent = Intent(context, NotificationReceiver::class.java)
        val requestCode = Constants.NOTIFICATION_REQUEST_CODE
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        completion()
    }

    fun cancelNotification() {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val requestCode = Constants.NOTIFICATION_REQUEST_CODE
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent =
            PendingIntent.getService(
                context, requestCode, intent,
                PendingIntent.FLAG_NO_CREATE
            )
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
    }


}