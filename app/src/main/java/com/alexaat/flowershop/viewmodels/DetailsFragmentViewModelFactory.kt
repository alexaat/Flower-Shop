package com.alexaat.flowershop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetailsFragmentViewModelFactory(private val id:Long): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsFragmentViewModel::class.java)) {
            return DetailsFragmentViewModel(id = id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}