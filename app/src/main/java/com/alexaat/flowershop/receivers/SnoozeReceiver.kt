package com.alexaat.flowershop.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.alexaat.flowershop.util.*

class SnoozeReceiver:BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
        notificationManager.cancel(ITEMS_IN_CART_NOTIFICATION_ID)

        cancelAllAlarms(context)
        setAlarms(context)
    }

}

