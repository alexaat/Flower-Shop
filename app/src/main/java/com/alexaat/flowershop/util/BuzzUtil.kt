package com.alexaat.flowershop.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity

fun buzz(context: Context){

    val patten =  longArrayOf(0,400)
    val vibrator = context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
    vibrator.let {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            it.vibrate(VibrationEffect.createWaveform(patten,-1))
        }else{
            it.vibrate(patten,-1)
        }
    }
}