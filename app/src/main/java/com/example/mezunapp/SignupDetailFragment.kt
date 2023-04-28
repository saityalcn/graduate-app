package com.example.mezunapp

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.mezunapp.models.Graduate
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignupDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignupDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var selectedImage: Uri
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val programs: MutableList<String> = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    selectedImage = uri
                    requireView().findViewById<ImageView>(R.id.imageViewSelectedPhoto)
                        .setImageURI(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView: ImageView = requireView().findViewById(R.id.imageViewSelectPhoto)

        programs.add("Lisans")
        programs.add("Yüksek Lisans")
        programs.add("Doktora")

        val spinner = requireView().findViewById<Spinner>(R.id.programSelectSpinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, programs
            )
            spinner.adapter = adapter
            imageView.setOnClickListener {
                Log.v("Signup", "Add Photo")

                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

        requireView().findViewById<Button>(R.id.saveBtn).setOnClickListener{
            onSaveBtnClick(view)
        }

    }

    fun onSaveBtnClick(view: View) {
        val db = Firebase.firestore
        val auth = Firebase.auth

        // personal inputs
        val name: String =
            requireView().findViewById<EditText>(R.id.editTextName).text.toString()
        val surname: String =
            requireView().findViewById<EditText>(R.id.editTextSurname).text.toString()
        val phoneNumber: String = requireView().findViewById<EditText>(R.id.editTextPhoneNumber).text.toString()


        // education inputs
        val selectedProgramIndex: Int = requireView().findViewById<Spinner>(R.id.programSelectSpinner)!!.selectedItemPosition
        val selectedProgram: String = programs.get(selectedProgramIndex)
        val startYear: String = requireView().findViewById<EditText>(R.id.editTextStartYear).text.toString()
        val gradYear: String =
            requireView().findViewById<EditText>(R.id.editTextGradYear).text.toString()

        // company inputs
        val currentCompany: String = requireView().findViewById<EditText>(R.id.editTextCurrentJobCompany).text.toString()
        val currentCompanyCity: String = requireView().findViewById<EditText>(R.id.editTextCurrentJobCity).text.toString()
        val currentCompanyCountry: String = requireView().findViewById<EditText>(R.id.editTextCurrentJobCountry).text.toString()


        val email: String = auth.currentUser!!.email!!

        uploadImage().addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                lateinit var grad: Graduate
                if (it != null)
                    grad = Graduate(name, surname, startYear, gradYear, email, it,selectedProgram, currentCompany, currentCompanyCity, currentCompanyCountry)
                else
                    grad = Graduate(name, surname, startYear, gradYear, email, Uri.EMPTY,selectedProgram, currentCompany, currentCompanyCity, currentCompanyCountry)

                db.collection("graduates").document(auth.currentUser!!.uid).set(grad.toMap())
                    .addOnSuccessListener {
                        Log.d("GRAD", "GRAD SUCCESS")
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                    }.addOnFailureListener {
                    showErrorSnackbar(view, it.localizedMessage)
                }
            }.addOnFailureListener {
                showErrorSnackbar(view, it.localizedMessage)
            }
        }
    }

    fun uploadImage(): UploadTask {
        var storageRef = Firebase.storage.reference
        val time = System.currentTimeMillis()
        val profileImageRef = storageRef.child("profiles/" + time.toString())
        val imageUploadTask = profileImageRef.putFile(selectedImage)
        return imageUploadTask
    }

    fun showErrorSnackbar(view: View, message: String) {
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
         * @return A new instance of fragment SignupDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignupDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}