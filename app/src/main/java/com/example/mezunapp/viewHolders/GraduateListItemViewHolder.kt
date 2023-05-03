package com.example.mezunapp.viewHolders

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mezunapp.LoginActivity
import com.example.mezunapp.ProfileActivity
import com.example.mezunapp.R
import com.example.mezunapp.models.Graduate
import com.squareup.picasso.Picasso
import java.util.Date

class GraduateListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindItems(graduate: Graduate){
        itemView.findViewById<TextView>(R.id.textViewGraduteName).setText(graduate.name + " " + graduate.surname)
        itemView.findViewById<TextView>(R.id.textViewAddress).setText(graduate.currentJobCity + ", "  + graduate.currentJobCountry)
        itemView.findViewById<TextView>(R.id.textViewProgram).setText(graduate.programName)
        Picasso.get().load(graduate.profilePhotoLink).into(itemView.findViewById<ImageView>(R.id.imageView))

        itemView.setOnClickListener{
            val intent = Intent(itemView.context, ProfileActivity::class.java)
            intent.putExtra("uid", graduate.uid)
            itemView.context.startActivity(intent)
        }
    }
}