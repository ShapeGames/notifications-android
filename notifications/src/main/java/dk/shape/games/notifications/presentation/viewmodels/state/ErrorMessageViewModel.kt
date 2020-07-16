package dk.shape.games.notifications.presentation.viewmodels.state

import android.content.Context
import androidx.databinding.ObservableField
import dk.shape.games.notifications.R
import dk.shape.games.statusmessages.presentation.*
import java.util.*

private const val MESSAGE_DISMISS_DELAY = 5000L

class ErrorMessageViewModel(
    private val contextResolver: () -> Context
) {
    val presentationViewModel = ObservableField(StatusMessagePresentationViewModel())

    private val statusMessagePresenter = StatusMessagePresenter(presentationViewModel)

    private val dismissDate: Date
        get() = Date().apply {
            time += MESSAGE_DISMISS_DELAY
        }

    fun showErrorMessage(errorType: ErrorType) = statusMessagePresenter.showSingleStatusMessage(
        getErrorStatusMessage(errorType)
    )

    fun showErrorMessage() = showErrorMessage(ErrorType.SavingPreferences)

    private fun getErrorStatusMessage(
        errorType: ErrorType
    ): StatusMessagePresentation = StatusMessagePresentation(
        id = "notifications error " + Math.random().toString(),
        type = StatusMessagePresentation.Type.Error,
        iconResource = 0,
        presentationTitle = StatusMessagePresentationTitle(
            title = contextResolver().getString(R.string.error_message_title),
            textColor = R.color.grey_000
        ),
        presentationText = StatusMessagePresentationText(
            text = contextResolver().getString(errorType.textRes),
            textColor = R.color.grey_000_alpha_75
        ),
        actions = StatusMessagePresentationActions(
            deactivatedAt = dismissDate
        )
    )

    enum class ErrorType(val textRes: Int) {
        SavingPreferences(R.string.error_saving_preferences)
    }
}