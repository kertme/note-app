package com.noteapp.core.ui.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPx():Float = LocalDensity.current.run { toPx() }
