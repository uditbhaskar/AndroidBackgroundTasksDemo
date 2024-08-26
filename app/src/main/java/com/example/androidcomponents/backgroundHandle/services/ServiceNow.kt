package com.example.androidcomponents.backgroundHandle.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast

class ServiceNow : Service() {

    private val tag = "ServiceNow"
    private val startCommand = "Service Started"
    private val endCommand = "Service Stopped"

    override fun onBind(intent: Intent?): IBinder? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, startCommand)
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(tag, endCommand)
        Toast.makeText(this, "Service Ended", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }

}