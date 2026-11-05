package com.example.uclostfound.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uclostfound.databinding.ItemRowBinding
import com.example.uclostfound.model.Item
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemAdapter(
    private val items: List<Item>,
    private val onClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.VH>() {

    class VH(val b: ItemRowBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val format = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        holder.b.tvName.text = item.name
        holder.b.tvStatus.text = item.status
        holder.b.tvDate.text = format.format(Date(item.dateReported))
        holder.b.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}