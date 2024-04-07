package com.tms.an16.tasty.ui

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.tms.an16.tasty.R
import com.tms.an16.tasty.databinding.ActivityMainBinding
import com.tms.an16.tasty.receiver.NetworkReceiver
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private lateinit var navController: NavController

    private val networkReceiver = NetworkReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Tasty)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        registerReceiver(
            networkReceiver, IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION
            )
        )

//        binding?.bottomNavigation?.visibility = View.VISIBLE

        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.recipesFragment,
                R.id.favoriteRecipesFragment,
                R.id.triviaFragment
            )
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.recipesFragment -> showBottomNav()
                R.id.favoriteRecipesFragment -> showBottomNav()
                R.id.triviaFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }

        binding?.bottomNavigation?.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    private fun showBottomNav() {
        binding?.bottomNavigation?.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding?.bottomNavigation?.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

//    override fun onResume() {
//        super.onResume()
//        registerReceiver(
//            networkReceiver, IntentFilter(
//                ConnectivityManager.CONNECTIVITY_ACTION
//            )
//        )
//    }

//    override fun onStop() {
//        super.onStop()
//        unregisterReceiver(networkReceiver)
//    }
}