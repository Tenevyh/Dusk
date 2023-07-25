package com.tenevyh.android.dusk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tenevyh.android.dusk.ui.login.ChatAuthStateListener
import com.tenevyh.android.dusk.ui.login.LoginFragment
import com.tenevyh.android.dusk.ui.main.ChatLandingFragment
import com.tenevyh.android.dusk.ui.utils.replaceFragment

private const val TAG = "CurrentUser"

class MainActivity : AppCompatActivity(), ChatAuthStateListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showFragment()
    }

    override fun onStop() {
        super.onStop()
        if( FirebaseAuth.getInstance().currentUser != null) {
            val uidUser = FirebaseAuth.getInstance().currentUser!!.uid
            Firebase.database.getReference("users").child(uidUser).child("online")
                .setValue(false)
        }
    }

    override fun onAuthStateChanged() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            showFragment()
        }
    }

    private fun showFragment(){
        if (FirebaseAuth.getInstance().currentUser == null){
            replaceFragment(R.id.fragmentContainer, LoginFragment())
        } else {
            val uidUser = FirebaseAuth.getInstance().currentUser!!.uid
            Firebase.database.getReference("users").child(uidUser).child("online")
                .setValue(true)
            replaceFragment(R.id.fragmentContainer, ChatLandingFragment())
        }
    }
    
    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 0) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}