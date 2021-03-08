package com.example.androiddevchallenge.ui

import android.os.CountDownTimer

abstract class CountDownTimerExtended(val millisInFuture: Long, val countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

    var tickCount: Int = 0

    override fun onTick(millis: Long) {
        tickCount++
    }
}