package dk.shape.games.notifications.utils

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.shape.games.notifications.R

abstract class ExpandableBottomSheetDialogFragment(
    private val topOffset: Int = 0
) : BottomSheetDialogFragment() {

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
            val windowHeight = Resources.getSystem().displayMetrics.heightPixels - topOffset
            bottomSheet.layoutParams?.apply {
                height = windowHeight
            }
            BottomSheetBehavior.from(bottomSheet).apply {
                peekHeight = windowHeight
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}