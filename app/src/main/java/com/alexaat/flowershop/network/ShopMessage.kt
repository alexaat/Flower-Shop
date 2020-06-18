package com.alexaat.flowershop.network

import java.text.SimpleDateFormat
import java.util.*

data class ShopMessage(
    val message_id:Long,
    val message_date:Long,
    val message:String,
    val opened:Boolean){

    fun getFormattedDate():String{
        val sdf = SimpleDateFormat("dd-MMM-YYYY HH:mm", Locale.UK)
        return sdf.format(Date(message_date))
    }
}