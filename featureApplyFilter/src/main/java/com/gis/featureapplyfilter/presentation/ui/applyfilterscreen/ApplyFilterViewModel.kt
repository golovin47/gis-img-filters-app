package com.gis.featureapplyfilter.presentation.ui.applyfilterscreen

import com.gis.featureapplyfilter.presentation.ui.applyfilterscreen.ApplyFilterIntent.ChooseFilter
import com.gis.featureapplyfilter.presentation.ui.applyfilterscreen.ApplyFilterStateChange.FilterChosen
import com.gis.utils.BaseViewModel
import io.reactivex.Observable

class ApplyFilterViewModel : BaseViewModel<ApplyFilterState>() {

  override fun initState(): ApplyFilterState = ApplyFilterState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(listOf(
      intentStream.ofType(ChooseFilter::class.java)
        .map { event -> FilterChosen(event.name) },

      intentStream.ofType(ApplyFilterIntent.FilterApplied::class.java)
        .map { event -> ApplyFilterStateChange.FilterApplied(event.bitmap) }
    ))

  override fun reduceState(previousState: ApplyFilterState, stateChange: Any): ApplyFilterState =
    when (stateChange) {
      is FilterChosen -> {
        val filters = previousState.filters.toMutableList().apply {
          map { item ->
            if (item.name == stateChange.name) item.copy(selected = true)
            else item.copy(selected = false)
          }
        }
        previousState.copy(loading = true, filters = filters)
      }

      is ApplyFilterStateChange.FilterApplied -> previousState.copy(
        loading = false,
        currentBitmap = stateChange.bitmap)

      else -> previousState
    }
}