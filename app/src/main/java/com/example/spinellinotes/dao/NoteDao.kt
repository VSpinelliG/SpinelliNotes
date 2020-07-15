package com.example.spinellinotes.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.spinellinotes.model.Note

@Dao
interface NoteDao{

    @Query("SELECT * FROM note")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM note ORDER BY id DESC")
    fun getAllNotesDesc(): LiveData<List<Note>>

    @Query("SELECT * FROM note ORDER BY title")
    fun getAllNotesAlpha(): LiveData<List<Note>>

    @Query("SELECT * FROM note ORDER BY title DESC")
    fun getAllNotesAlphaDesc(): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE id=:noteId LIMIT 1")
    fun getNoteById(noteId: Long) : Note

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addNote(note: Note) : Long

    @Update
    fun updateNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

}