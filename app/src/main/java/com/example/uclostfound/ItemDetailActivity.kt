package com.example.uclostfound

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.uclostfound.data.SampleData
import com.example.uclostfound.data.Session
import com.example.uclostfound.databinding.ActivityItemDetailBinding
import com.example.uclostfound.model.Claim
import com.example.uclostfound.model.Item
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemDetailBinding
    private var itemId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemId = intent.getStringExtra("itemId")

        binding.btnClaim.setOnClickListener {
            val item = SampleData.items.find { it.id == itemId }
            if (item != null) showClaimDialog(item)
        }
        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, ReportItemActivity::class.java)
            intent.putExtra("itemId", itemId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        showItem()
    }

    private fun showItem() {
        val item = SampleData.items.find { it.id == itemId }

        if (item == null) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val format = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        binding.tvName.text = item.name
        binding.tvStatus.text = item.status
        binding.tvCategory.text = "Category: ${item.category}"
        binding.tvLocation.text = "Location: ${item.location}"
        binding.tvDate.text = "Date: ${format.format(Date(item.dateReported))}"
        binding.tvReporter.text = "Reported by: ${item.reporterName}"
        binding.tvDescription.text = item.description

        if (item.holdingLocation.isNotEmpty()) {
            binding.tvHolding.visibility = View.VISIBLE
            binding.tvHolding.text = "Kept at: ${item.holdingLocation}"
        } else {
            binding.tvHolding.visibility = View.GONE
        }

        // BR-01: only FOUND items can be claimed
        // BR-05: you cannot claim an item you reported yourself
        val canClaim = item.status == "FOUND" &&
                item.reporterId != Session.currentUser?.uid
        binding.btnClaim.visibility = if (canClaim) View.VISIBLE else View.GONE
        // BR-07: only the owner or Staff can edit, only while LOST or FOUND
        binding.btnEdit.visibility = if (Session.canEdit(item)) View.VISIBLE else View.GONE
    }

    private fun showClaimDialog(item: Item) {
        val input = EditText(this)
        input.hint = "Unique marks, contents, or lock screen"
        input.minLines = 3
        input.setPadding(48, 24, 48, 24)

        AlertDialog.Builder(this)
            .setTitle("Claim ${item.name}")
            .setMessage("Describe something that proves this item is yours.")
            .setView(input)
            .setPositiveButton("Submit") { _, _ ->
                val proof = input.text.toString().trim()
                if (proof.length < 15) {
                    Toast.makeText(this, "Proof must be at least 15 characters", Toast.LENGTH_LONG).show()
                } else {
                    submitClaim(item, proof)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun submitClaim(item: Item, proof: String) {
        // BR-02: re-check the status right before saving
        val current = SampleData.items.find { it.id == item.id }
        if (current == null || current.status != "FOUND") {
            Toast.makeText(this, "This item can no longer be claimed", Toast.LENGTH_LONG).show()
            showItem()
            return
        }

        val user = Session.currentUser
        val claim = Claim(
            id = System.currentTimeMillis().toString(),
            itemId = item.id,
            claimantId = user?.uid ?: "",
            claimantName = user?.fullName ?: "Unknown",
            proofDescription = proof,
            status = "PENDING",
            rejectReason = "",
            dateSubmitted = System.currentTimeMillis()
        )

        SampleData.claims.add(claim)
        SampleData.updateItemStatus(item.id, "PENDING_CLAIM")
        Toast.makeText(this, "Claim submitted. Waiting for Staff review", Toast.LENGTH_LONG).show()
        showItem()
    }
}