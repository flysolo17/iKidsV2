package com.danica.ikidsv2.student.adapter

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.models.Content
import java.util.*

class StudentContentAdapter(val context: Context, private val contentList: List<Content>,val language : Int) : RecyclerView.Adapter<StudentContentAdapter.StudentContentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentContentViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.row_student_content,parent,false)
        return StudentContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentContentViewHolder, position: Int) {
        val content = contentList[position]
        if (!content.image.isNullOrEmpty())  {
            Glide.with(context).load(content.image).into(holder.image)
        }
        val word = content.description?.get(language) ?: "Unidentified"
        holder.textContent.text = word
        holder.textContent.setOnClickListener {
            holder.speak(word)
        }
    }

    override fun getItemCount(): Int {
        return contentList.size
    }
    class StudentContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        TextToSpeech.OnInitListener {
        val textToSpeech = TextToSpeech(itemView.context,this)
        val image : ImageView = itemView.findViewById(R.id.imageContent)
        val textContent : TextView = itemView.findViewById(R.id.textContentDesc)
        override fun onInit(status: Int) {
            if (status ==TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(itemView.context,"Language not supported", Toast.LENGTH_SHORT).show()
                }
            }
        }
        fun speak(word : String) {
            textToSpeech.speak(word,TextToSpeech.QUEUE_FLUSH,null,"")
        }

    }


}