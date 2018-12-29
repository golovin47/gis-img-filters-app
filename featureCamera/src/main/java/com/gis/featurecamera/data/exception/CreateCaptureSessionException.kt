package com.gis.featurecamera.data.exception

import android.hardware.camera2.CameraCaptureSession

data class CreateCaptureSessionException(val session: CameraCaptureSession) : Exception()