package id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.MainActivity
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.R
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.repository.CHANNEL_ID
import id.ac.ui.cs.mobileprogramming.wikansetiaji.mediatodo.repository.SHRD_PRF_KEY


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel1"
            val descriptionText = "record notif"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val sharedPref: SharedPreferences = context.getSharedPreferences(SHRD_PRF_KEY, 0)
        val itemCount = sharedPref.getInt(SHRD_PRF_KEY,0)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)


        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_text_fields_black_24dp)
            .setContentTitle("What to do today?")
            .setContentText("You have $itemCount more things to do")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (itemCount>0){
            Log.v("ALARM",itemCount.toString())
            with(NotificationManagerCompat.from(context)) {
                notify(0,builder.build())
            }
        }
    }
}