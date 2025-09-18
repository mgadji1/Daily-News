package com.example.dailynews.imageloader

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.dailynews.R

class GlideImageLoader(
    private val context : Context
) {
    fun loadImage(imageUrl : String, imageView : ImageView) {
        Glide.with(context)
            .load(imageUrl)
            .centerCrop()
            .placeholder(R.drawable.no_url)
            .error(R.drawable.no_url)
            .into(imageView)
    }
}