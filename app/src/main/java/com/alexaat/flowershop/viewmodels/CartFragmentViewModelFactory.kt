package com.alexaat.flowershop.viewmodels

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CartFragmentViewModelFactory(private val resources: Resources): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartFragmentViewModel::class.java)) {
            return CartFragmentViewModel(resources = resources) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}