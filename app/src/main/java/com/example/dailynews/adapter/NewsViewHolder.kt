package com.example.dailynews.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailynews.R

class NewsViewHolder(private val containerView : View) : RecyclerView.ViewHolder(containerView) {
    val imageView = containerView.findViewById<ImageView>(R.id.imageView)
    val tvTitle = containerView.findViewById<TextView>(R.id.tvTitle)
    val tvContent = containerView.findViewById<TextView>(R.id.tvContent)
}