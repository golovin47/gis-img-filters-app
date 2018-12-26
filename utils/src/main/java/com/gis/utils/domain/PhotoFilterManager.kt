package com.gis.utils.domain

import android.graphics.Bitmap
import com.gis.utils.domain.entity.PhotoFilterThumbnail
import io.reactivex.Completable
import io.reactivex.Observable

interface PhotoFilterManager {

  fun getFilterThumbnails(bitmap: Bitmap): Observable<List<PhotoFilterThumbnail>>

  fun applyFilter(bitmap: Bitmap, filterName: String): Observable<Bitmap>
}