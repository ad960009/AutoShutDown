package kr.ad960009.autoshutdown

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

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

            val intent = packageManager.getLaunchIntentForPackage(pkgName)
            if (intent != null) {
                startActivity(intent)
                Log.d("ad960009", "Start App $pkgName with ${intent.action}")
                for (category in intent.categories) {
                    Log.d("ad960009", "\tcategory: $category")
                }
            } else {
                Log.d("ad960009", "Can't start App $pkgName intent null")
            }
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
