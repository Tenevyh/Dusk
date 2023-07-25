package com.tenevyh.android.dusk.ui.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.tenevyh.android.dusk.R
import com.tenevyh.android.dusk.ui.repository.UserRepository
import com.tenevyh.android.dusk.ui.utils.mapFromFirebaseUser

class LoginFragment : Fragment() {

    private var chatAuthStateListener: ChatAuthStateListener? = null
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
    )
    private val signLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()){ res ->
        this.onSignInResult(res)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is ChatAuthStateListener) {
            chatAuthStateListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAuth.getInstance().signOut()
        doLogin()
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult){
        if(result.resultCode == AppCompatActivity.RESULT_OK) {
            FirebaseAuth.getInstance().currentUser?.also {
                user -> UserRepository().createOrUpdateUser(
                mapFromFirebaseUser(user)
                )
                chatAuthStateListener?.onAuthStateChanged()
            } ?: showError(requireActivity(),
            "Something went wrong")
        } else {
            if(result.idpResponse == null){
                requireActivity().finish()
            } else {
                result.idpResponse?.error?.message?.let {
                    showError(requireActivity(), it)
                }
            }
        }
    }

    private fun doLogin() {
        val signIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            //.setLogo(R.drawable.logo)
            //.setTheme(R.style.ChatAppLogin)
            .setAlwaysShowSignInMethodScreen(true)
            .setIsSmartLockEnabled(false)
            .setTosAndPrivacyPolicyUrls(
                "https://in.bpbonline.com/policies/terms-of-service",
                "https://in.bpbonline.com/pages/privacy-policy"
            ).build()
        signLauncher.launch(signIntent)
    }

    private fun showError(activity: Activity, errorMsg: String){
        val builder = AlertDialog.Builder(activity)
        builder.apply {
            setPositiveButton(
                R.string.retry
            ) { dialog, id -> doLogin()}
            setNegativeButton(
                R.string.exit
            ) { dialog, id -> activity.finish()}
            setMessage(errorMsg)
        }
        builder.create().show()
    }
}