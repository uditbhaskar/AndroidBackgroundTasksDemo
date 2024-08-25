package com.example.androidcomponents

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.androidcomponents.jobSchedulers.MyJobSchedulers
import com.example.androidcomponents.services.ServiceNow
import com.example.androidcomponents.services.ServiceNowForeground
import com.example.androidcomponents.ui.theme.AndroidComponentsTheme
import com.example.androidcomponents.workManager.MyWorkManager
import java.util.UUID

class MainActivity : ComponentActivity() {
    private val jobId = 123
    private val workerID = UUID.randomUUID()
    private val tag = "MainActivity"
    private val serviceIntent by lazy { Intent(this, ServiceNow::class.java) }
    private val serviceIntentForeground by lazy { Intent(this, ServiceNowForeground::class.java) }
    private val jobScheduler by lazy { getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler }
    private val workManager by lazy {
        WorkManager.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidComponentsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp, 10.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StartComponents(onClick = { clickType -> onStartClick(clickType) })
                }
            }
        }
    }

    private fun onStartClick(clickType: ButtonConstants) {

        when (clickType) {
            ButtonConstants.START -> {
                //  startService(serviceIntent)
                try {
                    startForegroundService(serviceIntentForeground)
                } catch (e: Exception) {
                    Log.d(tag, "onStartClick: ${e.message}")
                }
            }

            ButtonConstants.STOP -> {
                // Assuming this code is inside an Activity or another component
                stopService(serviceIntentForeground)

            }

            ButtonConstants.START_JOB_SCHEDULER -> {
                val jobInfo =
                    JobInfo.Builder(jobId, ComponentName(this, MyJobSchedulers::class.java))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build()

                jobScheduler.schedule(jobInfo)
            }

            ButtonConstants.JOB_STOP -> {
                jobScheduler.cancel(jobId)
            }

            ButtonConstants.START_WORK_MANAGER -> {
                workManager.enqueue(
                    OneTimeWorkRequestBuilder<MyWorkManager>().setId(workerID).build()
                )
            }

            ButtonConstants.STOP_WORK_MANAGER -> {
                workManager.cancelWorkById(workerID)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "OnStop")
    }

    override fun onDestroy() {
        Log.d(tag, "OnDestroy")
        super.onDestroy()
    }

}


@Composable
fun StartComponents(onClick: (ButtonConstants) -> Unit) {
    var serviceStarted by remember { mutableStateOf(false) }
    var jobSchedulerStarted by remember { mutableStateOf(false) }
    var workManagerStarted by remember { mutableStateOf(false) }
    Column {
        Button(
            onClick = {
                if (serviceStarted) {
                    onClick(ButtonConstants.STOP)
                    serviceStarted = false
                } else {
                    onClick(ButtonConstants.START)
                    serviceStarted = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (serviceStarted) Color.Red else Color.Gray
            )
        ) {
            Text(
                text = if (serviceStarted) "Stop Service" else "Start Service",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (jobSchedulerStarted) {
                    onClick(ButtonConstants.JOB_STOP)
                    jobSchedulerStarted = false
                } else {
                    onClick(ButtonConstants.START_JOB_SCHEDULER)
                    jobSchedulerStarted = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (jobSchedulerStarted) Color.Red else Color.Gray
            )
        ) {
            Text(
                text = if (jobSchedulerStarted) "Stop JobScheduler" else "Start JobScheduler",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (workManagerStarted) {
                    onClick(ButtonConstants.STOP_WORK_MANAGER)
                    workManagerStarted = false
                } else {
                    onClick(ButtonConstants.START_WORK_MANAGER)
                    workManagerStarted = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (workManagerStarted) Color.Red else Color.Gray
            )
        ) {
            Text(
                text = if (workManagerStarted) "Stop Work Manager" else "Start Work Manager",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidComponentsTheme {
        StartComponents(onClick = {})
    }
}

enum class ButtonConstants {
    START,
    STOP,
    START_JOB_SCHEDULER,
    JOB_STOP,
    START_WORK_MANAGER,
    STOP_WORK_MANAGER
}