package com.alexaat.flowershop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexaat.flowershop.R
import com.alexaat.flowershop.databinding.ShopMessageViewItemBinding
import com.alexaat.flowershop.network.ShopMessage

class MessagesAdapter: ListAdapter<ShopMessage, MessagesAdapter.MessageViewHolder>(ShopMessageCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder = MessageViewHolder.inflateFrom(parent)
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) = holder.bind(getItem(position))
    class MessageViewHolder(private val binding: ShopMessageViewItemBinding) : RecyclerView.ViewHolder(binding.root){
       companion object
       {
           fun inflateFrom(parent: ViewGroup):MessageViewHolder{
               val binding:ShopMessageViewItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.shop_message_view_item, parent, false)
               return MessageViewHolder(binding)
           }
       }

        fun bind(shopMessage: ShopMessage) {
            binding.shopMessage = shopMessage
            binding.executePendingBindings()
        }
    }
}

class ShopMessageCallBack: DiffUtil.ItemCallback<ShopMessage>() {
    override fun areItemsTheSame(oldItem: ShopMessage, newItem: ShopMessage): Boolean {
        return oldItem === newItem
    }
    override fun areContentsTheSame(oldItem: ShopMessage, newItem: ShopMessage): Boolean {
        return oldItem == newItem
    }
}
