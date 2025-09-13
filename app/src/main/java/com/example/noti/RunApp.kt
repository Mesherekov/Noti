package com.example.noti

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class RunApp: Application() {
    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelNoti = NotificationChannel(
                "noti_channel",
                "Notifications",
                NotificationManager.IMPORTANCE_MIN
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelNoti)
        }
    }
}