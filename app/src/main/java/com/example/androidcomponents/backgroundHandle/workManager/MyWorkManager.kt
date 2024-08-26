package com.example.androidcomponents.backgroundHandle.workManager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorkManager(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val tag = "WorkManager"
    private val startCommand = "Work Started"
    private val endCommand = "Work Stopped"

    override fun doWork(): Result {
        Log.d(tag, startCommand)
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, startCommand, Toast.LENGTH_SHORT).show()
        }
        Thread.sleep(10000)
        return Result.success()
    }

    override fun onStopped() {
        Log.d(tag, endCommand)
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, endCommand, Toast.LENGTH_SHORT).show()
        }
        super.onStopped()
    }
}
