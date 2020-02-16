package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class DeviceBootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED" || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            Log.v("DEVICE BOOT","True")
            val alarmIntent =  Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
            val manager =  context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            var interval:Long = 1000 * 60 * 60 * 24
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)

            manager.cancel(pendingIntent)
            manager.setRepeating(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                interval, pendingIntent)
        }
    }
}