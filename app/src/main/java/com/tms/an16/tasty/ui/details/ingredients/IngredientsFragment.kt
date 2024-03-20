package com.tms.an16.tasty.ui.details.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tms.an16.tasty.databinding.FragmentIngredientsBinding
import com.tms.an16.tasty.model.ExtendedIngredient
import com.tms.an16.tasty.model.Result
import com.tms.an16.tasty.ui.details.ingredients.adapter.IngredientsAdapter
import com.tms.an16.tasty.util.Constants


@Suppress("DEPRECATION")
class IngredientsFragment : Fragment() {

    private var binding: FragmentIngredientsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIngredientsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val myBundle: Result? = args?.getParcelable(Constants.RECIPE_RESULT_KEY)
        myBundle?.extendedIngredients?.let { setList(it) }
    }

    private fun setList(list: List<ExtendedIngredient>) {
        binding?.recyclerview?.run {
            if (adapter == null) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = IngredientsAdapter()
            }
            (adapter as? IngredientsAdapter)?.submitList(list)
        }
    }
}