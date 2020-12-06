package com.amitsalunke.lifecycleservice.model

import androidx.lifecycle.MutableLiveData

object SharedTime {
    val timerInMillis = MutableLiveData<Long>()
}