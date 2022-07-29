package com.android.stubhub.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.android.stubhub.R
import com.android.stubhub.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private var binding: ActivityMainBinding? = null
    private val adapter by lazy { TicketAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        loadData()
        loadView()
    }

    private fun loadView() {
        binding?.apply {
            button.setOnClickListener {
                filterCityAndPrice()
            }
            searchInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(searchInputPrice.query.isNotEmpty()){
                        filterCityAndPrice()
                    }else {
                        adapter.filter.filter("CITY:$newText")
                    }
                    return false
                }

            })
            searchInputPrice.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(searchInput.query.isNotEmpty()){
                        filterCityAndPrice()
                    } else {
                        adapter.filter.filter("PRICE:$newText")
                    }
                    return false
                }

            })
        }
    }

    private fun ActivityMainBinding.filterCityAndPrice() {
        adapter.filter.filter("${searchInput.query}AND${searchInputPrice.query}")
    }

    private fun loadData() {
        viewModel.tickets.onEach {
            binding?.apply {
                adapter.swapData(it)
                recycler.adapter = adapter
            }

        }.launchIn(lifecycleScope)
    }
}