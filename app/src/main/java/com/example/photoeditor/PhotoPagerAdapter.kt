package com.example.photoeditor

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PhotoPagerAdapter(fragment: Fragment, list: MutableList<Photo>) : FragmentStateAdapter(fragment) {
    private val photoMutableList: MutableList<Photo> = list
    lateinit var pageTotalPhotoFragment: PagePhotoFragment
    lateinit var pageMyPhotoFragment: PagePhotoFragment
    lateinit var pageAllPhotoFragment: PagePhotoFragment

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val result: MutableList<Photo> = mutableListOf()
                for (photo in photoMutableList) {
                    if (!photo.original) {
                        result.add(photo)
                    }
                }
                pageTotalPhotoFragment = PagePhotoFragment(result)
                return pageTotalPhotoFragment
            }
            1 -> {
                val result: MutableList<Photo> = mutableListOf()
                for (photo in photoMutableList) {
                    if (photo.original) {
                        result.add(photo)
                    }
                }
                pageMyPhotoFragment = PagePhotoFragment(result)
                return pageMyPhotoFragment
            }
            2 -> {
                pageAllPhotoFragment = PagePhotoFragment(photoMutableList)
                return pageAllPhotoFragment
            }
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
