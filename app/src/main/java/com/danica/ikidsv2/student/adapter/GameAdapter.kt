package com.danica.ikidsv2.student.adapter

import android.content.Context
import android.graphics.Color
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.danica.ikidsv2.R
import com.danica.ikidsv2.models.Choices
import com.google.android.material.card.MaterialCardView

class GameAdapter(val context : Context, private val choicesList : List<Choices>, private val gameClickListener: GameClickListener) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {
    interface GameClickListener {
        fun onChoiceClicked(choice : String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.row_choices,parent,false)
        return  GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.textChoice.text = choicesList[position].choice
        holder.cardChoice.setOnClickListener {
            holder.cardChoice.visibility = View.GONE
            gameClickListener.onChoiceClicked(choicesList[position].choice)
        }

    }

    override fun getItemCount(): Int {
        return choicesList.size
    }
    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardChoice : MaterialCardView = itemView.findViewById(R.id.cardChoice)
        val textChoice : TextView = itemView.findViewById(R.id.textChoice)
    }
}