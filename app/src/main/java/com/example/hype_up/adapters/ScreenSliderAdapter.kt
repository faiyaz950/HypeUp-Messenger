package com.example.hype_up.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hype_up.fragments.ChatFragment
import com.example.hype_up.fragments.PeopleFragment

class ScreenSliderAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa){
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when(position) {
        0 -> ChatFragment()
        else -> PeopleFragment()

}
}
