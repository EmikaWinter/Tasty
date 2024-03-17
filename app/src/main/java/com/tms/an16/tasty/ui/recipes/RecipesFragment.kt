package com.tms.an16.tasty.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tms.an16.tasty.databinding.FragmentRecipesBinding
import com.tms.an16.tasty.model.Result
import com.tms.an16.tasty.ui.recipes.adapter.RecipesAdapter
import com.tms.an16.tasty.util.Constants.Companion.API_KEY
import com.tms.an16.tasty.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private var binding: FragmentRecipesBinding? = null

    private val viewModel: RecipesViewModel by viewModels()

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

        requestApiData()
    }

    private fun requestApiData() {
        viewModel.getRecipes()
        viewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.results?.let { setList(it) }
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
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



    private fun setList(list: List<Result>) {
        binding?.recyclerView?.run {
            if (adapter == null) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = RecipesAdapter()
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
}