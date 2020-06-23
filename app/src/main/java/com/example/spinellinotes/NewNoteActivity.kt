package com.example.spinellinotes

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spinellinotes.model.ArrayNotes
import com.example.spinellinotes.model.Note
import kotlinx.android.synthetic.main.activity_new_note.*
import java.text.DateFormat
import java.util.*

class NewNoteActivity : AppCompatActivity() {

    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        toolbar_new_note.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(toolbar_new_note)
        supportActionBar?.apply { setDisplayShowHomeEnabled(true) }

        setDateAndTime()

        setNoteColor()

    }

    //pega a cor da nota selecionada na criação em tempo real
    private fun setNoteColor() {
        radio_group_new_note.setOnCheckedChangeListener { _, checkedId ->
            try {
                val radioButton: RadioButton = findViewById(checkedId)
                when (radioButton.text) {
                    this.resources.getString(R.string.yellow) -> card_view_new_note
                        .setCardBackgroundColor(Color.parseColor(this.resources.getString(R.string.parse_yellow)))
                    this.resources.getString(R.string.gray) -> card_view_new_note
                        .setCardBackgroundColor(Color.parseColor(this.resources.getString(R.string.parse_gray)))
                    this.resources.getString(R.string.green) -> card_view_new_note
                        .setCardBackgroundColor(Color.parseColor(this.resources.getString(R.string.parse_green)))
                    else -> card_view_new_note
                        .setCardBackgroundColor(Color.parseColor(this.resources.getString(R.string.parse_pink)))
                }
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, R.string.error_select_color, Toast.LENGTH_LONG).show()
            }
        }
    }

    //pegando os valores de data e hora
    @SuppressLint("NewApi")
    private fun setDateAndTime() {
        val locale = Locale("pt", "BR")
        Locale.setDefault(locale)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val minute = calendar.get(Calendar.MINUTE)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        button_date_new_note.setOnClickListener {
            val datePickerListener =
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    calendar.set(year, month, day, hour, minute, 0)

                    val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
                    button_date_new_note.text = dateFormat.format(calendar.time)
                }
            DatePickerDialog(this, datePickerListener, year, month, day).show()
        }

        button_time_new_note.setOnClickListener{
            val timePickerListener =
                TimePickerDialog.OnTimeSetListener{_, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    button_time_new_note.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
                }
            TimePickerDialog(this, timePickerListener, hour, minute, true).show()
        }

        button_notify_new_note.setOnClickListener{
            val default0:String = this.resources.getString(R.string.notify_default_0)
            val default1:String = this.resources.getString(R.string.notify_default_1)
            val default2:String = this.resources.getString(R.string.notify_default_2)
            val array = arrayOf(default0, default1, default2)
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle(R.string.notify_dialog)
                .setItems(array) { _, which ->
                    when(array[which]){
                        default0 -> button_notify_new_note.text = this.resources.getString(R.string.notify)
                        default1 -> button_notify_new_note.text =  default1
                        else -> button_notify_new_note.text = default2
                    }
                }
                .setNegativeButton(R.string.cancel_dialog){dialog,_ ->
                    dialog.dismiss()
                }
            alertDialog.show()
            alertDialog.create()
        }
    }

    //criação do toolbar com os botões
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_new_note, menu)
        return true
    }

    //tratamento dos clicks dos botões do toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.save -> {
                if (title_new_note.text.toString() == "") Toast.makeText(this, R.string.required_title, Toast.LENGTH_SHORT).show() else {
                    var id = 0
                    if (ArrayNotes.notes.size != 0) {
                        id = ArrayNotes.notes.size
                    }

                    //pegando a cor selecionada pelo usuário ao confirmar a criação
                    val radioButton = findViewById<RadioButton>(radio_group_new_note.checkedRadioButtonId)
                    val background:Int
                    background = when (radioButton.text) {
                        this.resources.getString(R.string.yellow) -> Color.parseColor(this.resources.getString(R.string.parse_yellow))
                        this.resources.getString(R.string.gray) -> Color.parseColor(this.resources.getString(R.string.parse_gray))
                        this.resources.getString(R.string.green) -> Color.parseColor(this.resources.getString(R.string.parse_green))
                        else -> Color.parseColor(this.resources.getString(R.string.parse_pink))
                    }

                    //adicionando nota ao array
                    ArrayNotes.notes.add(
                        Note(
                            id, title_new_note.text.toString(), resume_new_note.text.toString(),
                            description_new_note.text.toString(), background, calendar,
                            button_notify_new_note.text.toString(), Date()
                        )
                    )

                    finish()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

