package com.alexaat.flowershop.viewmodels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexaat.flowershop.network.Flower
import com.alexaat.flowershop.network.FlowersApi
import com.alexaat.flowershop.receivers.AlarmReceiver
import com.alexaat.flowershop.util.FIRST_ALARM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val REQUEST_CODE_FIRST_ALARM = 0

class ListFragmentViewModel(private val context: Context): ViewModel(){

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    //private var alarmManager: AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
    //private val notifyIntent = Intent(context, AlarmReceiver::class.java)

    //private val notifyPendingIntent1: PendingIntent

//    init{
//        notifyPendingIntent1 = PendingIntent.getBroadcast(
//            context,
//            REQUEST_CODE_FIRST_ALARM,
//            notifyIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        setAlarm()
//    }

    fun onResume(){
        getFlowers()
        checkCart()
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

   //  private fun setAlarm(){

//        coroutineScope.launch(Dispatchers.IO) {
    //val getFlowersInCartDeferred = FlowersApi.retrofitService.getFlowersInCartAsync()
//            try{
//                val flowerResult = getFlowersInCartDeferred.await()
//                if(flowerResult.isNotEmpty()){

//
//                    var triggerTime = SystemClock.elapsedRealtime() + FIRST_ALARM
//                    AlarmManagerCompat.setExact(
//                        alarmManager,
//                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                        triggerTime,
//                        notifyPendingIntent1
//                    )


//                    triggerTime = SystemClock.elapsedRealtime() + SECOND_ALARM
//                    AlarmManagerCompat.setExactAndAllowWhileIdle(
//                        alarmManager,
//                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                        triggerTime,
//                        notifyPendingIntent2
//                    )
//
//
//                    triggerTime = SystemClock.elapsedRealtime() + THIRD_ALARM
//                    AlarmManagerCompat.setExactAndAllowWhileIdle(
//                        alarmManager,
//                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                        triggerTime,
//                        notifyPendingIntent3
//                    )
    //                  Log.i("TAG","Alarm is set")
//                }
//            }catch(e:Exception){
//            }
//        }
//}


}

class OnCartButtonClicked(val listener: ()->Unit){
    fun onClick(){
        listener()
    }
}

enum class LoadingStatus{
    LOADING,
    SUCCESS,
    FAIL
}