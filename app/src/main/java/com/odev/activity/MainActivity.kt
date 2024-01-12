package com.odev.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.odev.adapter.DeleteOrEditRemindersAdapter
import com.odev.adapter.ListRemindersAdapter
import com.odev.hatrlatici.R
import com.odev.hatrlatici.databinding.ActivityMainBinding
import com.odev.model.Reminder

class MainActivity : AppCompatActivity() {//uygulamanın ana ekranı için temel ayarları ve işlemleri
    private lateinit var binding : ActivityMainBinding
    private var auth = FirebaseAuth.getInstance()
    private var database = FirebaseDatabase.getInstance()
    private lateinit var listRemindersAdapter : ListRemindersAdapter
    private lateinit var deleteOrEditRemindersAdapter: DeleteOrEditRemindersAdapter
    var remindersInArray = ArrayList<Reminder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.editOrDeleteReminderRv.visibility = View.GONE
        setContentView(binding.root)

        goToAddNewTaskActivity()
        getData()
        editOrDeleteRemider()
        logOut()
    }
    private fun logOut() {
        binding.logOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(this@MainActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun editOrDeleteRemider() {//kullanıcının hatırlatmalrı düzenlemek veya silmek için bir düzenleme ve silme ektranı açma
        binding.optionsBtn.setOnClickListener {
            if(binding.editOrDeleteReminderRv.visibility == View.GONE){
                binding.listReminderRv.visibility = View.GONE
                binding.editOrDeleteReminderRv.visibility = View.VISIBLE
                listRemindersAdapter.notifyDataSetChanged()
                binding.optionsBtn.setImageResource(R.drawable.baseline_check_24)
            }else{
                binding.listReminderRv.visibility = View.VISIBLE
                binding.editOrDeleteReminderRv.visibility = View.GONE
                deleteOrEditRemindersAdapter.notifyDataSetChanged()
                binding.optionsBtn.setImageResource(R.drawable.threedot)
            }
        }
    }

    private fun getData() {//firebase databaseden hatırlatmalaarı çekmek kullanıcının e posta ileeşleşen hatırlatmaları bir listeye yerleştirir
        remindersInArray.clear()
        database.getReference("Reminders").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(snap in snapshot.children){
                        if(snap.child("mail").getValue().toString().equals(auth.currentUser?.email.toString())){
                            remindersInArray.add(snap.getValue(Reminder::class.java)!!)
                        }
                    }
                }
                setRecycler()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setRecycler() {//hatırlatmalrı içeren liste oluşturur
        listRemindersAdapter = ListRemindersAdapter(this@MainActivity,remindersInArray)
        binding.listReminderRv.layoutManager = LinearLayoutManager(this@MainActivity,RecyclerView.VERTICAL,false)
        binding.listReminderRv.adapter = listRemindersAdapter

        deleteOrEditRemindersAdapter = DeleteOrEditRemindersAdapter(this@MainActivity,remindersInArray)
        binding.editOrDeleteReminderRv.layoutManager = LinearLayoutManager(this@MainActivity,RecyclerView.VERTICAL,false)
        binding.editOrDeleteReminderRv.adapter = deleteOrEditRemindersAdapter
    }

    private fun goToAddNewTaskActivity() {
        binding.addNewTaskBtn.setOnClickListener {
            val intent = Intent(this@MainActivity,AddNewTaskActivity::class.java)
            startActivity(intent) // Yeni görev eklemek için diğer aktiviteye geçiş yapıyoruz
        }
    }

    override fun onRestart() { //bir aktivite yeniden başlatıldığında çağırılan metot
        getData()
        super.onRestart()
    }
}