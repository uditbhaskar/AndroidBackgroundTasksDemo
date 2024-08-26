package com.example.androidcomponents.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.androidcomponents.R.drawable.ic_launcher_foreground

class ServiceNowForeground: Service() {

    private val tag = "ServiceNowForeground"
    private val startCommand = "Service ForeGround Started"
    private val endCommand = "Service ForeGround Stopped"
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, startCommand)
        Toast.makeText(this, "Service ForeGround Started", Toast.LENGTH_SHORT).show()


        val notification = NotificationCompat.Builder(this, "your_channel_id")
            .setContentTitle("Service Running") // Set the title of the notification
            .setContentText("This is a foreground service.") // Set the text content of the notification
            .setSmallIcon(ic_launcher_foreground) // Set the icon. Replace 'ic_notification' with your own drawable resource
            .build()

        startForeground(1, notification)
        return START_STICKY
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "your_channel_id",
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        Log.d(tag, endCommand)
        Toast.makeText(this, "Service ForeGround Stopped", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }
}