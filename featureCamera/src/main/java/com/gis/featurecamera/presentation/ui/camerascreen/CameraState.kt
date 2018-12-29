package com.gis.featurecamera.presentation.ui.camerascreen

import android.view.TextureView
import androidx.lifecycle.Lifecycle
import com.gis.featurecamera.domain.entity.CameraFacing
import com.gis.featurecamera.presentation.ui.camerascreen.FocusState.FocusIdle

data class CameraState(
  val focusState: FocusState = FocusIdle,
  val cameraFacing: CameraFacing = CameraFacing.FacingRear,
  val error: Throwable? = null
)


sealed class CameraIntent {
  class PrepareCamera(val textureView: TextureView, val lifecycle: Lifecycle) : CameraIntent()
  object TakePhoto : CameraIntent()
  object SwitchCamera : CameraIntent()
}


sealed class CameraStateChange {
  object Idle : CameraStateChange()
  class PhotoTaken(val imagePath: String) : CameraStateChange()
  class CameraSwitched(val cameraFacing: CameraFacing) : CameraStateChange()
  object FocusStarted : CameraStateChange()
  object FocusFinished : CameraStateChange()
  class Error(val error: Throwable) : CameraStateChange()
}


sealed class FocusState {
  object FocusIdle : FocusState()
  object FocusStarted : FocusState()
  object FocusFinished : FocusState()
}