package com.tenevyh.android.dusk.ui.main

import android.provider.ContactsContract.Contacts
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tenevyh.android.dusk.R
import com.tenevyh.android.dusk.ui.contacts.ContactsFragment
import com.tenevyh.android.dusk.ui.profile.UserProfileFragment

val TAB_TITLES = arrayOf(R.string.tab_text_1,
    R.string.tab_text_2,
    R.string.tab_text_3)

class ChatViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment){

    override fun getItemCount() = TAB_TITLES.size

    override fun createFragment(position: Int) = when (position) {
        0 -> {
            ContactsFragment()
        }
        1 -> {
            PlaceholderFragment()
        }
        2 -> {
            UserProfileFragment()
        }
        else -> ContactsFragment()
    }

}

