package kr.ad960009.autoshutdown

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.concurrent.TimeUnit

class PowerConnectReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                Log.d(TAG, "Power connected")
                RemoveJob(context!!, JobWorker.POWER_OFF)
                val serviceIntent = Intent(context, ForegroundService::class.java)
                serviceIntent.action = ".TimerActivity"
                serviceIntent.putExtra("time", 0L)
                //context.startService(serviceIntent)
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                Log.d(TAG, "Power disconnected")
                MakeJob(context!!, JobWorker.POWER_OFF, TimeUnit.MINUTES.toMillis(10))
                val serviceIntent = Intent(context, ForegroundService::class.java)
                serviceIntent.action = ".TimerActivity"
                serviceIntent.putExtra("time", TimeUnit.MINUTES.toMillis(10))
                //context!!.startService(serviceIntent)
            }
        }
    }
}