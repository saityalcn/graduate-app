package com.example.mezunapp

import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.mezunapp.models.Graduate
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

class SignupActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setTitle("Profil Oluştur")
        changeFragment(SignupMainFragment())

    }

    fun onSignupBtnClick(view: View){
        val auth = Firebase.auth
        val email:String = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
        val password:String = findViewById<EditText>(R.id.editTextTextPassword).text.toString()
        val passwordReEnter = findViewById<EditText>(R.id.editTextTextRePassword).text.toString()

        if(password != passwordReEnter){
            showErrorSnackbar(view,"Entered passwords must be match")
        } else {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                if (auth.currentUser != null) {
                    auth.currentUser!!.sendEmailVerification().addOnSuccessListener {
                        changeFragment(SignupDetailFragment())
                    }.addOnFailureListener {
                        showErrorSnackbar(view, it.localizedMessage)
                    }

                }
            }.addOnFailureListener {
                showErrorSnackbar(view, it.localizedMessage)
            }
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


    fun changeFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.signupFrameLayout, fragment)
        fragmentTransaction.commit()
    }

}