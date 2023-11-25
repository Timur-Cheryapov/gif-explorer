package com.byticher.gif.domain

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi

fun Vibrator.clickEasy() = this.vibrate(
    VibrationEffect.createOneShot(50L, VibrationEffect.DEFAULT_AMPLITUDE)
)

fun Vibrator.clickMedium() = this.vibrate(
    VibrationEffect.createOneShot(70L, VibrationEffect.DEFAULT_AMPLITUDE)
)

fun Vibrator.clickHard() = this.vibrate(
    VibrationEffect.createOneShot(100L, VibrationEffect.DEFAULT_AMPLITUDE)
)

@RequiresApi(Build.VERSION_CODES.Q)
fun Vibrator.doubleClick() = this.vibrate(
    VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
)