package com.odev.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.odev.hatrlatici.R
import com.odev.model.Reminder
import java.util.Calendar

class ListRemindersAdapter( //hatırlatmanın aktarılmasını ve listelenmesini sağlar.
    private val context: Context, var reminderList : ArrayList<Reminder>) :
    RecyclerView.Adapter<ListRemindersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.list_reminder_item,parent,false)
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
        holder.dateTv.text = calendar.get(Calendar.DAY_OF_MONTH).toString() +  " " + getMonth + " " + calendar.get(Calendar.YEAR)
        holder.clockTv.text = reminder.date?.hours.toString() + ":"+ reminder.date?.minutes
        holder.noteTv.setText(reminder.note)
    }
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){ //görünümlere erişilmesini sağlar
        val clockTv = view.findViewById<TextView>(R.id.clockTv)
        val dateTv = view.findViewById<TextView>(R.id.dateTv)
        var noteTv = view.findViewById<TextView>(R.id.noteTv3)
    }
}