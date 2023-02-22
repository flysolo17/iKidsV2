package com.danica.ikidsv2.teacherNav.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.models.Lessons
import com.danica.ikidsv2.utils.dateFormat
import com.google.android.material.imageview.ShapeableImageView

class LessonsAdapter(val context : Context, private val lessonsList : List<Lessons>,val lessonAdapterClicked: LessonAdapterClicked) : RecyclerView.Adapter<LessonsAdapter.LessonsViewHolder>() {
    interface LessonAdapterClicked {
        fun onLessonClicked(lesson : Lessons)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonsViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.row_lessons,parent,false)
        return LessonsViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonsViewHolder, position: Int) {
        val lesson = lessonsList[position]
        holder.textTitle.text =lesson.title
        holder.textDesc.text = lesson.description
        holder.itemView.setOnClickListener {
            lessonAdapterClicked.onLessonClicked(lesson)
        }
        if (lesson.image!!.isNotEmpty()) {
            Glide.with(context).load(lesson.image).into(holder.imageLessons)
        }
        if (lesson.isOpen) {
            holder.imageAvailability.background.setTint(Color.GREEN)
        } else {
            holder.imageAvailability.background.setTint(Color.LTGRAY)
        }
        holder.textCreatedAt.text = dateFormat(lesson.createdAt!!)
    }

    override fun getItemCount(): Int {
        return lessonsList.size
    }
    class LessonsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textTitle : TextView = itemView.findViewById(R.id.textTitle)
        val textDesc : TextView = itemView.findViewById(R.id.textDesc)
        val textCreatedAt : TextView = itemView.findViewById(R.id.textDate)
        val imageLessons : ShapeableImageView = itemView.findViewById(R.id.imageLesson)
        val imageAvailability : ImageView = itemView.findViewById(R.id.imageIsAvailable)
    }


}