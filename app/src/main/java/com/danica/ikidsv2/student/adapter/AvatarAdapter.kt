package com.danica.ikidsv2.student.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import de.hdodenhof.circleimageview.CircleImageView

class AvatarAdapter(val context: Context,private val imageList : List<String>,val currentAvatar : String,val avatarClickListener: AvatarClickListener) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    interface AvatarClickListener {
        fun onAvatarClicked(url : String)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_avatars,parent,false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val uri = imageList[position]
        if (uri == currentAvatar) {
            holder.imageAvatar.borderWidth = 5
            holder.imageAvatar.borderColor = Color.GRAY
        }
        Glide.with(context).load(uri).into(holder.imageAvatar)
        holder.itemView.setOnClickListener {
            avatarClickListener.onAvatarClicked(uri)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
    class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageAvatar : CircleImageView = itemView.findViewById(R.id.imageAvatars)
    }
}