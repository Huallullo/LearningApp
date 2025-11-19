// Models.kt
package com.tuapp.learning.models

data class User(
    val name: String,
    val photoUrl: String,
    val coursesInProgress: Int,
    val completedCourses: Int,
    val totalPoints: Int
)

data class Course(
    val id: Int,
    val title: String,
    val category: String,
    val progress: Int,
    val duration: String,
    val rating: Float,
    val students: Int,
    val isPopular: Boolean,
    val description: String,
    val modules: Int,
    val lessons: Int
)

data class Achievement(
    val id: Int,
    val title: String,
    val icon: String,
    val unlocked: Boolean,
    val date: String,
    val description: String = ""
)

data class Certificate(
    val id: Int,
    val course: String,
    val date: String,
    val verified: Boolean
)