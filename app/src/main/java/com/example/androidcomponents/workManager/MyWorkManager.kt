package com.example.androidcomponents.workManager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorkManager(private val context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    private val tag = "Work Manager"
    private val startCommand = "Work Started"
    private val endCommand = "Work Stopped"

    override fun doWork(): Result {
        Log.d(tag, startCommand)
        Thread.sleep(10000)
//        Toast.makeText(context, startCommand, Toast.LENGTH_SHORT).show()
        return Result.failure()
    }

    override fun onStopped() {
        Log.d(tag, endCommand)
        super.onStopped()
    }
}