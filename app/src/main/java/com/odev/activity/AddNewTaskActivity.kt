package com.odev.activity

import android.R
import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.odev.model.BroadcastReceiver
import com.odev.hatrlatici.databinding.AddNewTaskActivityBinding
import com.odev.model.Reminder
import java.util.Calendar
import java.util.Date

class AddNewTaskActivity : AppCompatActivity() {
    private lateinit var binding: AddNewTaskActivityBinding
    private lateinit var calendar: Calendar
    private lateinit var reminder : Reminder
    var getSelectedHour = Date()
    var music: String = ""
    var database = FirebaseDatabase.getInstance() // firebase realtime database ile etkileşimde bulunmak için
    var auth = FirebaseAuth.getInstance()
    var remindBeforeSelectedMin = arrayListOf<String>()
    var remindBeforeSelected = 0

    override fun onCreate(savedInstanceState: Bundle?) {// aktivitenin oluşturulma aşamasındaki işlemleri içerir
        super.onCreate(savedInstanceState)
        binding = AddNewTaskActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.listView.visibility = View.GONE

        calendar = Calendar.getInstance()
        selectDate() //Tarih seçmek için

        setTime() // Saat seçmek için

        pickMusic(music)

        binding.createTask.setOnClickListener {
            saveToData()
        }

        remindBeforeHowMuchMin() // hatırlatma öncesi süreyi belirleme

        goToMainActivity() // ana ekrana geçiş

    }

    private fun goToMainActivity() {
        binding.backIv.setOnClickListener {
            finish()// aktivite sonlandığında kullanıcı o anki aktiviteden çıkar ve önceki ekrana geri döner.
        }
    }

    private fun remindBeforeHowMuchMin() {
        binding.selectRemindTime.setOnClickListener {
            if(binding.listView.visibility == View.GONE)
            {
                binding.listView.visibility = View.VISIBLE
            }else {
                binding.listView.visibility = View.GONE
            }
        }

        remindBeforeSelectedMin.add("10 Dakika Önce")
        remindBeforeSelectedMin.add("20 Dakika Önce")
        remindBeforeSelectedMin.add("30 Dakika Önce")
        remindBeforeSelectedMin.add("40 Dakika Önce")
        val adapter = ArrayAdapter<String>(this, R.layout.simple_list_item_1, remindBeforeSelectedMin)
        binding.listView.adapter = adapter
        binding.listView.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2 == 0)
                    remindBeforeSelected = 10
                else if(p2 == 1)
                    remindBeforeSelected = 20
                else if(p2 == 2)
                    remindBeforeSelected = 30
                else if(p2 == 3)
                    remindBeforeSelected = 40
                Toast.makeText(this@AddNewTaskActivity,"$remindBeforeSelected dakika öncesi ayarlandı",Toast.LENGTH_LONG).show()
                binding.listView.visibility = View.GONE
            }
        })
    }

    private fun pickMusic(music: String) {//bildrim sesi seçme
        binding.selectNotificationSoundBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val selectedMusicUri: Uri? = data?.data
            music = selectedMusicUri.toString()
        }
    }

    private fun setTime() {
        binding.pickClockBtn.setOnClickListener {
            openShowTimePicker() //Saat seçme dialoğunu burada açacağız
        }
        binding.clockConst.setOnClickListener {
            openShowTimePicker() //Saat seçme dialoğunu açmak için aynı şekilde, fakat dışarıdaki constraint layouta tıklama durumunu da dinliyor
        }
    }

    private fun selectDate() {
        binding.pickDateBtn.setOnClickListener {
            openDatePicker() //Tarih seçme dialoğunu burada açacağız
        }
        binding.dateConst.setOnClickListener {
            openDatePicker() //Tarih seçme dialoğunu açmak için aynı şekilde, fakat dışarıdaki constraint layouta tıklama durumunu da dinliyor
        }
    }

    private fun openShowTimePicker() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)

                getSelectedHour = calendar.time

                binding.selectedClockTv.text =
                    getSelectedHour.hours.toString() + ":" + getSelectedHour.minutes

            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun openDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay)

                val selectedDate = calendar.time

                binding.selectedDateTv.text = calendar.get(Calendar.DAY_OF_MONTH)
                    .toString() + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun saveToData(){ //girildiği notu ve seçtiği tarihi firebase databaseine kaydeder
        var ref = database.getReference("Reminders")
        calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE) - remindBeforeSelected))
        var key = ref.push().key
        var getNote = binding.putTextEt.text.toString()
        var getTime : Date = calendar.time
        reminder = Reminder(auth.currentUser?.email.toString(),getNote,getTime,key.toString())
        ref.child(key.toString()).setValue(reminder)
            .addOnSuccessListener {
                addNewTaskAndCreateAlarm(music)
            }
    }

    private fun addNewTaskAndCreateAlarm(music: String) {
        val requestCode = System.currentTimeMillis().toInt()

        if (binding.checkBox.isChecked == false) {
            var i = Intent(applicationContext, BroadcastReceiver::class.java)
            i.putExtra("music", music)
            i.putExtra("time",calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar.get(Calendar.MINUTE))
            i.putExtra("note",binding.putTextEt.text.toString())

            var pi: PendingIntent = PendingIntent.getBroadcast(
                applicationContext, requestCode, i,PendingIntent.FLAG_IMMUTABLE
            )
            var am: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
            Toast.makeText(this, "Hatırlatıcı oluşturuldu.", Toast.LENGTH_SHORT).show()
        } else {
            var i = Intent(applicationContext, BroadcastReceiver::class.java)
            i.putExtra("music", music)
            i.putExtra("time",calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar.get(Calendar.MINUTE))
            i.putExtra("note",reminder.note)
            var pi: PendingIntent = PendingIntent.getBroadcast(
                applicationContext, requestCode, i, PendingIntent.FLAG_IMMUTABLE
            )
            var am: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pi)
            Toast.makeText(this, "Hatırlatıcı tekrar edecek şekilde oluşturuldu.", Toast.LENGTH_SHORT).show()
        }
    }
}