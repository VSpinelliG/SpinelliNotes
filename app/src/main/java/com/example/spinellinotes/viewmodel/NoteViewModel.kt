package com.example.spinellinotes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.spinellinotes.db.NoteRoomDatabase
import com.example.spinellinotes.model.Note
import com.example.spinellinotes.repository.NoteRepository
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteRepository: NoteRepository
    val allNotes: LiveData<List<Note>>

    init {
        val notesDao = NoteRoomDatabase.getDatabase(application).noteDao()
        noteRepository = NoteRepository(notesDao)
        allNotes = noteRepository.allNotes
    }

    fun getNoteById(id: Long) : Note{
        return noteRepository.getNoteById(id)
    }

    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.addNote(note)
    }

    fun update(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.updateNote(note)
    }

    fun delete(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.deleteNote(note)
    }

}