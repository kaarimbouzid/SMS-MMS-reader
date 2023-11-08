package com.sync.smsmmsreader.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sync.smsmmsreader.R
import com.sync.smsmmsreader.model.MmsMessage

class MmsAdapter(private val messages: List<MmsMessage>) : RecyclerView.Adapter<MmsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageIdTextView: TextView = itemView.findViewById(R.id.messageIdTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mms, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.messageIdTextView.text = "Message ID: ${message.messageId}"
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}