package com.example.uclostfound.data

import com.example.uclostfound.model.Claim
import com.example.uclostfound.model.Item

object SampleData {
    val items = mutableListOf(
        Item("1", "LOST",  "Blue Wallet",    "Bag",    "Leather, two cards", "Canteen",  1729814400000, "LOST",          "u1", "Ana"),
        Item("2", "FOUND", "Black Umbrella", "Other",  "Long handle",        "Library",  1729900800000, "FOUND",         "u2", "Ben", "Security Office"),
        Item("3", "FOUND", "Calculator",     "Gadget", "Casio fx-991",       "Room 301", 1729728000000, "PENDING_CLAIM", "u3", "Cara", "Security Office"),
        Item("4", "FOUND", "ID Lace",        "ID",     "UC lanyard",         "Gate 2",   1729641600000, "CLAIMED",       "u2", "Ben", "Security Office")
    )

    val claims = mutableListOf(
        Claim(
            id = "c1",
            itemId = "3",
            claimantId = "u4",
            claimantName = "Juan Dela Cruz",
            proofDescription = "Scratch on the back and my name written inside the cover",
            status = "PENDING",
            rejectReason = "",
            dateSubmitted = 1729800000000
        )
    )

    fun updateItemStatus(itemId: String, status: String) {
        val i = items.indexOfFirst { it.id == itemId }
        if (i >= 0) items[i] = items[i].copy(status = status)
    }

    fun updateItem(updated: Item) {
        val i = items.indexOfFirst { it.id == updated.id }
        if (i >= 0) items[i] = updated
    }

    fun updateClaim(updated: Claim) {
        val i = claims.indexOfFirst { it.id == updated.id }
        if (i >= 0) claims[i] = updated
    }
}