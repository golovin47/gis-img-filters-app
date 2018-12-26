package com.gis.utils.data.entity

import android.graphics.Bitmap
import com.gis.utils.domain.entity.PhotoFilterThumbnail
import com.zomato.photofilters.utils.ThumbnailItem

class PhotoFilterThumbnailImpl(private val thumbnail: ThumbnailItem)
  : PhotoFilterThumbnail(thumbnail.filterName, thumbnail.image) {

  override val name: String
    get() = thumbnail.filterName

  override val image: Bitmap
    get() = thumbnail.image
}