# Prepare for Constants

 

```kotlin
object Constants {

    const val NOTIFICATION_CHANNEL_ID = "notificatoin_channel_id"
    const val NOTIFICATION_CHANNEL_NAME = "notification_channel"
    const val NOTIFICATION_REQUEST_CODE = 3434

}
```

# Application()

- Create Notification Channel

```kotlin
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
```

# Create BroadcastReceiver

### Create notification layout

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="256dp"
    android:orientation="vertical">

    <TextView
        android:id="@+id/textview_expanded"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="8dp"
        android:gravity="center_horizontal"
        android:text="This is a custom notification"
        style="@style/TextAppearance.Compat.Notification"
        android:background="@android:color/holo_red_light"
        android:textColor="#FFFFFF" />

    <ImageView
        android:id="@+id/imageview_expanded"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop"
        android:src="@drawable/image1"/>

</LinearLayout>
```

### Write Notification Code with your layout

```kotlin
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
```

# IntervalNotificationManager

```kotlin
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
```

# MainActivity with time picker

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            setNotificationSchedule()
        }

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
```