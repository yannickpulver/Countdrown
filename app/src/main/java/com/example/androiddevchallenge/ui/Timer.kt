package com.example.androiddevchallenge.ui

data class Timer(val isPaused: Boolean, val minutes: Long, val seconds: Long) {
    companion object {
        fun default() = Timer(true, 9, 3)
    }
}