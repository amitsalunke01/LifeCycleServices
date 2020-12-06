package com.amitsalunke.lifecycleservice.model

sealed class TimerEvent {
    object START : TimerEvent()
    object END : TimerEvent()
}