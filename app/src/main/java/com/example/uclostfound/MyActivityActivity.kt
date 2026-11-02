package com.example.uclostfound

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uclostfound.adapter.ClaimAdapter
import com.example.uclostfound.adapter.ItemAdapter
import com.example.uclostfound.data.SampleData
import com.example.uclostfound.data.Session
import com.example.uclostfound.databinding.ActivityMyActivityBinding
import com.google.android.material.tabs.TabLayout

class MyActivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvList.layoutManager = LinearLayoutManager(this)

        binding.tabs.addTab(binding.tabs.newTab().setText("My Reports"))
        binding.tabs.addTab(binding.tabs.newTab().setText("My Claims"))
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                show(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        show(binding.tabs.selectedTabPosition.coerceAtLeast(0))
    }

    private fun show(position: Int) {
        val uid = Session.currentUser?.uid

        if (position == 0) {
            val mine = SampleData.items.filter { it.reporterId == uid }
            binding.tvEmpty.text = "You have not reported any items yet"
            binding.tvEmpty.visibility = if (mine.isEmpty()) View.VISIBLE else View.GONE
            binding.rvList.adapter = ItemAdapter(mine) { item ->
                val intent = Intent(this, ItemDetailActivity::class.java)
                intent.putExtra("itemId", item.id)
                startActivity(intent)
            }
        } else {
            val mine = SampleData.claims.filter { it.claimantId == uid }
            binding.tvEmpty.text = "You have not submitted any claims yet"
            binding.tvEmpty.visibility = if (mine.isEmpty()) View.VISIBLE else View.GONE
            binding.rvList.adapter = ClaimAdapter(mine, false)
        }
    }
}