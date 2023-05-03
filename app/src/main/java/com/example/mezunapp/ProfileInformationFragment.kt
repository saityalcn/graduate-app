package com.example.mezunapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mezunapp.models.Graduate

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileInformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileInformationFragment(graduate: Graduate) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var graduate: Graduate = graduate

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
        return inflater.inflate(R.layout.fragment_profile_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textViewStartDate: TextView = requireView().findViewById<TextView>(R.id.textViewStartDate)
        val textViewGradDate: TextView = requireView().findViewById<TextView>(R.id.textViewGradDate)
        val textViewProgram: TextView = requireView().findViewById<TextView>(R.id.textViewProgram)

        val textViewCompanyName: TextView = requireView().findViewById<TextView>(R.id.textViewCompanyName)
        val textViewCompanyCity: TextView = requireView().findViewById<TextView>(R.id.textViewCompanyCity)
        val textViewCompanyCountry: TextView = requireView().findViewById<TextView>(R.id.textViewCompanyCountry)

        val textViewEmail: TextView = requireView().findViewById<TextView>(R.id.textViewEmail)
        val textViewPhoneNumber: TextView = requireView().findViewById<TextView>(R.id.textViewPhoneNumber)


        textViewStartDate.setText(graduate.startYear)
        textViewGradDate.setText(graduate.graduateYear)
        textViewProgram.setText(graduate.programName)

        textViewCompanyName.setText(graduate.currentJobCompany)
        textViewCompanyCity.setText(graduate.currentJobCity)
        textViewCompanyCountry.setText(graduate.currentJobCountry)

        textViewEmail.setText(graduate.email)
        textViewPhoneNumber.setText("05053651492")
    }

}