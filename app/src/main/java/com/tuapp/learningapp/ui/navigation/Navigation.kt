// Navigation.kt
package com.tuapp.learning.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CourseDetails : Screen("course_details/{courseId}") {
        fun createRoute(courseId: Int) = "course_details/$courseId"
    }
    object Progress : Screen("progress")
    object Achievements : Screen("achievements")
}