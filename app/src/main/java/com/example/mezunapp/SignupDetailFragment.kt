package com.example.mezunapp

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.mezunapp.models.Graduate
import com.google.android.material.snackbar.Snackbar
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream

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

    private val CAMERA_REQUEST_CODE = 100
    private var imageUri: Uri? = null

    lateinit var selectedImage: Uri
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val programs: MutableList<String> = mutableListOf<String>()
    private val uid = arguments?.getString("uid")
    lateinit var graduate: Graduate
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

       val auth = Firebase.auth

        createPopupMenu()

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
        }


        requireView().findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE

        if(auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
            initFields()
            Log.d("INIT", auth.currentUser!!.uid)
        }

        requireView().findViewById<Button>(R.id.saveBtn).setOnClickListener{
            if(auth.currentUser != null && auth.currentUser!!.isEmailVerified)
                onEditSaveBtnClick(view)

            else
                onCreateSaveBtnClick(view)
        }

    }

    fun createPopupMenu(){
        val button = requireView().findViewById<ImageView>(R.id.imageViewSelectPhoto)
        val popupMenu = PopupMenu(requireView().context, button)
        popupMenu.menuInflater.inflate(R.menu.addphoto_popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.openCamera -> {
                    if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE), CAMERA_REQUEST_CODE)
                    } else if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE), CAMERA_REQUEST_CODE)
                    }else {
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis())
                        values.put(MediaStore.Images.Media.DESCRIPTION, "")
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        imageUri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                    }
                    true
                }
                R.id.selectGallery -> {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    true
                }
                else -> false
            }
        }
        button.setOnClickListener {
            popupMenu.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if(imageUri != null)
                selectedImage = imageUri as Uri
        }
    }

    fun initFields(){
        val db = Firebase.firestore
        val auth = Firebase.auth

        val progressBar = requireView().findViewById<ProgressBar>(R.id.progressBar)
        val formWrapper = requireView().findViewById<LinearLayout>(R.id.form_wrapper)


        progressBar.visibility = View.VISIBLE
        formWrapper.visibility = View.GONE

        // personal inputs
        val name = requireView().findViewById<EditText>(R.id.editTextName)
        val surname = requireView().findViewById<EditText>(R.id.editTextSurname)
        val phoneNumber = requireView().findViewById<EditText>(R.id.editTextPhoneNumber)

        // education inputs
        val selectedProgram = requireView().findViewById<Spinner>(R.id.programSelectSpinner)
        val startYear = requireView().findViewById<EditText>(R.id.editTextStartYear)
        val gradYear = requireView().findViewById<EditText>(R.id.editTextGradYear)

        // company inputs
        val currentCompany = requireView().findViewById<EditText>(R.id.editTextCurrentJobCompany)
        val currentCompanyCity = requireView().findViewById<EditText>(R.id.editTextCurrentJobCity)
        val currentCompanyCountry = requireView().findViewById<EditText>(R.id.editTextCurrentJobCountry)

        db.collection("graduates").document(auth.currentUser!!.uid).get().addOnSuccessListener {
            val grad: Graduate = Graduate.fromDocumentSnapshot(it)
            graduate = grad

            name.setText(grad.name)
            surname.setText(grad.surname)

            // phoneNumber.setText(grad.phoneNumber)

            selectedProgram.setSelection(programs.indexOf(grad.programName))
            startYear.setText(grad.startYear)
            gradYear.setText(grad.graduateYear)

            currentCompany.setText(grad.currentJobCompany)
            currentCompanyCity.setText(grad.currentJobCity)
            currentCompanyCountry.setText(grad.currentJobCountry)

            phoneNumber.setText(grad.phoneNumber)

            requireView().findViewById<LinearLayout>(R.id.imageSelectView).visibility = View.GONE

            progressBar.visibility = View.GONE
            formWrapper.visibility = View.VISIBLE
        }

    }

    fun onCreateSaveBtnClick(view: View) {
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
                    grad = Graduate(name, surname, startYear, gradYear, email,phoneNumber, it,
                        mutableListOf<String>(), selectedProgram, currentCompany, currentCompanyCity, currentCompanyCountry, auth.currentUser!!.uid)
                else
                    grad = Graduate(name, surname, startYear, gradYear,phoneNumber, email, Uri.EMPTY,mutableListOf<String>(), selectedProgram, currentCompany, currentCompanyCity, currentCompanyCountry, auth.currentUser!!.uid)

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

    fun onEditSaveBtnClick(view: View){
        val db = Firebase.firestore
        val auth = Firebase.auth

        val progressBar = requireView().findViewById<ProgressBar>(R.id.progressBar)
        val formWrapper = requireView().findViewById<LinearLayout>(R.id.form_wrapper)

        progressBar.visibility = View.VISIBLE
        formWrapper.visibility = View.GONE


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

        val docRef = db.collection("graduates").document(auth.currentUser!!.uid)

        lateinit var grad: Graduate
        grad = Graduate(name, surname, startYear, gradYear, graduate.email, phoneNumber, graduate.profilePhotoLink,
            graduate.mediaNames, selectedProgram, currentCompany, currentCompanyCity, currentCompanyCountry, graduate.uid)

        docRef.update(grad.toMap()).addOnSuccessListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }.addOnFailureListener{
            progressBar.visibility = View.GONE
            formWrapper.visibility = View.VISIBLE
            showErrorSnackbar(requireView(), it.localizedMessage)
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