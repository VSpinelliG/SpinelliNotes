package com.example.spinellinotes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spinellinotes.adapters.NoteAdapter
import com.example.spinellinotes.model.Note
import com.example.spinellinotes.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NoteAdapter.OnItemClickListener {

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        runAdapter()

        /*
        //quando não há nenhuma nota
        if (button_first_add.isVisible) {
            button_first_add.setOnClickListener {
                val intent = Intent(this, NewNoteActivity::class.java)
                startActivity(intent)
            }
        }
         */

    }

    //após criar uma nota, a main é continuada
    /*override fun onRestart() {
        runAdapter()
        super.onRestart()
    }*/

    //renderiza o recycler view (usado a cada criação, ordenação e exclusão de nota)
    //e define o toolbar criado para esta activity
    private fun runAdapter() {
        setSupportActionBar(toolbar_main)

        val adapter = NoteAdapter(this)
        recycle_view.adapter = adapter

        noteViewModel.allNotes.observe(this, Observer { notes ->
            notes?.let { adapter.setNotes(it) }
        })

        recycle_view.layoutManager = LinearLayoutManager(this)
        recycle_view.setHasFixedSize(true)

    }

    //criação do toolbar com os botões
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)
        /*
        if (recycle_view.adapter?.itemCount == 0) {
            menu.getItem(0).isVisible = false
            menu.getItem(1).isVisible = false

            label_no_note.isVisible = true
            button_first_add.isVisible = true

            recycle_view.isVisible = false
        }
        else {

            menu.getItem(0).isVisible = true
            menu.getItem(1).isVisible = true

            label_no_note.isVisible = false
            button_first_add.isVisible = false

            recycle_view.isVisible = true
        }
        */
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
                //sortOptions()
                return true
            }
            R.id.exit -> {
                finishAndRemoveTask()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //ordenando as notas
    /*
    private fun sortOptions() {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setTitle(R.string.title_dialog_sort)
        alertDialog.setCancelable(false)

        alertDialog.setNegativeButton(R.string.cancel_dialog) { dialog , _->
            dialog.dismiss()
        }

        val title:String = this.resources.getString(R.string.title)
        val resume:String = this.resources.getString(R.string.resume)
        val description:String = this.resources.getString(R.string.description)

        val array = arrayOf(title, resume, description)

        alertDialog.setItems(array) { dialog, which ->

            val option = array[which]

            //seleciona por qual categoria irá ordenar
            try {
                when (option) {
                    title -> ArrayNotes.notes.sortWith(compareBy { it.title })
                    resume -> ArrayNotes.notes.sortWith(compareBy { it.resume })
                    else -> ArrayNotes.notes.sortWith(compareBy { it.description })
                }
                dialog.dismiss()
                runAdapter()
            }catch (e:IllegalArgumentException){
                Toast.makeText(this, R.string.error_sort, Toast.LENGTH_LONG).show()
            }
        }

        alertDialog.create()
        alertDialog.show()
    }*/

    //mostrando uma nota
    override fun onItemClicked(note: Note) {

        val intent = Intent(this, ExtendedNoteActivity::class.java)
        println("aaaaaaaaaaaaaaaaaa " + note.id)
        println(noteViewModel.allNotes)
        //println(note.id?.let { noteViewModel.getNoteById(it).value })
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
                noteViewModel.allNotes.observe(this, Observer { notes ->
                    for (n : Note in notes){
                        if (n.id == note.id){
                            //verificando se há notificação
                            //caso haja, deverá ser excluída tbm
                            if (n.broadcastCode != null) {
                                val intent = Intent("NOTIFY")
                                intent.putExtra("title", n.title)

                                val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                val pIntent = PendingIntent.getBroadcast(this,
                                    n.broadcastCode.toInt(), intent, PendingIntent.FLAG_NO_CREATE)
                                if (pIntent != null) {
                                    alarm.cancel(pIntent)
                                }
                            }
                            noteViewModel.delete(note)
                        }
                    }
                })
                //runAdapter()
            }
            .create()
            .show()
    }

}

//referências principais:
//https://devofandroid.blogspot.com/2018/03/add-itemsmenu-in-actionbartoolbar.html
//https://www.youtube.com/watch?v=SYh9-JyLic0
