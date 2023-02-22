package com.danica.ikidsv2.teacherNav.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.models.Classes
import com.danica.ikidsv2.models.Leaderboard
import com.danica.ikidsv2.models.User
import com.danica.ikidsv2.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class MyStudentAdapter(val context : Context,val leaderboardList : List<Leaderboard>) : RecyclerView.Adapter<MyStudentAdapter.MyStudentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStudentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_student,parent,false)
        return MyStudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyStudentViewHolder, position: Int) {
        val classes = leaderboardList[position]
        holder.displayStudent(classes.studentID)
        holder.textStudentScore.text = classes.score.toString()
    }

    override fun getItemCount(): Int {
        return leaderboardList.size
    }
    class MyStudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textStudentName : TextView = itemView.findViewById(R.id.textStudentName)
        val imageStudentAvatar : CircleImageView = itemView.findViewById(R.id.imageStudentAvatar)
        val textStudentScore : TextView = itemView.findViewById(R.id.textScore)
        val firestore = FirebaseFirestore.getInstance()
        fun displayStudent(id : String) {
            firestore.collection(Constants.USER_TABLE)
                .document(id)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val user : User? = it.toObject(User::class.java)
                        user?.let { student ->
                            textStudentName.text = student.name
                            if (!student.avatar.isNullOrEmpty()) {
                                Glide.with(itemView.context).load(student.avatar).into(imageStudentAvatar)
                            }
                        }
                    }
                }
        }

    }
}