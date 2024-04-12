package com.tms.an16.tasty.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.tms.an16.tasty.R
import com.tms.an16.tasty.controller.SelectedRecipeController
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

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fav_recipes_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.delete_all) {
                    viewModel.deleteAllFavoriteRecipes()

                    Snackbar.make(
                        requireView(),
                        getString(R.string.all_recipes_removed),
                        Snackbar.LENGTH_SHORT
                    ).setAction(getString(R.string.OK)) {}
                        .show()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun setList(list: List<FavoritesEntity>) {
        binding?.recyclerView?.run {
            if (adapter == null) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = FavoriteRecipesAdapter(onClick = { favorite ->

                    SelectedRecipeController.selectedRecipeEntity = favorite.recipeEntity

                    findNavController().navigate(
                        FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsFragment(
                            favorite.recipeEntity.recipeId
                        )
                    )
                }, onLongClick = { favorite ->
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.delete_from_favorites))
                        .setIcon(R.drawable.ic_delete)
                        .setMessage(getString(R.string.this_action_cannot_be_undone))
                        .setPositiveButton(getString(R.string.yes)) { dialog, _ ->

                            viewModel.deleteFavoriteRecipe(favorite)

                            dialog.dismiss()
                            Toast.makeText(
                                requireContext(), getString(R.string.deleted), Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                })
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
        } else {
            binding?.run {
                noDataImageView.visibility = View.INVISIBLE
                noDataTextView.visibility = View.INVISIBLE
            }
        }
    }
}