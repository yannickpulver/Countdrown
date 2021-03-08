package com.example.androiddevchallenge.ui

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
        fun default() = Timer(true, 9f, 3f, 0f)
    }
}

fun Long.toTwoDigits() = "%02d".format(this)