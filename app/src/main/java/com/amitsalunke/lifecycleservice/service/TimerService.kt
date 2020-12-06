package com.amitsalunke.lifecycleservice.service

import android.content.Intent
import androidx.lifecycle.LifecycleService
//to work on theory
class TimerService : LifecycleService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let{
            when(it){

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}