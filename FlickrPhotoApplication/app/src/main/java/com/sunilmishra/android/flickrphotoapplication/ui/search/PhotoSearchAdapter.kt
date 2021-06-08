package com.sunilmishra.android.flickrphotoapplication.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sunilmishra.android.flickrphotoapplication.R
import com.sunilmishra.android.flickrphotoapplication.data.Photo
import com.sunilmishra.android.flickrphotoapplication.extensions.load
import kotlinx.android.synthetic.main.layout_photo_item.view.*

class PhotoSearchAdapter : PagedListAdapter<Photo, PhotoSearchAdapter.ViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_photo_item,parent,false))
    }

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bind( item: Photo?) {
            with(itemView) {
                item?.let {
                    photoImageView.load(it.getUrl())
                }
            }
        }
    }
}

private class DiffCallback : DiffUtil.ItemCallback<Photo>() {

    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }
}