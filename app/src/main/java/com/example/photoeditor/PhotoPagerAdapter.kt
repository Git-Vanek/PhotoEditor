package com.example.photoeditor

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PhotoPagerAdapter(fragment: Fragment, list: MutableList<Photo>, reg: Boolean) : FragmentStateAdapter(fragment) {
    private val photoMutableList: MutableList<Photo> = list
    private val userReg: Boolean = reg

    val pageTotalPhotoFragment: PagePhotoFragment by lazy {
        val result: MutableList<Photo> = mutableListOf()
        for (photo in photoMutableList) {
            if (!photo.original) {
                result.add(photo)
            }
        }
        PagePhotoFragment.newInstance(result)
    }

    val pageMyPhotoFragment: PagePhotoFragment by lazy {
        val result: MutableList<Photo> = mutableListOf()
        for (photo in photoMutableList) {
            if (photo.original) {
                result.add(photo)
            }
        }
        PagePhotoFragment.newInstance(result)
    }

    val pageAllPhotoFragment: PagePhotoFragment by lazy {
        PagePhotoFragment.newInstance(photoMutableList)
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
