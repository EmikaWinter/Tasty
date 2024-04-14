package com.tms.an16.tasty.ui.recipes.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.tms.an16.tasty.databinding.RecipesBottomSheetBinding
import com.tms.an16.tasty.ui.recipes.RecipesViewModel
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.tms.an16.tasty.util.Constants.Companion.DEFAULT_MEAL_TYPE
import java.util.Locale

class RecipesBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: RecipesViewModel by activityViewModels()
    private var binding: RecipesBottomSheetBinding? = null

    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RecipesBottomSheetBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        readMealAndDietType()

        chipSetOnCheckedStateChangeListener()

        binding?.applyButton?.setOnClickListener {
            viewModel.saveMealAndDietTypeTemp(
                mealTypeChip, mealTypeChipId, dietTypeChip, dietTypeChipId
            )

            val action = RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment()
            action.backFromBottomSheet = true
            findNavController().navigate(action)
        }
    }

    private fun readMealAndDietType() {
        viewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner) { value ->
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
            binding?.run {
                updateChip(value.selectedMealTypeId, mealTypeChipGroup)
                updateChip(value.selectedDietTypeId, dietTypeChipGroup)
            }
        }
    }

    private fun chipSetOnCheckedStateChangeListener() {
        binding?.run {
            mealTypeChipGroup.setOnCheckedStateChangeListener { group, selectedChipId ->

                mealTypeChip =
                    group.findViewById<Chip>(selectedChipId.first()).text.toString().lowercase(
                        Locale.ROOT
                    )
                mealTypeChipId = selectedChipId.first()
            }

            dietTypeChipGroup.setOnCheckedStateChangeListener { group, selectedChipId ->

                dietTypeChip =
                    group.findViewById<Chip>(selectedChipId.first()).text.toString().lowercase(
                        Locale.ROOT
                    )
                dietTypeChipId = selectedChipId.first()
            }
        }
    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != 0) {
            try {
                val targetView = chipGroup.findViewById<Chip>(chipId)
                targetView.isChecked = true
                chipGroup.requestChildFocus(targetView, targetView)
            } catch (_: Exception) {
            }
        }
    }
}