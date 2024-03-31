package com.tms.an16.tasty.ui.details.instructions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.tms.an16.tasty.databinding.FragmentInstructionsBinding
import com.tms.an16.tasty.model.Result
import com.tms.an16.tasty.util.Constants

@Suppress("DEPRECATION")
class InstructionsFragment : Fragment() {

    private var binding: FragmentInstructionsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstructionsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val myBundle: Result? = args?.getParcelable(Constants.RECIPE_RESULT_KEY)

        if (myBundle != null) {
            binding?.run {
                instructionsWebView.webViewClient = object : WebViewClient() {}
                instructionsWebView.settings.javaScriptEnabled = true
                instructionsWebView.loadUrl(myBundle.sourceUrl)
            }
        }
    }
}