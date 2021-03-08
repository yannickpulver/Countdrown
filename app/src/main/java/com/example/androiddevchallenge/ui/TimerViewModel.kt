/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui

import android.os.CountDownTimer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor() : BaseViewModel() {

    private val _state = MutableStateFlow(Timer.default())
    val state: StateFlow<Timer> = _state

    private var countDownTimer: CountDownTimer? = null

    fun play() {
        updateState { it.copy(isPaused = false) }
        countDownTimer = getCountdown(_state.value).apply { start() }
    }

    fun pause() {
        updateState { it.copy(isPaused = true) }
        countDownTimer?.cancel()
    }

    private fun getCountdown(timer: Timer) : CountDownTimer {
        val millis = timer.minutes * 1000 * 60 + timer.seconds * 1000

        return object : CountDownTimer(millis, 1000) {
            override fun onTick(millis: Long) {
                val minutes = millis / 1000 / 60
                val seconds = millis / 1000 % 60
                updateState { it.copy(minutes = minutes, seconds = seconds) }
            }

            override fun onFinish() {
                TODO("Not yet implemented")
            }
        }
    }


    @Synchronized
    private fun updateState(callback: (Timer) -> Timer) = ioScope.launch {
        val oldState = _state.value
        val newState = callback(oldState)
        _state.emit(newState)
    }

}
