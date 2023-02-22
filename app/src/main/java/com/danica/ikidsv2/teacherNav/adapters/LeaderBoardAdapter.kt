package com.danica.ikidsv2.teacherNav.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.models.TopScores
import com.danica.ikidsv2.models.User
import com.danica.ikidsv2.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class LeaderBoardAdapter(val context: Context,val topScores: List<TopScores>) : RecyclerView.Adapter<LeaderBoardAdapter.LeaderboardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_student,parent,false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val score = topScores[position]
        holder.textStudentName.text = score.name
        if (!score.image.isNullOrEmpty()) {
            Glide.with(context).load(score.image).into(holder.imageStudentAvatar)
        }
        holder.textStudentScore.text = score.score.toString()
    }

    override fun getItemCount(): Int {
        return topScores.size
    }
    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textStudentName : TextView = itemView.findViewById(R.id.textStudentName)
        val imageStudentAvatar : CircleImageView = itemView.findViewById(R.id.imageStudentAvatar)
        val textStudentScore : TextView = itemView.findViewById(R.id.textScore)

    }

}