package com.tenevyh.android.dusk.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.tenevyh.android.dusk.MainActivity
import com.tenevyh.android.dusk.R
import com.tenevyh.android.dusk.ui.repository.ChatUser
import com.tenevyh.android.dusk.ui.repository.UserRepository
import com.tenevyh.android.dusk.ui.utils.FetchChatUserListener
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.tenevyh.android.dusk.ui.login.LoginFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.tasks.await

class UserProfileFragment : Fragment(R.layout.fragment_profile){

    private lateinit var currentUser: ChatUser
    private lateinit var data: UserRepository
    private var profilePicUri: Uri? = null

    private var profilePicChooser =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
            if (result.resultCode == Activity.RESULT_OK){
                result.data?.let { imageUri ->
                    ivUserImage.setImageURI(imageUri.data)
                    profilePicUri = imageUri.data

                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        data = UserRepository()
        FirebaseAuth.getInstance().currentUser?.let { firebaseUser ->
            pageProgress.isVisible = true
            data.getCurrentUserByUid(firebaseUser, object : FetchChatUserListener{
                override fun onFetchUser(chatUser: ChatUser) {
                    currentUser = chatUser
                    showUserDetails(currentUser, firebaseUser)
                }
            })
        }
        ivUserImage.setOnClickListener{
            chooseProfilePicture()
        }
        btnUpdateImage.setOnClickListener{
            FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
                updateProfileImageOnStorage(userId)
            }
        }
        nameTextInputLayout.setEndIconOnClickListener {
            updateName()
        }
        emailTextInputLayout.setEndIconOnClickListener {
            updateEmail()
        }
        etPasswordTextInputLayout.setEndIconOnClickListener {
            FirebaseAuth.getInstance().currentUser?.updatePassword(etPassword.text.toString())
        }
        logout.setOnClickListener {
                AuthUI.getInstance().signOut(requireContext())
            Thread.sleep(1500)
            if( FirebaseAuth.getInstance().currentUser != null) {
                val uidUser = FirebaseAuth.getInstance().currentUser!!.uid
                Firebase.database.getReference("users").child(uidUser).child("online")
                    .setValue(false)
            }
                startActivity(Intent(context, MainActivity::class.java))
                activity?.finish()
        }
    }

    private fun showUserDetails(chatUser: ChatUser, firebaseUser: FirebaseUser) {
        etName.setText(chatUser.displayName)
        chatUser.photoUrl?.let { _photoUrl ->
            if (_photoUrl.isNotEmpty()) {
                Picasso.get().load(_photoUrl)
                    .placeholder(R.drawable.ic_anon_user_48dp)
            }
        }
        etEmail.setText(firebaseUser.email)
        pageProgress.isVisible = false
    }

    private fun updateName() {
        if (TextUtils.isEmpty(etName.text)){
            etName.error = "Name can not be empty."
            return
        }
        etName.error = null

        val nameUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName(etName.text.toString())
            .build()

        FirebaseAuth.getInstance().currentUser?.updateProfile(nameUpdate)
            ?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateUserAtRealtimeDatabase()
            }
        }
    }
    private fun updateEmail() {
        etEmail.error = null
        etEmail.text?.let { email ->
            if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                FirebaseAuth.getInstance().currentUser?.updateEmail(etEmail.text.toString())
            } else {
                etEmail.error = "Email is not valid."
            }
        }
    }
    private fun chooseProfilePicture(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        profilePicChooser.launch(intent)
    }
    private fun updateUserAtRealtimeDatabase(photoUri: String = currentUser.photoUrl ?: "") {
        val user = ChatUser(
            displayName = etName.text.toString(),
            lastSeen = System.currentTimeMillis(),
            uid = FirebaseAuth.getInstance().currentUser!!.uid,
            photoUrl = photoUri)

        data.createOrUpdateUser(user)
    }


    private fun updateProfileImageOnStorage(usageId: String) {
        pageProgress.isVisible = true
        val ref: StorageReference = FirebaseStorage.getInstance().reference
            .child("profile_pics/${usageId}")

        profilePicUri?.let { profileUri ->
            ref.putFile(profileUri)
                .addOnCompleteListener{
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        updateFirebaseUserPicture(uri)
                        updateUserAtRealtimeDatabase(uri.toString())
                        pageProgress.isVisible = false
                    }
                }
                .addOnFailureListener{ e ->
                }
        }
    }
    private fun updateFirebaseUserPicture(uri: Uri) {
        val picUpdate = UserProfileChangeRequest.Builder()
            .setPhotoUri(uri)
            .build()

        FirebaseAuth.getInstance().currentUser?.updateProfile(picUpdate)
    }
}