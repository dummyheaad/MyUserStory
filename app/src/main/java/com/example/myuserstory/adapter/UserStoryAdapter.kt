package com.example.myuserstory.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myuserstory.data.remote.response.ListStoryItem
import com.example.myuserstory.databinding.ItemUserStoryBinding
import com.example.myuserstory.ui.detailuserstory.DetailUserStoryActivity
import com.example.myuserstory.utils.withDateFormat

class UserStoryAdapter : PagingDataAdapter<ListStoryItem, UserStoryAdapter.UserStoryViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserStoryViewHolder {
        val binding = ItemUserStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserStoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserStoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class UserStoryViewHolder(private val binding: ItemUserStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(data.photoUrl)
                .into(binding.ivItemPhoto)

            binding.tvItemName.text = data.name
            binding.tvItemDescription.text = data.description
            binding.tvItemCreated.text = data.createdAt.withDateFormat()

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailUserStoryActivity::class.java)
                intent.putExtra(DetailUserStoryActivity.ID, data.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}