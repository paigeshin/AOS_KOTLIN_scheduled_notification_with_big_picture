package com.paigesoftware.notificaitonwithbigimage

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationCahnnel()
    }

    private fun createNotificationCahnnel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(notificationChannel)
        }
    }

}