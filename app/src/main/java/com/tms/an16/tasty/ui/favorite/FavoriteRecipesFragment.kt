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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.tms.an16.tasty.R
import com.tms.an16.tasty.database.entity.FavoritesEntity
import com.tms.an16.tasty.databinding.FragmentFavoriteRecipesBinding
import com.tms.an16.tasty.ui.favorite.adapter.FavoriteRecipesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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


        lifecycleScope.launch {
            viewModel.readFavoriteRecipes.collectLatest {
                setList(it)
                setNoDataError(it)
            }
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
                        "All recipes removed.",
                        Snackbar.LENGTH_SHORT
                    ).setAction("Okay") {}
                        .show()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun setList(list: List<FavoritesEntity>) {
        binding?.recyclerView?.run {
            if (adapter == null) {
                adapter = FavoriteRecipesAdapter(onClick = { favorite ->
                    findNavController().navigate(
                        FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsFragment(
                            favorite.recipeEntity.recipeId
                        )
                    )
                }, onLongClick = { favorite ->
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete this recipe from favorites?")
                        .setIcon(R.drawable.ic_delete)
                        .setMessage("This action cannot be undone")
                        .setPositiveButton("Yes") { dialog, _ ->

                            viewModel.deleteFavoriteRecipe(favorite)

                            dialog.dismiss()
                            Toast.makeText(
                                requireContext(), "Deleted", Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton("No") { dialog, _ ->
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