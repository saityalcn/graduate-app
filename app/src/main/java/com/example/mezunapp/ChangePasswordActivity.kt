package com.example.mezunapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val auth = Firebase.auth

        val currentPasswordField = findViewById<EditText>(R.id.editTextCurrentPassword)
        val newPasswordField = findViewById<EditText>(R.id.editTextNewPassword)
        val reNewPasswordField = findViewById<EditText>(R.id.editTextReNewPassword)

        findViewById<Button>(R.id.changePasswordBtn).setOnClickListener{ btnView ->
            if(newPasswordField.text.toString() == reNewPasswordField.text.toString()){
                auth.signInWithEmailAndPassword(auth.currentUser!!.email!!, currentPasswordField.text.toString()).addOnSuccessListener {
                    auth.currentUser!!.updatePassword(newPasswordField.text.toString()).addOnSuccessListener {
                        finish()
                    }.addOnFailureListener{ex ->
                        showErrorSnackbar(btnView,ex.localizedMessage)
                    }
                }.addOnFailureListener{ex->
                    showErrorSnackbar(btnView,ex.localizedMessage)
                }
            }else {
                showErrorSnackbar(btnView,"Girilen Şifreler Uyuşmalıdır")
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

}