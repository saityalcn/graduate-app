package com.example.mezunapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.PictureDrawable
import android.media.Image
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager.widget.ViewPager
import com.example.mezunapp.adapters.ProfileTabPagerAdapter
import com.example.mezunapp.models.Graduate
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var userId: String
    private var profileEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(arguments?.getString("uid") != null)
            userId = arguments?.getString("uid")!!

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            val auth = Firebase.auth

            val mailButton = requireView().findViewById<ImageView>(R.id.redirectToMailImage)
            val wpButton = requireView().findViewById<ImageView>(R.id.redirectToWpImage)
            val editButton = requireView().findViewById<ImageView>(R.id.imageViewEdit)

            Log.d("EMAIL", profileEmail)
            if(auth.currentUser != null){
                if(userId.equals(auth.currentUser!!.uid)){
                    mailButton.visibility = View.INVISIBLE
                    wpButton.visibility = View.INVISIBLE

                    editButton.visibility = View.VISIBLE
                } else{
                    mailButton.visibility = View.VISIBLE
                    wpButton.visibility = View.VISIBLE

                    editButton.visibility = View.GONE
                }
            } else{
                mailButton.visibility = View.VISIBLE
                wpButton.visibility = View.VISIBLE

                editButton.visibility = View.GONE
            }

            // Mail
            mailButton.setOnClickListener{
                if(profileEmail != "") {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(profileEmail))
                        putExtra(Intent.EXTRA_TEXT, "MezunApp üzerinden gönderildi.")
                    }

                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(intent)
                    }
                }else{
                    showErrorSnackbar(it, "Kullanıcının e-postası çekilirken bir hatayla karşılaştık. Daha sonra tekrar deneyiniz.")
                }
        }

        editButton.setOnClickListener{
            val intent = Intent(activity, EditGraduateActivity::class.java)
            activity?.startActivity(intent)
        }

        val viewPager: ViewPager = requireView().findViewById(R.id.view_pager)
        val textViewName: TextView = requireView().findViewById<TextView>(R.id.textViewGradName)
        val textViewAddress: TextView = requireView().findViewById<TextView>(R.id.textViewAddress)

        val imageViewProfilePhoto: ImageView = requireView().findViewById<ImageView>(R.id.imageViewProfilePhoto)

        if(::userId.isInitialized){
            val db = Firebase.firestore
            db.collection("graduates").document(userId).get().addOnSuccessListener {
                val grad = Graduate.fromDocumentSnapshot(it)
                val adapter = ProfileTabPagerAdapter(parentFragmentManager,grad)
                viewPager.adapter = adapter
                textViewName.setText(grad.name + " " + grad.surname)
                textViewAddress.setText(grad.currentJobCity + ", " + grad.currentJobCountry)
                Picasso.get().load(grad.profilePhotoLink).into(imageViewProfilePhoto)
                profileEmail = grad.email

                wpMessage("05309463046")
            }
        }

        val tabLayout: TabLayout = requireView().findViewById(R.id.profileTab)
        tabLayout.setupWithViewPager(viewPager)
    }

    fun wpMessage(phoneNumber: String){
        // whatsapp

        val redirectToWpImage: ImageView = requireView().findViewById<ImageView>(R.id.redirectToWpImage)

        redirectToWpImage.setOnClickListener{
            val message = "\nMezunApp üzerinden gönderildi."
            val whatsappUrl = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(whatsappUrl)
            startActivity(intent)
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}