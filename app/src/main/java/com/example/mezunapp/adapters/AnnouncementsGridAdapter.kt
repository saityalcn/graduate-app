package com.example.mezunapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mezunapp.R
import com.example.mezunapp.models.Announcement
import com.squareup.picasso.Picasso


class AnnouncementsGridAdapter : BaseAdapter{
    val context: Context?
    val list: MutableList<Announcement>

    constructor(context: Context, list: MutableList<Announcement>){
        this.context = context
        this.list = list
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(i: Int): Any {
        return list.get(i)
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
        var currentView: View? = view
        if (currentView == null) {
            currentView = LayoutInflater.from(context).inflate(R.layout.announcement_list_item, viewGroup, false)
        }


        val currentAnnouncement: Announcement = getItem(i) as Announcement


        // val imageViewPoster: ImageView = view.findViewById(R.id.imageViewPoster) as ImageView
        if(currentView != null){
            currentView!!.findViewById<TextView>(R.id.textViewAnnouncementTitle).setText(currentAnnouncement.title)
            val imageView: ImageView = currentView!!.findViewById<ImageView>(R.id.imageView)
            Picasso.get().load(currentAnnouncement.imageUrl).into(imageView);
        }

        // imageViewPoster.setImageResource(mevcutFilm.getPosterId())

        return currentView!!
    }

}