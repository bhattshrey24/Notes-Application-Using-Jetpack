package com.example.notesapplicationusingjetpack.di

import android.app.Application
import androidx.room.Room
import com.example.notesapplicationusingjetpack.feature_note.data.data_source.NoteDatabase
import com.example.notesapplicationusingjetpack.feature_note.data.repository.NoteRepositoryImpl
import com.example.notesapplicationusingjetpack.feature_note.domain.repository.NoteRepository
import com.example.notesapplicationusingjetpack.feature_note.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao) // Just by changing here you can
    // change which implementation class you want for this repository for the whole app
        //and even while testing we can pass fake repository here and its that simple
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository)
        )
    }
}