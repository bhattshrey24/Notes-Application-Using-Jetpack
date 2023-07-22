package com.example.notesapplicationusingjetpack.feature_note.data.data_source

import androidx.room.*
import com.example.notesapplicationusingjetpack.feature_note.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    fun getNotes(): Flow<List<Note>> // this is not a suspend function unlike others
    // because it returns a flow

    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE) // This will also update the
    // note because see we used OnConflictStrategy.REPLACE which means if db finds
    // a note with id which already exists then it will simply replace old note with
    // this new one basically we make changes and insert the note and db will automatically
    // update the note if its present already and insert if not
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}