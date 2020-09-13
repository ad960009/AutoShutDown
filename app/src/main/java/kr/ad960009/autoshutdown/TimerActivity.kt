package kr.ad960009.autoshutdown

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TimerActivity : AppCompatActivity() {
    var timer: CountDownTimer? = null
    lateinit var timerTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        val time = savedInstanceState!!.getLong("time", 0)
        if (time == 0L)
        {
            finishActivity(0)
        }
        timerTextView = findViewById<TextView>(R.id.textViewTimer)
    }

    override fun onResume() {
        super.onResume()
        timer = CountDown(10 * 60 * 1000, 10)
        timer?.start()
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        timer = null
    }

    fun UpdateText(millisUntilFinished: Long) {
        timerTextView.text =
            "${(millisUntilFinished / 1000)}.${((millisUntilFinished % 1000) / 10)}"
    }

    inner class CountDown(
        millisInFuture: Long,
        countDownInterval: Long
    ) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            this@TimerActivity.UpdateText(millisUntilFinished)
        }

        override fun onFinish() {
            this@TimerActivity.timer = null
            MakeJob(this@TimerActivity, JobWorker.POWER_OFF_NOW, 1000)
        }
    }
}