package com.alexaat.flowershop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alexaat.flowershop.R
import com.alexaat.flowershop.adapters.MyOrdersAdapter
import com.alexaat.flowershop.databinding.FragmentMyOrdersBinding
import com.alexaat.flowershop.viewmodels.LoadingStatus
import com.alexaat.flowershop.viewmodels.MyOrdersFragmentViewModel
import com.google.android.material.snackbar.Snackbar


class MyOrdersFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val binding: FragmentMyOrdersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_orders, container, false)

        val viewModel: MyOrdersFragmentViewModel = ViewModelProvider(this).get(MyOrdersFragmentViewModel::class.java)

        binding.lifecycleOwner = this

        val adapter = MyOrdersAdapter()
        binding.myOrdersRecyclerview.adapter = adapter

        viewModel.myOrders.observe(viewLifecycleOwner, Observer {
            it?.let{list->
                adapter.submitList(list)

            }
        })

        viewModel.noOrderToDisplayNotification.observe(viewLifecycleOwner, Observer {
            if(it){
                view?.let{v->
                    val bar = Snackbar.make(v, getString(R.string.no_orders_to_display),Snackbar.LENGTH_SHORT)
                    bar.view.setBackgroundResource(R.color.request_unsuccessful)
                    bar.show()
                }
            }
        })

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            when(it){
                LoadingStatus.SUCCESS ->{
                    binding.fragmentMyOrdersStatusImage.visibility = View.GONE
                }
                LoadingStatus.FAIL ->{
                    binding.fragmentMyOrdersStatusImage.setImageResource(R.drawable.ic_cloud_off)
                }
                else ->{
                    binding.fragmentMyOrdersStatusImage.setImageResource(R.drawable.loading_spinner)
                }
            }
        })


        return binding.root
    }

}
