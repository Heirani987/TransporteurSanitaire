package com.transporteursanitaire.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.transporteursanitaire.databinding.ItemHomeIconBinding

class HomeIconAdapter(
    private val items: List<HomeItem>,
    private val onItemClick: (HomeItem) -> Unit
) : RecyclerView.Adapter<HomeIconAdapter.HomeIconViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeIconViewHolder {
        val binding = ItemHomeIconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeIconViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeIconViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class HomeIconViewHolder(private val binding: ItemHomeIconBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem) {
            binding.tvIconTitle.text = item.title
            binding.ivIcon.setImageResource(item.iconRes)
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}