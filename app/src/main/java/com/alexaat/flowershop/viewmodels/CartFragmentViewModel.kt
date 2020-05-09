package com.alexaat.flowershop.viewmodels


import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexaat.flowershop.R
import com.alexaat.flowershop.network.Flower
import com.alexaat.flowershop.network.FlowersApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class CartFragmentViewModel(private val resources: Resources): ViewModel(){

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    private val _flowersInCart = MutableLiveData<List<Flower>>()
    val flowersInCart:LiveData<List<Flower>>
        get() = _flowersInCart

    private val _isRemovedFromCart = MutableLiveData<Boolean>()
    val isRemovedFromCart:LiveData<Boolean>
        get() = _isRemovedFromCart

    private val _orderWasPlaced =  MutableLiveData<Boolean>()
    val orderWasPlaced:LiveData<Boolean>
        get()= _orderWasPlaced

    private val _navigateToFlowerList = MutableLiveData(false)
    val navigateToFlowerList:LiveData<Boolean>
        get() = _navigateToFlowerList

    private val _stockValueInCart = MutableLiveData<String>()
    val stockValueInCart:LiveData<String>
        get() = _stockValueInCart

    init{
        getCart()
    }

    private fun getCart(){
        coroutineScope.launch {
            val getFlowersInCartDeferred = FlowersApi.retrofitService.getFlowersInCartAsync()
            try{
                val flowerResult = getFlowersInCartDeferred.await()
                _flowersInCart.value = flowerResult
                var valueInCart = 0.0
                for(f in flowerResult){
                    valueInCart+=f.available*f.price
                }
                if(valueInCart==0.0){
                    _stockValueInCart.value = ""
                }else{
                    val formattedStockValue = DecimalFormat("#,###,##0.00").format(valueInCart)
                    val text = resources.getString(R.string.unit_price_format, formattedStockValue)
                    _stockValueInCart.value = text
                }
            }catch(e:Exception){
                _flowersInCart.value = null
            }
        }
    }


    fun onDeleteButtonClicked(id:Long):Boolean{
        coroutineScope.launch {
            val removeItemFromCartDeferred = FlowersApi.retrofitService.removeFromCartAsync(id)
            try{
                val isRemoved = removeItemFromCartDeferred.await()
                _isRemovedFromCart.value = isRemoved
                _isRemovedFromCart.value = null
            }catch(e: Exception){
                _isRemovedFromCart.value = false
                _isRemovedFromCart.value = null
            }
            val getFlowersInCartDeferred = FlowersApi.retrofitService.getFlowersInCartAsync()
            try{
                val flowerResult = getFlowersInCartDeferred.await()
                if(flowerResult.isEmpty()){
                    _navigateToFlowerList.value = true
                    _navigateToFlowerList.value = false
                }else {
                    _flowersInCart.value = flowerResult
                    var valueInCart = 0.0
                    for(f in flowerResult){
                        valueInCart+=f.available*f.price
                    }
                    if(valueInCart==0.0){
                        _stockValueInCart.value = ""
                    }else{
                        val formattedStockValue = DecimalFormat("#,###,##0.00").format(valueInCart)
                        val text = resources.getString(R.string.unit_price_format, formattedStockValue)
                        _stockValueInCart.value = text
                    }
                }
            }catch(e:Exception){
                _flowersInCart.value = null
            }
        }
        return true
    }

    fun buyButtonClicked():Boolean{
         coroutineScope.launch {
            val buyItemsInCartDeferred = FlowersApi.retrofitService.buyItemsInCartAsync(-1)
            try {
                _orderWasPlaced.value = buyItemsInCartDeferred.await()
                _orderWasPlaced.value = null
                _navigateToFlowerList.value = true
                _navigateToFlowerList.value = false

            }catch (e:Exception){
                _orderWasPlaced.value = false
                _orderWasPlaced.value = null
            }
        }
        return true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }



}


