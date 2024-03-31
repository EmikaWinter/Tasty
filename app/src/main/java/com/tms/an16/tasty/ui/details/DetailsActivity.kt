package com.tms.an16.tasty.ui.details

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.tms.an16.tasty.R
import com.tms.an16.tasty.databinding.ActivityDetailsBinding
import com.tms.an16.tasty.model.Result
import com.tms.an16.tasty.receiver.NetworkReceiver
import com.tms.an16.tasty.ui.details.adapter.PagerAdapter
import com.tms.an16.tasty.ui.details.ingredients.IngredientsFragment
import com.tms.an16.tasty.ui.details.instructions.InstructionsFragment
import com.tms.an16.tasty.ui.details.overview.OverviewFragment
import com.tms.an16.tasty.util.Constants.Companion.RECIPE_RESULT_KEY
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private var binding: ActivityDetailsBinding? = null

    private val args by navArgs<DetailsActivityArgs>()

    private val networkReceiver = NetworkReceiver()

    private var url = "no data"
    private var title = "no data"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbar)
        binding?.toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments = ArrayList<Fragment>()
        fragments.add(OverviewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        val titles = ArrayList<String>()
        titles.add("Overview")
        titles.add("Ingredients")
        titles.add("Instructions")

        val resultBundle = Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_KEY, args.result)
        val result: Result? = resultBundle.getParcelable(RECIPE_RESULT_KEY)
        url = result?.sourceUrl.toString()
        title = result?.title.toString()

        val pagerAdapter = PagerAdapter(
            resultBundle,
            fragments,
            this
        )

        binding?.viewPager2?.isUserInputEnabled = false
        binding?.viewPager2?.apply {
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding!!.tabLayout, binding!!.viewPager2) { tab, position ->
            tab.text = titles[position]
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.share_recipe -> {
                val shareIntent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    this.putExtra(
                        Intent.EXTRA_TEXT,
                        "${getString(R.string.awesome_recipe_check_it_out)} \n $title \n $url"
                    )
                    this.type = "text/plain"
                }
                startActivity(shareIntent)
            }

            R.id.save_as_pdf_recipe -> {
//              TODO
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(
            networkReceiver, IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION
            )
        )
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(networkReceiver)
    }
}