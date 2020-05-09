package com.alexaat.flowershop.adapters


import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.alexaat.flowershop.R
import com.alexaat.flowershop.network.Flower
import com.alexaat.flowershop.network.MyOrder
import com.bumptech.glide.Glide
import java.text.DecimalFormat

@BindingAdapter("setOverviewImage")
fun ImageView.setImageSrc(flower: Flower?) {
    if (flower == null) {
        setImageResource(R.drawable.loading_spinner)
    } else {
            var a = 1.0F
            val url = flower.img_src.replace("\\", "")
            Glide.with(this).load(url).placeholder(R.drawable.loading_spinner)
                .error(R.drawable.ic_broken_image).into(this)
            if(flower.available==0.0){
                a = 0.5F
            }
            alpha = a
    }
}


@BindingAdapter("setTitle")
fun TextView.setTitle(flower:Flower?){
      text = flower?.title ?: ""
}

@BindingAdapter("setDescription")
fun TextView.setDescription(flower:Flower?){
      text = flower?.description ?: ""
}


@BindingAdapter("setUnitPrice")
fun TextView.setUnitPrice(flower:Flower?){
      var txt = ""
      if(flower!=null){
            val formattedPricePerItem = DecimalFormat("#,###,##0.00").format(flower.price)
            txt = resources.getString(R.string.unit_price_format, formattedPricePerItem)
      }
      text = txt
}

@BindingAdapter("setNumberInStock")
fun TextView.setNumberInStock(flower:Flower?) {
      if (flower == null) {
            text = ""
      } else {
             var txt = flower.available.toString()
             if(flower.available==0.0) txt="OUT OF STOCK"
             text = txt
            }
      }


@BindingAdapter("setSubtotal")
fun TextView.setSubtotal(flower:Flower?){
      var txt = ""
      if(flower!=null){
            val formattedSubtotal = DecimalFormat("#,###,##0.00").format(flower.price*flower.available)
            txt = resources.getString(R.string.unit_price_format, formattedSubtotal)
      }
      text = txt
}

@BindingAdapter("setDate")
fun TextView.setDate(myOrder:MyOrder){
    text = myOrder.getFormattedDate()
}

@BindingAdapter("setOrders")
fun TextView.setOrders(myOrder:MyOrder){
    var ordersText = ""
    for(flower in myOrder.orders){
        val numberOfItems = flower.available

        if((numberOfItems%1)==0.0){
            ordersText+="${flower.title} x ${numberOfItems.toInt()} \n"
        }else{
            ordersText+="${flower.title} x $numberOfItems \n"
        }
    }
    text = ordersText.dropLast(2)
}

@BindingAdapter("setTotal")
fun TextView.setTotal(myOrder:MyOrder){
    var totalPrice = 0.0
    for(flower in myOrder.orders){
        totalPrice+=flower.price*flower.available
    }
    val formattedTotalPrice = DecimalFormat("#,###,##0.00").format(totalPrice)
    text = resources.getString(R.string.unit_price_format, formattedTotalPrice)
}

