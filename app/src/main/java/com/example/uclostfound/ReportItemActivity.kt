package com.example.uclostfound

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uclostfound.data.SampleData
import com.example.uclostfound.data.Session
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
    private val categories = listOf("ID", "Bag", "Gadget", "Clothing", "Other")
    private var editingItem: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spCategory.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, categories
        )

        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            binding.tilHolding.visibility =
                if (checkedId == R.id.rbFound) View.VISIBLE else View.GONE
        }

        // Edit mode: an itemId was passed in
        val editId = intent.getStringExtra("itemId")
        if (editId != null) {
            val item = SampleData.items.find { it.id == editId }
            if (item == null || !Session.canEdit(item)) {
                Toast.makeText(this, "You cannot edit this report", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            editingItem = item
            fillForm(item)
        }

        updateDateButton()
        binding.btnDate.setOnClickListener { showDatePicker() }
        binding.btnSubmit.setOnClickListener { submit() }
    }

    private fun fillForm(item: Item) {
        title = "Edit Report"
        binding.btnSubmit.text = "Save changes"

        // The buttons show the current status
        if (item.status == "FOUND") {
            binding.rbFound.isChecked = true
        } else {
            binding.rbLost.isChecked = true
        }

        // Only Staff can change a Lost report to Found (or back).
        // Reports that were originally posted as Found stay locked.
        val canChangeStatus = Session.isStaff() && item.type == "LOST"
        binding.rbLost.isEnabled = canChangeStatus
        binding.rbFound.isEnabled = canChangeStatus

        binding.etName.setText(item.name)
        binding.spCategory.setSelection(categories.indexOf(item.category).coerceAtLeast(0))
        binding.etLocation.setText(item.location)
        binding.etDescription.setText(item.description)
        binding.etHolding.setText(item.holdingLocation)
        binding.tilHolding.visibility = if (item.status == "FOUND") View.VISIBLE else View.GONE
        selectedDate = item.dateReported
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

        val category = binding.spCategory.selectedItem.toString()

        // EDIT: update the existing record
        val existing = editingItem
        if (existing != null) {
            // Re-check: the item may have changed while the form was open
            val latest = SampleData.items.find { it.id == existing.id }
            if (latest == null || !Session.canEdit(latest)) {
                Toast.makeText(this, "This report can no longer be edited", Toast.LENGTH_LONG).show()
                finish()
                return
            }

            // Only Staff can change the status, and only on reports originally posted as Lost
            val newStatus =
                if (Session.isStaff() && latest.type == "LOST") {
                    if (isFound) "FOUND" else "LOST"
                } else {
                    latest.status
                }

            SampleData.updateItem(
                latest.copy(
                    name = name,
                    category = category,
                    description = description,
                    location = location,
                    dateReported = selectedDate,
                    status = newStatus,
                    holdingLocation = if (newStatus == "FOUND") holding else ""
                )
            )

            val message =
                if (newStatus != latest.status) "Item marked as $newStatus" else "Report updated"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // CREATE: a new report
        val type = if (isFound) "FOUND" else "LOST"
        val item = Item(
            id = System.currentTimeMillis().toString(),
            type = type,
            name = name,
            category = category,
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