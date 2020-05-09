package com.alexaat.flowershop.network

import java.text.SimpleDateFormat
import java.util.*

data class MyOrder(
    val order_id:Long,
    val date:Long,
    val orders:List<Flower>

){
    fun getFormattedDate():String{
        val sdf = SimpleDateFormat("dd-MMM-YYYY HH:mm",Locale.UK)
        return sdf.format(Date(date))
    }
}



