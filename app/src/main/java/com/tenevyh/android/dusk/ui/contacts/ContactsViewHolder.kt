package com.tenevyh.android.dusk.ui.contacts

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.tenevyh.android.dusk.R
import com.tenevyh.android.dusk.ui.repository.ChatUser
import kotlinx.android.synthetic.main.item_view_contacts.view.*

class ContactsViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val name: TextView = itemView.findViewById(R.id.contactTextView)
    private val pic: ImageView = itemView.findViewById(R.id.ivUserImage)

    fun bind(user: ChatUser) {
        if (FirebaseAuth.getInstance().currentUser == null) {
        } else {
            if (FirebaseAuth.getInstance().currentUser!!.uid == user.uid){
                name.text = "Личный дневник"
                pic.setImageResource(R.drawable.self)
            } else {
                name.text = user.displayName
                user.photoUrl?.let { _photoUrl ->
                    if (_photoUrl.isNotEmpty()) {
                        Picasso.get().load(_photoUrl)
                            .placeholder(R.drawable.ic_anon_user_48dp)
                            .into(pic)
                    }
                }
            }
        }
    }
}