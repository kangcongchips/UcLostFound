package com.example.uclostfound

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uclostfound.adapter.ItemAdapter
import com.example.uclostfound.data.SampleData
import com.example.uclostfound.data.Session
import com.example.uclostfound.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ItemAdapter(SampleData.items) { item ->
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra("itemId", item.id)
            startActivity(intent)
        }
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.adapter = adapter

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, ReportItemActivity::class.java))
        }

        binding.btnMyActivity.setOnClickListener {
            startActivity(Intent(this, MyActivityActivity::class.java))
        }

        // Role-based UI: only Staff sees the Claim Review button
        binding.btnClaimReview.visibility = if (Session.isStaff()) View.VISIBLE else View.GONE
        binding.btnClaimReview.setOnClickListener {
            startActivity(Intent(this, ClaimReviewActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            Session.currentUser = null
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}