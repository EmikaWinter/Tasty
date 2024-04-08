package com.tms.an16.tasty.ui.details.instructions

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tms.an16.tasty.databinding.FragmentInstructionsBinding
import com.tms.an16.tasty.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstructionsFragment : Fragment() {

    private var binding: FragmentInstructionsBinding? = null

    private val viewModel: InstructionsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstructionsBinding.inflate(inflater)
        return binding?.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments

        val recipeId = args?.getInt(Constants.RECIPE_RESULT_KEY) ?: 0

        viewModel.loadSelectedRecipeById(recipeId)

        viewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
            binding?.run {
                instructionsWebView.webViewClient = object : WebViewClient() {}
                instructionsWebView.settings.javaScriptEnabled = true
                instructionsWebView.loadUrl(recipe.sourceUrl)
            }
        }
    }
}