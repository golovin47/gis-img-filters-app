package com.gis.featurecamera.domain.entity

sealed class CameraFacing {
  object FacingFront : CameraFacing()
  object FacingRear : CameraFacing()
}