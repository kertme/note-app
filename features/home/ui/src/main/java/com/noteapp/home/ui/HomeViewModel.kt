package com.noteapp.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteapp.home.domain.usecase.DeleteNoteUseCase
import com.noteapp.home.domain.usecase.GetNotesUseCase
import com.noteapp.home.ui.model.NoteUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    val notes = getNotesUseCase.execute().map { notes ->
        notes.toNoteUiModels()
    }

    fun deleteNote(noteUiModel: NoteUiModel) {
        viewModelScope.launch {
            deleteNoteUseCase.execute(noteUiModel.toNoteDomainModel())
        }
    }
}