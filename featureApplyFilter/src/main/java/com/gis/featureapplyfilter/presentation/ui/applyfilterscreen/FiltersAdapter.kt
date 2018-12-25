package com.gis.featureapplyfilter.presentation.ui.applyfilterscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gis.featureapplyfilter.R
import com.gis.featureapplyfilter.databinding.ItemFiltersBinding
import com.gis.featureapplyfilter.presentation.ui.applyfilterscreen.ApplyFilterIntent.ChooseFilter
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class FiltersAdapter(private val clicksPublisher: Subject<ApplyFilterIntent>) : ListAdapter<FilterListItem, FilterViewHolder>(
  object : DiffUtil.ItemCallback<FilterListItem>() {
    override fun areItemsTheSame(oldItem: FilterListItem, newItem: FilterListItem): Boolean =
      oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: FilterListItem, newItem: FilterListItem): Boolean =
      oldItem == newItem
  }) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
    val binding = DataBindingUtil.inflate<ItemFiltersBinding>(
      LayoutInflater.from(parent.context),
      R.layout.item_filters,
      parent,
      false)

    return FilterViewHolder(binding)
  }

  override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
    holder.bind(getItem(position), clicksPublisher)
  }
}

class FilterViewHolder(private val binding: ItemFiltersBinding) : RecyclerView.ViewHolder(binding.root) {

  fun bind(item: FilterListItem, clicksPublisher: Subject<ApplyFilterIntent>) {
    RxView.clicks(binding.tvFilterName)
      .skip(500, TimeUnit.MILLISECONDS)
      .map { ChooseFilter(binding.tvFilterName.text.toString()) }
      .subscribe(clicksPublisher)

    binding.tvFilterName.text = item.name
  }
}

data class FilterListItem(val name: String, val selected: Boolean)