package com.example.spinellinotes

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.Toast
import com.example.spinellinotes.model.ArrayNotes
import com.example.spinellinotes.model.Note
import kotlinx.android.synthetic.main.activity_extended_note.*
import java.text.DateFormat
import java.util.*

class ExtendedNoteActivity : AppCompatActivity() {

    private lateinit var calendar:Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extended_note)

        toolbar_extended_note.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(toolbar_extended_note)
        supportActionBar?.apply { setDisplayShowHomeEnabled(true) }

        val note:Note = ArrayNotes.notes[intent.getIntExtra("noteId", 0)]

        setNote(note)

        setDateAndTime()

        setNoteColor()

    }

    private fun setNote(note: Note) {

        title_extended_note.setText(note.title)
        title_extended_note.setSelection(title_extended_note.text.length)

        if (note.resume == "") {
            resume_extended_note.hint = "(${this.resources.getString(R.string.resume)})"
        }
        else{
            resume_extended_note.setText(note.resume)
            resume_extended_note.setSelection(title_extended_note.text.length)
        }
        if (note.description == "") {
            description_extended_note.hint = "(${this.resources.getString(R.string.description)})"
        }
        else {
            description_extended_note.setText(note.description)
            description_extended_note.setSelection(description_extended_note.text.length)
        }
        if (note.notify != this.resources.getString(R.string.notify_default_0)){
            button_notify_extended_note.text = note.notify
        }

        //setando calendario
        this.calendar = note.notifyDateTime
        button_date_extended_note.text = DateFormat.getDateInstance(DateFormat.DEFAULT).format(calendar.time)
        button_time_extended_note.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)

        //retirando linha dos campos
        title_extended_note.background = null
        resume_extended_note.background = null
        description_extended_note.background = null

        card_view_extended_note.setCardBackgroundColor(note.background)

        when (note.background) {
            Color.parseColor(this.resources.getString(R.string.parse_yellow)) -> yellow_extended_note.isChecked = true
            Color.parseColor(this.resources.getString(R.string.parse_gray)) -> gray_extended_note.isChecked = true
            Color.parseColor(this.resources.getString(R.string.parse_green)) -> green_extended_note.isChecked = true
            else -> pink_extended_note.isChecked = true
        }
    }

    @SuppressLint("NewApi")
    private fun setDateAndTime() {
        val locale = Locale("pt", "BR")
        Locale.setDefault(locale)

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val minute = calendar.get(Calendar.MINUTE)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)


        button_date_extended_note.setOnClickListener {
            val datePickerListener =
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    calendar.set(year, month, day, 0, 0, 0)

                    val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
                    button_date_extended_note.text = dateFormat.format(calendar.time)
                }
            DatePickerDialog(this, datePickerListener, year, month, day).show()
        }

        button_time_extended_note.setOnClickListener{
            val timePickerListener =
                TimePickerDialog.OnTimeSetListener{ _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    button_time_extended_note.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
                }
            TimePickerDialog(this, timePickerListener, hour, minute, true).show()
        }

        button_notify_extended_note.setOnClickListener{
            val default0:String = this.resources.getString(R.string.notify_default_0)
            val default1: String = this.resources.getString(R.string.notify_default_1)
            val default2:String = this.resources.getString(R.string.notify_default_2)
            val array = arrayOf(default0, default1, default2)
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle(R.string.notify_dialog)
                .setItems(array) { _, which ->
                    when(array[which]){
                        default0 -> button_notify_extended_note.text = this.resources.getString(R.string.notify)
                        default1 -> button_notify_extended_note.text =  default1
                        else -> button_notify_extended_note.text = default2
                    }
                }
                .setNegativeButton(R.string.cancel_dialog){dialog,_ ->
                    dialog.dismiss()
                }
            alertDialog.show()
            alertDialog.create()
        }
    }

    private fun setNoteColor() {
        radio_group_extended_note.setOnCheckedChangeListener { _, checkedId ->
            try {
                val radioButton: RadioButton = findViewById(checkedId)
                when (radioButton.text) {
                    this.resources.getString(R.string.yellow) -> card_view_extended_note
                        .setCardBackgroundColor(Color.parseColor(this.resources.getString(R.string.parse_yellow)))
                    this.resources.getString(R.string.gray) -> card_view_extended_note
                        .setCardBackgroundColor(Color.parseColor(this.resources.getString(R.string.parse_gray)))
                    this.resources.getString(R.string.green) -> card_view_extended_note
                        .setCardBackgroundColor(Color.parseColor(this.resources.getString(R.string.parse_green)))
                    else -> card_view_extended_note
                        .setCardBackgroundColor(Color.parseColor(this.resources.getString(R.string.parse_pink)))
                }
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, R.string.error_select_color, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_new_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.save -> {
                if (title_extended_note.text.toString() == "") {
                    Toast.makeText(this, R.string.required_title, Toast.LENGTH_SHORT).show()
                } else {
                    val radioButton = findViewById<RadioButton>(radio_group_extended_note.checkedRadioButtonId)
                    val background: Int
                    background = when (radioButton.text) {
                        this.resources.getString(R.string.yellow) -> Color.parseColor(this.resources.getString(R.string.parse_yellow))
                        this.resources.getString(R.string.gray) -> Color.parseColor(this.resources.getString(R.string.parse_gray))
                        this.resources.getString(R.string.green) -> Color.parseColor(this.resources.getString(R.string.parse_green))
                        else -> Color.parseColor(this.resources.getString(R.string.parse_pink))
                    }
                    val id = intent.getIntExtra("noteId", 0)
                    ArrayNotes.notes[id] = Note(
                        id,
                        title_extended_note.text.toString(),
                        resume_extended_note.text.toString(),
                        description_extended_note.text.toString(),
                        background,
                        calendar,
                        button_notify_extended_note.text.toString(),
                        ArrayNotes.notes[id].createDate
                    )
                    finish()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}