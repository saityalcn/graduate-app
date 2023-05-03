package com.example.mezunapp.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.*
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
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.ktx.auth


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    lateinit var gradutesList: RecyclerView
    lateinit var graduates: MutableList<Graduate>
    lateinit var gradAdapter: GraduateListAdapter
    private lateinit var drawerLayout: DrawerLayout

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

        setHasOptionsMenu(true)

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager: LinearLayoutManager  = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        gradutesList = requireView().findViewById(R.id.graduates_list)
        graduates = mutableListOf<Graduate>()


        val searchView = requireView().findViewById<android.widget.SearchView>(R.id.gradSearchView)

        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Bu metod, kullanıcı arama butonuna tıkladığında veya klavyeden "enter" tuşuna bastığında çalışır
                // Arama sorgusu işlemleri burada yapılır
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val newData:MutableList<Graduate> = mutableListOf<Graduate>()
                if(::gradAdapter.isInitialized) {
                    graduates.forEach {
                        if (it.name.contains(newText!!)) {
                            newData.add(it)
                        }
                    }
                    gradAdapter.setData(newData)
                    gradAdapter.notifyDataSetChanged()
                }
                return true
            }
        })


        val db = Firebase.firestore

        val gradsDb = db.collection("graduates")

        gradsDb.get().addOnSuccessListener {
            for (map in it) {
                val an: Graduate = Graduate.toObject(map)
                graduates.add(an)
            }
            gradAdapter = GraduateListAdapter(list=graduates)
            gradutesList.adapter = gradAdapter
            gradutesList.layoutManager = layoutManager
        }.addOnFailureListener{
            Log.d("FAILURE", it.toString())
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        requireActivity().menuInflater.inflate(R.menu.action_bar_with_filter_button, menu)
        super.onCreateOptionsMenu(menu, inflater!!)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.filterBtn -> {
            }
            else -> {
                Log.d("ERROR", "Error on onOptionsItemSelected")
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}