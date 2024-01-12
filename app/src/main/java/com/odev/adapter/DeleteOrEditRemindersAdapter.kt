package com.odev.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.odev.hatrlatici.R
import com.odev.model.Reminder
import java.util.Calendar

class DeleteOrEditRemindersAdapter( //hatırlatma listesini temsil eder.
    private val context: Context, var reminderList : ArrayList<Reminder>) :
    RecyclerView.Adapter<DeleteOrEditRemindersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { //fonksiyonun bir bölümünün görünümünü oluşturur.
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.delete_or_edit_reminder_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { //hatırlatma bağlantıları
        val reminder = reminderList[position]
        var calendar = Calendar.getInstance()
        calendar.time = reminder.date
        var getMonth = calendar.get(Calendar.MONTH) + 1

        holder.dateTv.text = calendar.get(Calendar.DAY_OF_MONTH).toString() +  " " +
            getMonth + " " + calendar.get(Calendar.YEAR)
        holder.clockTv.text = reminder.date?.hours.toString() + ":"+ reminder.date?.minutes
        holder.noteEt.setText(reminder.note)
        holder.deleteOrListIv.setOnClickListener {
            var builder = AlertDialog.Builder(context)
            builder.setTitle("Silmek istediğinizden emin misiniz?")
                .setNegativeButton("Evet"){dialog,which ->
                    Firebase.database.getReference("Reminders").child(reminder.itemLocation).removeValue()
                        .addOnSuccessListener {
                            reminderList.remove(reminder)
                            notifyDataSetChanged()
                        }
                }
                .setPositiveButton("Hayır"){dialog,which ->}
                .show()
        }

    }
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){ //bir hatırlatmanın aktarılması içerir.
        val clockTv = view.findViewById<TextView>(R.id.clockTv)
        val dateTv = view.findViewById<TextView>(R.id.dateTv)
        var noteEt = view.findViewById<EditText>(R.id.noteEt3)
        var deleteOrListIv = view.findViewById<ImageView>(R.id.deleteOrListIv)
    }
}