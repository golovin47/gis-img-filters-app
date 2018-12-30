package com.gis.featurecamera.presentation.ui.takenphotoscreen

data class TakenPhotoState(val showActions: Boolean = true)


sealed class TakenPhotoIntent {
  object ShowActions : TakenPhotoIntent()
  object HideActions : TakenPhotoIntent()
  class AcceptPhoto(val imagePath: String) : TakenPhotoIntent()
  object CancelPhoto : TakenPhotoIntent()
}


sealed class TakenPhotoStateChange {
  object ShowActions : TakenPhotoStateChange()
  object HideActions : TakenPhotoStateChange()
}