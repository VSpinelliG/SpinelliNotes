package com.example.spinellinotes.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.spinellinotes.ExtendedNoteActivity
import com.example.spinellinotes.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(c: Context, i: Intent) {

        //ao clicar na notificação, a nota será exibida
        val resultIntent = Intent(c, ExtendedNoteActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        resultIntent.putExtra("noteId", i.getLongExtra("noteId", 0))

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(c).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        //val contentIntent: PendingIntent = PendingIntent.getActivity(c, i.getIntExtra("id", 100), intent, 0)

        val mBuilder = NotificationCompat.Builder(c, "1")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Notas do Spinelli")
            .setContentText(i.getStringExtra("title"))
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .setVibrate(listOf(1000L,1000L, 1000L, 1000L, 1000L).toLongArray())
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (i.getStringExtra("resume") != null) {
            mBuilder.setContentTitle(i.getStringExtra("title"))
                    .setContentText(i.getStringExtra("resume"))
        }

        val mNotificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(i.getIntExtra("id", 100), mBuilder.build())



    }
}