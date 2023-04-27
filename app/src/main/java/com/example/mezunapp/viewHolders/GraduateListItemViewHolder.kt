package com.example.mezunapp.viewHolders

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mezunapp.R
import com.example.mezunapp.models.Graduate
import java.util.Date

class GraduateListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindItems(graduate: Graduate){
        itemView.findViewById<TextView>(R.id.textViewGraduteName).setText(graduate.name + " " + graduate.surname)
        itemView.findViewById<TextView>(R.id.textViewAddress).setText(graduate.currentJobCity + ", "  + graduate.currentJobCountry)
        itemView.findViewById<TextView>(R.id.textViewProgram).setText(graduate.programName)

    }
}