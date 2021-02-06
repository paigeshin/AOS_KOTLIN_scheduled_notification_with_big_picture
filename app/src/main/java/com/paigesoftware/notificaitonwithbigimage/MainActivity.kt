package com.paigesoftware.notificaitonwithbigimage

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            setNotificationSchedule()
        }

    }

    //notification이 되는지 안되는지 확인하는 함수
    private fun testNotification() {
        val repeatingIntent = Intent(this, MainActivity::class.java)
            repeatingIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //replace the same old activity when activity is already opened
            val requestCode = Constants.NOTIFICATION_REQUEST_CODE
            val pendingIntent = PendingIntent.getActivity(
                this,
                requestCode,
                repeatingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val expandedView = RemoteViews(packageName, R.layout.notification_expanded)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            val notificationBuilder = NotificationCompat.Builder(
                this,
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

    private fun setNotificationSchedule() {
        val myTimeListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            if(view.isShown) {
                IntervalNotificationManager(this@MainActivity).createNotification(hourOfDay, minute) {
                    Toast.makeText(this@MainActivity, "Created Notification!", Toast.LENGTH_LONG).show()
                    val textView = findViewById<TextView>(R.id.textView)
                    textView.setText("$hourOfDay:$minute")
                }
            }
        }
        val hourOfCalendar: Int = 12
        val minuteOfCalendar: Int = 0
        val timePickerDialog = TimePickerDialog(
            this@MainActivity,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            myTimeListener,
            hourOfCalendar,
            minuteOfCalendar,
            false
        )
        timePickerDialog.setTitle("스케쥴 바꾸기")
        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "확인", timePickerDialog)
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", timePickerDialog)

        timePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        timePickerDialog.show()
    }

}