package com.example.mezunapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.mezunapp.AccountFragmentAuthenticated
import com.example.mezunapp.AccountFragmentNotAuthenticated
import com.example.mezunapp.GalleryFragment
import com.example.mezunapp.ProfileInformationFragment
import com.example.mezunapp.models.Graduate

class ProfileTabPagerAdapter(fragmentManager: FragmentManager, grad: Graduate) :
    FragmentStatePagerAdapter(fragmentManager) {

    private val fragments = arrayOf(ProfileInformationFragment(grad), GalleryFragment(grad))

    override fun getCount(): Int = fragments.size

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        0 -> "Bilgiler"
        1 -> "Galeri"
        else -> "ERROR"
    }
}

