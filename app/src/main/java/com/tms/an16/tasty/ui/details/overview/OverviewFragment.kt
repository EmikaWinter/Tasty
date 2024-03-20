package com.tms.an16.tasty.ui.details.overview

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.tms.an16.tasty.R
import com.tms.an16.tasty.databinding.FragmentOverviewBinding
import com.tms.an16.tasty.model.Result
import com.tms.an16.tasty.util.Constants.Companion.RECIPE_RESULT_KEY
import com.tms.an16.tasty.util.parseHtml

@Suppress("DEPRECATION")
class OverviewFragment : Fragment() {

    private var binding: FragmentOverviewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOverviewBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        if (myBundle != null) {
            binding!!.mainImageView.run {
                Glide.with(requireContext()).load(myBundle.image).into(this)
            }
            binding!!.titleTextView.text = myBundle.title
            binding!!.likesTextView.text = myBundle.aggregateLikes.toString()
            binding!!.timeTextView.text = myBundle.readyInMinutes.toString()
            parseHtml(binding!!.summaryTextView, myBundle.summary)

            updateColors(myBundle.vegetarian, binding!!.vegetarianTextView)
            updateColors(myBundle.vegan, binding!!.veganTextView)
            updateColors(myBundle.cheap, binding!!.cheapTextView)
            updateColors(myBundle.dairyFree, binding!!.dairyTextView)
            updateColors(myBundle.glutenFree, binding!!.glutenFreeTextView)
            updateColors(myBundle.veryHealthy, binding!!.healthyTextView)
        }
    }

    private fun updateColors(stateIsOn: Boolean, textView: TextView) {
        if (stateIsOn) {
            textView.setTextColor(getColor(requireContext(), R.color.green))
            textView.setDrawableTintColor(R.color.green)
        }
    }

    private fun TextView.setDrawableTintColor(colorRes: Int) {
        for (drawable in this.compoundDrawablesRelative) {
            drawable?.colorFilter =
                PorterDuffColorFilter(getColor(context, colorRes), PorterDuff.Mode.SRC_IN)
        }
    }
}