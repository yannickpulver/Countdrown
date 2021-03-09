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
package com.yannickpulver.countdrown.ui

data class Timer(
    val isPaused: Boolean,
    val minutes: Float,
    val seconds: Float,
    val progress: Float
) {
    val niceSeconds get() = seconds.toLong().toTwoDigits()
    val niceMinutes get() = minutes.toLong().toTwoDigits()
    val ringing get() = !isPaused && progress == 0f && minutes == 0f && seconds == 0f

    companion object {
        fun default() = Timer(true, 1f, 0f, 0f)
    }
}

fun Long.toTwoDigits() = "%02d".format(this)
