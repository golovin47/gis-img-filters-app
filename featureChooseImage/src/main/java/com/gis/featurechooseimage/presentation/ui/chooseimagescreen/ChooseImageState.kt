package com.gis.featurechooseimage.presentation.ui.chooseimagescreen

data class ChooseImageState(
  val loading: Boolean = false,
  val imagePath: String = "",
  val error: Throwable? = null
)


sealed class ChooseImageIntent {
  object ChooseImage : ChooseImageIntent()
  object ImageCancelled : ChooseImageIntent()
  class ImagePathCreated(val imagePath: String) : ChooseImageIntent()
  class ImageChosen(val imagePath: String) : ChooseImageIntent()
}


sealed class ChooseImageStateChange {
  object Loading : ChooseImageStateChange()
  object Cancelled : ChooseImageStateChange()
  class ImagePathCreated(val imagePath: String) : ChooseImageStateChange()
  class Error(val error: Throwable) : ChooseImageStateChange()
  object HideError : ChooseImageStateChange()
}