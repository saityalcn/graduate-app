package com.example.mezunapp.models

import android.net.Uri
import com.google.firebase.Timestamp

class Announcement {
    lateinit var title: String
    lateinit var text: String
    lateinit var imageUrl: Uri
    lateinit var expireDate: Timestamp
    lateinit var userId: String

    constructor(title: String, text: String, expireDate: Timestamp, imageUrl: Uri, userId: String){
        this.title = title
        this.text = text
        this.expireDate = expireDate
        this.imageUrl = imageUrl
        this.userId = userId
    }


    fun toMap() : HashMap<String, Any>{
        return hashMapOf(
            "title" to title,
            "text" to text,
            "expireDate" to expireDate,
            "imageUrl" to imageUrl,
            "userId" to userId
        )
    }

    companion object{
        fun toObject(map: HashMap<String, Any>){
            var an: Announcement = Announcement(
                map["title"] as String,
                map["text"] as String,
                map["expireDate"] as Timestamp,
                map["imageUrl"] as Uri,
                map["userId"] as String
            )
        }
    }


}