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
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CountDownTimerViewModel : ViewModel() {

    var remainingTime by mutableStateOf(CHALLENGE_TIME)
    var timerState by mutableStateOf(TIMER_START)
    var totalTime = CHALLENGE_TIME
    var progress by mutableStateOf(1.00f)
    private var timer: CountDownTimer? = null

    fun startTimer() {
        timer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                progress = millisUntilFinished.toFloat() / CHALLENGE_TIME
                timerState = TIMER_RUNNING
            }

            override fun onFinish() {
                timerState = TIMER_END
                totalTime = CHALLENGE_TIME
                progress = 0.00f
                remainingTime = 0
            }
        }.start()
    }

    fun pauseTimer() {
        timer?.cancel()
        timerState = TIMER_PAUSE
        totalTime = remainingTime
    }

    fun resetTimer() {
        timer?.cancel()
        timerState = TIMER_START
        totalTime = CHALLENGE_TIME
        remainingTime = totalTime
        progress = 1.00f
    }
}
