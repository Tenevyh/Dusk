package com.tenevyh.android.dusk.ui.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.tenevyh.android.dusk.ui.utils.FetchChatUserListener
import com.tenevyh.android.dusk.ui.utils.mapFromFirebaseUser
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.tasks.await


class UserRepository{

    fun createOrUpdateUser(user: ChatUser) {
        val database = Firebase.database
        val userRef = database.getReference("users")
        val userNodeRef = userRef.child(user.uid)
        userNodeRef.setValue(user)
    }

    fun getCurrentUserByUid(firebaseUser: FirebaseUser, listener: FetchChatUserListener) {
        val database = Firebase.database
        val userDbRef = database.getReference("users")
        userDbRef.child(firebaseUser.uid).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue<ChatUser>()
                        ?: mapFromFirebaseUser(FirebaseAuth.getInstance().currentUser!!)
                    listener.onFetchUser(user)
                }

                override fun onCancelled(error: DatabaseError) {
                    val user = mapFromFirebaseUser(FirebaseAuth.getInstance().currentUser!!)
                    listener.onFetchUser(user)
                }
            }
        )
    }


      suspend fun getOnline(user: String): Boolean {
        var onlineUser: Boolean = false
          return try {
              val data = Firebase.database.getReference("users").child(user).get().await()
              onlineUser = data.children.elementAt(2).value.toString().toBoolean()
              onlineUser
          } catch (exception: Exception) {
              exception.printStackTrace()
              false
          }
    }

  /*  suspend fun getUserList(): ArrayList<String>{
        val userList = ArrayList<String>()
        return try {
            val data = Firebase.database.getReference("users").get().await()
            for (x in 0..data.childrenCount) {
                userList.add(data.children.elementAt(x.toInt()).key.toString())
            }
            userList
        } catch (exception: Exception) {
            exception.printStackTrace()
            userList
        }
    }

   */
}