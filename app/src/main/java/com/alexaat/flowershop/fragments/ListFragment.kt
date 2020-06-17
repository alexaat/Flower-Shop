package com.alexaat.flowershop.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.alexaat.flowershop.NEW_MESSAGE_INTENT_ACTION
import com.alexaat.flowershop.R
import com.alexaat.flowershop.adapters.ClickListener
import com.alexaat.flowershop.adapters.FlowerListAdapter
import com.alexaat.flowershop.databinding.FragmentListBinding
import com.alexaat.flowershop.util.buzz
import com.alexaat.flowershop.viewmodels.*
import com.google.android.material.snackbar.Snackbar

class ListFragment : Fragment() {

    private lateinit var onCartButtonClicked: OnCartButtonClicked
    private lateinit var onMessagesButtonClicked: OnMessagesButtonClicked
    private lateinit var onResumeFragmentEvent: OnResumeFragmentEvent
    private var onCartIconChangeEvent:OnCartIconChangeEvent? = null
    private var onMessageIconChangeEvent:OnMessageIconChangeEvent? = null
    private lateinit var receiver:BroadcastReceiver

    override fun onResume() {
        onResumeFragmentEvent.onResume()
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)

        val binding: FragmentListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        val viewModelFactory = ListFragmentViewModelFactory(requireActivity())
        val viewModel = ViewModelProvider(this,viewModelFactory).get(ListFragmentViewModel::class.java)
        binding.lifecycleOwner = this

        onResumeFragmentEvent = OnResumeFragmentEvent{
            viewModel.onResume()
        }

        onCartButtonClicked = OnCartButtonClicked{
            viewModel.onCartButtonClicked()
        }

        onMessagesButtonClicked = OnMessagesButtonClicked{
            viewModel.onMessagesButtonClicked()
        }

        //adapter
        val clickListener = ClickListener{
            viewModel.onItemClicked(it)
        }
        val adapter = FlowerListAdapter(clickListener)
        binding.flowersRecyclerView.adapter = adapter

        setObservers(viewModel,adapter,binding)

        val filter = IntentFilter()
        filter.addAction(NEW_MESSAGE_INTENT_ACTION)
        receiver = object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                buzz(requireActivity().applicationContext)
                viewModel.checkMessages()
            }
        }
        requireActivity().registerReceiver(receiver,filter)


        return binding.root
    }

    private fun setObservers(viewModel:ListFragmentViewModel, adapter:FlowerListAdapter, binding: FragmentListBinding){

        viewModel.listOfFlowers.observe(viewLifecycleOwner, Observer {
            it?.let{
               adapter.submitList(it)
            }
        })

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            when(it){
                LoadingStatus.SUCCESS ->{
                    binding.fragmentListStatusImage.visibility = View.GONE
                }
                LoadingStatus.FAIL ->{
                    binding.fragmentListStatusImage.setImageResource(R.drawable.ic_cloud_off)
                }
                else ->{
                    binding.fragmentListStatusImage.setImageResource(R.drawable.loading_spinner)
                }
            }
        })

        viewModel.navigateToDetailsFragment.observe(viewLifecycleOwner, Observer {
            if(it>0){
                val navController = findNavController()
                val action = ListFragmentDirections.actionListFragmentToDetailsFragment(it)
                navController.navigate(action)
            }
        })

        viewModel.navigateToCartFragment.observe(viewLifecycleOwner, Observer {
            if(it){
                val action = ListFragmentDirections.actionListFragmentToCartFragment()
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

        viewModel.navigateToMessagesFragment.observe(viewLifecycleOwner, Observer {
            if(it){
                val navController = findNavController()
                val action = ListFragmentDirections.actionListFragmentToMessagesFragment()
                navController.navigate(action)
            }
        })

        viewModel.isNewMessage.observe(viewLifecycleOwner, Observer {
            onMessageIconChangeEvent?.onIconChanged(it)
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
        onCartIconChangeEvent = OnCartIconChangeEvent{
            if(it){
                  setCartIcon(menu,R.drawable.ic_cart_empty)
               }else{
                   setCartIcon(menu,R.drawable.ic_cart_full)
               }
        }

        onMessageIconChangeEvent = OnMessageIconChangeEvent{
            if(it){
                setMessageIcon(menu,R.drawable.ic_message_full)
            }else{
                setMessageIcon(menu,R.drawable.ic_message_empty)
            }
        }



    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.item_details_menu_cart){
           onCartButtonClicked.onClick()
           return true
        }
        if(item.itemId == R.id.item_details_menu_message){
            onMessagesButtonClicked.onClick()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    private fun setCartIcon(menu: Menu, iconResource:Int){
        if(menu.size()>1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                menu.getItem(1).icon = resources.getDrawable(iconResource, activity?.theme)
            } else {
                menu.getItem(1).icon = resources.getDrawable(iconResource)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun setMessageIcon(menu: Menu, iconResource:Int){
        if(menu.size()>0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                menu.getItem(0).icon = resources.getDrawable(iconResource, activity?.theme)
            } else {
                menu.getItem(0).icon = resources.getDrawable(iconResource)
            }
        }
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(receiver)
        super.onDestroy()
    }
}

class OnResumeFragmentEvent(private val listener: ()->Unit){
    fun onResume(){
        listener()
    }
}

class OnCartIconChangeEvent(private val listener: (Boolean)->Unit){
    fun onIconChanged(isCartEmpty:Boolean){
        listener(isCartEmpty)
    }
}

class OnMessageIconChangeEvent(private val listener: (Boolean)->Unit){
    fun onIconChanged(isNewMessageEmpty:Boolean){
        listener(isNewMessageEmpty)
    }
}

