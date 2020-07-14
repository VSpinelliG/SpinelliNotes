package com.example.spinellinotes

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.spinellinotes.model.Note
import com.example.spinellinotes.viewmodel.NoteViewModel
import kotlinx.android.synthetic.main.activity_new_note.*
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NewNoteActivity : AppCompatActivity() {

    private var calendar: Calendar = Calendar.getInstance()
    private lateinit var noteViewModel: NoteViewModel
    private var decrementTime:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        toolbar_new_note.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(toolbar_new_note)
        supportActionBar?.apply { setDisplayShowHomeEnabled(true) }

        setDateAndTime()

        setNoteColor()

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
                if (title_new_note.text.toString() == "") {
                    Toast.makeText(this, R.string.required_title, Toast.LENGTH_SHORT).show()
                }
                else if (button_notify_new_note.text != this.resources.getString(R.string.notify)
                    && button_date_new_note.text == this.resources.getString(R.string.date)
                    && button_time_new_note.text == this.resources.getString(R.string.time)) {

                    Toast.makeText(this, R.string.required_date_time, Toast.LENGTH_SHORT).show()
                }
                else {

                    //pegando a cor selecionada pelo usuário ao confirmar a criação
                    val radioButton = findViewById<RadioButton>(radio_group_new_note.checkedRadioButtonId)
                    val background:Int
                    background = when (radioButton.text) {
                        this.resources.getString(R.string.yellow) -> Color.parseColor(this.resources.getString(R.string.parse_yellow))
                        this.resources.getString(R.string.gray) -> Color.parseColor(this.resources.getString(R.string.parse_gray))
                        this.resources.getString(R.string.green) -> Color.parseColor(this.resources.getString(R.string.parse_green))
                        else -> Color.parseColor(this.resources.getString(R.string.parse_pink))
                    }

                    //adicionando nota
                    noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

                    var hasDate = false
                    var hasTime = false
                    var broadcastCode: Long? = null

                    //verificando se foi passado data e hora
                    if (button_date_new_note.text != this.resources.getString(R.string.date)) {
                        hasDate = true
                    }
                    if (button_time_new_note.text != this.resources.getString(R.string.time)) {
                        hasTime = true
                    }

                    //criando a notificação
                    if (button_notify_new_note.text != this.resources.getString(R.string.notify)) {
                        //criando um "id" para cada broadcast ser único e possibilitar
                        //multiplas notificações
                        broadcastCode = System.currentTimeMillis()

                        scheduleNotification(broadcastCode)
                    }

                    noteViewModel.insert(
                        Note(
                            null,
                            title_new_note.text.toString(),
                            resume_new_note.text.toString(),
                            description_new_note.text.toString(),
                            background,
                            calendar,
                            hasDate,
                            hasTime,
                            button_notify_new_note.text.toString(),
                            broadcastCode,
                            Date()
                        )
                    )
                    finish()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
                Toast.makeText(this, R.string.error_select_color, Toast.LENGTH_SHORT).show()
            }
        }
    }

    //pegando os valores de data e hora
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
                DatePickerDialog.OnDateSetListener { _, y, m, d ->
                    calendar.set(Calendar.YEAR, y)
                    calendar.set(Calendar.MONTH, m)
                    calendar.set(Calendar.DAY_OF_MONTH, d)

                    val dateFormat = DateFormat.getDateInstance(DateFormat.FULL)
                    button_date_new_note.text = dateFormat.format(calendar.time)
                }
            DatePickerDialog(this, datePickerListener, year, month, day).show()
        }

        button_time_new_note.setOnClickListener{
            val timePickerListener =
                TimePickerDialog.OnTimeSetListener{_, h, m ->
                    calendar.set(Calendar.HOUR_OF_DAY, h)
                    calendar.set(Calendar.MINUTE, m)
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
            var personalize = false

            alertDialog.setTitle(R.string.notify_dialog)
                .setItems(array) { _, which ->
                    when(array[which]){
                        default0 -> button_notify_new_note.text = this.resources.getString(R.string.notify)
                        default1 -> button_notify_new_note.text = this.resources.getString(R.string.notify_default_1)
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

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog)
        val positiveButton:Button = dialog.findViewById(R.id.confirm_notify)
        val radioGroup:RadioGroup = dialog.findViewById(R.id.radio_group_notify)
        val time:EditText = dialog.findViewById(R.id.time_notify)

        positiveButton.setOnClickListener{
            if (time.text.toString() != "") {

                val radioButton: RadioButton = dialog.findViewById(radioGroup.checkedRadioButtonId)

                button_notify_new_note.text = "${time.text} ${radioButton.text}"

                decrementTime =
                    if (radioButton.text == this.resources.getString(R.string.notify_minute_before)) {
                        TimeUnit.MINUTES.toMillis(time.text.toString().toLong())
                    } else {
                        TimeUnit.HOURS.toMillis(time.text.toString().toLong())
                    }

                //decrementando tempo para notificar
                //calendar.timeInMillis -= decrementTime

                //Toast.makeText(this, button_notify_new_note.text, Toast.LENGTH_SHORT).show()
                //showRemainingTime()
                dialog.dismiss()
            }
            else{
                Toast.makeText(this, R.string.time_value_invalid, Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()

    }

    private fun showRemainingTime(){
        var millis = calendar.timeInMillis - System.currentTimeMillis()

        millis -= decrementTime

        if (millis > 0 && button_notify_new_note.text != this.resources.getString(R.string.notify)){
            val notifyLabel: String

            //comparar se o tempo é menor que uma hora
            when {
                TimeUnit.MILLISECONDS.toMinutes(millis) < 60 -> {
                    notifyLabel = String.format("${this.resources.getString(R.string.notify_remaining_new)} %02d min.",
                        TimeUnit.MILLISECONDS.toMinutes(millis))
                }
                //comparar se o tempo é menor que um dia
                TimeUnit.MILLISECONDS.toDays(millis) < 1 -> {
                    notifyLabel = String.format("${this.resources.getString(R.string.notify_remaining_new)} %02d hora(s) e %02d min.",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)))
                }
                //comparar se o tempo é menor que um mês
                TimeUnit.MILLISECONDS.toDays(millis) < 31 -> {
                    notifyLabel = String.format("${this.resources.getString(R.string.notify_remaining_new)} %02d dias.",
                        TimeUnit.MILLISECONDS.toDays(millis))
                }
                //comparar se o tempo é menor que um ano
                TimeUnit.MILLISECONDS.toDays(millis) < 365 -> {
                    notifyLabel = String.format("${this.resources.getString(R.string.notify_remaining_new)} %02d mes(es).",
                        TimeUnit.MILLISECONDS.toDays(millis)/31)
                }
                //se o tempo for maior que um ano
                else -> {
                    notifyLabel = String.format("${this.resources.getString(R.string.notify_remaining_new)} %02d ano(s).",
                        TimeUnit.MILLISECONDS.toDays(millis)/365)
                }
            }

            Toast.makeText(this, notifyLabel , Toast.LENGTH_LONG).show()

            calendar.set(Calendar.SECOND, 0)
        }
        //caso informe uma data/horário passado
        else if (millis < 0) {
            Toast.makeText(this, R.string.notify_invalid , Toast.LENGTH_SHORT).show()
            button_notify_new_note.text = this.resources.getString(R.string.notify)
        }
    }

    private fun scheduleNotification(broadcastCode: Long?) {

        if (broadcastCode != null) {
            val intent = Intent("NOTIFY")
            intent.putExtra("title", title_new_note.text.toString())

            val pIntent = PendingIntent.getBroadcast(this,
                broadcastCode.toInt(), intent, 0)

            val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            showRemainingTime()

            alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis - decrementTime, pIntent)

        }
    }

}

