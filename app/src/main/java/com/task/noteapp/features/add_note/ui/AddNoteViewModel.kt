package com.task.noteapp.features.add_note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.noteapp.core.db.Note
import com.task.noteapp.features.add_note.domain.NoteRepository
import com.task.noteapp.features.add_note.domain.model.NoteDetailsType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * @author: R. Cemre Ünal,
 * created on 9/22/2022
 */

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val repository: NoteRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun setUiState(noteDetailsType: NoteDetailsType, note: Note?) {
        val currentState = when (noteDetailsType) {
            NoteDetailsType.ADD -> State.ADD_NEW_NOTE
            NoteDetailsType.VIEW -> State.VIEW_NOTE_DETAILS
        }
        _state.update { oldState ->
            oldState.copy(currentState = currentState, photoUrl = note?.imageUrl, note = note)
        }
    }

    fun changeUiStateToEditNote() {
        _state.update { oldState ->
            oldState.copy(currentState = State.EDIT_NOTE)
        }
    }

    fun setPhoto(photoUrl: String?) {
        _state.update { oldState ->
            oldState.copy(photoUrl = photoUrl)
        }
    }

    fun saveNote(
        title: String,
        content: String? = null,
    ) {

        viewModelScope.launch {
            val currentDate = Calendar.getInstance().time
            val noteId = state.value.note?.dbId ?: 0
            var createDate: Date = currentDate
            var modifyDate: Date? = null

            when (state.value.currentState) {
                State.EDIT_NOTE -> {
                    createDate = state.value.note?.createDate!!
                    modifyDate = currentDate
                }
                else -> {}
            }
            repository.addNote(
                Note(
                    dbId = noteId,
                    title = title,
                    content = content,
                    imageUrl = state.value.photoUrl,
                    createDate = createDate,
                    modifyDate = modifyDate
                )
            )
        }
    }

    enum class State {
        INITIAL,
        VIEW_NOTE_DETAILS,
        ADD_NEW_NOTE,
        EDIT_NOTE
    }

    data class UiState(
        val currentState: State = State.INITIAL,
        val photoUrl: String? = null,
        val note: Note? = null
    )
}