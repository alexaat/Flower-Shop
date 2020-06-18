package com.alexaat.flowershop.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexaat.flowershop.network.FlowersApi
import com.alexaat.flowershop.network.ShopMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MessageFragmentViewModel: ViewModel(){

private var viewModelJob = Job()
private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

private val _shopMessages = MutableLiveData<List<ShopMessage>>()
val shopMessages:LiveData<List<ShopMessage>>
    get() = _shopMessages

private val _loadingStatus = MutableLiveData<LoadingStatus>()
val loadingStatus:LiveData<LoadingStatus>
    get() = _loadingStatus



init{
    getMessages()
}

fun setMessagesAsOpened(){
    coroutineScope.launch {
        val setMessagedAsReadDeferred = FlowersApi.retrofitService.setMessagesAsReadAsync()
        try{
            val result = setMessagedAsReadDeferred.await()
        }catch(e:Exception){}
    }
}

fun getMessages(){
    _loadingStatus.value = LoadingStatus.LOADING
    coroutineScope.launch {
        val getShopMessagesDeferred = FlowersApi.retrofitService.getShopMessagesAsync()
        try{
            val result = getShopMessagesDeferred.await()
            _shopMessages.postValue(result)
            _loadingStatus.postValue(LoadingStatus.SUCCESS)
        }catch(e:Exception){
            _shopMessages.postValue(null)
            _loadingStatus.postValue(LoadingStatus.FAIL)
        }
    }
}


override fun onCleared() {
     viewModelJob.cancel()
     super.onCleared()
}
}