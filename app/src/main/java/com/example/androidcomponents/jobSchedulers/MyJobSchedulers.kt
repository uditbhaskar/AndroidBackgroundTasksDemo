package com.example.androidcomponents.jobSchedulers

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import android.widget.Toast

class MyJobSchedulers: JobService() {
    val tag = "MyJobSchedulers"

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(tag, "Job started!")
        Toast.makeText(this, "Job Started", Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Toast.makeText(this, "Job Stopped", Toast.LENGTH_SHORT).show()
        Log.d(tag, "Job stopped!")
        jobFinished(params, false)
        return false
    }

}