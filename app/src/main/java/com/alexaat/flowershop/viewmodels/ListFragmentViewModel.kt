package com.alexaat.flowershop.viewmodels


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexaat.flowershop.network.Flower
import com.alexaat.flowershop.network.FlowersApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ListFragmentViewModel(private val context: Context): ViewModel(){

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    fun onResume(){
        getFlowers()
        checkCart()
        checkMessages()
    }



    private val _navigateToDetailsFragment = MutableLiveData(-1L)
    val navigateToDetailsFragment:LiveData<Long>
    get() = _navigateToDetailsFragment

    private val _listOfFlowers = MutableLiveData<List<Flower>>(null)
    val listOfFlowers:LiveData<List<Flower>>
    get() = _listOfFlowers

    private val _navigateToCartFragment = MutableLiveData(false)
    val navigateToCartFragment:LiveData<Boolean>
        get() = _navigateToCartFragment

    private val _navigateToMessagesFragment = MutableLiveData(false)
    val navigateToMessagesFragment:LiveData<Boolean>
        get() = _navigateToMessagesFragment

    private val _notifyThatCarIsEmpty = MutableLiveData(false)
    val notifyThatCarIsEmpty:LiveData<Boolean>
        get() = _notifyThatCarIsEmpty

    private val _notifyThatThereIsProblemLoadingData = MutableLiveData(false)
    val notifyThatThereIsProblemLoadingData:LiveData<Boolean>
        get() = _notifyThatThereIsProblemLoadingData

    private val _isCartEmpty = MutableLiveData(true)
    val isCartEmpty:LiveData<Boolean>
        get() = _isCartEmpty

    private val _loadingStatus = MutableLiveData(LoadingStatus.LOADING)
    val loadingStatus:LiveData<LoadingStatus>
        get() = _loadingStatus

    private val _isNewMessage = MutableLiveData(false)
    val isNewMessage:LiveData<Boolean>
        get() = _isNewMessage


    private fun getFlowers(){
        _loadingStatus.value = LoadingStatus.LOADING
         coroutineScope.launch {
             val getFlowersDeferred = FlowersApi.retrofitService.getFlowersAsync()
             try{
                 val listResult = getFlowersDeferred.await()
                 _listOfFlowers.value = listResult
                 _loadingStatus.value = LoadingStatus.SUCCESS
             }catch(e:Exception){
                 _listOfFlowers.value = null
                 _loadingStatus.value = LoadingStatus.FAIL
             }
         }
    }

    fun checkMessages(){
        coroutineScope.launch(Dispatchers.IO){
            val getMessagesDeferred = FlowersApi.retrofitService.getShopMessagesAsync()
            try {
                val result = getMessagesDeferred.await()
                for(msg in result){
                    if(!msg.opened){
                        _isNewMessage.postValue(true)
                        return@launch
                    }
                    _isNewMessage.postValue(false)
                }
            }catch(e:Exception){

            }

        }
    }

     override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

     fun onItemClicked(id:Long){
         _navigateToDetailsFragment.value = id
         _navigateToDetailsFragment.value = -1
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

     fun onMessagesButtonClicked(){
        _navigateToMessagesFragment.value = true
        _navigateToMessagesFragment.value = false
    }

     private fun checkCart(){
         coroutineScope.launch {
             val getFlowersInCartDeferred = FlowersApi.retrofitService.getFlowersInCartAsync()
             try{
                 val flowerResult = getFlowersInCartDeferred.await()
                 if(flowerResult.isNotEmpty()){
                     _isCartEmpty.value = false
                 }
             }catch(e:Exception){
             }
         }
     }

}

class OnCartButtonClicked(val listener: ()->Unit){
    fun onClick(){
        listener()
    }
}

class OnMessagesButtonClicked(val listener: ()->Unit){
    fun onClick(){
        listener()
    }
}


enum class LoadingStatus{
    LOADING,
    SUCCESS,
    FAIL
}