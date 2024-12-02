package com.example.photoeditor

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PhotoPagerAdapter(fragment: Fragment, list1: MutableList<Photo>, list2: MutableList<Photo>, reg: Boolean) : FragmentStateAdapter(fragment) {
    private val userList: MutableList<Photo> = list1
    private val totalList: MutableList<Photo> = list2
    private val userReg: Boolean = reg

    val pageTotalPhotoFragment: PagePhotoFragment by lazy {
        PagePhotoFragment.newInstance(totalList)
    }

    val pageMyPhotoFragment: PagePhotoFragment by lazy {
        PagePhotoFragment.newInstance(userList)
    }

    val pageAllPhotoFragment: PagePhotoFragment by lazy {
        PagePhotoFragment.newInstance((userList + totalList).toMutableList())
    }

    override fun getItemCount(): Int {
        return if (userReg) {
            3
        } else {
            1
        }
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> pageTotalPhotoFragment
            1 -> pageMyPhotoFragment
            2 -> pageAllPhotoFragment
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
