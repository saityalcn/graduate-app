package com.example.mezunapp.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mezunapp.R
import com.example.mezunapp.adapters.AnnouncementsGridAdapter
import com.example.mezunapp.adapters.GraduateListAdapter
import com.example.mezunapp.databinding.FragmentDashboardBinding
import com.example.mezunapp.models.Announcement
import com.example.mezunapp.models.Graduate
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import com.google.firebase.Timestamp

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    lateinit var gradutesList: RecyclerView


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager: LinearLayoutManager  = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        gradutesList = requireView().findViewById(R.id.graduates_list)
        val graduates: MutableList<Graduate> = mutableListOf<Graduate>()


        val db = Firebase.firestore

        val announcementsDb = db.collection("graduates")

        announcementsDb.get().addOnSuccessListener {
            for (map in it) {
                var i = 0
                while(i<20) {
                    val an: Graduate = Graduate(
                        map["name"] as String,
                        map["surname"] as String,
                        map["startDate"] as Timestamp,
                        map["graduateDate"] as Timestamp,
                        map["email"] as String,
                        map["profilePhotoName"] as String,
                        map["programName"] as String,
                        map["currentjobCountry"] as String,
                        map["currentJobCity"] as String,
                        map["currentJobCompany"] as String
                    )
                    graduates.add(an)
                    i++
                }
            }
            val gradAdapter = GraduateListAdapter(list=graduates)
            gradutesList.adapter = gradAdapter
            gradutesList.layoutManager = layoutManager
        }.addOnFailureListener{
            Log.d("FAILURE", it.toString())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}