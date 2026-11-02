package com.example.uclostfound.data

import com.example.uclostfound.model.Item

object SampleData {
    val items = mutableListOf(
        Item("1", "LOST",  "Blue Wallet",    "Bag",    "Leather, two cards", "Canteen",  1729814400000, "LOST",          "u1", "Ana"),
        Item("2", "FOUND", "Black Umbrella", "Other",  "Long handle",        "Library",  1729900800000, "FOUND",         "u2", "Ben", "Security Office"),
        Item("3", "FOUND", "Calculator",     "Gadget", "Casio fx-991",       "Room 301", 1729728000000, "PENDING_CLAIM", "u3", "Cara", "Security Office"),
        Item("4", "FOUND", "ID Lace",        "ID",     "UC lanyard",         "Gate 2",   1729641600000, "CLAIMED",       "u2", "Ben", "Security Office")
    )
}