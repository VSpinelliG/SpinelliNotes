package com.example.spinellinotes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spinellinotes.adapters.NoteAdapter
import com.example.spinellinotes.db.NoteRoomDatabase
import com.example.spinellinotes.model.Note
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NoteAdapter.OnItemClickListener {

    private lateinit var notes: LiveData<List<Note>>
    private lateinit var db:NoteRoomDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        db = NoteRoomDatabase.getDatabase(this)

        notes = db.noteDao().getAllNotes()

        runAdapter()

        //quando não há nenhuma nota
        /*
        if (button_first_add.isVisible) {
            button_first_add.setOnClickListener {
                val intent = Intent(this, NewNoteActivity::class.java)
                startActivity(intent)
            }
        }
         */

    }

    //após criar uma nota, a main é continuada
    /*
    override fun onRestart() {
        runAdapter()
        super.onRestart()
    }
     */

    //renderiza o recycler view (usado a cada criação, ordenação e exclusão de nota)
    //e define o toolbar criado para esta activity
    private fun runAdapter() {
        setSupportActionBar(toolbar_main)

        val adapter = NoteAdapter(this)
        recycle_view.adapter = adapter

        notes.observe(this, Observer { notes ->
            notes?.let { adapter.setNotes(it) }
        })

        recycle_view.layoutManager = LinearLayoutManager(this)
        recycle_view.setHasFixedSize(true)

    }

    //criação do toolbar com os botões
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)
        label_no_note.isVisible = false
        button_first_add.isVisible = false
        return true
    }

    //tratamento dos clicks dos botões do toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.add -> {
                val intent = Intent(this, NewNoteActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.sort -> {
                sortOptions()
                return true
            }
            R.id.exit -> {
                finishAndRemoveTask()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //ordenando as notas
    private fun sortOptions() {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setTitle(R.string.title_dialog_sort)
        alertDialog.setCancelable(false)

        alertDialog.setNegativeButton(R.string.cancel_dialog) { dialog , _->
            dialog.dismiss()
        }

        val creation:String = this.resources.getString(R.string.sort_id)
        val creationDesc:String = this.resources.getString(R.string.sort_id_desc)
        val alpha:String = this.resources.getString(R.string.sort_alpha)
        val alphaDesc = this.resources.getString(R.string.sort_alpha_desc)

        val array = arrayOf(creation, creationDesc, alpha, alphaDesc)

        alertDialog.setItems(array) { dialog, which ->

            val option = array[which]

            //seleciona por qual categoria irá ordenar
            try {
                notes = when (option) {
                    this.resources.getString(R.string.sort_id) -> db.noteDao().getAllNotes()
                    this.resources.getString(R.string.sort_id_desc) -> db.noteDao().getAllNotesDesc()
                    this.resources.getString(R.string.sort_alpha) -> db.noteDao().getAllNotesAlpha()
                    else -> db.noteDao().getAllNotesAlphaDesc()
                }
                dialog.dismiss()
                runAdapter()
            }catch (e:IllegalArgumentException){
                Toast.makeText(this, R.string.error_sort, Toast.LENGTH_LONG).show()
            }
        }

        alertDialog.create()
        alertDialog.show()
    }

    //mostrando uma nota
    override fun onItemClicked(note: Note) {

        val intent = Intent(this, ExtendedNoteActivity::class.java)
        intent.putExtra("noteId", note.id)
        startActivity(intent)

    }

    //deletando nota
    override fun onDeleteClicked(note: Note) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage(R.string.message_dialog_delete)
            .setCancelable(false)
            .setNegativeButton(R.string.no_dialog) { dialog,_->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.yes_dialog) { _,_ ->
                //verificando se há notificação
                //caso haja, deverá ser excluída tbm
                try {
                    if (note.broadcastCode != null) {
                        val intent = Intent("NOTIFY")
                        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val pIntent = PendingIntent.getBroadcast(this,
                            note.broadcastCode.toInt(), intent, PendingIntent.FLAG_NO_CREATE)
                        if (pIntent != null) {
                            alarm.cancel(pIntent)
                        }
                    }
                    db.noteDao().deleteNote(note)
                }catch (e: Exception){
                    Toast.makeText(this, R.string.error_delete, Toast.LENGTH_LONG).show()
                }
                //runAdapter()
            }
            .create()
            .show()
    }

}

//referências principais:
//https://devofandroid.blogspot.com/2018/03/add-itemsmenu-in-actionbartoolbar.html
//https://www.youtube.com/watch?v=SYh9-JyLic0
