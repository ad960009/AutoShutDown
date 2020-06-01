package kr.ad960009.autoshutdown

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivityDummy : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_dummy)
        val b = findViewById<Button>(R.id.button)
        b.setOnClickListener { MakeJob(this, JobWorker.POWER_OFF, 1000) }
    }
}
