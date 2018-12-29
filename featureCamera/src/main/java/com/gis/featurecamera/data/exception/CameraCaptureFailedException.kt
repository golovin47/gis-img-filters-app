package com.gis.featurecamera.data.exception

import android.hardware.camera2.CaptureFailure

data class CameraCaptureFailedException(val failure: CaptureFailure) : Exception()