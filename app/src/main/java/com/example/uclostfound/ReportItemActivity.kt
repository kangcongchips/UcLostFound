package com.example.uclostfound

import com.example.uclostfound.data.Session
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.uclostfound.data.SampleData
import com.example.uclostfound.databinding.ActivityReportItemBinding
import com.example.uclostfound.model.Item
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReportItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportItemBinding
    private var selectedDate: Long = System.currentTimeMillis()
    private val format = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categories = listOf("ID", "Bag", "Gadget", "Clothing", "Other")
        binding.spCategory.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, categories
        )

        updateDateButton()
        binding.btnDate.setOnClickListener { showDatePicker() }

        // The "Kept at" field is only needed for Found reports
        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            binding.tilHolding.visibility =
                if (checkedId == R.id.rbFound) View.VISIBLE else View.GONE
        }

        binding.btnSubmit.setOnClickListener { submit() }
    }

    private fun updateDateButton() {
        binding.btnDate.text = "Date: ${format.format(Date(selectedDate))}"
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        cal.timeInMillis = selectedDate
        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val picked = Calendar.getInstance()
                picked.set(year, month, day)
                selectedDate = picked.timeInMillis
                updateDateButton()
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        // Validation: the date cannot be in the future
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun submit() {
        val isFound = binding.rbFound.isChecked
        val name = binding.etName.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val holding = binding.etHolding.text.toString().trim()

        // Clear old errors
        binding.tilName.error = null
        binding.tilLocation.error = null
        binding.tilDescription.error = null
        binding.tilHolding.error = null

        var valid = true

        if (name.length < 3 || name.length > 50) {
            binding.tilName.error = "Name must be 3-50 characters"
            valid = false
        }
        if (location.isEmpty()) {
            binding.tilLocation.error = "Location is required"
            valid = false
        }
        if (description.length < 10) {
            binding.tilDescription.error = "Description must be at least 10 characters"
            valid = false
        }
        if (isFound && holding.isEmpty()) {
            binding.tilHolding.error = "Tell us where the item is being kept"
            valid = false
        }
        if (selectedDate > System.currentTimeMillis()) {
            binding.btnDate.text = "Date cannot be in the future"
            valid = false
        }

        if (!valid) return

        val type = if (isFound) "FOUND" else "LOST"
        val item = Item(
            id = System.currentTimeMillis().toString(),
            type = type,
            name = name,
            category = binding.spCategory.selectedItem.toString(),
            description = description,
            location = location,
            dateReported = selectedDate,
            status = type,
            reporterId = Session.currentUser?.uid ?: "",
            reporterName = Session.currentUser?.fullName ?: "Unknown",
            holdingLocation = if (isFound) holding else ""
        )

        SampleData.items.add(0, item)
        finish()
    }
}