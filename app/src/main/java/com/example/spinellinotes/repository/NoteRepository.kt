package com.example.spinellinotes.repository

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.loader.content.AsyncTaskLoader
import com.example.spinellinotes.dao.NoteDao
import com.example.spinellinotes.model.Note
import io.reactivex.Flowable

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    fun getNoteById(id: Long) : Note{
        return noteDao.getNoteById(id)
    }

    fun addNote(note: Note){
        noteDao.addNote(note)
    }

    fun updateNote(note: Note){
        noteDao.updateNote(note)
    }

    fun deleteNote(note: Note){
        noteDao.deleteNote(note)
    }

}