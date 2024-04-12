package com.tms.an16.tasty.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.android.material.tabs.TabLayoutMediator
import com.tms.an16.tasty.R
import com.tms.an16.tasty.controller.SelectedRecipeController
import com.tms.an16.tasty.databinding.FragmentDetailsBinding
import com.tms.an16.tasty.ui.details.adapter.PagerAdapter
import com.tms.an16.tasty.ui.details.ingredients.IngredientsFragment
import com.tms.an16.tasty.ui.details.instructions.InstructionsFragment
import com.tms.an16.tasty.ui.details.overview.OverviewFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var binding: FragmentDetailsBinding? = null

    private var url = "no data"
    private var title = "no data"

    private val overviewFragment = OverviewFragment()
    private val ingredientsFragment = IngredientsFragment()
    private val instructionsFragment = InstructionsFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.details_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        requireActivity().supportFragmentManager.popBackStack()
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
                        instructionsFragment.printPdf()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val fragments = ArrayList<Fragment>()
        fragments.add(overviewFragment)
        fragments.add(ingredientsFragment)
        fragments.add(instructionsFragment)

        val titles = ArrayList<String>()
        titles.add(getString(R.string.overview))
        titles.add(getString(R.string.ingredients))
        titles.add(getString(R.string.instructions))

        url = SelectedRecipeController.selectedRecipeEntity?.sourceUrl.toString()
        title = SelectedRecipeController.selectedRecipeEntity?.title.toString()

        val pagerAdapter = PagerAdapter(
            fragments, this
        )

        binding?.run {
            viewPager2.apply {
                adapter = pagerAdapter
                offscreenPageLimit = 3
                isUserInputEnabled = false
            }

            TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }
    }

}