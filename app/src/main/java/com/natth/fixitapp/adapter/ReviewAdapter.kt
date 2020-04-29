package com.natth.fixitapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.natth.fixitapp.R
import com.natth.fixitapp.model.ReviewsByTechnician
import kotlinx.android.synthetic.main.list_item_review.view.*

class ReviewAdapter(private val reviewsByTechnician:List<ReviewsByTechnician>) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_review,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reviewsByTechnician.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val rwt = reviewsByTechnician[position]
        holder.tvnameUser.text = rwt.firstname+ " " + rwt.lastname
        holder.tvTextreview.text = rwt.textrevew
        holder.tvDateTime.text = rwt.lastUpdate
        holder.tvReting.text = rwt.rating.toString()
        holder.tvStar.rating = rwt.rating.toFloat()

    }

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        // map view change adapter
        val tvnameUser: TextView = itemView.tvNameItem
        val tvReting: TextView =itemView.tvRetingItem
        val tvTextreview :TextView = itemView.tvTextreviewItem
        val tvDateTime : TextView = itemView.tvDatetimeItem
        val tvStar :RatingBar = itemView.ratingBar2
    }
}