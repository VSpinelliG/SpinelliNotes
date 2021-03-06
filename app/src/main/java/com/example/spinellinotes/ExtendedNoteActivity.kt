package com.example.spinellinotes

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.*
import com.example.spinellinotes.db.NoteRoomDatabase
import com.example.spinellinotes.model.Note
import kotlinx.android.synthetic.main.activity_extended_note.*
import java.lang.Exception
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ExtendedNoteActivity : AppCompatActivity() {

    private lateinit var note: Note
    private lateinit var db: NoteRoomDatabase
    private var calendar: Calendar = Calendar.getInstance()
    private var decrementTime:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extended_note)

        toolbar_extended_note.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(toolbar_extended_note)
        supportActionBar?.apply { setDisplayShowHomeEnabled(true) }

        db = NoteRoomDatabase.getDatabase(this)

        note = db.noteDao().getNoteById(intent.getLongExtra("noteId", 0))

        setNote(note)

    }

    //criação do toolbar com os botões
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_new_note, menu)
        return true
    }

    //tratamento dos clicks dos botões do toolbar
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

                    var hasDate = false
                    var hasTime = false
                    var broadcastCode = note.broadcastCode

                    //verificando se foi passado data e hora
                    if (button_date_extended_note.text != this.resources.getString(R.string.date)) {
                        hasDate = true
                    }
                    if (button_time_extended_note.text != this.resources.getString(R.string.time)) {
                        hasTime = true
                    }

                    //verificando se a notificação foi desativada
                    //caso tenha sido, será cancelado o alarm
                    if (button_notify_extended_note.text == this.resources.getString(R.string.notify)
                        && broadcastCode != null){

                        cancelAlarm(broadcastCode)

                        Toast.makeText(this, R.string.notify_cancel, Toast.LENGTH_SHORT).show()

                        //cancelando a notificação
                        broadcastCode = null
                    }

                    //verificando se a notificação foi alterada
                    if (button_notify_extended_note.text != this.resources.getString(R.string.notify)
                        && broadcastCode != null) {

                        cancelAlarm(broadcastCode)

                        //atualizando id pro novo broadcast
                        broadcastCode = System.currentTimeMillis()/1000

                        note.id?.let { scheduleNotification(broadcastCode, it) }
                    }

                    //passando os valores atualizados
                    try{
                        val n = Note(
                            note.id,
                            title_extended_note.text.toString(),
                            resume_extended_note.text.toString(),
                            description_extended_note.text.toString(),
                            background,
                            calendar,
                            hasDate,
                            hasTime,
                            button_notify_extended_note.text.toString(),
                            broadcastCode,
                            note.createDate
                        )
                        db.noteDao().updateNote(n)

                    }catch (e: Exception){
                        Toast.makeText(this, R.string.error_update , Toast.LENGTH_LONG).show()
                    }finally {
                        finish()
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //preenchendo os campos de visualização com os valores da nota selecionada
    @SuppressLint("SetTextI18n")
    private fun setNote(note: Note) {

        note_create_date.text =
            "${this.resources.getString(R.string.create_data)} ${DateFormat.getDateInstance(DateFormat.FULL).format(note.createDate)}"
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

        //retirando linha dos campos edittext
        title_extended_note.background = null
        resume_extended_note.background = null
        description_extended_note.background = null

        //pegando a cor e selecionando o radiobutton referente
        card_view_extended_note.setCardBackgroundColor(note.background)
        when (note.background) {
            Color.parseColor(this.resources.getString(R.string.parse_yellow)) -> yellow_extended_note.isChecked = true
            Color.parseColor(this.resources.getString(R.string.parse_gray)) -> gray_extended_note.isChecked = true
            Color.parseColor(this.resources.getString(R.string.parse_green)) -> green_extended_note.isChecked = true
            else -> pink_extended_note.isChecked = true
        }

        setDateAndTime()

        setNoteColor()
    }

    //método para caso o usuário mude os valores de data ou hora
    private fun setDateAndTime() {

        val locale = Locale("pt", "BR")
        Locale.setDefault(locale)

        //colocando os valores de data e hora no label dos botões referentes
        calendar = note.notifyDateTime

        if (note.hasValueDate) {
            button_date_extended_note.text = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)
        }

        if (note.hasValueTime){
            button_time_extended_note.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
        }

        if (note.notify != this.resources.getString(R.string.notify)){
            button_notify_extended_note.text = note.notify
        }

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val minute = calendar.get(Calendar.MINUTE)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)


        button_date_extended_note.setOnClickListener {
            val datePickerListener =
                DatePickerDialog.OnDateSetListener { _, y, m, d ->
                    calendar.set(Calendar.YEAR, y)
                    calendar.set(Calendar.MONTH, m)
                    calendar.set(Calendar.DAY_OF_MONTH, d)

                    val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
                    button_date_extended_note.text = dateFormat.format(calendar.time)
                }
            DatePickerDialog(this, datePickerListener, year, month, day).show()
        }

        button_time_extended_note.setOnClickListener{
            val timePickerListener =
                TimePickerDialog.OnTimeSetListener{ _, h, m ->
                    calendar.set(Calendar.HOUR_OF_DAY, h)
                    calendar.set(Calendar.MINUTE, m)
                    button_time_extended_note.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
                }
            TimePickerDialog(this, timePickerListener, hour, minute, true).show()
        }

        button_notify_extended_note.setOnClickListener{
            val default0:String = this.resources.getString(R.string.notify_default_0)
            val default1:String = this.resources.getString(R.string.notify_default_1)
            val default2:String = this.resources.getString(R.string.notify_default_2)
            val array = arrayOf(default0, default1, default2)
            val alertDialog = AlertDialog.Builder(this)
            var personalize = false

            alertDialog.setTitle(R.string.notify_dialog)
                .setItems(array) { _, which ->
                    when(array[which]){
                        default0 -> button_notify_extended_note.text = this.resources.getString(R.string.notify)
                        default1 -> button_notify_extended_note.text = this.resources.getString(R.string.notify_default_1)
                        else -> personalize = true
                    }
                    //caso seja selecionado a opção personalizar, não irá direito para a função
                    //de exibir o tempo restante para notificar. ela será chamada no showDialog()
                    if (personalize){
                        showDialog()
                    }
                }
                .setNegativeButton(R.string.cancel_dialog){dialog,_ ->
                    dialog.dismiss()
                }
            alertDialog.show()
            alertDialog.create()
        }
    }

    //método para caso o usuário mude a cor da nota
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

    @SuppressLint("SetTextI18n")
    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog)
        val positiveButton: Button = dialog.findViewById(R.id.confirm_notify)
        val radioGroup: RadioGroup = dialog.findViewById(R.id.radio_group_notify)
        val time: EditText = dialog.findViewById(R.id.time_notify)

        positiveButton.setOnClickListener{
            if (time.text.toString() != "") {

                val radioButton: RadioButton = dialog.findViewById(radioGroup.checkedRadioButtonId)

                button_notify_extended_note.text = "${time.text} ${radioButton.text}"

                decrementTime =
                    if (radioButton.text == this.resources.getString(R.string.notify_minute_before)) {
                        TimeUnit.MINUTES.toMillis(time.text.toString().toLong())
                    } else {
                        TimeUnit.HOURS.toMillis(time.text.toString().toLong())
                    }

                //decrementando tempo para notificar
                //calendar.timeInMillis -= decrementTime

                //showRemainingTime()
                dialog.dismiss()
            }
            else{
                Toast.makeText(this, R.string.time_value_invalid, Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun showRemainingTime() {
        var millis = calendar.timeInMillis - System.currentTimeMillis()

        millis -= decrementTime

        if (millis > 0 && button_notify_extended_note.text != this.resources.getString(R.string.notify)){
            val notifyLabel: String

            //comparar se o tempo é menor que uma hora
            when {
                TimeUnit.MILLISECONDS.toMinutes(millis) < 60 -> {
                    notifyLabel = String.format("${this.resources.getString(R.string.notify_remaining_extended)} %02d min.",
                        TimeUnit.MILLISECONDS.toMinutes(millis))
                }
                //comparar se o tempo é menor que um dia
                TimeUnit.MILLISECONDS.toDays(millis) < 1 -> {
                    notifyLabel = String.format("${this.resources.getString(R.string.notify_remaining_extended)} %02d hora(s) e %02d min.",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)))
                }
                //comparar se o tempo é menor que um mês
                TimeUnit.MILLISECONDS.toDays(millis) < 31 -> {
                    notifyLabel = String.format("${this.resources.getString(R.string.notify_remaining_extended)} %02d dias.",
                        TimeUnit.MILLISECONDS.toDays(millis))
                }
                //comparar se o tempo é menor que um ano
                TimeUnit.MILLISECONDS.toDays(millis) < 365 -> {
                    notifyLabel = String.format("${this.resources.getString(R.string.notify_remaining_extended)} %02d mes(es).",
                        TimeUnit.MILLISECONDS.toDays(millis)/31)
                }
                //se o tempo for maior que um ano
                else -> {
                    notifyLabel = String.format("${this.resources.getString(R.string.notify_remaining_extended)} %02d ano(s).",
                        TimeUnit.MILLISECONDS.toDays(millis)/365)
                }
            }

            Toast.makeText(this, notifyLabel , Toast.LENGTH_LONG).show()

            calendar.set(Calendar.SECOND, 0)
        }
        //caso informe uma data/horário passado
        else if (millis < 0) {
            Toast.makeText(this, R.string.notify_invalid , Toast.LENGTH_SHORT).show()
            button_notify_extended_note.text = this.resources.getString(R.string.notify)
        }
    }

    private fun cancelAlarm(broadcastCode: Long) {
        val intent = Intent("NOTIFY")
        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(this,
            broadcastCode.toInt(), intent, PendingIntent.FLAG_NO_CREATE)
        if (pIntent != null) {
            alarm.cancel(pIntent)
        }
    }

    private fun scheduleNotification(broadcastCode: Long, id: Long) {

        val intent = Intent("NOTIFY")
        intent.putExtra("id", broadcastCode.toInt())
        intent.putExtra("noteId", id)
        intent.putExtra("title", title_extended_note.text.toString())
        //caso haja resumo, o título da notificação será o próprio título da nota e o corpo será o resumo
        //casonão haja, o título da notificação será o nome do app e o corpo será o título da nota
        if (resume_extended_note.text.toString() != "") {
            intent.putExtra("resume", resume_extended_note.text.toString())
        }

        val pIntent = PendingIntent.getBroadcast(this,
            broadcastCode.toInt(), intent, 0)

        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        showRemainingTime()

        alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis - decrementTime, pIntent)

    }


}