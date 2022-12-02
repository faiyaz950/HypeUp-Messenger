package com.example.hype_up


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class UserViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
    fun bind (user: User){
        itemView.titleTv.text = user.name
        Picasso.get().load(user.imageUrl).into(itemView.userImg)
    }
}