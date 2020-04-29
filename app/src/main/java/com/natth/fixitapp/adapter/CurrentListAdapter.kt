package com.natth.fixitapp.adapter

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.natth.fixitapp.R
import com.natth.fixitapp.activites.ChatActivity
import com.natth.fixitapp.activites.DetailFixActivity
import com.natth.fixitapp.model.RequestFix
import com.natth.fixitapp.model.RequestFixByuser
import kotlinx.android.synthetic.main.activity_detail_fix.view.*
import kotlinx.android.synthetic.main.current_list_item.view.*


class CurrentListAdapter(private val RequestFixByuser: List<RequestFixByuser>, val onItemClicked:(v:View)-> Unit): RecyclerView.Adapter<CurrentListAdapter.ViewHolder>() {
    val REQUEST_PHONE_CALL = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.current_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = RequestFixByuser[position]
        if (item.status == "accept") {
            holder.fixId.text = "รายการซ่อมที่ fx-" + item.idRequest.toString()
            holder.nameCus.text = "จากร้าน : " + item.nameStore

            holder.statusFix.text = "สถานะ: รับซ่อมแล้ว"

            holder.date.text = "เมื่อเวลา: " + item.lastUpdate
            holder.itemView.setOnClickListener { v ->
                val context = v.context
                val intent = Intent(context, DetailFixActivity::class.java)
                intent.putExtra("id_fix", item.idRequest.toString())
                context.startActivity(intent)

            }
            holder.btnChat.setOnClickListener { v ->
                val context = v.context
             val i  = Intent(context ,ChatActivity::class.java)
                i.putExtra("id_fix_chat",item.idRequest.toString())
                context.startActivity(i)

            }
            holder.btnCall.setOnClickListener(onItemClicked)

        }

    }

    override fun getItemCount(): Int = RequestFixByuser.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var fixId:TextView = itemView.tvListId
        var nameCus:TextView = itemView.tvName_Store
        var statusFix:TextView = itemView.tvStatus
        var date:TextView = itemView.tvDateTime
        var btnChat :Button = itemView.btnitemChat
        var btnCall :Button = itemView.btnitemCall

    }




}