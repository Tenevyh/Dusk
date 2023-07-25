package com.tenevyh.android.dusk.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.tenevyh.android.dusk.R


class ChatScreenAdapter(
    private val options: FirebaseRecyclerOptions<ChatModel>,
    private val onItemClickListener: OnItemClickListener?
    ) : FirebaseRecyclerAdapter<ChatModel, ChatViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val holder = ChatViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_chat, parent, false))

        holder.itemView.setOnClickListener{
            onItemClickListener?.onItemClick(options.snapshots[holder.adapterPosition])
        }
        return holder
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: ChatModel) {
        holder.bind(model)
    }

    override fun onDataChanged() {
    }

    interface OnItemClickListener {
        fun onItemClick(chatWith: ChatModel)
    }
}