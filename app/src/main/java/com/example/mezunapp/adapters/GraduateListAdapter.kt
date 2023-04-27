package com.example.mezunapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mezunapp.R
import com.example.mezunapp.models.Announcement
import com.example.mezunapp.models.Graduate
import com.example.mezunapp.viewHolders.GraduateListItemViewHolder

class GraduateListAdapter(val list: MutableList<Graduate>): RecyclerView.Adapter<GraduateListItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraduateListItemViewHolder {
        return GraduateListItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.graduate_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: GraduateListItemViewHolder, position: Int) {
        holder.bindItems(list[position])
    }

}