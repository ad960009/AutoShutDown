package kr.ad960009.autoshutdown

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

val TAG = "ad960009"

fun MakeJob(context: Context, jobID: Int, nextMills: Long) {
    val jobService = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    val componentName = ComponentName(context, JobWorker::class.java)
    val job = JobInfo.Builder(jobID, componentName)
        .setMinimumLatency(nextMills)
        .setOverrideDeadline(nextMills * 2).build()
    val result = jobService.schedule(job) == JobScheduler.RESULT_SUCCESS
    Log.d(TAG, "Call Make job($jobID): $result")
}

fun RemoveJob(context: Context, jobID: Int) {
    val jobService = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    jobService.cancel(jobID)
    Log.d(TAG, "Call cancel job")
}

class JobWorker : JobService() {
    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Called onStopJob ${params?.jobId}")
        return false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Called onStartJob ${params?.jobId}")
        when (params?.jobId) {
            POWER_OFF -> powerOff()
            BATTERY_CHECK -> {
                CheckBattery()
            }
            START_TORQUE -> {
                startTorque()
            }
        }

        return false
    }

    fun IsCharging(): Boolean {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            registerReceiver(null, ifilter)
        }
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

        // How are we charging?
        val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
        return isCharging
    }

    fun CheckBattery() {
        val isCharging = IsCharging()
        Log.d(TAG, "Charging in BatteryCheck: $isCharging")
        if (isCharging) {
            RemoveJob(this, POWER_OFF)
        } else {
            MakeJob(this, POWER_OFF, TimeUnit.MINUTES.toMillis(7))
        }
        MakeJob(this, JobWorker.BATTERY_CHECK, TimeUnit.MINUTES.toMillis(10))
    }

    fun powerOff() {
        val isCharging = IsCharging()
        Log.d(TAG, "Charging in PowerOff: $isCharging")
        if (!isCharging)
            Runtime.getRuntime().exec("su -c svc power shutdown")
    }

    fun startTorque() {
        val proc = Runtime.getRuntime()
            .exec("su -c am start -a android.intent.action.Main -n org.prowl.torque/org.prowl.torque.landing.FrontPage")
        proc.waitFor()
        val reader = BufferedReader(InputStreamReader(proc.inputStream))

        while (true) {
            var line: String? = reader.readLine() ?: break
            Log.d(TAG, line)
        }
        Log.d(TAG, "am start exit: ${proc.exitValue()}")
    }

    companion object {
        val BATTERY_CHECK = 0
        val POWER_OFF = 1
        val START_TORQUE = 2
    }
}