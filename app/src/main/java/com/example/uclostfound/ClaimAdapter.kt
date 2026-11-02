package com.example.uclostfound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uclostfound.data.SampleData
import com.example.uclostfound.databinding.ItemClaimBinding
import com.example.uclostfound.model.Claim

class ClaimAdapter(
    private val claims: List<Claim>,
    private val showActions: Boolean,
    private val onApprove: (Claim) -> Unit = {},
    private val onReject: (Claim) -> Unit = {}
) : RecyclerView.Adapter<ClaimAdapter.VH>() {

    class VH(val b: ItemClaimBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemClaimBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val claim = claims[position]
        val itemName = SampleData.items.find { it.id == claim.itemId }?.name ?: "Unknown item"

        holder.b.tvItemName.text = itemName
        holder.b.tvClaimant.text = "Claimant: ${claim.claimantName}"
        holder.b.tvProof.text = "Proof: ${claim.proofDescription}"
        holder.b.tvClaimStatus.text = "Claim status: ${claim.status}"

        if (claim.rejectReason.isNotEmpty()) {
            holder.b.tvReason.visibility = View.VISIBLE
            holder.b.tvReason.text = "Reason: ${claim.rejectReason}"
        } else {
            holder.b.tvReason.visibility = View.GONE
        }

        holder.b.llActions.visibility = if (showActions) View.VISIBLE else View.GONE
        holder.b.btnApprove.setOnClickListener { onApprove(claim) }
        holder.b.btnReject.setOnClickListener { onReject(claim) }
    }

    override fun getItemCount() = claims.size
}