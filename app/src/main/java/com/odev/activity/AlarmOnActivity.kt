package com.odev.activity

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.odev.hatrlatici.R
import com.odev.hatrlatici.databinding.ActivityAlarmOnBinding
import com.odev.hatrlatici.databinding.AddNewTaskActivityBinding
import com.odev.model.Reminder
import com.odev.model.ShakeDetector
import java.util.Calendar

class AlarmOnActivity : AppCompatActivity() {// bir alarm tetiklendiğinde veya başladığında verileri güncelleyen işlem
    private lateinit var binding : ActivityAlarmOnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmOnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateIntentData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        updateIntentData()
    }

    private fun updateIntentData() {
        var note = intent.getStringExtra("note2")
        var time = intent.getStringExtra("time2")
        binding.showTimeTv.text = time
        binding.noteTv.text = note.toString()
    }

}