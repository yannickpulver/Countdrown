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

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor() : BaseViewModel() {

    private val _state = MutableStateFlow(Timer.default())
    val state: StateFlow<Timer> = _state

    private var countDownTimer: CountDownTimerExtended? = null

    private val maxMinutes = 24f
    private val maxSeconds = 59f

    fun play() {
        updateState { it.copy(isPaused = false, progress = 1f) }
        countDownTimer = getCountdown(_state.value).apply { start() }
    }

    fun pause() {
        updateState { it.copy(isPaused = true) }
        countDownTimer?.cancel()
    }

    fun offsetMinutes(offset: Float) = updateState {
        var minutes = (it.minutes + offset)
        minutes = (if (minutes < 0) minutes + (maxMinutes + 1) else minutes) % (maxMinutes + 1)
        it.copy(minutes = minutes, progress = 0f)
    }

    fun offsetSeconds(offset: Float) = updateState {
        var seconds = (it.seconds + offset)
        seconds = (if (seconds < 0) seconds + (maxSeconds + 1) else seconds) % (maxSeconds + 1)
        it.copy(seconds = seconds, progress = 0f)
    }

    private fun getCountdown(timer: Timer): CountDownTimerExtended {
        val millis =
            timer.minutes.toLong() * 1000 * 60 + timer.seconds.toLong() * 1000 + 999 // Padding otherwise counter jumps around

        return object : CountDownTimerExtended(millis, 1000) {
            override fun onTick(millis: Long) {
                super.onTick(millis)
                val minutes = millis / 1000 / 60
                val seconds = millis / 1000 % 60
                val progress =
                    if (tickCount == 1) 1f else millis.toFloat() / millisInFuture.toFloat()
                updateState {
                    it.copy(
                        minutes = minutes.toFloat(),
                        seconds = seconds.toFloat(),
                        progress = progress
                    )
                }
            }

            override fun onFinish() {
                updateState { it.copy(progress = 0f) }
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
