package com.example.mezunapp.ui.dashboard

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
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


        val progressBar = requireView().findViewById<ProgressBar>(R.id.progressBar)

        progressBar.visibility = View.VISIBLE
        gradutesList.visibility = View.GONE

        val searchView = requireView().findViewById<android.widget.SearchView>(R.id.gradSearchView)

        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val newData:MutableList<Graduate> = mutableListOf<Graduate>()
                if(::gradAdapter.isInitialized) {
                    graduates.forEach {
                        if (it.name.toLowerCase().contains(newText!!.toLowerCase()) || it.surname.toLowerCase().contains(newText!!.toLowerCase())) {
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

            val progressBar = requireView().findViewById<ProgressBar>(R.id.progressBar)
            progressBar.visibility = View.GONE
            gradutesList.visibility = View.VISIBLE
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
                createFilterPopup()
            }
            else -> {
                Log.d("ERROR", "Error on onOptionsItemSelected")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun createFilterPopup(){
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())

        val dialogView = inflater.inflate(R.layout.dialog_filter, null)

        val minGradYear = dialogView.findViewById<EditText>(R.id.etMinGradYear)
        val maxGradYear = dialogView.findViewById<EditText>(R.id.etMaxGradYear)
        val city = dialogView.findViewById<EditText>(R.id.etCity)
        val country = dialogView.findViewById<EditText>(R.id.etCountry)

        val programs: MutableList<String> = mutableListOf<String>()

        programs.add("")
        programs.add("Lisans")
        programs.add("Yüksek Lisans")
        programs.add("Doktora")

        val spinner = dialogView.findViewById<Spinner>(R.id.programSelectSpinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, programs
            )
            spinner.adapter = adapter
        }

        builder.apply {
            setView(dialogView)
            setPositiveButton("Onayla") { _, _ ->
                var minGradYearNum = 0
                var maxGradYearNum = 5000

                if(minGradYear.text.toString() != "")
                    minGradYearNum = minGradYear.text.toString().toInt()

                if(maxGradYear.text.toString() != "")
                    maxGradYearNum = maxGradYear.text.toString().toInt()

                val country = country.text.toString()
                val city = city.text.toString()
                val program = spinner.selectedItem.toString()

                 filter(minGradYearNum,maxGradYearNum,country,city,program)
            }
            setNegativeButton("İptal") { _, _ ->
            }
        }

        builder.create().show()

    }

    fun filter(minGradYear: Int,maxGradYear: Int,country: String, city: String, program: String){
        val newData:MutableList<Graduate> = mutableListOf<Graduate>()
        var temp: Int = maxGradYear

        if(maxGradYear == 0)
            temp = 5000

        if(::gradAdapter.isInitialized) {
            graduates.forEach {
                if (it.currentJobCity.toLowerCase().contains(city.toLowerCase()) && it.currentJobCountry.toLowerCase().contains(country.toLowerCase())){
                    if((it.graduateYear.toInt() in (minGradYear + 1) until temp) && (it.programName == program || program == ""))
                        newData.add(it)
                }
            }
            gradAdapter.setData(newData)
            gradAdapter.notifyDataSetChanged()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}