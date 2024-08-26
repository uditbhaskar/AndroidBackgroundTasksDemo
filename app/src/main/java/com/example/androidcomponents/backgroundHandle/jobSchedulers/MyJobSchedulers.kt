package com.example.androidcomponents.backgroundHandle.jobSchedulers

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import android.widget.Toast

@SuppressLint("SpecifyJobSchedulerIdRange")
class MyJobSchedulers: JobService() {
    private val tag = "MyJobSchedulers"

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