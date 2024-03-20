package com.tms.an16.tasty.ui.details

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
import com.tms.an16.tasty.ui.IngredientsFragment
import com.tms.an16.tasty.ui.InstructionsFragment
import com.tms.an16.tasty.ui.OverviewFragment
import com.tms.an16.tasty.ui.details.adapter.PagerAdapter
import com.tms.an16.tasty.util.Constants.Companion.RECIPE_RESULT_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private var binding: ActivityDetailsBinding? = null

    private val args by navArgs<DetailsActivityArgs>()
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
//        menuInflater.inflate(R.menu.details_menu, menu)
//        menuItem = menu!!.findItem(R.id.save_to_favorites_menu)
//        checkSavedRecipes(menuItem)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
//        if (item.itemId == android.R.id.home) {
//            finish()
//        } else if (item.itemId == R.id.save_to_favorites_menu && !recipeSaved) {
//            saveToFavorites(item)
//        } else if (item.itemId == R.id.save_to_favorites_menu && recipeSaved) {
//            removeFromFavorites(item)
//        }
            return super.onOptionsItemSelected(item)
        }
    }