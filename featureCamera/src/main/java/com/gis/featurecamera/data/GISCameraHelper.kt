package com.gis.featurecamera.data

import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.WindowManager
import java.util.*
import kotlin.math.sign

object GISCameraHelper {

  private val maxPreviewWidth = 1920
  private val maxPreviewHeight = 1920
  private val maxStillImageWidth = 1920
  private val maxStillImageHeight = 1920

  private val jpegOrientations = SparseIntArray(4).apply {
    append(Surface.ROTATION_0, 90)
    append(Surface.ROTATION_90, 0)
    append(Surface.ROTATION_180, 270)
    append(Surface.ROTATION_270, 180)
  }

  fun chooseDefaultCamera(cameraManager: CameraManager): String =
    getCamera(cameraManager, CameraCharacteristics.LENS_FACING_BACK)

  fun switchCamera(cameraManager: CameraManager, currentCameraId: String?): String {
    currentCameraId?.let {
      val currentFacing = cameraManager.getCameraCharacteristics(currentCameraId).get(CameraCharacteristics.LENS_FACING)

      currentFacing?.let {
        val targetFacing =
          if (currentFacing == CameraCharacteristics.LENS_FACING_FRONT)
            CameraCharacteristics.LENS_FACING_BACK
          else CameraCharacteristics.LENS_FACING_FRONT

        return getCamera(cameraManager, targetFacing)
      }
    }
    return chooseDefaultCamera(cameraManager)
  }

  private fun getCamera(cameraManager: CameraManager, targetFacing: Int): String {
    lateinit var possibleResult: String

    val camerasIds = cameraManager.cameraIdList

    if (camerasIds.isEmpty()) return ""

    for (cameraId in camerasIds) {
      val characteristics = cameraManager.getCameraCharacteristics(cameraId)

      val configMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
      configMap ?: continue

      val cameraFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
      if (cameraFacing != null && cameraFacing == targetFacing) {
        return cameraId
      }

      possibleResult = cameraId
    }

    return possibleResult //In case there is no camera with targetFacing
  }

  fun getPreviewSize(characteristics: CameraCharacteristics): Size {
    val configMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
    val outputSizes = configMap!!.getOutputSizes(SurfaceTexture::class.java)

    if (outputSizes.isEmpty())
      throw IllegalStateException("No supported sizes for SurfaceTexture")

    val filteredOutputSizes = outputSizes
      .filter { size ->
        size.width <= maxPreviewWidth && size.height <= maxPreviewHeight
      }

    if (filteredOutputSizes.isEmpty())
      return outputSizes[0]

    return Collections.max(filteredOutputSizes, CompareSizesByArea())
  }

  fun getStillImageSize(characteristics: CameraCharacteristics, previewSize: Size): Size {
    val configMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
    val outputSizes = configMap!!.getOutputSizes(SurfaceTexture::class.java)

    if (outputSizes.isEmpty())
      throw IllegalStateException("No supported sizes for SurfaceTexture")

    val filteredOutputSizes = outputSizes
      .filter { size ->
        size.width == size.height * previewSize.width / previewSize.height
      }
      .filter { size ->
        size.width <= maxStillImageWidth && size.height <= maxStillImageHeight
      }

    if (filteredOutputSizes.isEmpty())
      return outputSizes[0]

    return Collections.max(filteredOutputSizes, CompareSizesByArea())
  }

  /**
   * Retrieves the JPEG orientation from the specified screen rotation.
   *
   * @param screenRotation The screen rotation.
   * @return The JPEG orientation (one of 0, 90, 270, and 360)
   */
  fun getJpegOrientation(characteristics: CameraCharacteristics, screenRotation: Int): Int {
    val sensorOrientation = getSensorOrientation(characteristics)
    return (jpegOrientations.get(screenRotation) + sensorOrientation + 270) % 360
  }

  /**
   * Returns degrees from 0, 90, 180, 270
   * @see CameraCharacteristics#SENSOR_ORIENTATION
   */
  private fun getSensorOrientation(characteristics: CameraCharacteristics): Int =
    characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

  /**
   * Converts values provided by {@link Display#getRotation()} into degrees
   * @param windowManager
   */
  fun rotationInDegrees(windowManager: WindowManager): Int =
    when (windowManager.defaultDisplay.rotation) {
      Surface.ROTATION_90 -> 90
      Surface.ROTATION_180 -> 180
      Surface.ROTATION_270 -> 270
      else -> 0
    }

  /**
   * Sensor could be rotated in the device, this method returns normal orientation sensor dimension
   */
  fun getSensorSizeRotated(characteristics: CameraCharacteristics, sensorSize: Size): Size {
    val sensorOrientationDegrees = getSensorOrientation(characteristics)

    return if (sensorOrientationDegrees % 180 == 0)
      sensorSize
    else Size(sensorSize.height, sensorSize.width)

    // swap dimensions
  }
}

private class CompareSizesByArea : Comparator<Size> {

  override fun compare(left: Size, right: Size): Int =
    (left.width.toLong() * left.height - right.width.toLong() * right.height).sign
}