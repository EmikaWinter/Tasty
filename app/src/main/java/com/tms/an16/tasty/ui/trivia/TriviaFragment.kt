package com.tms.an16.tasty.ui.trivia

import android.content.Intent
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
import com.tms.an16.tasty.R
import com.tms.an16.tasty.databinding.FragmentTriviaBinding
import com.tms.an16.tasty.util.Constants.Companion.API_KEY
import com.tms.an16.tasty.network.NetworkResult
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

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.trivia_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.share_trivia_menu) {
                    val shareIntent = Intent().apply {
                        this.action = Intent.ACTION_SEND
                        this.putExtra(Intent.EXTRA_TEXT, "${getString(R.string.interesting_fact)} \n $trivia")
                        this.type = "text/plain"
                    }
                    startActivity(shareIntent)
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.getTrivia(API_KEY)

        viewModel.triviaResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding?.run {
                        progressBar.visibility = View.INVISIBLE
                        triviaCardView.visibility = View.VISIBLE
                        interestingFactTextView.visibility = View.VISIBLE
                        triviaBulbImageView.visibility = View.VISIBLE
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
                        interestingFactTextView.visibility = View.INVISIBLE
                        triviaBulbImageView.visibility = View.INVISIBLE
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
                        interestingFactTextView.visibility = View.INVISIBLE
                        triviaBulbImageView.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            viewModel.readTrivia.observe(viewLifecycleOwner) { database ->
                if (!database.isNullOrEmpty()) {
                    binding?.run {
                        triviaCardView.visibility = View.VISIBLE
                        interestingFactTextView.visibility = View.VISIBLE
                        triviaBulbImageView.visibility = View.VISIBLE
                        triviaTextView.text = database.first().trivia.text
                    }
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