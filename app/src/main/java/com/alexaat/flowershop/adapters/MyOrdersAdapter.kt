package com.alexaat.flowershop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexaat.flowershop.R
import com.alexaat.flowershop.adapters.MyOrdersAdapter.MyOrdersViewHolder
import com.alexaat.flowershop.databinding.MyOrdersViewItemBinding
import com.alexaat.flowershop.network.MyOrder


class MyOrdersAdapter: ListAdapter<MyOrder, MyOrdersViewHolder>(MyOrdersCallBack()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersViewHolder = MyOrdersViewHolder.inflateFrom(parent)
    override fun onBindViewHolder(holder: MyOrdersViewHolder, position: Int) = holder.bind(getItem(position))
    class MyOrdersViewHolder(private val binding:MyOrdersViewItemBinding) :RecyclerView.ViewHolder(binding.root){
       companion object{
           fun inflateFrom(parent: ViewGroup):MyOrdersViewHolder{
               val inflater =  LayoutInflater.from(parent.context)
               val res = R.layout.my_orders_view_item
               val binding:MyOrdersViewItemBinding = DataBindingUtil.inflate(inflater, res,parent,false)
               return MyOrdersViewHolder(binding)
           }
       }

        fun bind(myOrder:MyOrder){
            binding.myOrder = myOrder
            binding.executePendingBindings()
        }
    }
}

class MyOrdersCallBack: DiffUtil.ItemCallback<MyOrder>() {
    override fun areItemsTheSame(oldItem: MyOrder, newItem: MyOrder): Boolean {
        return oldItem.order_id == newItem.order_id
    }
    override fun areContentsTheSame(oldItem: MyOrder, newItem: MyOrder): Boolean {
        return oldItem == newItem
    }
}



