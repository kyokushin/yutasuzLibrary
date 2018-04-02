package com.yutasuz.examples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.yutasuz.library.ui.widget.MaskImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        recycler_view.layoutManager = LinearLayoutManager(this,
//                LinearLayoutManager.VERTICAL, false)
        recycler_view.layoutManager = GridLayoutManager(this, 4)
        recycler_view.adapter = Adapter()

    }

    class ViewHolder(parent: ViewGroup)
        : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_mask_imageview, parent, false)) {
        val maskImageView = itemView.findViewById(R.id.mask_image_view) as MaskImageView
    }

    class Adapter : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent)
        }

        override fun getItemCount(): Int {
            return 500
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Picasso.get()
                    .load(R.mipmap.sample_photo)
                    .noFade()
                    .noPlaceholder()
                    .into(holder.maskImageView)
        }

    }
}
