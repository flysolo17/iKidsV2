package com.danica.ikidsv2.student.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.danica.ikidsv2.R
import com.danica.ikidsv2.models.Lessons

class LearnAdapter(private val context : Context,private val lessonsList : List<Lessons>) : RecyclerView.Adapter<LearnAdapter.LearnViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_learn,parent,false)
        return LearnViewHolder(view)
    }

    override fun onBindViewHolder(holder: LearnViewHolder, position: Int) {
        val lessons = lessonsList[position]

    }

    override fun getItemCount(): Int {
        return lessonsList.size
    }
    class LearnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val textClassname : TextView = itemView.findViewById(R.id.textClassName)
        val textClassTeacher : TextView = itemView.findViewById(R.id.textClassTeacher)
    }

}