package com.tms.an16.tasty.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tms.an16.tasty.R
import com.tms.an16.tasty.controller.NetworkState
import com.tms.an16.tasty.controller.SelectedRecipeController
import com.tms.an16.tasty.database.entity.RecipeEntity
import com.tms.an16.tasty.databinding.FragmentRecipesBinding
import com.tms.an16.tasty.network.NetworkResult
import com.tms.an16.tasty.ui.recipes.adapter.RecipesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {

    private var binding: FragmentRecipesBinding? = null

    private val viewModel: RecipesViewModel by activityViewModels()

    private val args by navArgs<RecipesFragmentArgs>()

    private var dataRequested = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipesBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkIsBackOnline()

        isNetworkConnected()

        val menu: MenuHost = requireActivity()
        menu.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recipes_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@RecipesFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun isNetworkConnected() {
        viewModel.isNetworkConnected.observe(viewLifecycleOwner) {
            readDatabase()

            when (it) {
                NetworkState.UNKNOWN -> return@observe

                NetworkState.CONNECTED -> {
                    binding?.choiceActionButton?.setOnClickListener {
                        findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
                    }

                    if (viewModel.backOnline) {
                        readDatabase()

                        hideNoInternetError()
                        Toast.makeText(
                            context,
                            getString(R.string.we_are_back_online),
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.saveBackOnline(false)
                    }
                }

                NetworkState.DISCONNECTED -> {
                    loadDataFromCache()

                    Toast.makeText(
                        context,
                        getString(R.string.no_internet_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.saveBackOnline(true)

                    binding?.choiceActionButton?.setOnClickListener {
                        Toast.makeText(
                            context,
                            getString(R.string.no_internet_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                else -> {
//                    do nothing
                }
            }
        }
    }

    private fun checkIsBackOnline() {
        lifecycleScope.launch {
            viewModel.readBackOnline.collectLatest {
                viewModel.backOnline = it
            }
        }
    }

    private fun readDatabase() {
        viewModel.readRecipes.observe(viewLifecycleOwner) { database ->
            if (database.isNotEmpty()
                && !args.backFromBottomSheet
                || database.isNotEmpty() && dataRequested
            ) {
                setList(database)
                hideShimmerEffect()
            } else {
                if (!dataRequested && viewModel.isNetworkConnected.value == NetworkState.CONNECTED) {
                    requestApiData()
                    dataRequested = true
                }
            }
        }
    }

    private fun requestApiData() {
        viewModel.getRecipes(viewModel.applyQueries())
        viewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    viewModel.saveMealAndDietType()
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        getMessageFromResponse(response.messageId, response.message),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    private fun searchApiData(query: String) {
        showShimmerEffect()
        viewModel.searchRecipes(viewModel.applySearchQuery(query))
        viewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.recipes?.let { setList(it) }
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        getMessageFromResponse(response.messageId, response.message),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    private fun getMessageFromResponse(messageId: Int?, message: String?): String =
        when {
            messageId != null -> getString(messageId)
            message != null -> message
            else -> getString(R.string.empty_string)
        }


    private fun loadDataFromCache() {
        viewModel.readRecipes.observe(viewLifecycleOwner) { database ->
            if (database.isNotEmpty()) {
                hideNoInternetError()
                setList(database)
            } else {
                setNoInternetError()
            }
        }
    }

    private fun setNoInternetError() {
        binding?.run {
            hideShimmerEffect()
            errorImageView.visibility = View.VISIBLE
            errorTextView.visibility = View.VISIBLE
        }
    }

    private fun hideNoInternetError() {
        binding?.run {
            errorImageView.visibility = View.INVISIBLE
            errorTextView.visibility = View.INVISIBLE
        }
    }

    private fun setList(list: List<RecipeEntity>) {
        binding?.recyclerView?.run {
            if (adapter == null) {
                adapter = RecipesAdapter { recipe ->

                    SelectedRecipeController.selectedRecipeEntity = recipe

                    findNavController().navigate(
                        RecipesFragmentDirections.actionRecipesFragmentToDetailsFragment(
                            recipe.recipeId
                        )
                    )
                }
            }
            (adapter as? RecipesAdapter)?.submitList(list)
        }
    }

    private fun showShimmerEffect() {
        binding?.shimmerFrameLayout?.startShimmer()
        binding?.shimmerFrameLayout?.visibility = View.VISIBLE
        binding?.recyclerView?.visibility = View.GONE
    }

    private fun hideShimmerEffect() {
        binding?.shimmerFrameLayout?.stopShimmer()
        binding?.shimmerFrameLayout?.visibility = View.GONE
        binding?.recyclerView?.visibility = View.VISIBLE
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }
}