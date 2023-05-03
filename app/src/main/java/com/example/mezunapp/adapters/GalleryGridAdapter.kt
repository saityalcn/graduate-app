package com.example.mezunapp.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.mezunapp.ProfileActivity
import com.example.mezunapp.R
import com.example.mezunapp.VideoActivity
import com.example.mezunapp.models.Announcement
import com.squareup.picasso.Picasso

class GalleryGridAdapter: BaseAdapter {
        val context: Context?
        val list: MutableList<String>

        constructor(context: Context, list: MutableList<String>){
            this.context = context
            this.list = list
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(i: Int): String {
            return list.get(i)
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
            var currentView: View? = view
            if (currentView == null) {
                currentView = LayoutInflater.from(context).inflate(R.layout.gallery_list_item, viewGroup, false)
            }

            val currentMediaUri: Uri = Uri.parse(getItem(i)) as Uri
            val imageView: ImageView = currentView!!.findViewById<ImageView>(R.id.galleryItemImage)

            if(currentView != null){
                Glide.with(currentView)
                    .load(currentMediaUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_add_photo)
                    .into(imageView)

                currentView.setOnClickListener{
                    val intent = Intent(currentView.context, VideoActivity::class.java)
                    intent.putExtra("mediaUri", currentMediaUri)
                    currentView.context.startActivity(intent)
                }
            }

            return currentView!!
        }
}