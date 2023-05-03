package com.example.mezunapp.ui.home

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mezunapp.adapters.AnnouncementsGridAdapter
import com.example.mezunapp.R
import com.example.mezunapp.databinding.FragmentHomeBinding
import com.example.mezunapp.models.Announcement
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    lateinit var announcementsGrid: GridView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val progressBar = requireView().findViewById<ProgressBar>(R.id.progressBar)
        announcementsGrid = requireView().findViewById(R.id.announcements_grid)
        val announcements: MutableList<Announcement> = mutableListOf<Announcement>()

        progressBar.visibility = View.VISIBLE
        announcementsGrid.visibility = View.GONE

        val db = Firebase.firestore

        val announcementsDb = db.collection("announcements")

        announcementsDb.get().addOnSuccessListener {
            for(item in it){
                val an: Announcement = Announcement(item["title"] as String, item["text"] as String, item["expireDate"] as Timestamp, Uri.parse(item["imageUrl"] as String) as Uri, "")
                announcements.add(an)
            }
            val announcementsAdapter = AnnouncementsGridAdapter(requireActivity(),list=announcements)
            announcementsGrid.adapter = announcementsAdapter
            progressBar.visibility = View.GONE
            announcementsGrid.visibility = View.VISIBLE
        }.addOnFailureListener{
            Log.d("FAILURE", it.toString())
        }



        announcementsGrid.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            Toast.makeText(
                requireActivity(), announcements.get(position).title + " selected",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}