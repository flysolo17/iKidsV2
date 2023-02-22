package com.danica.ikidsv2.teacherNav.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.danica.ikidsv2.R
import com.danica.ikidsv2.models.Classes
import com.danica.ikidsv2.utils.dateFormat2

class ClassesAdapter(val context : Context, private val classList : List<Classes>, val classesClickListener: ClassesClickListener) : RecyclerView.Adapter<ClassesAdapter.ClassesViewHolder>() {

    interface ClassesClickListener {
        fun deleteClass(classID : String)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassesViewHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.row_classes,parent,false)
        return ClassesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassesViewHolder, position: Int) {
        val  classes = classList[position]
        holder.textClassName.text = classes.name
        holder.textClassCode.text = classes.code
        holder.textCreatedAt.text = dateFormat2(classes.createdAt!!)
        holder.buttonDelete.setOnClickListener {
            classesClickListener.deleteClass(classes.id!!)
        }
    }

    override fun getItemCount(): Int {
        return classList.size
    }
    class ClassesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textClassName = itemView.findViewById<TextView>(R.id.textClassName)
        val textClassCode = itemView.findViewById<TextView>(R.id.textClassCode)
        val textCreatedAt = itemView.findViewById<TextView>(R.id.textCreatedAt)
        val textStudents = itemView.findViewById<TextView>(R.id.textStudentCount)
        val buttonDelete = itemView.findViewById<TextView>(R.id.buttonDelete)
    }
}