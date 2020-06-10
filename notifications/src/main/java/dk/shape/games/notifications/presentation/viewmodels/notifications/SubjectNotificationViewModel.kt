package dk.shape.games.notifications.presentation.viewmodels.notifications

import android.view.View
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dk.shape.games.notifications.aliases.PreferencesSaveAction
import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.presentation.SubjectNotificationStateData

internal data class SubjectNotificationViewModel(
    val subjectId: String,
    val subjectName: String,
    val subjectType: SubjectType,
    private val onClosedPressed: () -> Unit,
    private val onPreferencesSaved: PreferencesSaveAction
) {
    val selectedIdentifiers: HashSet<StatsNotificationIdentifier> = HashSet()

    val defaultIdentifiers: HashSet<StatsNotificationIdentifier> = HashSet()

    val initialIdentifiers: HashSet<StatsNotificationIdentifier> = HashSet()

    val savingPreferences: ObservableBoolean = ObservableBoolean(false)

    val hasStateChanges: ObservableBoolean = ObservableBoolean(false)

    val activeNotificationState: ObservableBoolean = ObservableBoolean(false)

    val notificationTypes: ObservableField<List<SubjectNotificationTypeViewModel>> = ObservableField(
        emptyList()
    )

    private val stateDataSubject: () -> SubjectNotificationStateData = {
        SubjectNotificationStateData(
            subjectId = subjectId,
            subjectType = subjectType,
            notificationTypeIdentifiers = notificationTypes.get().orEmpty()
                .filter { it.isActivated.get() }.map { it.identifier }
        )
    }

    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        activeNotificationState.set(isChecked)
        if (isChecked) {
            notificationTypes.get()?.forEach {
                it.isActivated.set(defaultIdentifiers.contains(it.identifier))
            }
        } else {
            notificationTypes.get()?.forEach {
                it.isActivated.set(false)
            }
        }
    }

    val notifySelection = {
        val sameIndetifiers = compareIndetifiers(
            initialIdentifiers = initialIdentifiers,
            currentIdentifiers = selectedIdentifiers
        )
        hasStateChanges.set(!sameIndetifiers)
    }

    val onPreferencesSavedListener = View.OnClickListener {
        val sameIndetifiers = compareIndetifiers(
            initialIdentifiers = initialIdentifiers,
            currentIdentifiers = selectedIdentifiers
        )
        if (sameIndetifiers) {
            return@OnClickListener
        }
        savingPreferences.set(true)
        onPreferencesSaved(stateDataSubject(), onClosedPressed) {
            savingPreferences.set(false)
        }
    }

    private fun compareIndetifiers(
        initialIdentifiers: Set<StatsNotificationIdentifier>,
        currentIdentifiers: Set<StatsNotificationIdentifier>
    ): Boolean {
        val sequenceOne = initialIdentifiers.sorted().map { it.name }.joinToString { it }
        val sequenceTwo = currentIdentifiers.sorted().map { it.name }.joinToString { it }
        return sequenceOne == sequenceTwo
    }
}