package com.tenevyh.android.dusk.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.tenevyh.android.dusk.R
import com.tenevyh.android.dusk.databinding.FragmentChatHomeBinding
import kotlinx.android.synthetic.main.fragment_chat_home.*


class ChatLandingFragment : Fragment(R.layout.fragment_chat_home){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = ChatViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) {
            tab, position -> tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }
}