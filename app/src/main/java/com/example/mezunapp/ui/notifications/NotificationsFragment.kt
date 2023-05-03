package com.example.mezunapp.ui.notifications

import android.content.ClipData.Item
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.mezunapp.AccountFragmentNotAuthenticated
import com.example.mezunapp.ProfileFragment
import com.example.mezunapp.R
import com.example.mezunapp.databinding.FragmentNotificationsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val profileFragment = ProfileFragment()
    val nonAuthenticatedFragment = AccountFragmentNotAuthenticated()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        val auth = Firebase.auth

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if(auth.currentUser != null)
            setHasOptionsMenu(true)

        else
            setHasOptionsMenu(false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = Firebase.auth

        if(auth.currentUser != null){
            val bundle = Bundle()
            bundle.putString("uid", auth.currentUser!!.uid)
            profileFragment.arguments = bundle
            replaceFragment(profileFragment)
        }


        else
            replaceFragment(nonAuthenticatedFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        requireActivity().menuInflater.inflate(R.menu.action_bar_with_buttons, menu)
        super.onCreateOptionsMenu(menu, inflater!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val auth = Firebase.auth

        if(auth.currentUser != null){
            val bundle = Bundle()
            bundle.putString("uid", auth.currentUser!!.uid)
            profileFragment.arguments = bundle
            replaceFragment(profileFragment)
        }

        else
            replaceFragment(nonAuthenticatedFragment)

    }

    fun replaceFragment(fragment: Fragment){
        val fragmentManager: FragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val auth = Firebase.auth
        when(item.itemId){
            R.id.signOutBtn -> {
                auth.signOut()
                replaceFragment(nonAuthenticatedFragment)
            }
            else -> {
                Log.d("ERROR", "Error on onOptionsItemSelected")
            }
        }
        return super.onOptionsItemSelected(item)
    }
}