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
    shouldFillScreen: Boolean = true
): BottomSheetDialogFragment() {

    private var isVisisble: Boolean = false

    protected var fillsScreen: Boolean = shouldFillScreen
        private set

    protected var topOffset: Int = 0
        set(value) {
            field = value
            if (isVisisble) {
                setFullyExpanded(fillsScreen, value)
            }
        }

    private var originalHeight: Int = -1

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    protected fun BottomSheetDialogFragment.requireBottomSheetView(): ViewGroup? =
        requireView().parent as? ViewGroup?

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            setOnShowListener {
                isVisisble = true
                setFullyExpanded(fillsScreen, topOffset)
            }
        }
    }

    protected fun setFullyExpanded(fillsScreen: Boolean, topOffset: Int = 0) {
        val bottomSheet = requireBottomSheetView()
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheet.layoutParams?.let {
                val windowHeight = Resources.getSystem().displayMetrics.heightPixels - topOffset
                this.fillsScreen = fillsScreen
                if (fillsScreen) {
                    if (originalHeight == -1) {
                        originalHeight = it.height
                    }
                    it.height = windowHeight
                    bottomSheet.layoutParams = it
                    behavior.peekHeight = windowHeight
                } else {
                    if (originalHeight != -1) {
                        it.height = originalHeight
                        bottomSheet.layoutParams = it
                        behavior.peekHeight = originalHeight
                    }
                }
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}