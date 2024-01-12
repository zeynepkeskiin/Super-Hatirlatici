package com.odev.model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.odev.activity.AlarmOnActivity
import com.odev.hatrlatici.R

class BroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        var music = p1?.getStringExtra("music")
        var time = p1?.getStringExtra("time")
        var note = p1?.getStringExtra("note")

        createNotificationChannel(p0!!)
        var mp : MediaPlayer
        if(music.isNullOrBlank()){
            mp =  MediaPlayer.create(p0, R.raw.alarm_sound)
            mp.start()
        }else {
            mp = MediaPlayer.create(p0, Uri.parse(music))
            mp.start()
        }

        var shakeDetector = ShakeDetector(p0){
            mp.stop()
            Toast.makeText(p0,"Alarm durduruldu", Toast.LENGTH_LONG).show()
        }
        shakeDetector.startListening()
        val notificationIntent = Intent(p0, AlarmOnActivity::class.java)
        notificationIntent.putExtra("time2",time)
        notificationIntent.putExtra("note2",note)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val requestCode = System.currentTimeMillis().toInt()

        val pendingIntent = PendingIntent.getActivity(
            p0, requestCode, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(p0!!, "channelId")
            .setSmallIcon(androidx.core.R.drawable.notification_action_background)
            .setContentTitle("Süper Hatırlatıcı")
            .setContentText("$note")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())

        Toast.makeText(p0,"Alarm çalıyor",Toast.LENGTH_LONG).show()
    }


    private fun createNotificationChannel(p0: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channelId",
                "Channel Name",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                p0?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}