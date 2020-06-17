package com.alexaat.flowershop.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import retrofit2.http.Headers


private const val BASE_URL = "http://alexaat.com/"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit = Retrofit
    .Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()


interface FlowersApiService{

    @GET("flowers")
    fun getFlowersAsync(): Deferred<List<Flower>>

    @GET("flowers")
    fun getFlowerAsync(@Query("id") id:Long): Deferred<Flower>

    @FormUrlEncoded
    @POST("flowers/")
    fun resetAsync(@Field("id") id:Long): Deferred<Boolean>

    @GET("flowers/cart")
    fun getFlowersInCartAsync(): Deferred<List<Flower>>

    @GET("flowers/cart")
    fun moveFlowerToCartAsync(@Query("id") id:Long): Deferred<Boolean>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "flowers/", hasBody = true)
    fun buyItemsInCartAsync(@Field("id") id:Long):Deferred<Boolean>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "flowers/cart/", hasBody = true)
    fun removeFromCartAsync(@Field("id") id:Long):Deferred<List<Flower>>

    @GET("flowers/orders")
    fun getMyOrdersAsync():Deferred<List<MyOrder>>

    //Messaging Service
    @GET("flowers/message/getMessages/")
    fun getShopMessagesAsync():Deferred<List<ShopMessage>>

    @FormUrlEncoded
    @POST("flowers/message/registerToken/")
    fun registerNewTokenAsync(@Field("token") token:String): Deferred<Boolean>

    @GET("flowers/message/setMessagesAsRead")
    fun setMessagesAsReadAsync():Deferred<Boolean>

}

object FlowersApi{
    val retrofitService: FlowersApiService by lazy {
        retrofit.create(FlowersApiService::class.java)
    }
}