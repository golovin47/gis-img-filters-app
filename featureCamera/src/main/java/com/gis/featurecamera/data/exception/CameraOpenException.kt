package com.gis.featurecamera.data.exception

data class CameraOpenException(val reason: String) : Exception(reason)