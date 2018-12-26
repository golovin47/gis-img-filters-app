package com.gis.featureapplyfilter.presentation.ui.applyfilterscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gis.featureapplyfilter.R
import com.gis.featureapplyfilter.databinding.ItemFiltersBinding
import com.gis.utils.domain.ImageLoader
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class FiltersAdapter(private val clicksPublisher: Subject<String>,
                     private val imageLoader: ImageLoader) : ListAdapter<FilterListItem, FilterViewHolder>(
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

    return FilterViewHolder(binding, imageLoader)
  }

  override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
    holder.bind(getItem(position), clicksPublisher)
  }
}

class FilterViewHolder(private val binding: ItemFiltersBinding, private val imageLoader: ImageLoader) : RecyclerView.ViewHolder(binding.root) {

  fun bind(item: FilterListItem, clicksPublisher: Subject<String>) {
    RxView.clicks(binding.ivImg)
      .skip(500, TimeUnit.MILLISECONDS)
      .map { item.name }
      .subscribe(clicksPublisher)

    imageLoader.loadBitmap(binding.ivImg, item.image)
  }
}