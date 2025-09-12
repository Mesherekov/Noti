package com.example.noti

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder

class ForegroundService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    override fun onCreate() {
        super.onCreate()

        startForeground(1, serviceNotification())
    }
    fun serviceNotification(): Notification{
        return Notification()
    }
}