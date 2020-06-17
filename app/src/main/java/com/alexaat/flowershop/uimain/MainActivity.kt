package com.alexaat.flowershop.uimain

import android.app.*
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.alexaat.flowershop.R
import com.alexaat.flowershop.databinding.ActivityMainBinding
import com.alexaat.flowershop.network.FlowersApi
import com.alexaat.flowershop.util.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*

private val TOPIC = "FlowerShopMessages"

class MainActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel(
            channelId = getString(R.string.items_in_cart_notification_channel_id),
            channelName = getString(R.string.items_in_cart_notification_channel_name)
        )

        createNotificationChannel(
            channelId=  getString(R.string.message_notification_channel_id),
            channelName = getString(R.string.message_notification_channel_name)
        )

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )

        if (application.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.mainActivityLinearLayout.setBackgroundResource(R.drawable.flowers_background_land)
        } else if (application.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.mainActivityLinearLayout.setBackgroundResource(R.drawable.flowers_background_port)
        }

        val navController = findNavController(R.id.nav_host)
        drawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        NavigationUI.setupActionBarWithNavController(this, navController,drawerLayout)
        NavigationUI.setupWithNavController(binding.navDrawer,navController)
        navController.addOnDestinationChangedListener{nc,destination, _ ->
            if(destination.id==nc.graph.startDestination){
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }else{
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        binding.navDrawer.setNavigationItemSelectedListener(this)
        binding.navDrawer.bringToFront()

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host)
        return NavigationUI.navigateUp(navController,appBarConfiguration)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.aboutFragment -> findNavController(
                R.id.nav_host
            ).navigate(R.id.aboutFragment)
            R.id.rulesFragment -> findNavController(
                R.id.nav_host
            ).navigate(R.id.rulesFragment)
            R.id.my_orders ->  findNavController(
                R.id.nav_host
            ).navigate(R.id.myOrdersFragment)
            R.id.reset ->{

                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.reset_dialog_title))

                val message = getString(R.string.reset_dialog_text)
                builder.setMessage(message)

                builder.setPositiveButton(getString(R.string.reset_dialog_positive_button)){ dialog, _ ->

                    val viewModelJob = Job()
                    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )
                    coroutineScope.launch {
                        val resetAsync = FlowersApi.retrofitService.resetAsync(-1)
                        try{
                            val result = resetAsync.await()
                            showResetNotification(result)
                        }catch(e:Exception){
                            val result = null
                            showResetNotification(result)
                        }
                    }
                    dialog.dismiss()
                }
                builder.setNeutralButton(getString(R.string.cancel)){ dialog, _ ->
                    dialog.dismiss()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
       return true
    }

    private fun showResetNotification(result:Boolean?){

    drawerLayout.close()
    val navController = findNavController(R.id.nav_host)
    val action = navController.graph.startDestination
    navController.popBackStack()
    navController.navigate(action)



    result?.let{
        if(it){
            val view = findViewById<LinearLayout>(R.id.main_activity_linear_layout)
            val bar = Snackbar.make(view,getString(R.string.reset_is_successful),Snackbar.LENGTH_SHORT)
            bar.view.setBackgroundResource(R.color.request_successful)
            bar.show()
        }else{
            val view = findViewById<LinearLayout>(R.id.main_activity_linear_layout)
            val bar = Snackbar.make(view,getString(R.string.could_not_reset),Snackbar.LENGTH_SHORT)
            bar.view.setBackgroundResource(R.color.request_unsuccessful)
            bar.show()
        }
        return
    }
    val view = findViewById<LinearLayout>(R.id.main_activity_linear_layout)
    val bar = Snackbar.make(view,getString(R.string.could_not_reset),Snackbar.LENGTH_SHORT)
    bar.view.setBackgroundResource(R.color.request_unsuccessful)
    bar.show()
}

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (application.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.mainActivityLinearLayout.setBackgroundResource(R.drawable.flowers_background_land)
        } else if (application.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.mainActivityLinearLayout.setBackgroundResource(R.drawable.flowers_background_port)
        }

    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText =  when(channelId){
                getString(R.string.items_in_cart_notification_channel_id) -> getString(R.string.items_in_cart_channel_description)
                else -> getString(R.string.messages_channel_description)
            }
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
                setShowBadge(false)
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }
            val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onResume() {
        cancelAllAlarms(applicationContext)
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        setAlarms(applicationContext)
    }

}
