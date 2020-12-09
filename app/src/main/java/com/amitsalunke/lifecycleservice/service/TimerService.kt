package com.amitsalunke.lifecycleservice.service

import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.from
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.amitsalunke.lifecycleservice.MainActivity
import com.amitsalunke.lifecycleservice.R
import com.amitsalunke.lifecycleservice.model.SharedTime
import com.amitsalunke.lifecycleservice.model.TimerEvent
import com.amitsalunke.lifecycleservice.util.Constants
import com.amitsalunke.lifecycleservice.model.SharedTimeEvent
import com.amitsalunke.lifecycleservice.util.TimerUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//to work on theory
class TimerService : LifecycleService() {
    private val TAG: String = "TimerService"

    //need to work for more efficent way
    /*companion object {
        val timerEvent = MutableLiveData<TimerEvent>()
    }*/

    private lateinit var notificationManager: NotificationManagerCompat
    private var isServiceStopped = false

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, " Service on create ")
        notificationManager = from(this)
        initValues()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                Constants.ACTION_START_SERVICE -> {
                    Log.e(TAG, "Started Service")
                    startForegroundService()
                }
                Constants.ACTION_STOP_SERVICE -> {
                    Log.e(TAG, "Stopped Service")
                    stopForegroundService()
                }
                else -> Log.e(TAG, "Not found command");
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun startForegroundService() {
        SharedTimeEvent.timerEvent.postValue(TimerEvent.START)
        startTimer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel()
        }
        startForeground(Constants.NOTIFICATION_ID, getNotificationBuilder().build())
        updateNotificationWithTime()
    }

    private fun updateNotificationWithTime() {
        SharedTime.timerInMillis.observe(this, Observer {
            if (!isServiceStopped) {
                val notiBuilder = getNotificationBuilder().setContentText(
                    TimerUtil.getFormattedTime(it, false)
                )
                notificationManager.notify(Constants.NOTIFICATION_ID, notiBuilder.build())
            }
        })
    }


    private fun stopForegroundService() {
        //SharedTimeEvent.timerEvent.postValue(TimerEvent.END)
        isServiceStopped = true
        initValues()
        notificationManager.cancel(Constants.NOTIFICATION_ID)
        stopForeground(true)
        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notificationChannel() {
        val notificationChannel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun getNotificationBuilder() =
        NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.twotone_alarm_black_24)
            .setContentTitle("Timer Life Cycle")
            .setContentText("00.00.00")
            .setContentIntent(getMainActivityPendingIntent())


    private fun getMainActivityPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            143,
            Intent(this, MainActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    //dealing with timer
    private fun initValues() {
        SharedTimeEvent.timerEvent.value = TimerEvent.END
        SharedTime.timerInMillis.value = 0L
    }

    private fun startTimer() {
        val timeStarted = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (!isServiceStopped && SharedTimeEvent.timerEvent.value == TimerEvent.START) {
                val lapTimer = System.currentTimeMillis() - timeStarted
                SharedTime.timerInMillis.postValue(lapTimer)
                delay(50L)
            }
        }
    }


}