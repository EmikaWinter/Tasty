package com.tms.an16.tasty.ui.details.instructions

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.tms.an16.tasty.controller.SelectedRecipeController
import com.tms.an16.tasty.databinding.FragmentInstructionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InstructionsFragment : Fragment() {

    private var binding: FragmentInstructionsBinding? = null

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

        val recipe = SelectedRecipeController.selectedRecipeEntity ?: return

        binding?.run {
            instructionsWebView.webViewClient = object : WebViewClient() {
                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                    progressBar.isVisible = false
                }
            }
            instructionsWebView.settings.javaScriptEnabled = true
            instructionsWebView.loadUrl(recipe.sourceUrl)

        }
    }

    fun printPdf() {
        binding?.run {
            val printManager: PrintManager =
                requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter =
                instructionsWebView.createPrintDocumentAdapter("recipe_instructions")
            printManager.print(
                "print_instructions",
                printAdapter,
                null
            )
        }
    }
}