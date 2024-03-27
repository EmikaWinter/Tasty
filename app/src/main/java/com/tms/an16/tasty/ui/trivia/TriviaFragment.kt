package com.tms.an16.tasty.ui.trivia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tms.an16.tasty.databinding.FragmentTriviaBinding
import com.tms.an16.tasty.util.Constants.Companion.API_KEY
import com.tms.an16.tasty.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TriviaFragment : Fragment() {

    private var binding: FragmentTriviaBinding? = null

    private val viewModel: TriviaViewModel by viewModels()

    private var trivia = "No Food Trivia"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTriviaBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getTrivia(API_KEY)
        viewModel.triviaResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding?.run {
                        progressBar.visibility = View.INVISIBLE
                        triviaCardView.visibility = View.VISIBLE
                        triviaTextView.text = response.data?.text
                        if (response.data != null) {
                            trivia = response.data.text
                        }
                    }
                }

                is NetworkResult.Error -> {
                    binding?.run {
                        progressBar.visibility = View.INVISIBLE
                        triviaCardView.visibility = View.INVISIBLE
                    }

                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    binding?.run {
                        progressBar.visibility = View.VISIBLE
                        triviaCardView.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            viewModel.readTrivia.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    binding?.triviaTextView?.text = database.first().trivia.text
                    trivia = database.first().trivia.text
                } else {
                    setNoInternetError()
                }
            }
        }
    }

    private fun setNoInternetError() {
        binding?.run {
            errorImageView.visibility = View.VISIBLE
            errorTextView.visibility = View.VISIBLE
        }
    }
}