package com.alexaat.flowershop.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.alexaat.flowershop.network.FlowersApi
import com.alexaat.flowershop.receivers.AlarmReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val SECOND = 1000L
const val MINUTE = 60* SECOND
const val HOUR = 60* MINUTE
const val DAY = 24*HOUR

const val FIRST_ALARM = DAY
const val SECOND_ALARM = 7* DAY
const val THIRD_ALARM = 28* DAY

const val REQUEST_CODE_FIRST_ALARM = 0
const val REQUEST_CODE_SECOND_ALARM = 1
const val REQUEST_CODE_THIRD_ALARM = 2

private lateinit var notifyPendingIntent1: PendingIntent
private lateinit var notifyPendingIntent2: PendingIntent
private lateinit var notifyPendingIntent3: PendingIntent

private val job = Job()
private val coroutineScope = CoroutineScope(job + Dispatchers.Main)

fun setAlarms(app:Context){
    setPendingIntents(app)
    val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    coroutineScope.launch(Dispatchers.IO) {
        val getFlowersInCartDeferred = FlowersApi.retrofitService.getFlowersInCartAsync()
        try {
            val flowerResult = getFlowersInCartDeferred.await()
            if(flowerResult.isNotEmpty()){

                var triggerTime = System.currentTimeMillis()+FIRST_ALARM
                AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager,
                    AlarmManager.RTC_WAKEUP,triggerTime,notifyPendingIntent1)

                triggerTime = System.currentTimeMillis()+SECOND_ALARM
                AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager,
                    AlarmManager.RTC_WAKEUP,triggerTime,notifyPendingIntent2)

                triggerTime = System.currentTimeMillis()+THIRD_ALARM
                AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager,
                    AlarmManager.RTC_WAKEUP,triggerTime,notifyPendingIntent3)
            }

        }catch (e:Exception){}
    }
}


fun cancelAllAlarms(app:Context){
    setPendingIntents(app)
    val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(notifyPendingIntent1)
    alarmManager.cancel(notifyPendingIntent2)
    alarmManager.cancel(notifyPendingIntent3)
}

private fun setPendingIntents(app:Context){
    val notifyIntent = Intent(app, AlarmReceiver::class.java)
    notifyPendingIntent1 = PendingIntent.getBroadcast(
        app,
        REQUEST_CODE_FIRST_ALARM,
        notifyIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    notifyPendingIntent2 = PendingIntent.getBroadcast(
        app,
        REQUEST_CODE_SECOND_ALARM,
        notifyIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    notifyPendingIntent3 = PendingIntent.getBroadcast(
        app,
        REQUEST_CODE_THIRD_ALARM,
        notifyIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}




