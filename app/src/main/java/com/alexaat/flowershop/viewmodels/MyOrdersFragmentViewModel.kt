package com.alexaat.flowershop.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexaat.flowershop.network.FlowersApi
import com.alexaat.flowershop.network.MyOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyOrdersFragmentViewModel:ViewModel(){

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    private val _loadingStatus = MutableLiveData(LoadingStatus.LOADING)
    val loadingStatus:LiveData<LoadingStatus>
        get() = _loadingStatus

    init{
        getMyOrders()
    }

    private val _myOrders = MutableLiveData<List<MyOrder>>()
    val myOrders:LiveData<List<MyOrder>>
        get() = _myOrders

    private val _noOrderToDisplayNotification = MutableLiveData(false)
    val noOrderToDisplayNotification:LiveData<Boolean>
        get() = _noOrderToDisplayNotification

    private fun getMyOrders(){
        _loadingStatus.value = LoadingStatus.LOADING
        coroutineScope.launch {
            val getMyOrdersDeferred = FlowersApi.retrofitService.getMyOrdersAsync()
            try{
                val flowerResult = getMyOrdersDeferred.await()
                _myOrders.value = flowerResult
                _loadingStatus.value = LoadingStatus.SUCCESS
                if(flowerResult.isEmpty()){
                    _noOrderToDisplayNotification.value = true
                    _noOrderToDisplayNotification.value = false
                }
            }catch(e:Exception){
                _myOrders.value = null
                _loadingStatus.value = LoadingStatus.FAIL
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}