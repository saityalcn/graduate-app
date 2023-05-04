package com.example.mezunapp.adapters

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.mezunapp.ProfileActivity
import com.example.mezunapp.R
import com.example.mezunapp.VideoActivity
import com.example.mezunapp.models.Announcement
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
                currentView = LayoutInflater.from(context)
                    .inflate(R.layout.gallery_list_item, viewGroup, false)
            }

            val currentMediaUri: Uri = Uri.parse(getItem(i)) as Uri
            val imageView: ImageView = currentView!!.findViewById<ImageView>(R.id.galleryItemImage)

            if (currentView != null) {
                Glide.with(currentView)
                    .load(currentMediaUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_add_photo)
                    .into(imageView)

                currentView.setOnClickListener {
                    val intent = Intent(currentView.context, VideoActivity::class.java)
                    intent.putExtra("mediaUri", currentMediaUri)
                    currentView.context.startActivity(intent)
                }

                createPopupMenu(currentView, currentMediaUri.toString())
            }

            return currentView!!
        }

        fun deleteMedia(mediaLink: String){
            val db = Firebase.firestore
            val auth = Firebase.auth
            val graduateRef = db.collection("graduates").document(auth.currentUser!!.uid)

            graduateRef.get().addOnSuccessListener {documentSnapshot ->
                val mediaNamesList = documentSnapshot.get("mediaNames") as ArrayList<String>?
                if (mediaNamesList != null) {
                    val indexToRemove = mediaNamesList.indexOf(mediaLink)

                    if (indexToRemove >= 0) {

                        mediaNamesList.removeAt(indexToRemove)

                        graduateRef.update("mediaNames", mediaNamesList)
                            .addOnSuccessListener {
                                Log.d("TAG", "Document successfully updated!")
                                list.remove(mediaLink)
                                this.notifyDataSetChanged()
                            }
                            .addOnFailureListener { e ->
                                Log.e("TAG", "Error updating document", e)
                            }
                    }
                }
            }
        }

    fun createPopupMenu(view: View, currentMediaUri: String){
        val button = view
        val popupMenu = PopupMenu(view.context, button)
        popupMenu.menuInflater.inflate(R.menu.delete_popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.deleteMedia -> {
                    deleteMedia(currentMediaUri)
                    true
                }

                else -> false
            }
        }
        button.setOnLongClickListener {
            popupMenu.show()

            true
        }
    }
}