package dk.shape.games.notifications.presentation

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.shape.games.notifications.R

abstract class ExpandableBottomSheetDialogFragment(
    val topOffset: Int = 0
): BottomSheetDialogFragment() {

    private var originalHeight: Int = -1

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    protected fun BottomSheetDialogFragment.requireBottomSheetView(): ViewGroup? =
        requireView().parent as? ViewGroup?

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            setOnShowListener {
                setFullyExpanded()
            }
        }
    }

    private fun setFullyExpanded() {
        val bottomSheet = requireBottomSheetView()
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheet.layoutParams?.let {
                val windowHeight = Resources.getSystem().displayMetrics.heightPixels - topOffset
                if (originalHeight == -1) {
                    originalHeight = it.height
                }
                it.height = windowHeight
                bottomSheet.layoutParams = it
                behavior.peekHeight = windowHeight
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}