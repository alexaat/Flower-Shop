package com.alexaat.flowershop.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import com.alexaat.flowershop.uimain.MainActivity
import com.alexaat.flowershop.R
import com.alexaat.flowershop.receivers.SnoozeReceiver

const val ITEMS_IN_CART_NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0
private const val FLAGS = PendingIntent.FLAG_UPDATE_CURRENT

fun NotificationManager.sendNotification(messageBody: String, context: Context, cartImage: Bitmap){

    val contentIntent = Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        ITEMS_IN_CART_NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val snoozeIntent = Intent(context, SnoozeReceiver::class.java)
    val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        snoozeIntent,
        FLAGS)


    val builder = NotificationCompat.Builder(context, context.getString(R.string.items_in_cart_notification_channel_id))
        .setSmallIcon(R.drawable.ic_flower_in_pot)
        .setContentTitle(context.getString(R.string.items_in_cart_notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setLargeIcon(cartImage)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(
            R.drawable.ic_flower_in_pot,
            context.getString(R.string.snooze),
            snoozePendingIntent
        )


    notify(ITEMS_IN_CART_NOTIFICATION_ID, builder.build())
}
