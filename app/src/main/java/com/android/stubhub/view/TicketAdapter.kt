package com.android.stubhub.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.android.stubhub.data.model.Ticket
import com.android.stubhub.databinding.ItemCategoryListBinding
import com.android.stubhub.databinding.ItemHeaderListBinding
import com.android.stubhub.databinding.ItemSubheaderListBinding
import com.android.stubhub.databinding.ItemTicketListBinding
import java.util.Locale


class TicketAdapter : RecyclerView.Adapter<TicketAdapter.VH>(), Filterable {

    private val list = mutableListOf<Item>()
    var filterList = listOf<Item>()

    init {
        filterList = list
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filterList = if (charSearch.isEmpty()) {
                    list
                } else {
                    val resultList = mutableListOf<Item>()
                    for (row in list) {
                        if(charSearch.contains("AND")){
                            if(row.ticket.city?.lowercase(Locale.ROOT)?.contains(charSearch.split("AND")[0].lowercase(Locale.ROOT)) == true
                                && row.ticket.price.isLessThanOrEqual(charSearch.split("AND")[1].toInt())){
                                resultList.add(Item.CategoryItem(row.ticket))
                                resultList.add(row)
                            }
                        } else if (charSearch.startsWith("PRICE:")) {
                            if (row.ticket.price.isLessThanOrEqual(charSearch.replace("PRICE:", "").toInt())) {
                                resultList.add(Item.CategoryItem(row.ticket))
                                resultList.add(row)
                            }
                        } else if(charSearch.startsWith("CITY:")) {
                            if (row.ticket.city?.lowercase(Locale.ROOT)?.contains(charSearch.replace("CITY:", "") .lowercase(Locale.ROOT)) == true
                            ) {
                                resultList.add(Item.CategoryItem(row.ticket))
                                resultList.add(row)
                            }
                        }

                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as? List<Item> ?: listOf()
                notifyDataSetChanged()
            }

        }
    }

    private fun Int?.isLessThanOrEqual(other: Int): Boolean {
        if (this == null) return false
        return this >= other
    }

    override fun getItemCount(): Int = filterList.count()
    override fun getItemViewType(position: Int): Int = filterList[position].ordinal()

    fun swapData(list: List<Item>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return when (viewType) {
            Item.HeaderItem.ordinal() -> {
                val binding = ItemHeaderListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                VH.HeaderVH(binding)
            }
            Item.SubHeaderItem.ordinal() -> {
                val binding = ItemSubheaderListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                VH.SubHeaderVH(binding)
            }
            Item.CategoryItem.ordinal() -> {
                val binding =
                    ItemCategoryListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                VH.CategoryVH(binding)
            }
            Item.EventItem.ordinal() -> {
                val binding = ItemTicketListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                VH.EventVH(binding)
            }
            else -> throw IllegalStateException("HomeAdapter attempting to create an unhandled view type")
        }

    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = filterList[position]
        when (getItemViewType(position)) {
            Item.HeaderItem.ordinal() -> {
                if (holder !is VH.HeaderVH || item !is Item.HeaderItem) throw IllegalStateException(
                    "ViewHolder or list item does not match view type"
                )
                holder.bind(item)
            }
            Item.SubHeaderItem.ordinal() -> {
                if (holder !is VH.SubHeaderVH || item !is Item.SubHeaderItem) throw IllegalStateException(
                    "ViewHolder or list item does not match view type"
                )
                holder.bind(item)
            }
            Item.CategoryItem.ordinal() -> {
                if (holder !is VH.CategoryVH || item !is Item.CategoryItem) throw IllegalStateException(
                    "ViewHolder or list item does not match view type"
                )
                holder.bind(item)
            }
            Item.EventItem.ordinal() -> {
                if (holder !is VH.EventVH || item !is Item.EventItem) throw IllegalStateException("ViewHolder or list item does not match view type")
                holder.bind(item)
            }

        }
    }


    sealed class VH(view: View) : RecyclerView.ViewHolder(view) {
        class HeaderVH(private val binding: ItemHeaderListBinding) : VH(binding.root) {
            fun bind(item: Item.HeaderItem) {
                binding.header.text = item.ticket.name
            }
        }

        class SubHeaderVH(private val binding: ItemSubheaderListBinding) : VH(binding.root) {
            fun bind(item: Item.SubHeaderItem) {
                binding.name.text = item.ticket.name
            }
        }

        class CategoryVH(private val binding: ItemCategoryListBinding) : VH(binding.root) {
            fun bind(item: Item.CategoryItem) {
                binding.category.text = item.ticket.name
            }
        }

        class EventVH(private val binding: ItemTicketListBinding) : VH(binding.root) {
            fun bind(item: Item.EventItem) {
                binding.apply {
                    city.text = "City: ${item.ticket?.city}"
                    price.text = "Price: $ ${item.ticket?.price}"
                }
            }
        }

    }

    sealed class Item(val ticket: Ticket) {
        class HeaderItem(ticket: Ticket) : Item(ticket) {
            companion object
        }

        class SubHeaderItem(ticket: Ticket) : Item(ticket) {
            companion object
        }

        class CategoryItem(ticket: Ticket) : Item(ticket) {
            companion object
        }

        class EventItem(ticket: Ticket) : Item(ticket) {
            companion object
        }
    }

    private inline fun <reified T : Any> T.ordinal(): Int {
        if (T::class.isSealed) {
            return T::class.java.classes.indexOfFirst { sub -> sub == javaClass }
        }

        val klass = if (T::class.isCompanion) {
            javaClass.declaringClass
        } else {
            javaClass
        }

        return klass?.superclass?.classes?.indexOfFirst { it == klass } ?: -1
    }
}