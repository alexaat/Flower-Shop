package com.alexaat.flowershop


import android.content.Intent
import android.widget.Toast
import com.alexaat.flowershop.network.FlowersApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private var viewModelJob = Job()
private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )
const val NEW_MESSAGE_INTENT_ACTION = "com.alexaat.flowershop.broadcasts.notifyNewMessage"


class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        coroutineScope.launch {
            val intent = Intent()
            intent.action = NEW_MESSAGE_INTENT_ACTION
            applicationContext.sendBroadcast(intent)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        registerToken(token)
    }

    private fun registerToken(token: String) {
        coroutineScope.launch (Dispatchers.IO){
            val registerNewTokenDeferred = FlowersApi.retrofitService.registerNewTokenAsync(token)
            try{
                val resp =  registerNewTokenDeferred.await()

            }catch(e:Exception){

            }
        }
    }
}