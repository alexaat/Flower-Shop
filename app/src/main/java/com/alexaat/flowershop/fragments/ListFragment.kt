package com.alexaat.flowershop.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.alexaat.flowershop.R
import com.alexaat.flowershop.adapters.ClickListener
import com.alexaat.flowershop.adapters.FlowerListAdapter
import com.alexaat.flowershop.databinding.FragmentListBinding
import com.alexaat.flowershop.viewmodels.ListFragmentViewModel
import com.alexaat.flowershop.viewmodels.ListFragmentViewModelFactory
import com.alexaat.flowershop.viewmodels.LoadingStatus
import com.alexaat.flowershop.viewmodels.OnCartButtonClicked
import com.google.android.material.snackbar.Snackbar

class ListFragment : Fragment() {

    private lateinit var onCartButtonClicked: OnCartButtonClicked
    private lateinit var onResumeFragmentEvent: OnResumeFragmentEvent
    private var onCartIconChangeEvent:OnCartIconChangeEvent? = null

    override fun onResume() {
        onResumeFragmentEvent.onResume()
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)

        createNotificationChannel(
            channelId = getString(R.string.items_in_cart_notification_channel_id),
            channelName = getString(R.string.items_in_cart_notification_channel_name)
        )

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
        //adapter
        val clickListener = ClickListener{
            viewModel.onItemClicked(it)
        }
        val adapter = FlowerListAdapter(clickListener)
        binding.flowersRecyclerView.adapter = adapter

        setObservers(viewModel,adapter,binding)

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
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
                setShowBadge(false)
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }
            val notificationManager = requireActivity().getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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
        if(menu.size()>0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                menu.getItem(0).icon = resources.getDrawable(iconResource, activity?.theme)
            } else {
                menu.getItem(0).icon = resources.getDrawable(iconResource)
            }
        }
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

