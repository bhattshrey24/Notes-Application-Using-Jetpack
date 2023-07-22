package com.example.notesapplicationusingjetpack.feature_note.domain.use_case

import com.example.notesapplicationusingjetpack.feature_note.domain.model.Note
import com.example.notesapplicationusingjetpack.feature_note.domain.repository.NoteRepository
import com.example.notesapplicationusingjetpack.feature_note.domain.util.NoteOrder
import com.example.notesapplicationusingjetpack.feature_note.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNotes(
    private val repository: NoteRepository
) {

    operator fun invoke(
        noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending)
    ): Flow<List<Note>> { // See how beautifully he sorted the list
        return repository.getNotes().map { notes ->
            when(noteOrder.orderType) {
                is OrderType.Ascending -> {
                    when(noteOrder) {
                        is NoteOrder.Title -> notes.sortedBy { it.title.lowercase() } // lowercase
                        // because this will first convert title to lowercase and then sort because
                        // if we don't do this then since there can be capital letters too then the
                        // sorting will be weird
                        is NoteOrder.Date -> notes.sortedBy { it.timestamp }
                        is NoteOrder.Color -> notes.sortedBy { it.color }
                    }
                }
                is OrderType.Descending -> {
                    when(noteOrder) {
                        is NoteOrder.Title -> notes.sortedByDescending { it.title.lowercase() }
                        is NoteOrder.Date -> notes.sortedByDescending { it.timestamp }
                        is NoteOrder.Color -> notes.sortedByDescending { it.color }
                    }
                }
            }
        }
    }
}