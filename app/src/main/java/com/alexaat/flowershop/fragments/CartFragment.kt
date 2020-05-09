package com.alexaat.flowershop.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.alexaat.flowershop.R
import com.alexaat.flowershop.adapters.CartAdapter
import com.alexaat.flowershop.adapters.OnBuyButtonClickListener
import com.alexaat.flowershop.adapters.OnDeleteItemClickListener
import com.alexaat.flowershop.databinding.FragmentCartBinding
import com.alexaat.flowershop.viewmodels.CartFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import android.app.AlertDialog
import com.alexaat.flowershop.viewmodels.CartFragmentViewModelFactory


class CartFragment : Fragment() {

    private lateinit var onBuyButtonClickListener: OnBuyButtonClickListener
    private var stockValueInCart = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding:FragmentCartBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_cart, container, false)

        binding.lifecycleOwner = this

        val viewModelFactory = CartFragmentViewModelFactory(resources)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(CartFragmentViewModel::class.java)

        val onDeleteItemClickListener = OnDeleteItemClickListener{
            viewModel.onDeleteButtonClicked(it)
        }
        val cartAdapter = CartAdapter(onDeleteItemClickListener,viewModel)
        binding.cartRecyclerView.adapter = cartAdapter

        onBuyButtonClickListener = OnBuyButtonClickListener{
           viewModel.buyButtonClicked()
        }

        setHasOptionsMenu(true)

        setObservers(viewModel,cartAdapter)

        return binding.root
    }

    private fun setObservers(viewModel:CartFragmentViewModel, cartAdapter:CartAdapter){
        viewModel.flowersInCart.observe(viewLifecycleOwner, Observer {
            it?.let{list->
               cartAdapter.addHeaderAndFooterAndSubmitList(ArrayList(list))
            }
        })

        viewModel.isRemovedFromCart.observe(viewLifecycleOwner, Observer {
            it?.let{isRemoved ->
                if(isRemoved){
                    view?.let{v->
                        Snackbar.make(v,getString(R.string.item_is_removed_from_cart),Snackbar.LENGTH_SHORT).show()
                        cartAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
        viewModel.orderWasPlaced.observe(viewLifecycleOwner, Observer {
            it?.let{order->
                if(order){
                    view?.let{v->
                        val bar = Snackbar.make(v,"Your order was placed successfully",Snackbar.LENGTH_SHORT)
                        bar.view.setBackgroundResource(R.color.request_successful)
                        bar.show()
                    }
                }else{
                    view?.let{v->
                        val bar = Snackbar.make(v,"Couldn't place order",Snackbar.LENGTH_SHORT)
                        bar.view.setBackgroundResource(R.color.request_unsuccessful)
                        bar.show()
                    }
                }
            }
        })

        viewModel.navigateToFlowerList.observe(viewLifecycleOwner, Observer {
            if(it){
                val action = CartFragmentDirections.actionCartFragmentToListFragment()
                findNavController().navigate(action)
            }
        })

        viewModel.stockValueInCart.observe(viewLifecycleOwner,Observer{
            it?.let{
                stockValueInCart = it
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cart_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.cart_menu_buy -> {

                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.confirm_purchase))

                var message = getString(R.string.place_order_message)
                if(stockValueInCart!=""){
                    message = resources.getString(R.string.place_order_message_with_price,stockValueInCart)
                }
                builder.setMessage(message)

                builder.setPositiveButton(getString(R.string.place_order)){ dialog, _ ->
                    onBuyButtonClickListener.onClick()
                    dialog.dismiss()
                }
                builder.setNeutralButton(getString(R.string.cancel)){ dialog, _ ->
                    dialog.dismiss()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()

                return true
                }
        }
        return super.onOptionsItemSelected(item)
    }
}
