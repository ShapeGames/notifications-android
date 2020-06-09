package dk.shape.games.notifications.presentation

import android.view.View
import android.widget.CompoundButton

internal typealias PreferencesSaveAction = (stateSubject: SubjectNotificationStateData, onFailed: () -> Unit, onSuccess: () -> Unit) -> Unit

internal data class SubjectNotificationViewModel(
    val subjectId: String,
    val subjectName: String,
    val subjectType: String,
    val subjectInitialNotificationState: Boolean,
    val notificationTypes: List<SubjectNotificationTypeViewModel>,
    private val onBackPressed: () -> Unit,
    private val onPreferencesSaved: PreferencesSaveAction
) {
    private val stateDataSubject: () -> SubjectNotificationStateData = {
        SubjectNotificationStateData(
            subjectId = subjectId,
            subjectType = subjectType,
            isNotificationActive = isActivated,
            notificationTypeIdentifiers = notificationTypes.filter { it.isActivated }
                .map { it.identifier }
        )
    }

    var isActivated: Boolean = subjectInitialNotificationState

    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        isActivated = isChecked
    }

    val onClosePressedListener = View.OnClickListener {
        onBackPressed()
    }

    val onPreferencesSavedListener = View.OnClickListener {

        onPreferencesSaved(stateDataSubject(), {

        }, {
            onBackPressed()
        })
    }
}