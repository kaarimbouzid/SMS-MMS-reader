package com.sync.smsmmsreader.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sync.smsmmsreader.R
import com.sync.smsmmsreader.model.MyItem

class MyAdapter(private val itemList: MutableList<MyItem>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderPhoneNumTxtV: TextView = itemView.findViewById(R.id.senderPhoneNum)
        val dataTypeTxtV: TextView = itemView.findViewById(R.id.dataType)
        val dataTxtV: TextView = itemView.findViewById(R.id.data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.senderPhoneNumTxtV.text = currentItem.senderPhoneNum
        holder.dataTypeTxtV.text = currentItem.dataType
        holder.dataTxtV.text = currentItem.data
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun addItem(item: MyItem) {
        itemList.add(item)
        notifyItemInserted(itemList.size - 1)
    }
}
