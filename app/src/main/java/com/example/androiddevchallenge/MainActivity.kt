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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    val viewModel = CountDownTimerViewModel()
    Surface(color = MaterialTheme.colors.background) {
        TimerView(viewModel)
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}

@Composable
fun TimerView(viewModel: CountDownTimerViewModel) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        TimerBackgroundImage()
        Column {
            TimerControlsView(viewModel = viewModel)
        }
    }
}

@Composable
fun TimerBackgroundImage() {
    val image: Painter = painterResource(id = R.drawable.countdown_bg)
    Image(
        painter = image, contentDescription = "",
        Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds,
    )
}

@Composable
fun TimerControlsView(viewModel: CountDownTimerViewModel) {
    val playIcon = dimensionResource(R.dimen.play_icon)
    val animatedProgress by animateFloatAsState(
        targetValue = viewModel.progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Box(
            modifier = Modifier
                .height(220.dp)
                .width(220.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            if (viewModel.remainingTime == 0L) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize().alpha(pauseTimeAnimation()),
                    progress = 1f,
                    color = Color.Red,
                    strokeWidth = 10.dp
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    progress = 1f,
                    color = colorResource(R.color.teal_800),
                    strokeWidth = 10.dp
                )
            }

            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = animatedProgress,
                color = colorResource(R.color.teal_200),
                strokeWidth = 10.dp
            )
            if (viewModel.timerState == TIMER_PAUSE || viewModel.remainingTime in 1..FINAL_SECONDS)
                TimeDisplay(
                    viewModel = viewModel,
                    Modifier
                        .align(Alignment.Center)
                        .alpha(pauseTimeAnimation())
                )
            else
                TimeDisplay(viewModel = viewModel, Modifier.align(Alignment.Center))
        }
        TimerButtons(viewModel = viewModel, playIcon = playIcon)
    }
}

@Composable
fun TimeDisplay(viewModel: CountDownTimerViewModel, modifier: Modifier) {
    Text(
        modifier = modifier.padding(6.dp),
        text = formattedTime(viewModel = viewModel),
        style = MaterialTheme.typography.h4,
        textAlign = TextAlign.Center,
        fontSize = timeFontSize(viewModel = viewModel),
        fontWeight = FontWeight.Bold,
        color = timeTextColor(viewModel = viewModel)
    )
}

@Composable
fun timeFontSize(viewModel: CountDownTimerViewModel): TextUnit {
    return if (viewModel.remainingTime <= FINAL_SECONDS) {
        100.sp
    } else {
        60.sp
    }
}

@Composable
fun formattedTime(viewModel: CountDownTimerViewModel): String {
    return if (viewModel.remainingTime <= FINAL_SECONDS) {
        viewModel.remainingTime.formattedTimeTwo()
    } else {
        viewModel.remainingTime.formattedTime()
    }
}

@Composable
fun timeTextColor(viewModel: CountDownTimerViewModel): Color {
    return if (viewModel.remainingTime in 1..FINAL_SECONDS) {
        Color.Red
    } else {
        Color.White
    }
}

@Composable
fun pauseTimeAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = ANIMATION_DURATION
                0.9f at 500
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    return alpha
}

@Composable
fun TimerButtons(viewModel: CountDownTimerViewModel, playIcon: Dp) {
    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StartTimerButton(viewModel = viewModel, playIcon = playIcon)
        Spacer(Modifier.size(100.dp))
        ResetTimerButton(viewModel = viewModel, playIcon = playIcon)
    }
}

@Composable
fun StartTimerButton(viewModel: CountDownTimerViewModel, playIcon: Dp) {
    IconButton(
        onClick = {
            if (viewModel.timerState == TIMER_RUNNING) {
                viewModel.pauseTimer()
            } else {
                viewModel.startTimer()
            }
        },
        modifier = Modifier
            .size(playIcon, playIcon)
    ) {
        UpdateTimerIcon(viewModel = viewModel, playIcon = playIcon)
    }
}

@Composable
fun UpdateTimerIcon(viewModel: CountDownTimerViewModel, playIcon: Dp) {
    when (viewModel.timerState) {
        TIMER_RUNNING -> {
            Icon(
                Icons.Filled.PauseCircleFilled,
                viewModel.timerState,
                Modifier.size(playIcon, playIcon),
                tint = colorResource(R.color.pause)
            )
        }
        else -> {
            Icon(
                Icons.Filled.PlayCircleFilled,
                viewModel.timerState,
                Modifier.size(playIcon, playIcon),
                tint = colorResource(R.color.start)
            )
        }
    }
}

@Composable
fun ResetTimerButton(viewModel: CountDownTimerViewModel, playIcon: Dp) {
    IconButton(
        onClick = {
            viewModel.resetTimer()
        },
        modifier = Modifier
            .size(playIcon, playIcon)
    ) {
        Icon(
            Icons.Filled.StopCircle,
            viewModel.timerState,
            Modifier.size(playIcon, playIcon),
            tint = colorResource(R.color.stop)
        )
    }
}

fun Long.formattedTime(): String = String.format(
    "%02d:%02d",
    TimeUnit.MILLISECONDS.toMinutes(this),
    TimeUnit.MILLISECONDS.toSeconds(this) % 60
)

fun Long.formattedTimeTwo(): String = String.format(
    "%02d",
    TimeUnit.MILLISECONDS.toSeconds(this) % 60
)
