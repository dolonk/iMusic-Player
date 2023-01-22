package com.example.imusicplayer.Model.Ui

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.imusicplayer.R

class SplashScreen : AppCompatActivity() {

    private lateinit var mProgress: ProgressBar
    private var progressBarStatus = 0
    private var time: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        mProgress = findViewById(R.id.progressBarID)

        Thread {
            doWork()
            startApp()
            finish()
        }.start()
    }

    private fun doWork() {
        while (progressBarStatus < 100) {
            try {
                time += 10
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            progressBarStatus = time
            mProgress.progress = progressBarStatus
        }
    }

    private fun startApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

