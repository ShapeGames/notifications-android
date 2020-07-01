package dk.shape.games.notifications.presentation

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class ExpandableBottomSheetDialogFragment(
    private val topOffset: Int = 0
) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            setOnShowListener {
                setFullyExpanded()
            }
        }
    }

    private fun setFullyExpanded() {
        val bottomSheet = requireView().parent as? ViewGroup?
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheet.layoutParams?.let {
                val windowHeight = Resources.getSystem().displayMetrics.heightPixels - topOffset
                it.height = windowHeight
                bottomSheet.layoutParams = it
                behavior.peekHeight = windowHeight
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}