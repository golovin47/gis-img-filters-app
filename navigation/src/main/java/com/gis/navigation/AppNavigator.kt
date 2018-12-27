package com.gis.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions

class AppNavigator {

  private lateinit var navController: NavController

  fun setNavController(navController: NavController) {
    this.navController = navController
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

  fun goToChooseImageFromApplyFilterScreen() {
    navController.navigateUp()
  }
}