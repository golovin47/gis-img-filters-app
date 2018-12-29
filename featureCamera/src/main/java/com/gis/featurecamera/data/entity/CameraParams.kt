package com.gis.featurecamera.data.entity

import android.hardware.camera2.CameraCharacteristics
import android.util.Size

data class CameraParams(val cameraId: String,
                        val characteristics: CameraCharacteristics,
                        val previewSize: Size)