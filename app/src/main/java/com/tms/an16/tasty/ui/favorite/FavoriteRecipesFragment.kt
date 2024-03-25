package com.tms.an16.tasty.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.databinding.FragmentFavoriteRecipesBinding
import com.tms.an16.tasty.ui.favorite.adapter.FavoriteRecipesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {

    private var binding: FragmentFavoriteRecipesBinding? = null

    private val viewModel: FavoriteRecipesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteRecipesBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.readFavoriteRecipes.observe(viewLifecycleOwner) {
            setList(it)
            setNoDataError(it)
        }

    }

    private fun setList(list: List<FavoritesEntity>) {
        binding?.recyclerView?.run {
            if (adapter == null) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = FavoriteRecipesAdapter { favorite ->
                    findNavController().navigate(
                        FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsActivity(
                            favorite.result
                        )
                    )
                }
            }
            (adapter as? FavoriteRecipesAdapter)?.submitList(list)
        }
    }

    private fun setNoDataError(favorites: List<FavoritesEntity>?) {
        if (favorites.isNullOrEmpty()) {
            binding?.run {
                noDataImageView.visibility = View.VISIBLE
                noDataTextView.visibility = View.VISIBLE
            }
        }
    }
}