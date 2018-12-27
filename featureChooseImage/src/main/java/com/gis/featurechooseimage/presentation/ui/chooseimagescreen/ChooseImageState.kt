package com.gis.featurechooseimage.presentation.ui.chooseimagescreen

import android.net.Uri

data class ChooseImageState(
  val openCamera: Boolean = false,
  val openGallery: Boolean = false,
  val requestPermissionsForCamera: Boolean = false,
  val requestPermissionsForGallery: Boolean = false,
  val showExtraPermissionsDialog: Boolean = false,
  val goToAppSettings:Boolean = false,
  val imagePath: String = "",
  val uriForPhoto: Uri? = null,
  val error: Throwable? = null
)


sealed class ChooseImageIntent {
  object OpenCamera : ChooseImageIntent()
  object OpenGallery : ChooseImageIntent()
  object RequestPermissionsForCamera : ChooseImageIntent()
  object RequestPermissionsForGallery : ChooseImageIntent()
  object ShowExtraPermissionsDialog : ChooseImageIntent()
  object DismissExtraPermissionsDialog : ChooseImageIntent()
  object GoToAppSettings : ChooseImageIntent()
  object ImageCancelled : ChooseImageIntent()
  class CameraImageChosen(val imagePath: String) : ChooseImageIntent()
  class GalleryImageChosen(val uri: Uri) : ChooseImageIntent()
}


sealed class ChooseImageStateChange {
  object Idle : ChooseImageStateChange()
  class OpenCamera(val uri: Uri, val path: String) : ChooseImageStateChange()
  object OpenGallery : ChooseImageStateChange()
  object RequestPermissionsForCamera : ChooseImageStateChange()
  object RequestPermissionsForGallery : ChooseImageStateChange()
  object ShowExtraPermissionsDialog : ChooseImageStateChange()
  object DismissExtraPermissionsDialog : ChooseImageStateChange()
  object GoToAppSettings : ChooseImageStateChange()
  class Error(val error: Throwable) : ChooseImageStateChange()
  object HideError : ChooseImageStateChange()
}