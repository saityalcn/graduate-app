package com.example.mezunapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mezunapp.adapters.AnnouncementsGridAdapter
import com.example.mezunapp.adapters.GalleryGridAdapter
import com.example.mezunapp.models.Announcement
import com.example.mezunapp.models.Graduate
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GalleryFragment(grad:Graduate) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val REQUEST_CODE_PICK_MEDIA = 1
    private var graduate: Graduate = grad
    private lateinit var pathString: String

    lateinit var galleryGrid: GridView


    lateinit var selectedContent: Uri

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = Firebase.auth

        requireView().findViewById<ImageView>(R.id.addPhotoImage).visibility =
            View.INVISIBLE

        requireView().findViewById<ImageView>(R.id.uploadPhotoImage).visibility =
            View.INVISIBLE

        createPopupMenu()

        if(auth.currentUser != null && (auth.currentUser!!.uid == graduate.uid)){
                requireView().findViewById<ImageView>(R.id.addPhotoImage).visibility =
                    View.VISIBLE
        }

        requireView().findViewById<ImageView>(R.id.uploadPhotoImage).setOnClickListener{
            val uploadTask = uploadContent()
            if(uploadTask != null) {
                uploadTask.addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener {
                        val db = Firebase.firestore
                        val userRef = db.collection("graduates").document(auth.currentUser!!.uid)
                        graduate.mediaNames.add(it.toString())
                        userRef.update("mediaNames", graduate.mediaNames).addOnSuccessListener {
                            showSuccessSnackbar(requireView(), "Medya başarıyla yüklendi.")
                        }.addOnFailureListener {
                            showErrorSnackbar(
                                requireView(),
                                "Medya yüklenirken bir hatayla karşılaştık. Daha sonra tekrar deneyiniz."
                            )
                        }
                    }.addOnFailureListener {
                        showErrorSnackbar(
                            requireView(),
                            "Medya yüklenirken bir hatayla karşılaştık. Daha sonra tekrar deneyiniz."
                        )
                    }
                }
            } else{
                showErrorSnackbar(
                    requireView(),
                    "Beklenmedik bir hatayla karşılaştık. Daha sonra tekrar deneyiniz.")
            }
        }

        // gridView:
        galleryGrid = requireView().findViewById(R.id.gallery_grid)
        val uriList: MutableList<String> = mutableListOf<String>()

        Log.d("MEDIAAAAAAAAAAaaLENGTHs", graduate.mediaNames.size.toString())

        graduate.mediaNames.forEach{
            Log.d("MEDIAAAAAAAAAAaa", it)
            uriList.add(it)
        }

        val galleryAdapter = GalleryGridAdapter(requireActivity(),list=uriList)
        galleryGrid.adapter = galleryAdapter

    }

    fun createPopupMenu(){
        val button = requireView().findViewById<ImageView>(R.id.addPhotoImage)
        val popupMenu = PopupMenu(requireView().context, button)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.image -> {
                    selectMedia("image/*")
                    pathString = "image/"
                    true
                }
                R.id.video -> {
                    selectMedia("video/*")
                    pathString = "video/"
                    true
                }
                else -> false
            }
        }
        button.setOnClickListener {
            popupMenu.show()
        }
    }

    fun selectMedia(mediaType:String){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = mediaType
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        startActivityForResult(intent, REQUEST_CODE_PICK_MEDIA)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_MEDIA && resultCode == Activity.RESULT_OK) {
            val mediaUri = data?.data
            if (mediaUri != null) {
                requireView().findViewById<ImageView>(R.id.uploadPhotoImage).visibility = View.VISIBLE
                selectedContent = mediaUri
            }
        }
    }


    fun uploadContent(): UploadTask? {
        if(::pathString.isInitialized) {
            var storageRef = Firebase.storage.reference
            val time = System.currentTimeMillis()
            val profileImageRef = storageRef.child(pathString + time.toString())
            val imageUploadTask = profileImageRef.putFile(selectedContent)
            return imageUploadTask
        }

        return null
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