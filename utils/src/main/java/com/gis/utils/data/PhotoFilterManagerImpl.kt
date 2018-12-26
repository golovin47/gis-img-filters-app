package com.gis.utils.data

import android.content.Context
import android.graphics.Bitmap
import com.gis.utils.data.entity.PhotoFilterThumbnailImpl
import com.gis.utils.domain.PhotoFilterManager
import com.gis.utils.domain.entity.PhotoFilterThumbnail
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import io.reactivex.Observable

class PhotoFilterManagerImpl(private val context: Context) : PhotoFilterManager {

  override fun getFilterThumbnails(bitmap: Bitmap): Observable<List<PhotoFilterThumbnail>> {
    ThumbnailsManager.clearThumbs()

    FilterPack.getFilterPack(context).forEach { filter ->
      val item = ThumbnailItem().apply {
        this.image = bitmap
        this.filter = filter
        filterName = filter.name
      }
      ThumbnailsManager.addThumb(item)
    }

    val resultThumbs = ThumbnailsManager.processThumbs(context).map { PhotoFilterThumbnailImpl(it) }

    return Observable.just(resultThumbs)
  }

  override fun applyFilter(bitmap: Bitmap, filterName: String): Observable<Bitmap> =
    Observable.fromCallable {
      val newBitmap = Bitmap.createBitmap(bitmap)
      val filter: Filter = FilterPack.getFilterPack(context).find { item -> item.name == filterName }!!
      return@fromCallable filter.processFilter(newBitmap)
    }
}