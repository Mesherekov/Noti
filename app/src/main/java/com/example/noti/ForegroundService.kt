package com.example.noti

import android.app.Notification
import android.app.NotificationChannel
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ForegroundService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    override fun onCreate() {
        super.onCreate()

        startForeground(1, serviceNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start()
            Actions.START.toString() -> stopSelf()

        }
        return super.onStartCommand(intent, flags, startId)
    }
    fun serviceNotification(): Notification{
        return Notification()
    }
    private fun start(){
        val noti = NotificationCompat.Builder(this, "noti_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Уведомляем")
            .setContentText("ROe")
            .build()
        startForeground(1, noti)
    }
    enum class Actions {
        START, STOP
    }
}