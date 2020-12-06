package com.amitsalunke.lifecycleservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.amitsalunke.lifecycleservice.model.TimerEvent
import com.amitsalunke.lifecycleservice.service.TimerService
import com.amitsalunke.lifecycleservice.util.Constants
import com.amitsalunke.lifecycleservice.model.SharedTimeEvent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG: String = "MainActivity"
    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            Log.e(TAG, "clicked ")
            toggleButton()
        }

        setObservers()
    }

    private fun toggleButton() {
        if (!isTimerRunning) {
            Log.e(TAG, "Inside toggle button false ")
            sendCommandToService(Constants.ACTION_START_SERVICE)
        } else {
            Log.e(TAG, "Inside toggle button true")
            sendCommandToService(Constants.ACTION_STOP_SERVICE)
        }
    }

    private fun setObservers() {
        SharedTimeEvent.timerEvent.observe(this, Observer {
            when (it) {
                TimerEvent.START -> {
                    isTimerRunning = true
                    fab.setImageResource(R.drawable.twotone_stop_black_24)
                    Log.e(TAG, "I got start from Service")
                }

                TimerEvent.END -> {
                    isTimerRunning = false
                    fab.setImageResource(R.drawable.twotone_alarm_black_24)
                    Log.e(TAG, "I got stop from Service")
                }
            }
        })
    }

    private fun sendCommandToService(action: String) {
        Log.e(TAG, " send command to service ")
        startService(
            Intent(this, TimerService::class.java).apply {
                this.action = action
            }
        )
    }
}