package com.example.pomodoro

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvTimer: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnStartPause: Button
    private lateinit var btnReset: Button
    private lateinit var tvStats: TextView

    private var timer: CountDownTimer? = null
    private var isTimerRunning = false
    private var isWorkMode = true
    
    private val workTimeInMillis: Long = 25 * 60 * 1000
    private val breakTimeInMillis: Long = 5 * 60 * 1000
    private var timeLeftInMillis: Long = workTimeInMillis

    private var completedCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        tvTimer = findViewById(R.id.tvTimer)
        progressBar = findViewById(R.id.progressBar)
        btnStartPause = findViewById(R.id.btnStartPause)
        btnReset = findViewById(R.id.btnReset)
        tvStats = findViewById(R.id.tvStats)

        loadStats()
        updateCountDownText()

        btnStartPause.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        btnReset.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeftInMillis, 10) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
                updateProgressBar()
            }

            override fun onFinish() {
                isTimerRunning = false
                if (isWorkMode) {
                    completedCount++
                    saveStats()
                    updateStatsText()
                    switchToBreak()
                } else {
                    switchToWork()
                }
            }
        }.start()

        isTimerRunning = true
        btnStartPause.text = "暂停"
    }

    private fun pauseTimer() {
        timer?.cancel()
        isTimerRunning = false
        btnStartPause.text = "继续"
    }

    private fun resetTimer() {
        timer?.cancel()
        isTimerRunning = false
        isWorkMode = true
        timeLeftInMillis = workTimeInMillis
        tvStatus.text = "专注中"
        btnStartPause.text = "开始"
        updateCountDownText()
        updateProgressBar()
    }

    private fun switchToBreak() {
        isWorkMode = false
        timeLeftInMillis = breakTimeInMillis
        tvStatus.text = "休息中"
        btnStartPause.text = "开始休息"
        updateCountDownText()
        updateProgressBar()
    }

    private fun switchToWork() {
        isWorkMode = true
        timeLeftInMillis = workTimeInMillis
        tvStatus.text = "专注中"
        btnStartPause.text = "开始专注"
        updateCountDownText()
        updateProgressBar()
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        tvTimer.text = timeFormatted
    }

    private fun updateProgressBar() {
        val totalTime = if (isWorkMode) workTimeInMillis else breakTimeInMillis
        val progress = (timeLeftInMillis.toDouble() / totalTime.toDouble() * 1000).toInt()
        progressBar.progress = progress
    }

    private fun updateStatsText() {
        tvStats.text = "今日已完成: $completedCount 次"
    }

    private fun saveStats() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt("completed_count", completedCount)
            apply()
        }
    }

    private fun loadStats() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        completedCount = sharedPref.getInt("completed_count", 0)
        updateStatsText()
    }
}
