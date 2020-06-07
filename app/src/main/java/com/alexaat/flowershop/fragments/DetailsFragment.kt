package com.alexaat.flowershop.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.alexaat.flowershop.R
import com.alexaat.flowershop.databinding.FragmentDetailsBinding
import com.alexaat.flowershop.viewmodels.*
import com.google.android.material.snackbar.Snackbar

class DetailsFragment : Fragment() {

    private lateinit var onCartButtonClicked: OnCartButtonClicked
    private var onCartIconChangeEvent:OnCartIconChangeEvent? = null
    private lateinit var onResumeFragmentEvent: OnResumeFragmentEvent

    override fun onResume() {
        onResumeFragmentEvent.onResume()
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
        binding.lifecycleOwner = this

        val id = DetailsFragmentArgs.fromBundle(requireArguments()).id

        // view model
        val viewModelFactory = DetailsFragmentViewModelFactory(id)
        val viewModel = ViewModelProvider(this,viewModelFactory).get(DetailsFragmentViewModel::class.java)

        onResumeFragmentEvent = OnResumeFragmentEvent{
            viewModel.onResume()
        }

        onCartButtonClicked = OnCartButtonClicked{
            viewModel.onCartButtonClicked()
        }

        setHasOptionsMenu(true)

        val addToCartClickListener = AddToCartClickListener {
            viewModel.onAddToCartClicked(it)
        }
        binding.addToCartClickListener = addToCartClickListener

        setObservers(viewModel,binding)

        return binding.root
    }

    private fun setObservers(viewModel:DetailsFragmentViewModel, binding:FragmentDetailsBinding){

        viewModel.flower.observe(viewLifecycleOwner, Observer {
            it?.let{
                binding.flower = it
                binding.executePendingBindings()
            }
        })
        viewModel.flowerMovedToCart.observe(viewLifecycleOwner, Observer {
            it?.let{movedToCart->

                when(movedToCart){
                    RequestResult.Success ->{
                        Toast.makeText(context,getString(R.string.Item_is_added_to_cart),Toast.LENGTH_SHORT).show()
                    }
                    RequestResult.OutOfStock->{
                        Toast.makeText(context,getString(R.string.item_is_out_of_stock),Toast.LENGTH_SHORT).show()
                    }
                    RequestResult.ConnectionError ->{
                        Toast.makeText(context,getString(R.string.cannot_add_item_to_cart),Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        viewModel.navigateToCartFragment.observe(viewLifecycleOwner, Observer {
            if(it){
                val action = DetailsFragmentDirections.actionDetailsFragmentToCartFragment()
                findNavController().navigate(action)
            }
        })

        viewModel.notifyThatCarIsEmpty.observe(viewLifecycleOwner, Observer {
            if(it){
                view?.let{view->
                    val bar = Snackbar.make(view,getString(R.string.cart_is_empty),Snackbar.LENGTH_SHORT)
                    bar.view.setBackgroundResource(R.color.request_unsuccessful)
                    bar.show()
                }
            }
        })

        viewModel.notifyThatThereIsProblemLoadingData.observe(viewLifecycleOwner, Observer {
            if(it){
                view?.let{view->
                    val bar = Snackbar.make(view,getString(R.string.problem_with_internet_connection),Snackbar.LENGTH_SHORT)
                    bar.view.setBackgroundResource(R.color.request_unsuccessful)
                    bar.show()
                }
            }
        })

        viewModel.isCartEmpty.observe(viewLifecycleOwner,Observer{
            onCartIconChangeEvent?.onIconChanged(it)
        })

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            when(it){
                LoadingStatus.SUCCESS ->{
                    binding.fragmentDetailsStatusImage.visibility = View.GONE
                }
                LoadingStatus.FAIL ->{
                    binding.fragmentDetailsStatusImage.setImageResource(R.drawable.ic_cloud_off)
                }
                else ->{
                    binding.fragmentDetailsStatusImage.setImageResource(R.drawable.loading_spinner)
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
        onCartIconChangeEvent = OnCartIconChangeEvent{
            if(it){
                setCartIcon(menu,R.drawable.ic_cart_empty)
            }else{
                setCartIcon(menu,R.drawable.ic_cart_full)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.item_details_menu_cart){
            onCartButtonClicked.onClick()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    private fun setCartIcon(menu: Menu, iconResource:Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            menu.getItem(0).icon = resources.getDrawable(iconResource, activity?.theme)
        } else {
            menu.getItem(0).icon = resources.getDrawable(iconResource)
        }
    }

}

class AddToCartClickListener(private val listener: (id:Long) -> Unit){
    fun click(id:Long) =  listener(id)
}

