package com.example.uclostfound

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uclostfound.adapter.ClaimAdapter
import com.example.uclostfound.data.SampleData
import com.example.uclostfound.data.Session
import com.example.uclostfound.databinding.ActivityClaimReviewBinding
import com.example.uclostfound.model.Claim

class ClaimReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClaimReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClaimReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BR-03: only Staff may review claims
        if (!Session.isStaff()) {
            Toast.makeText(this, "Staff only", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.rvClaims.layoutManager = LinearLayoutManager(this)
        refresh()
    }

    private fun refresh() {
        val pending = SampleData.claims.filter { it.status == "PENDING" }
        binding.tvEmpty.visibility = if (pending.isEmpty()) View.VISIBLE else View.GONE
        binding.rvClaims.adapter = ClaimAdapter(
            pending, true,
            { claim -> approve(claim) },
            { claim -> askReason(claim) }
        )
    }

    private fun approve(claim: Claim) {
        SampleData.updateClaim(claim.copy(status = "APPROVED"))
        SampleData.updateItemStatus(claim.itemId, "CLAIMED")
        Toast.makeText(this, "Claim approved. Item is now CLAIMED", Toast.LENGTH_SHORT).show()
        refresh()
    }

    private fun askReason(claim: Claim) {
        val input = EditText(this)
        input.hint = "Reason for rejection"
        input.setPadding(48, 24, 48, 24)

        AlertDialog.Builder(this)
            .setTitle("Reject claim")
            .setView(input)
            .setPositiveButton("Reject") { _, _ ->
                val reason = input.text.toString().trim()
                if (reason.length < 5) {
                    Toast.makeText(this, "Please give a reason (at least 5 characters)", Toast.LENGTH_LONG).show()
                } else {
                    reject(claim, reason)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // BR-04: a rejected claim returns the item to FOUND and saves the reason
    private fun reject(claim: Claim, reason: String) {
        SampleData.updateClaim(claim.copy(status = "REJECTED", rejectReason = reason))
        SampleData.updateItemStatus(claim.itemId, "FOUND")
        Toast.makeText(this, "Claim rejected. Item is FOUND again", Toast.LENGTH_SHORT).show()
        refresh()
    }
}