package com.example.uclostfound.data

import com.example.uclostfound.model.Item
import com.example.uclostfound.model.User

object Session {
    var currentUser: User? = null

    fun isStaff() = currentUser?.role == "STAFF"

    // Owner or Staff can edit, but only while the item is LOST or FOUND
    fun canEdit(item: Item): Boolean {
        val editable = item.status == "LOST" || item.status == "FOUND"
        val allowed = isStaff() || item.reporterId == currentUser?.uid
        return editable && allowed
    }
}