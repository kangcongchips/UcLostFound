package com.example.uclostfound

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uclostfound.data.SampleData
import com.example.uclostfound.databinding.ActivityItemDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("itemId")
        val item = SampleData.items.find { it.id == id }

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
            binding.tvHolding.text = "Kept at: ${item.holdingLocation}"
        } else {
            binding.tvHolding.visibility = View.GONE
        }

        // BR-01: only FOUND items can be claimed
        if (item.status == "FOUND") {
            binding.btnClaim.visibility = View.VISIBLE
        } else {
            binding.btnClaim.visibility = View.GONE
        }

        binding.btnClaim.setOnClickListener {
            Toast.makeText(this, "Claim form comes next", Toast.LENGTH_SHORT).show()
        }
    }
}