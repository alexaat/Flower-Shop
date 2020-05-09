package com.alexaat.flowershop.network

import com.squareup.moshi.Json

data class Flower(
    val id:Long = 0,
    @Json(name = "icon_url") val img_src:String = "",
    val title:String="",
    val description: String ="",
    val price:Double = 0.0,
    val available:Double = 0.0

)