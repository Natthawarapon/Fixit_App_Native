package com.natth.fixitapp.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.natth.fixitapp.R
import com.natth.fixitapp.activites.DetailFixActivity
import com.natth.fixitapp.activites.RatedActivity
import com.natth.fixitapp.activites.ToRateActivity

import com.natth.fixitapp.model.RequestFixByuser
import com.natth.fixitapp.model.ReviewByIdFix
import com.natth.fixitapp.service.ApiService

import kotlinx.android.synthetic.main.previous_list_item.view.*
import kotlinx.android.synthetic.main.previous_reject_list_item.view.*
import kotlinx.android.synthetic.main.previous_review_list_item.view.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PreviousListAdapter(private val Plist: List<RequestFixByuser>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var VIEW_HOLDER_DONE: Int = 0
    var VIEW_HOLDER_REJECT: Int = 1
    var VIEW_HOLDER_REVIEW:Int = 2

    override fun getItemViewType(position: Int): Int {
        return if (Plist!!.get(position).status.equals("done")) {
            VIEW_HOLDER_DONE
        }else if (Plist!!.get(position).status.equals("reject")){
            VIEW_HOLDER_REJECT
        }else if (Plist!!.get(position).status.equals("cancel")){
            VIEW_HOLDER_REJECT
        }else if (Plist!!.get(position).status.equals("overtime")){
            VIEW_HOLDER_REJECT
        }else if (Plist!!.get(position).status.equals("dismissed")){
            VIEW_HOLDER_REJECT
        }
        else {
            VIEW_HOLDER_REVIEW
        }

        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  {

        val viewHolder: RecyclerView.ViewHolder = when (viewType) {

             VIEW_HOLDER_DONE -> return ViewHolder_Done(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.previous_list_item,
                    parent,
                    false
                )
            )
            VIEW_HOLDER_REJECT -> return  ViewHolder_Reject(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.previous_reject_list_item,
                    parent,
                    false
                )
            )
            else  -> return ViewHolder_Review(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.previous_review_list_item,
                    parent,
                    false
                )
            )
        }

        return return viewHolder

    }


    override fun onBindViewHolder(v: RecyclerView.ViewHolder, position: Int) {
        val item = Plist[position]

        if (v is ViewHolder_Done) {
            val viewholderDone = v
            val status = "ซ่อมเสร็จเรียบร้อย"
           var c=  v.context
            viewholderDone.fixId!!.setText(String.format("%s","รายการซ่อม :fx-"+Plist!!.get(position).idRequest))
            viewholderDone.nameStoreFix!!.setText(String.format("%s","ร้านที่เรียกซ่อม :"+Plist!!.get(position).nameStore))
            viewholderDone.statusFix!!.setText(String.format("%s",status))
            viewholderDone.statusFix.setTextColor(Color.parseColor("#008000"))
            viewholderDone.date!!.setText(String.format("%s","เมื่อเวลา: " +Plist!!.get(position).lastUpdate))
            viewholderDone.tvscore!!.setText(String.format("%s" ,"ยังไม่ให้คะแนน"))
            viewholderDone.btnScore.setOnClickListener {
                var idFixDone  = (Plist!!.get(position).idRequest ).toString()
                val intent = Intent(c, ToRateActivity::class.java)
                intent.putExtra("namestore" , Plist!!.get(position).nameStore)
                intent.putExtra("idFixDone" ,idFixDone )
                intent.putExtra("dateFix" ,Plist!!.get(position).lastUpdate )
                c.startActivity(intent)
            }
//            viewholderDone.btnScore.setBackgroundColor(Color.parseColor("#FFC13A"))

            viewholderDone.itemView.tag =viewholderDone
        }else if (v is ViewHolder_Reject){
            val viewholderReject = v
            var status:String? = null
            if (Plist!!.get(position).status == "reject"){
                status = "ปธิเสธการเรียกซ่อม"
            }else if (Plist!!.get(position).status == "cancel"){
                status ="คุณยกเลิกการทำรายการ"
            }else if(Plist!!.get(position).status == "overtime"){
                status ="หมดเวลาการทำรายการ"
            }else{
                status = "ร้านยกเลิกการทำรายการ"
            }
            viewholderReject.fixId!!.setText(String.format("%s","รายการซ่อม :fx-"+Plist!!.get(position).idRequest))
            viewholderReject.nameStoreFix!!.setText(String.format("%s","ร้านที่เรียกซ่อม :"+Plist!!.get(position).nameStore))
            viewholderReject.statusFix!!.setText(String.format("%s",status))
            viewholderReject.statusFix.setTextColor(Color.parseColor("#FF0000"))
            viewholderReject.date!!.setText(String.format("%s","เมื่อเวลา: " +Plist!!.get(position).lastUpdate))
            viewholderReject.itemView.tag =viewholderReject

        }else if (v is ViewHolder_Review){
            val viewholderReview = v
            val status = "ซ่อมเสร็จเรียบร้อย"
            var c=  v.context
            viewholderReview.fixId!!.setText(String.format("%s","รายการซ่อม :fx-"+Plist!!.get(position).idRequest))
            viewholderReview.nameStoreFix!!.setText(String.format("%s","ร้านที่เรียกซ่อม :"+Plist!!.get(position).nameStore))
            viewholderReview.statusFix!!.setText(String.format("%s",status))
            viewholderReview.statusFix.setTextColor(Color.parseColor("#008000"))
            viewholderReview.date!!.setText(String.format("%s","เมื่อเวลา: " +Plist!!.get(position).lastUpdate))
            viewholderReview.tvscore!!.setText(String.format("%s" ,"ให้คะแนนแล้ว"))
            var idFixReview = (Plist!!.get(position).idRequest).toString()
            viewholderReview.btnScore.setOnClickListener {
                val intent = Intent(c, RatedActivity::class.java)
                intent.putExtra("namestore" , Plist!!.get(position).nameStore)
                intent.putExtra("idFixReview" ,idFixReview)
                intent.putExtra("dateFix" ,Plist!!.get(position).lastUpdate )
                c.startActivity(intent)
            }

            viewholderReview.itemView.tag =viewholderReview
        }


    }

    override fun getItemCount(): Int = Plist.size



inner class ViewHolder_Done(itemView: View) : RecyclerView.ViewHolder(itemView) {
val context = itemView.context
    var fixId: TextView = itemView.tvListIdPD
    var nameStoreFix: TextView = itemView.tvnameStoreFixPD
    var statusFix: TextView = itemView.tvStatusPD
    var date: TextView = itemView.tvDateTimePD
    var tvscore :TextView = itemView.tvScorePD
    var btnScore :Button = itemView.btnScorePD

}
inner class ViewHolder_Reject(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var fixId: TextView = itemView.tvListIdPR
    var nameStoreFix: TextView = itemView.tvnameStoreFixPR
    var statusFix: TextView = itemView.tvStatusPR
    var date: TextView = itemView.tvDateTimePR

}
inner class ViewHolder_Review(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val context = itemView.context
    var fixId: TextView = itemView.tvListIdPV
    var nameStoreFix: TextView = itemView.tvnameStoreFixPV
    var statusFix: TextView = itemView.tvStatusPV
    var date: TextView = itemView.tvDateTimePV
    var tvscore :TextView = itemView.tvScorePV
    var btnScore :Button = itemView.btnScorePV

}

}