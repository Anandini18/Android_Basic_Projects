package com.nandini.android.fragmentstab

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CustomAdapter(var context: Context, var imageList: ArrayList<String>):RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {

        var view= LayoutInflater.from(context).inflate(R.layout.item_view_photo,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        var image: String = imageList[position]

        Glide.with(context).load(image).into(holder.img_itm)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val img_itm: ImageView = itemView.findViewById(R.id.img_itm)
    }

}