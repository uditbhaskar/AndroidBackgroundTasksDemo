package com.example.androidcomponents

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.androidcomponents.jobSchedulers.MyJobSchedulers
import com.example.androidcomponents.services.ServiceNowForeground
import com.example.androidcomponents.ui.theme.AndroidComponentsTheme
import com.example.androidcomponents.workManager.MyWorkManager
import java.util.UUID

class MainActivity : ComponentActivity() {
    private val jobId = 123
    private var workerID: UUID? = null
    private val tag = "MainActivity"
    private val serviceIntentForeground by lazy { Intent(this, ServiceNowForeground::class.java) }
    private val jobScheduler by lazy { getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler }
    private val workManager by lazy { WorkManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidComponentsTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp, 10.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StartComponents(onClick = { clickType -> onStartClick(clickType) })
                    RequestNotificationPermission()
                }
            }
        }
    }

    private fun onStartClick(clickType: ButtonConstants) {
        when (clickType) {
            ButtonConstants.START -> {
                try {
                    startForegroundService(serviceIntentForeground)
                } catch (e: Exception) {
                    Log.d(tag, "onStartClick: ${e.message}")
                }
            }

            ButtonConstants.STOP -> {
                stopService(serviceIntentForeground)
            }

            ButtonConstants.START_JOB_SCHEDULER -> {
                val jobInfo = JobInfo.Builder(jobId, ComponentName(this, MyJobSchedulers::class.java))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build()

                jobScheduler.schedule(jobInfo)
            }

            ButtonConstants.JOB_STOP -> {
                jobScheduler.cancel(jobId)
            }

            ButtonConstants.START_WORK_MANAGER -> {
                workerID = UUID.randomUUID()
                workerID?.let {
                    workManager.enqueue(
                        OneTimeWorkRequestBuilder<MyWorkManager>().setId(it).build()
                    )
                }
            }

            ButtonConstants.STOP_WORK_MANAGER -> {
                workerID?.let {
                    workManager.cancelWorkById(it)
                }
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

    val buttonColor = Color(0xFF00668b)

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
            colors = ButtonDefaults.buttonColors(containerColor = if (serviceStarted) Color.Red else buttonColor
            ),
        ) {
            Text(
                text = if (serviceStarted) "Stop Service" else "Start Service",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 18.sp
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
                containerColor = if (jobSchedulerStarted) Color.Red else buttonColor
            ),
        ) {
            Text(
                text = if (jobSchedulerStarted) "Stop JobScheduler" else "Start JobScheduler",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 18.sp
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
                containerColor = if (workManagerStarted) Color.Red else buttonColor
            ),
        ) {
            Text(
                text = if (workManagerStarted) "Stop Work Manager" else "Start Work Manager",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 18.sp
            )
        }
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

@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }
    if (!hasNotificationPermission) {
        LaunchedEffect(key1 = Unit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(POST_NOTIFICATIONS)
            }
        }
    }
}