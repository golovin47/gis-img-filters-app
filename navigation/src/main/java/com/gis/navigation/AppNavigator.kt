package com.gis.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions

class AppNavigator {

  private lateinit var navController: NavController

  fun setNavController(navController: NavController) {
    this.navController = navController
  }

  fun goToCameraFromChooseImageScreen() {
    val navOptions = NavOptions.Builder()
      .setEnterAnim(R.anim.anim_fade_in)
      .setExitAnim(R.anim.anim_fade_out)
      .setPopEnterAnim(R.anim.anim_fade_in)
      .setPopExitAnim(R.anim.anim_fade_out)
      .build()
    navController.navigate(R.id.fromChooseImageToCamera, null, navOptions)
  }

  fun goToApplyFilterFromChooseImageScreen(imagePath: String) {
    val args: Bundle = Bundle().apply {
      putString("imagePath", imagePath)
    }

    val navOptions = NavOptions.Builder()
      .setEnterAnim(R.anim.anim_fade_in)
      .setExitAnim(R.anim.anim_fade_out)
      .setPopEnterAnim(R.anim.anim_fade_in)
      .setPopExitAnim(R.anim.anim_fade_out)
      .build()
    navController.navigate(R.id.fromChooseImageToApplyFilter, args, navOptions)
  }

  fun goToTakenPhotoFromCameraScreen(imagePath: String) {
    val args: Bundle = Bundle().apply {
      putString("imagePath", imagePath)
    }

    val navOptions = NavOptions.Builder()
      .setEnterAnim(R.anim.anim_fade_in)
      .setExitAnim(R.anim.anim_fade_out)
      .setPopEnterAnim(R.anim.anim_fade_in)
      .setPopExitAnim(R.anim.anim_fade_out)
      .build()
    navController.navigate(R.id.fromCameraToTakenPhoto, args, navOptions)
  }

  fun goToApplyFilterFromTakenPictureScreen(imagePath: String) {
    val args: Bundle = Bundle().apply {
      putString("imagePath", imagePath)
    }

    val navOptions = NavOptions.Builder()
      .setEnterAnim(R.anim.anim_fade_in)
      .setExitAnim(R.anim.anim_fade_out)
      .setPopEnterAnim(R.anim.anim_fade_in)
      .setPopExitAnim(R.anim.anim_fade_out)
      .setPopUpTo(R.id.cameraFragment, true)
      .build()
    navController.navigate(R.id.fromTakenPhotoToApplyFilter, args, navOptions)
  }

  fun goBack() {
    navController.navigateUp()
  }
}