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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.tms.an16.tasty.R
import com.tms.an16.tasty.databinding.FragmentRecipesBinding
import com.tms.an16.tasty.model.Result
import com.tms.an16.tasty.ui.recipes.adapter.RecipesAdapter
import com.tms.an16.tasty.util.NetworkListener
import com.tms.an16.tasty.util.NetworkResult
import com.tms.an16.tasty.util.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {

    private var binding: FragmentRecipesBinding? = null

    private lateinit var viewModel: RecipesViewModel

    private lateinit var networkListener: NetworkListener

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

        viewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]

        viewModel.readBackOnline.observe(viewLifecycleOwner) {
            viewModel.backOnline = it
        }

        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                networkListener = NetworkListener()
                networkListener.checkNetworkAvailability(requireContext())
                    .collect { status ->
                        viewModel.networkStatus = status
                        viewModel.showNetworkStatus(requireContext())
                        readDatabase()
                    }
            }
        }

        binding?.choiceActionButton?.setOnClickListener {
            if (viewModel.networkStatus) {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            } else {
                viewModel.showNetworkStatus(requireContext())
            }
        }

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

    private fun readDatabase() {
        lifecycleScope.launch {
            viewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()
                    && !args.backFromBottomSheet
                    || database.isNotEmpty() && dataRequested
                ) {
                    setList(database.first().foodRecipe.results)
                    hideShimmerEffect()
                } else {
                    if (!dataRequested) {
                        requestApiData()
                        dataRequested = true
                    }
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
                    response.data?.results?.let { setList(it) }
                    viewModel.saveMealAndDietType()
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
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
                    response.data?.results?.let { setList(it) }
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    private fun loadDataFromCache() {
        viewModel.readRecipes.observe(viewLifecycleOwner) { database ->
            if (!database.isNullOrEmpty()) {
                setList(database.first().foodRecipe.results)
            } else {
                setNoInternetError()
            }
        }
    }

    private fun setNoInternetError() {
        binding?.run {
            errorImage.visibility = View.VISIBLE
            errorText.visibility = View.VISIBLE
        }
    }

    private fun setList(list: List<Result>) {
        binding?.recyclerView?.run {
            if (adapter == null) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = RecipesAdapter { result ->
                    findNavController().navigate(
                        RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(
                            result
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
        if (query != null) {
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }
}