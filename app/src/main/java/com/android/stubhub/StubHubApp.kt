package com.android.stubhub

import android.app.Application
import com.android.stubhub.data.AppData
import com.android.stubhub.data.model.Ticket
import com.android.stubhub.data.model.Tickets

class StubHubApp: Application() {


    companion object {
        var ticketData: List<Ticket>? = null
    }

    override fun onCreate() {
        super.onCreate()
        loadTicketData()
    }

    private fun loadTicketData() {
        ticketData = AppData.create<Tickets>(applicationContext, "Ticket.json").children
    }
}