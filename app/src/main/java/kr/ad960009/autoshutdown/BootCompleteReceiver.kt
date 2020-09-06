package kr.ad960009.autoshutdown

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.concurrent.TimeUnit

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //makeBatteryCheckJob(context!!)
        startService(context!!)
    }

    fun startTorque(context: Context) {
        MakeJob(context, JobWorker.START_TORQUE, 1000)
    }

    fun startService(context: Context) {
        val intent = Intent(context, ForegroundService::class.java)
        intent.action = "org.prowl.torque"
        context.startForegroundService(intent)
    }

    fun makeBatteryCheckJob(context: Context) {
        MakeJob(context, JobWorker.BATTERY_CHECK, TimeUnit.MINUTES.toMillis(5))
    }
}