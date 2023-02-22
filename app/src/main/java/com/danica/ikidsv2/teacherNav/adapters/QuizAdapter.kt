package com.danica.ikidsv2.teacherNav.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.models.Quiz

class QuizAdapter(val context : Context,val quizList : List<Quiz>) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_quiz,parent,false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz : Quiz = quizList[position]
        if (!quiz.image.isNullOrEmpty()) {
            Glide.with(context).load(quiz.image).into(holder.imageQuiz)
        }
        holder.textQuestion.text = quiz.question
        holder.textAnswer.text = quiz.answer

    }

    override fun getItemCount(): Int {
        return quizList.size
    }
    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageQuiz : ImageView = itemView.findViewById(R.id.imageQuiz)
        val textQuestion : TextView = itemView.findViewById(R.id.textQuestion)
        val textAnswer : TextView = itemView.findViewById(R.id.textAnswer)

    }


}