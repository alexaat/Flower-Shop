package com.alexaat.flowershop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alexaat.flowershop.R
import com.alexaat.flowershop.databinding.CartItemLayoutBinding
import com.alexaat.flowershop.network.Flower
import androidx.recyclerview.widget.ListAdapter
import com.alexaat.flowershop.databinding.HeaderFooterLayoutBinding
import com.alexaat.flowershop.viewmodels.CartFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val ITEM_TYPE_HEADER = 1
const val ITEM_TYPE_LIST = 0
const val ITEM_TYPE_FOOTER = -1

class CartAdapter(private val onDeleteItemClickListener:OnDeleteItemClickListener, private var viewModel: CartFragmentViewModel ):ListAdapter<DataItem, RecyclerView.ViewHolder>(CartCallBack()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return when (viewType) {
            ITEM_TYPE_HEADER -> TextViewHolderHeaderAndFooter.inflateFrom(parent)
            ITEM_TYPE_LIST -> CartViewHolder.inflateFrom(parent)
            ITEM_TYPE_FOOTER -> TextViewHolderHeaderAndFooter.inflateFrom(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder:RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CartViewHolder -> {
                val flowerItem = getItem(position) as DataItem.FlowerItem
                holder.bind(flowerItem.flower, onDeleteItemClickListener)
            }
            is TextViewHolderHeaderAndFooter -> {
                holder.bind(viewModel)
            }
        }

    }

    class CartViewHolder(val binding: CartItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        companion object{
            fun inflateFrom(parent: ViewGroup):CartViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val res = R.layout.cart_item_layout
                val binding: CartItemLayoutBinding = DataBindingUtil.inflate(inflater,res, parent, false)
                return CartViewHolder(binding)
            }
        }
        fun bind(flower:Flower, onDeleteItemClickListener:OnDeleteItemClickListener){
            binding.flower = flower
            binding.onDeleteItemClickListener = onDeleteItemClickListener
            binding.executePendingBindings()
        }
    }

    class TextViewHolderHeaderAndFooter(private val binding:HeaderFooterLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): TextViewHolderHeaderAndFooter {
                val res = R.layout.header_footer_layout
                val binding: HeaderFooterLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),res,parent,false)
                return TextViewHolderHeaderAndFooter(binding)
            }
        }

        fun bind(viewModel: CartFragmentViewModel){
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_TYPE_HEADER
            is DataItem.FlowerItem -> ITEM_TYPE_LIST
            is DataItem.Footer -> ITEM_TYPE_FOOTER
        }
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndFooterAndSubmitList(list: ArrayList<Flower>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header) + listOf(DataItem.Footer)
                else -> listOf(DataItem.Header) + list.map{DataItem.FlowerItem(it)} + listOf(DataItem.Footer)
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

}

class OnDeleteItemClickListener(val listener: (id:Long) ->Boolean) {
    fun onClick(id: Long): Boolean {
        return listener(id)
    }
}
class OnBuyButtonClickListener(val listener: ()->Boolean){
    fun onClick():Boolean{
        return listener()
    }
}
class CartCallBack: DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem === newItem
    }
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}


sealed class DataItem {

    data class FlowerItem(val flower: Flower) : DataItem() {
        override val id = flower.id
    }

    object Header : DataItem() {
        override val id = Long.MIN_VALUE
    }

    object Footer : DataItem() {
        override val id = Long.MAX_VALUE
    }

    abstract val id: Long
}

