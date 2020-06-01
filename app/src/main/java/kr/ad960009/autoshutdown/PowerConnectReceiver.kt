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
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                Log.d(TAG, "Power disconnected")
                MakeJob(context!!, JobWorker.POWER_OFF, TimeUnit.MINUTES.toMillis(10))
            }
        }
    }
}