package com.alexaat.flowershop.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alexaat.flowershop.NEW_MESSAGE_INTENT_ACTION
import com.alexaat.flowershop.R
import com.alexaat.flowershop.adapters.MessagesAdapter
import com.alexaat.flowershop.databinding.FragmentMessagesBinding
import com.alexaat.flowershop.util.buzz
import com.alexaat.flowershop.viewmodels.LoadingStatus
import com.alexaat.flowershop.viewmodels.MessageFragmentViewModel
import com.google.android.material.snackbar.Snackbar



class MessagesFragment : Fragment() {

    private lateinit var receiver:BroadcastReceiver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding:FragmentMessagesBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_messages, container, false)
        binding.lifecycleOwner = this

        val viewModel: MessageFragmentViewModel = ViewModelProvider(this).get(MessageFragmentViewModel::class.java)

        val adapter = MessagesAdapter()
        binding.messagesRecyclerView.adapter = adapter

        viewModel.shopMessages.observe(viewLifecycleOwner, Observer {
            it?.let{ list->
                adapter.submitList(list)
            }
        })

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            it?.let{status ->
                if(status == LoadingStatus.FAIL){
                    view?.let{v->
                        val bar = Snackbar.make(v, getString(R.string.problem_with_internet_connection), Snackbar.LENGTH_SHORT)
                        bar.view.setBackgroundResource(R.color.request_unsuccessful)
                        bar.show()
                    }
                }
            }
        })

        viewModel.setMessagesAsOpened()

        val filter = IntentFilter()
        filter.addAction(NEW_MESSAGE_INTENT_ACTION)
        receiver = object:BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
               buzz(requireActivity().applicationContext)
               viewModel.getMessages()
               viewModel.setMessagesAsOpened()
            }
        }
        requireActivity().registerReceiver(receiver,filter)

        return binding.root
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(receiver)
        super.onDestroy()
    }
}
