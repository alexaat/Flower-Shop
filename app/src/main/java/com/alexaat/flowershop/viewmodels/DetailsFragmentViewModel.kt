package com.alexaat.flowershop.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexaat.flowershop.network.Flower
import com.alexaat.flowershop.network.FlowersApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DetailsFragmentViewModel(id:Long): ViewModel(){

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    private val _loadingStatus = MutableLiveData(LoadingStatus.LOADING)
    val loadingStatus:LiveData<LoadingStatus>
        get() = _loadingStatus

    init{
        getFlower(id)
    }
    fun onResume(){
       checkCart()
    }

    private val _flower = MutableLiveData<Flower>()
    val flower:LiveData<Flower>
        get() = _flower

    private val _flowerMovedToCart = MutableLiveData<RequestResult>()
    val flowerMovedToCart:LiveData<RequestResult>
        get() = _flowerMovedToCart

    private val _navigateToCartFragment = MutableLiveData(false)
    val navigateToCartFragment:LiveData<Boolean>
        get() = _navigateToCartFragment

    private val _notifyThatCarIsEmpty = MutableLiveData(false)
    val notifyThatCarIsEmpty:LiveData<Boolean>
        get() = _notifyThatCarIsEmpty

    private val _notifyThatThereIsProblemLoadingData = MutableLiveData(false)
    val notifyThatThereIsProblemLoadingData:LiveData<Boolean>
        get() = _notifyThatThereIsProblemLoadingData

    private val _isCartEmpty = MutableLiveData(true)
    val isCartEmpty:LiveData<Boolean>
        get() = _isCartEmpty

    private fun getFlower(id:Long){
        _loadingStatus.value = LoadingStatus.LOADING
        coroutineScope.launch {
            val getFlowerDeferred = FlowersApi.retrofitService.getFlowerAsync(id)
            try{
                val flowerResult = getFlowerDeferred.await()
                _flower.value = flowerResult
                _loadingStatus.value = LoadingStatus.SUCCESS
            }catch(e:Exception){
                _flower.value = null
                _loadingStatus.value = LoadingStatus.FAIL
            }
        }
        checkCart()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onAddToCartClicked(id:Long){
        coroutineScope.launch {
           // update database
            val moveFlowerToCartDeferred = FlowersApi.retrofitService.moveFlowerToCartAsync(id)
            try{
                val resp =  moveFlowerToCartDeferred.await()
                if(resp){
                     _flowerMovedToCart.value  = RequestResult.Success
                     _flowerMovedToCart.value  = null
                }else{
                    _flowerMovedToCart.value  = RequestResult.OutOfStock
                    _flowerMovedToCart.value  = null
                }
            }catch(e:Exception){
                _flowerMovedToCart.value  = RequestResult.ConnectionError
                _flowerMovedToCart.value  = null
            }
            // update item in fragment
            val getFlowerDeferred = FlowersApi.retrofitService.getFlowerAsync(id)
            try{
                val response =  getFlowerDeferred.await()
                _flower.value = response
            }catch(e:Exception){
                _flower.value = null
            }
        }
        checkCart()
    }

    private fun checkCart(){
        coroutineScope.launch {
            val getFlowersInCartDeferred = FlowersApi.retrofitService.getFlowersInCartAsync()
            try{
                val flowerResult = getFlowersInCartDeferred.await()
                _isCartEmpty.value = flowerResult.isEmpty()
               }catch(e:Exception){
            }
        }
    }

    fun onCartButtonClicked(){
        coroutineScope.launch {
            val getFlowersInCartDeferred = FlowersApi.retrofitService.getFlowersInCartAsync()
            try{
                val flowerResult = getFlowersInCartDeferred.await()
                if(flowerResult.isNotEmpty()){
                    _navigateToCartFragment.value = true
                    _navigateToCartFragment.value = false
                }else{
                    _notifyThatCarIsEmpty.value = true
                    _notifyThatCarIsEmpty.value = false
                }
            }catch(e:Exception){
                _notifyThatThereIsProblemLoadingData.value = true
                _notifyThatThereIsProblemLoadingData.value = false
            }
        }
    }
}

enum class RequestResult{
    Success,
    ConnectionError,
    OutOfStock
}

