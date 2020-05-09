package com.alexaat.flowershop.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexaat.flowershop.R
import com.alexaat.flowershop.adapters.FlowerListAdapter.*
import com.alexaat.flowershop.databinding.FlowerListItemBinding
import com.alexaat.flowershop.network.Flower

class FlowerListAdapter(private val clickListener:ClickListener):ListAdapter<Flower, ViewHolder>(FlowerCallBack()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.inflateFrom(parent)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) =  holder.bind(getItem(position), clickListener)


    class ViewHolder(private val binding: FlowerListItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object{
            fun inflateFrom(parent: ViewGroup):ViewHolder{
                val inflater =   LayoutInflater.from(parent.context)
                val res = R.layout.flower_list_item
                val binding: FlowerListItemBinding = DataBindingUtil.inflate(inflater,res,parent, false)
                return ViewHolder(binding)
            }
        }
        fun bind(flower:Flower, clickListener:ClickListener){
            binding.flower = flower
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }
}

class ClickListener(val listener: (id:Long) -> Unit){
    fun click(id:Long){
        listener(id)
    }
}
class FlowerCallBack: DiffUtil.ItemCallback<Flower>() {
    override fun areItemsTheSame(oldItem: Flower, newItem: Flower): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Flower, newItem: Flower): Boolean {
        return oldItem == newItem
    }
}