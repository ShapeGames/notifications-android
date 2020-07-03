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
        val bottomSheet = requireView().parent as? ViewGroup?
        bottomSheet?.let { sheet ->
            bottomSheet.layoutParams?.let {
                val windowHeight = Resources.getSystem().displayMetrics.heightPixels - topOffset
                it.height = windowHeight
                sheet.layoutParams = it
                BottomSheetBehavior.from(bottomSheet).apply {
                    peekHeight = windowHeight
                    state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }
}