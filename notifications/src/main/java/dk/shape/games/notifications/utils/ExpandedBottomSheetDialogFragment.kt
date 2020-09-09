package dk.shape.games.notifications.utils

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.shape.games.notifications.R

abstract class ExpandedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    open var isExpanded: Boolean = false

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            setOnShowListener {
                setFullyExpanded()
            }
        }
    }

    private fun setFullyExpanded() {
        (requireView().parent as? ViewGroup)?.let { bottomSheet ->
            BottomSheetBehavior.from(bottomSheet).apply {
                addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == STATE_EXPANDED) {
                            isExpanded = true
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
                state = STATE_EXPANDED
                skipCollapsed = true
            }
        }
    }
}