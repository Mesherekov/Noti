package com.example.noti

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class ForegroundService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    override fun onCreate() {
        super.onCreate()

    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start()
            Actions.START.toString() -> stopSelf()

        }
        return super.onStartCommand(intent, flags, startId)
    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("ForegroundServiceType")
    private fun start(){
        try {
            val noti = NotificationCompat.Builder(this, "noti_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Уведомляем")
                .setContentText("ROe")
                .build()
            startForeground(1, noti,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        }catch (ex: Exception){
            Log.e("ServiceError", ex.toString())
        }
    }
    enum class Actions {
        START, STOP
    }
}