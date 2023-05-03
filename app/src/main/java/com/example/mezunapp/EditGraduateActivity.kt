package com.example.mezunapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EditGraduateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_graduate)
        changeFragment(SignupDetailFragment())
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

    fun changeFragment(fragment: Fragment) {
        val auth = Firebase.auth
        val bundle = Bundle()

        if(auth.currentUser != null)
            bundle.putString("uid", auth.currentUser!!.uid)

        fragment.arguments = bundle

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.editGradFrameLayout, fragment)
        fragmentTransaction.commit()
    }
}