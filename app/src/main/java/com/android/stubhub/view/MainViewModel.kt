package com.android.stubhub.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.stubhub.StubHubApp
import com.android.stubhub.data.model.Ticket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _tickets: MutableStateFlow<List<TicketAdapter.Item>> = MutableStateFlow(listOf())
    val tickets: StateFlow<List<TicketAdapter.Item>> = _tickets

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _tickets.value = createViewData(StubHubApp.ticketData ?: listOf())
        }
    }

    private fun createViewData(data: List<Ticket>): List<TicketAdapter.Item> {
        val result = mutableListOf<TicketAdapter.Item>()
        for ((index, ticket) in flatten(data).withIndex()) {
            if (!ticket.events.isNullOrEmpty()) {
                result.add(TicketAdapter.Item.CategoryItem(ticket))
                result.addAll(ticket.events.map { TicketAdapter.Item.EventItem(it) })
            } else {
                if (index == 0)
                    result.add(TicketAdapter.Item.HeaderItem(ticket))
                else
                    result.add(TicketAdapter.Item.SubHeaderItem(ticket))
            }
        }
        return result
    }

    private fun flatten(data: List<Ticket>): List<Ticket> {
        val result = mutableListOf<Ticket>()
        for (i in data) {
            if (i.children != null) {
                result.add(i.copy(children = listOf()))
                result.addAll(flatten(i.children))
            }
        }
        return result
    }
}