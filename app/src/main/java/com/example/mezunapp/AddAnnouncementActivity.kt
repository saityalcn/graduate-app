package com.example.mezunapp

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mezunapp.models.Announcement
import com.google.android.material.snackbar.Snackbar
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*


class AddAnnouncementActivity : AppCompatActivity() {
    val myCalendar: Calendar = Calendar.getInstance()
    lateinit var editText: EditText
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    lateinit var selectedPhoto: Uri
    lateinit var selectedDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_announcement)

        setTitle("Duyuru Ekle")

        findViewById<Button>(R.id.submitBtn).setOnClickListener{
            saveAnnouncement(it)
        }

        editText = findViewById<EditText>(R.id.editTextDate) as EditText
        editText.isEnabled = false

        val date =
            OnDateSetListener { view, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateLabel()
            }

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

            if (uri != null) {
                selectedPhoto = uri
                findViewById<ImageView>(R.id.imageViewSelectedPhoto).setImageURI(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        findViewById<ImageView>(R.id.calendarImage).setOnClickListener{
            DatePickerDialog(
                this@AddAnnouncementActivity,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val imageView: ImageView = findViewById(R.id.imageViewSelectPhoto)

        imageView.setOnClickListener{
            Log.v("Signup", "Add Photo")

            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val contentWrapper = findViewById<LinearLayout>(R.id.contentWrapper)

        progressBar.visibility = View.GONE
        contentWrapper.visibility = View.VISIBLE
    }


    private fun updateLabel() {
        val myFormat = "dd/MM/yy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        editText.setText(dateFormat.format(myCalendar.time))
    }

    fun saveAnnouncement(view: View){
        var storageRef = Firebase.storage.reference
        val db = Firebase.firestore
        val auth = Firebase.auth

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val contentWrapper = findViewById<LinearLayout>(R.id.contentWrapper)

        progressBar.visibility = View.VISIBLE
        contentWrapper.visibility = View.GONE

        lateinit var imageUrl: Uri

        var title: String = findViewById<EditText>(R.id.editTextAnnouncementTitle)!!.text.toString()
        var text: String = findViewById<EditText>(R.id.editTextAnnouncementText)!!.text.toString()


        val time= System.currentTimeMillis()
        val announcementImageRef = storageRef.child("announcements/" + time.toString())

        val uploadTask = announcementImageRef.putFile(selectedPhoto)

        uploadTask.addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                if(it != null){
                    imageUrl = it
                } else{
                    imageUrl = Uri.EMPTY
                }
                val announcement: Announcement = Announcement(title,text, Timestamp((myCalendar.time.time / 1000), 0),imageUrl, auth.currentUser!!.uid)
                db.collection("announcements").add(announcement.toMap()).addOnSuccessListener {
                    finish()
                }.addOnFailureListener{
                    progressBar.visibility = View.GONE
                    contentWrapper.visibility = View.VISIBLE
                    showErrorSnackbar(view,"Duyuru kaydedilirken hata oluştu.")
                }
            }
        }.addOnFailureListener{
            progressBar.visibility = View.GONE
            contentWrapper.visibility = View.VISIBLE
            showErrorSnackbar(view, "Fotoğraf yüklenirken bir hata oluştu.")
        }
    }


    fun showErrorSnackbar(view: View, message: String){
        val snack: Snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val view = snack.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        view.setBackgroundColor(Color.RED)
        snack.show()
    }

    fun showSuccessSnackbar(view: View, message: String){
        val snack: Snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        val view = snack.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        view.setBackgroundColor(Color.GREEN)
        snack.show()
    }


}