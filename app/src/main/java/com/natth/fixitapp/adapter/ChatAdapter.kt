package com.natth.fixitapp.adapter

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.natth.fixitapp.R
import com.natth.fixitapp.R.layout.*
import com.natth.fixitapp.model.Chat
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.me_image_list_tiem.view.*
import kotlinx.android.synthetic.main.me_message_list_item.view.*
import kotlinx.android.synthetic.main.you_image_list_item.view.*
import java.io.InputStream
import java.net.URL


class ChatAdapter(private val chat: List<Chat>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var VIEW_HOLDER_ME: Int = 0
    var VIEW_HOLDER_YOU: Int = 1
    var VIEW_HOLDER_IMAGE_ME :Int = 2
    var VIEW_HOLDER_IMAGE_YOU :Int = 3
     lateinit var  context:Context;


    override fun getItemViewType(position: Int): Int {
        return if (chat!!.get(position).who.equals("user")and chat!!.get(position).type.equals("Message")) {
            VIEW_HOLDER_ME
        } else  if (chat!!.get(position).who.equals("user")and chat!!.get(position).type.equals("Image")){
             VIEW_HOLDER_IMAGE_ME
        }else if (chat!!.get(position).who.equals("tech")and chat!!.get(position).type.equals("Message")){
            VIEW_HOLDER_YOU
        }else{
            VIEW_HOLDER_IMAGE_YOU
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  {

        val viewHolder: RecyclerView.ViewHolder = when (viewType) {
            VIEW_HOLDER_ME -> return ViewHolderMe(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.me_message_list_item,
                    parent,
                    false
                )
            )
            VIEW_HOLDER_IMAGE_ME -> return ViewHolderMeImage(
                LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.me_image_list_tiem,
                    parent,
                    false
                )
            )

            VIEW_HOLDER_YOU -> return ViewHolderYou(
                LayoutInflater.from(parent.getContext()).inflate(
                    you_message_list_item,
                    parent,
                    false
                )
            )
            else  -> return ViewHolderYouImage(
                LayoutInflater.from(parent.getContext()).inflate(
                    you_image_list_item,
                    parent,
                    false
                )
            )
        }

        return return viewHolder

    }

    override fun onBindViewHolder(v: RecyclerView.ViewHolder, position: Int) {
        val item = chat[position]
        if (v is ViewHolderMe) { // Handle Image Layout
            val viewHolderMe = v as ViewHolderMe
            viewHolderMe.text_messages!!.setText(String.format("%s",chat!!.get(position).msg))
            viewHolderMe.text_time!!.setText(String.format("%s",chat!!.get(position).dateTime))
            viewHolderMe.itemView.tag = viewHolderMe
        } else if (v is ViewHolderYou) { // Handle Video Layout
            val viewHolderYou = v as ViewHolderYou
            viewHolderYou.text_messages!!.setText(String.format("%s",chat!!.get(position).msg))
            viewHolderYou.text_time!!.setText(String.format("%s",chat!!.get(position).dateTime))
            viewHolderYou.itemView.tag = viewHolderYou
        } else if (v is ViewHolderMeImage){
            val ViewHolderMeImage = v

            Picasso.get()
                .load(chat!!.get(position).msg)
                .resize(50, 50)
                .centerCrop()
                .into(ViewHolderMeImage.image_messages)
            ViewHolderMeImage.text_time!!.setText(String.format("%s",chat!!.get(position).dateTime))
            ViewHolderMeImage.itemView.tag = ViewHolderMeImage
        }else if (v is ViewHolderYouImage){

            val ViewHolderYouImage = v

            Picasso.get()
                .load(chat!!.get(position).msg)
                .resize(50, 50)
                .centerCrop()
                .into(ViewHolderYouImage.image_messages)
            ViewHolderYouImage.text_time!!.setText(String.format("%s",chat!!.get(position).dateTime))
            ViewHolderYouImage.itemView.tag = ViewHolderYouImage

        }

//        v.text_messages.text = item.text_msg.toString()
//        v.text_time.text = item.dateTime.toString()
//        if (item.who == "user") {
//            holder.message_root.apply {
//                setBackgroundColor(R.drawable.rect_round_primary_color)
//                val lParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.START)
//                this.layoutParams = lParams
//            }
//
//        }else if (item.who == "tech"){
//            holder.message_root.apply {
//                setBackgroundColor(R.drawable.rect_round_white)
//                val lParams = FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END)
//                this.layoutParams = lParams
//            }
//        }
    }

    override fun getItemCount(): Int = chat.size

    inner class ViewHolderMe(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        val context = itemView!!.context
        var text_messages: TextView = itemView!!.textView_message_text
        var text_time: TextView = itemView!!.textView_message_time


    }

    inner class ViewHolderMeImage(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val context = itemView!!.context
        var image_messages: ImageView = itemView!!.imageView_message_image_me
        var text_time: TextView = itemView!!.textView_message_time_me

    }

    inner class ViewHolderYou(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        val context = itemView!!.context
        var text_messages= itemView?.findViewById<TextView>(R.id.textView_message_text_you)
        var text_time= itemView?.findViewById<TextView>(R.id.textView_message_time_you)

    }

    inner class ViewHolderYouImage(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val context = itemView!!.context
        var image_messages: ImageView = itemView!!.imageView_message_image_you
        var text_time= itemView?.findViewById<TextView>(R.id.textView_message_time_you)

    }
}