package com.paigesoftware.notificaitonwithbigimage

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat


class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val repeatingIntent = Intent(context, MainActivity::class.java)
        repeatingIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //replace the same old activity when activity is already opened
        val requestCode = Constants.NOTIFICATION_REQUEST_CODE
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            repeatingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val expandedView = RemoteViews(context?.packageName, R.layout.notification_expanded)
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val notificationBuilder = NotificationCompat.Builder(
            context!!,
            Constants.NOTIFICATION_CHANNEL_ID
        )
        val notification = notificationBuilder
            .setContentIntent(pendingIntent)
            .setCustomBigContentView(expandedView)
            .setCustomHeadsUpContentView(expandedView)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        notificationManager?.notify(0, notification)
    }




}