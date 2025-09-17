package com.example.dailynews.imageloader

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

class GlideImageLoader(
    private val context : Context
) {
    fun loadImage(imageUrl : String, imageView : ImageView) {
        Glide.with(context).load(imageUrl).centerCrop().into(imageView)
    }
}