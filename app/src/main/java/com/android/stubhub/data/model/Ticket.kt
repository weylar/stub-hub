package com.android.stubhub.data.model

data class Tickets(val children: List<Ticket>)

data class Ticket(
    val id: Int?,
    val city: String?,
    val name: String?,
    val price: Int?,
    val events: List<Ticket>?,
    val children: List<Ticket>?
)
