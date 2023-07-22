package com.example.notesapplicationusingjetpack.feature_note.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapplicationusingjetpack.feature_note.domain.model.Note
import com.example.notesapplicationusingjetpack.feature_note.domain.use_case.NoteUseCases
import com.example.notesapplicationusingjetpack.feature_note.domain.util.NoteOrder
import com.example.notesapplicationusingjetpack.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    private var recentlyDeletedNote: Note? = null // We are keeping track of this so that
    // we can undo last deleted note

    private var getNotesJob: Job? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                if (state.value.noteOrder::class == event.noteOrder::class &&
                    state.value.noteOrder.orderType == event.noteOrder.orderType // i.e.
                // we are checking whether user chose the same option as before then no
                // need to perform any sorting. '::class' because NoteOrder is a sealed
                // class and its children are classes of the types of 'sorting order'
                // which we want to check. '::class' will check if objects are of same class
                ) {
                    return
                }
                getNotes(event.noteOrder)
            }
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note)
                    recentlyDeletedNote = event.note
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.addNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }
            is NotesEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        getNotesJob?.cancel() // we are doing this because whenever we recall this
        //   function 'getNotes' then it will launch a new coroutine but we want
        //   to make sure that old one is cancelled before starting new
        //   therefore we did this
        getNotesJob = noteUseCases.getNotes(noteOrder)
            .onEach { notes -> // this on each is for flow like we
                // are saying on each emission do what is specified in brackets
                _state.value = state.value.copy(
                    notes = notes, // i.e. the notes that we get from flow
                    noteOrder = noteOrder // i.e. changing the order of the state
                )
            }
            .launchIn(viewModelScope)
    }
}