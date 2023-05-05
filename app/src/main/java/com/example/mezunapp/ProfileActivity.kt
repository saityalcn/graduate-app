package com.example.mezunapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val uid = intent.extras!!.getString("uid")

        changeFragment(ProfileFragment(), uid)

        setTitle("Profil")
    }

    fun changeFragment(fragment: Fragment, uid: String?){
        val bundle = Bundle()
        if(uid != null) {
            bundle.putString("uid", uid)
            fragment.arguments = bundle
        }
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.profileFrameLayout, fragment)
        fragmentTransaction.commit()
    }
}