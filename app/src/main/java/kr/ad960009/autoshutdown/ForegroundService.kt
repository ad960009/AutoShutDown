package kr.ad960009.autoshutdown

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.concurrent.TimeUnit

class ForegroundService : Service() {

    lateinit var receiver: PowerConnectReceiver

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        receiver = PowerConnectReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        registerReceiver(receiver, filter)
        SetForeground()
        Log.d(TAG, "onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            Log.d("ad960009", "Foreground service start")
            val pkgName = intent.action
            if (pkgName == null) {
                Log.d("ad960009", "Foreground service pkgName null")
                return START_STICKY
            }
            if (pkgName.equals("")) {
                Log.d("ad960009", "Foreground service pkgName empty")
                return START_STICKY
            }

            if (pkgName.startsWith("."))
            {
                val time = intent.getLongExtra("time", 0)
                val timerIntent = Intent(this, TimerActivity::class.java)
                timerIntent.putExtra("time", time)
                timerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //startActivity(timerIntent)
            }

            val newIntent = packageManager.getLaunchIntentForPackage(pkgName)
            if (newIntent != null) {
                startActivity(newIntent)
                Log.d("ad960009", "Start App $pkgName with ${newIntent.action}")
                for (category in newIntent.categories) {
                    Log.d("ad960009", "\tcategory: $category")
                }
            } else {
                Log.d("ad960009", "Can't start App $pkgName intent null")
            }
            MakeJob(this, JobWorker.POWER_OFF, TimeUnit.MINUTES.toMillis(10))
        }

        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    fun SetForeground() {
        val channel = NotificationChannel(
            TAG,
            "Connection Receiver",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )

        val builder = NotificationCompat.Builder(this, TAG)
        builder.setSmallIcon(R.drawable.placeholder)
        builder.setContentTitle(TAG)
        builder.setContentText("서비스 실행중")

        val indent = Intent(this, this::class.java)
        indent.action = exitAction
        val pendingIntent = PendingIntent.getForegroundService(this, 0, indent, 0)

        builder.setContentIntent(pendingIntent)
        startForeground(5, builder.build())
    }

    companion object {
        const val startAction = "Start"
        const val exitAction = "Exit"
    }
}
