package com.amitsalunke.lifecycleservice.model

import androidx.lifecycle.MutableLiveData
import com.amitsalunke.lifecycleservice.model.TimerEvent

object SharedTimeEvent {
    val timerEvent = MutableLiveData<TimerEvent>()
}