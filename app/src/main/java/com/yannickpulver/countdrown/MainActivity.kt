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
package com.yannickpulver.countdrown

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yannickpulver.countdrown.ui.TimerViewModel
import com.yannickpulver.countdrown.ui.theme.MyTheme
import com.yannickpulver.countdrown.ui.theme.blue300
import com.yannickpulver.countdrown.ui.theme.blue500
import com.yannickpulver.countdrown.ui.theme.red500
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MyTheme {
                ProvideWindowInsets {
                    MyApp()
                }
            }
        }
    }
}

// max it to 24min 3 seconds

// Start building your app here!
@Composable
fun MyApp() {
    val viewModel: TimerViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    Surface(color = MaterialTheme.colors.background) {

        if (state.ringing) {
            val value by animateFloatAsState(
                targetValue = 1f,
                animationSpec = snap(delayMillis = 1500)
            )
            // AnimatedVisibility(visible = state.ringing, exit = fadeOut(0f), enter = fadeIn(0f, animationSpec = tween(durationMillis = 1000, easing = LinearEasing))) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(red500)
                    .alpha(value)
            )
        }

        FloatingBackground(state.progress)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            if (state.isPaused) {
                // Reverting delta for more natural feel
                val minutesState = rememberDraggableState { viewModel.offsetMinutes((-it / 50)) }
                val secondsState = rememberDraggableState { viewModel.offsetSeconds((-it / 50)) }
                DraggableClock(state.niceMinutes, state.niceSeconds, minutesState, secondsState)
            } else {
                Text(
                    text = "${state.niceMinutes}:${state.niceSeconds}",
                    style = MaterialTheme.typography.h1
                )
            }

            if (state.isPaused) {
                PlayButton { viewModel.play() }
            } else {
                PauseButton { viewModel.pause() }
            }
            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(visible = state.isPaused) {
                Text(
                    stringResource(R.string.info),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp),
                    color = MaterialTheme.colors.onSurface.copy(if (state.isPaused) 0.4f else 0f),
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@Composable
private fun FloatingBackground(progress: Float) {
    val duration = if (progress == 1f) 400 else 1000
    val easing = if (progress == 1f) FastOutSlowInEasing else LinearEasing

    val backgroundProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = duration, easing = easing)
    )

    val infiniteTransition = rememberInfiniteTransition()
    val offset by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val infiniteTransition2 = rememberInfiniteTransition()
    val offset2 by infiniteTransition2.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(backgroundProgress)
        ) {
            Canvas(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                onDraw = {
                    val path2 = getWave(size.height, size.width * 2)
                    path2.translate(Offset(-size.width * offset2, 0f))
                    drawPath(path2, blue300)

                    val path = getWave(size.height, size.width * 2)
                    path.translate(Offset(-size.width * offset, 0f))
                    drawPath(path, blue500)
                }
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(blue500)
            )
        }
    }
}

private fun getWave(h: Float, w: Float): Path {
    return Path().apply {
        moveTo(0f, h * 0.5f)
        quadraticBezierTo(w * 0.125f, h * 0.25f, w * 0.25f, h * 0.5f)
        quadraticBezierTo(w * 0.375f, h * 0.75f, w * 0.5f, h * 0.5f)
        quadraticBezierTo(w * 0.625f, h * 0.25f, w * 0.75f, h * 0.5f)
        quadraticBezierTo(w * 0.875f, h * 0.75f, w, h * 0.5f)
        lineTo(w, h)
        lineTo(0f, h)
    }
}

@Composable
private fun DraggableClock(
    minutes: String,
    seconds: String,
    minutesDragState: DraggableState,
    secondsDragState: DraggableState
) {
    Row {
        Text(
            text = minutes,
            style = MaterialTheme.typography.h1,
            modifier = Modifier.draggable(
                orientation = Orientation.Vertical,
                state = minutesDragState

            )
        )
        Text(text = ":", style = MaterialTheme.typography.h1)
        Text(
            text = seconds,
            style = MaterialTheme.typography.h1,
            modifier = Modifier.draggable(
                orientation = Orientation.Vertical,
                state = secondsDragState
            )
        )
    }
}

@Composable
private fun PauseButton(onClick: () -> Unit) {
    FlatButton(onClick) {
        Icon(Icons.Filled.Pause, contentDescription = "Pause")
    }
}

@Composable
private fun PlayButton(onClick: () -> Unit) {
    FlatButton(onClick) {
        Icon(Icons.Filled.PlayArrow, contentDescription = "Pause")
    }
}

@Composable
private fun FlatButton(onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
    Button(
        onClick = onClick,
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = MaterialTheme.colors.primary.copy(0.5f)
        ),
        content = content
    )
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
