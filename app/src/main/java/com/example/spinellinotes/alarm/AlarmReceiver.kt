package com.example.spinellinotes.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.spinellinotes.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(c: Context, i: Intent) {

        val mBuilder = NotificationCompat.Builder(c, "1")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Notas do Spinelli")
            .setContentText(i.getStringExtra("title"))
            .setAutoCancel(true)
            .setVibrate(listOf(1000L,1000L, 1000L, 1000L, 1000L).toLongArray())
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        val mNotificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(100, mBuilder.build())


    }
}