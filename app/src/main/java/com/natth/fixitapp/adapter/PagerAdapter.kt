package com.natth.fixitapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.natth.fixitapp.fragment.ListFragment
import com.natth.fixitapp.fragment.NotiFragment
import com.natth.fixitapp.fragment.HomeFragment

class PagerAdapter (fm:FragmentManager) : FragmentPagerAdapter(fm){

    override fun getItem(position: Int): Fragment {
        return  when (position){
            0-> {
                HomeFragment()
            }
            1->{
                ListFragment()
            }
            2->{
                NotiFragment()
            }
            else -> {
                HomeFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }
}