package com.alexaat.flowershop.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.alexaat.flowershop.R
import com.alexaat.flowershop.network.FlowersApi
import com.alexaat.flowershop.util.sendNotification
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class AlarmReceiver:BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {

        val job = Job()
        val coroutineScope = CoroutineScope(job + Dispatchers.IO)
        coroutineScope.launch {
            val getFlowersInCartDeferred = FlowersApi.retrofitService.getFlowersInCartAsync()
            try{
                val flowerResult = getFlowersInCartDeferred.await()
                if(flowerResult.isNotEmpty()){
                    val futureTarget = Glide.with(context)
                            .asBitmap()
                            .load(flowerResult[0].img_src)
                            .error(R.drawable.ic_cart_full)
                            .submit()

                    coroutineScope.launch(Dispatchers.IO) {
                        val bitmap = futureTarget.get()
                        val notificationManager = ContextCompat.getSystemService(
                            context,
                            NotificationManager::class.java
                        ) as NotificationManager
                        notificationManager.sendNotification(
                            context.getString(R.string.you_have_items_in_cart),
                            context,
                            bitmap
                        )
                    }
                }
            }catch (e:Exception){
            }
        }
    }

}