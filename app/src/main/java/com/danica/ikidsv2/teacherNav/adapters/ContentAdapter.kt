package com.danica.ikidsv2.teacherNav.adapters

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.danica.ikidsv2.R
import com.danica.ikidsv2.models.Content
import java.util.*

class ContentAdapter(val context: Context,val contentList : List<Content>,val contentClickListener: ContentClickListener) : RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    interface ContentClickListener {
        fun deleteContent(position: Int)
        fun editContent(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
       val view : View = LayoutInflater.from(context).inflate(R.layout.row_content,parent,false)
        return  ContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val content = contentList[position]
        if (!content.image.isNullOrEmpty()) {
            Glide.with(context).load(content.image).into(holder.imageContent)
        }
        if (content.description?.size == 3) {
            holder.textInIlocano.text = content.description[0]
            holder.textInTagalog.text = content.description[1]
            holder.textInEnglish.text = content.description[2]
        }
        holder.buttonDelete.setOnClickListener {
            contentClickListener.deleteContent(position)
        }
        holder.buttonEdit.setOnClickListener {
            contentClickListener.editContent(position)
        }
        holder.textInIlocano.setOnClickListener {
            holder.speak(content.description!![0])
        }
        holder.textInTagalog.setOnClickListener {
            holder.speak(content.description!![1])
        }
        holder.textInEnglish.setOnClickListener {
            holder.speak(content.description!![2])
        }
    }

    override fun getItemCount(): Int {
        return  contentList.size
    }
    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) ,TextToSpeech.OnInitListener {
        val textToSpeech = TextToSpeech(itemView.context,this)
        val imageContent : ImageView = itemView.findViewById(R.id.imageContent)
        var textInIlocano : TextView  = itemView.findViewById(R.id.textInIlocano)
        val textInTagalog : TextView = itemView.findViewById(R.id.textInTagalog)
        val textInEnglish : TextView = itemView.findViewById(R.id.textInEnglish)
        val buttonDelete : ImageButton  = itemView.findViewById(R.id.buttonDelete)
        val buttonEdit : ImageButton = itemView.findViewById(R.id.buttonEdit)
        override fun onInit(status: Int) {
            if (status ==TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(itemView.context,"Language not supported",Toast.LENGTH_SHORT).show()
                }
            }
        }
        fun speak(word : String) {
            textToSpeech.speak(word,TextToSpeech.QUEUE_FLUSH,null,"")
        }
    }
}