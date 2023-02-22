package com.danica.ikidsv2.teacherNav.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.danica.ikidsv2.R
import com.danica.ikidsv2.models.Classes

class Classes2Adapter(val context : Context, private val classList : List<Classes>,val classes2AdapterClickListener: Classes2AdapterClickListener) : RecyclerView.Adapter<Classes2Adapter.Classes2ViewHolder>(){

    interface Classes2AdapterClickListener {
        fun isCheck(id: String,data : Boolean)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Classes2ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_classes_2,parent,false)
        return Classes2ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Classes2ViewHolder, position: Int) {
        val  classes = classList[position]
        holder.textClassName.text = classes.name
        holder.textClassCode.text = classes.code
        holder.checkBox.setOnClickListener {
            classes2AdapterClickListener.isCheck(classes.id!!,holder.checkBox.isChecked)
        }
    }

    override fun getItemCount(): Int {
        return  classList.size
    }
    class Classes2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textClassName = itemView.findViewById<TextView>(R.id.textClassName)
        val textClassCode = itemView.findViewById<TextView>(R.id.textClassCode)
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)
    }

}