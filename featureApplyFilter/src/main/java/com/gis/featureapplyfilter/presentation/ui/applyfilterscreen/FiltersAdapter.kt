package com.gis.featureapplyfilter.presentation.ui.applyfilterscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gis.featureapplyfilter.R
import com.gis.featureapplyfilter.databinding.ItemFiltersBinding
import com.gis.featureapplyfilter.databinding.ItemNoFilterBinding
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

  private val NO_FILTERS_ITEM = 0x099
  private val DEFAULT_ITEM = 0x098

  override fun getItemViewType(position: Int): Int =
    if (position == 0) NO_FILTERS_ITEM
    else DEFAULT_ITEM

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
    val binding =
      when (viewType) {
        NO_FILTERS_ITEM ->
          DataBindingUtil.inflate<ItemNoFilterBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_no_filter,
            parent,
            false)

        else -> DataBindingUtil.inflate<ItemFiltersBinding>(
          LayoutInflater.from(parent.context),
          R.layout.item_filters,
          parent,
          false)
      }


    return FilterViewHolder(binding, imageLoader)
  }

  override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
    holder.bind(getItem(position), clicksPublisher)
  }
}

class FilterViewHolder(private val binding: ViewDataBinding, private val imageLoader: ImageLoader) : RecyclerView.ViewHolder(binding.root) {

  fun bind(item: FilterListItem, clicksPublisher: Subject<String>) {
    when (binding) {
      is ItemFiltersBinding -> {
        RxView.clicks(binding.ivImg)
          .skip(500, TimeUnit.MILLISECONDS)
          .map { item.name }
          .subscribe(clicksPublisher)

        imageLoader.loadBitmap(binding.ivImg, item.image)
      }

      is ItemNoFilterBinding ->
        RxView.clicks(binding.tvName)
          .skip(500, TimeUnit.MILLISECONDS)
          .map { item.name }
          .subscribe(clicksPublisher)
    }
  }
}