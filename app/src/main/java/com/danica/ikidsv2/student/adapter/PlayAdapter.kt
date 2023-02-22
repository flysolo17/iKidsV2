package com.danica.ikidsv2.student.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.danica.ikidsv2.R
import com.danica.ikidsv2.models.Lessons
import com.danica.ikidsv2.models.Quiz

class PlayAdapter(val context: Context,private val lessonsList : List<Lessons>,val onPlayClickListener: OnPlayClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface OnPlayClickListener {
        fun onPlay(position: Int,lessons: Lessons)
    }
    private var LEFT = 0
    private var RIGHT = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == LEFT) {
            LeftViewHolder(
                LayoutInflater.from(context).inflate(R.layout.row_levels_left, parent, false)
            )
        } else {
            RightViewHolder(
                LayoutInflater.from(context).inflate(R.layout.row_levels_right, parent, false)
            )
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lesson = lessonsList[position]
        if (holder.itemViewType == LEFT) {
            val leftView = holder as LeftViewHolder
            leftView.textLevel.text = "${position + 1}"
            if (lesson.isOpen) {
                leftView.imageNotLocked.visibility = View.VISIBLE
                leftView.imageLocked.visibility = View.GONE
            } else {
                leftView.imageNotLocked.visibility = View.GONE
                leftView.imageLocked.visibility = View.VISIBLE
            }
            leftView.imageNotLocked.setOnClickListener {
                onPlayClickListener.onPlay(position,lesson)
            }
        } else {
            val rightView = holder as RightViewHolder
            rightView.textLevel.text = "${position + 1}"
            if (lesson.isOpen) {
                rightView.imageNotLocked.visibility = View.VISIBLE
                rightView.imageLocked.visibility = View.GONE
                rightView.textLevel.visibility = View.VISIBLE
            } else {
                rightView.imageNotLocked.visibility = View.GONE
                rightView.imageLocked.visibility = View.VISIBLE
                rightView.textLevel.visibility = View.GONE
            }
            rightView.imageNotLocked.setOnClickListener {
                onPlayClickListener.onPlay(position,lesson)
            }
        }

    }

    override fun getItemCount(): Int {
        return lessonsList.size
    }
    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            LEFT
        } else {
            RIGHT
        }
    }
    internal class LeftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageNotLocked: ImageView
        var imageLocked: ImageView
        var textLevel : TextView
        init {
            imageNotLocked = itemView.findViewById(R.id.imageNotLock)
            imageLocked = itemView.findViewById(R.id.imageLocked)
            textLevel = itemView.findViewById(R.id.textLevel)
        }
    }

    internal class RightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageNotLocked: ImageView
        var imageLocked: ImageView
        var textLevel : TextView
        init {
            imageNotLocked = itemView.findViewById(R.id.imageNotLock)
            imageLocked = itemView.findViewById(R.id.imageLocked)
            textLevel = itemView.findViewById(R.id.textLevel)
        }
    }
}