package com.example.noti

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.icu.util.Calendar
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask


class ForegroundService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    override fun onCreate() {
        super.onCreate()

    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
        }
        var listNoti: MutableList<NotiInfo>

        val runCatch = runCatching {
            var listIntervalNoti = MainViewModel.getAllData(applicationContext).filter {
            it.isActive && it.period != 0
            }.toMutableList()
            val timer = Timer()
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        listIntervalNoti = MainViewModel.getAllData(applicationContext).filter {
                            it.isActive && it.period != 0
                        }.toMutableList()
                        listNoti = MainViewModel.getAllData(applicationContext).filter {
                            it.isActive && it.period == 0
                        }.toMutableList()
                        val currentTime = Calendar.getInstance().time
                        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                        listNoti.forEachIndexed { id, item ->

                            if (item.hour == currentTime.hours && item.minute == currentTime.minutes &&
                                (if(item.day!=null) item.day.id==currentDay else true)) {
                                sendNoti(item)
                                MainViewModel.updateNoti(
                                    applicationContext,
                                    item.copy(isActive = false)
                                )
                            }

                        }
                    }
                },
                0,
                10000
            )
            listIntervalNoti.forEach {
                val timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            sendNoti(it)
                        }
                    },
                    0,
                    it.period*1000*60L
                )
            }
        }
        runCatch.onFailure {
            Log.e("TimerError", it.toString())
        }
        return super.onStartCommand(intent, flags, startId)
    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("ForegroundServiceType")
    private fun start(){
        try {
            val noti = NotificationCompat.Builder(this, "noti_channel")
                .setSmallIcon(R.drawable.notif)
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
    fun sendNoti(info: NotiInfo) {
        val time = if(info.hour!=-1) LocalTime.of(info.hour,
            info.minute) else null
        val format24hShort = if (time!=null) time.format(DateTimeFormatter.ofPattern("HH:mm")) else "Раз в ${info.period} минут"
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val noti = NotificationCompat.Builder(this, "noti_channel")
            .setSmallIcon(R.drawable.notif)
            .setContentTitle(info.message)
            .setContentText(format24hShort)
            .setContentIntent(pendingIntent)
            .build()


        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(info.id, noti)
    }
}