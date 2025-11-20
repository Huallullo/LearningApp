// MainActivity.kt
package com.tuapp.learning

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import com.tuapp.learning.models.*
import com.tuapp.learning.ui.navigation.Screen
import com.tuapp.learning.ui.screens.*
import com.tuapp.learning.ui.theme.LearningAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        analytics = Firebase.analytics
        if (BuildConfig.DEBUG) {
            analytics.setAnalyticsCollectionEnabled(true)
        }
        analytics.logEvent("app_opened") {
            param("timestamp", System.currentTimeMillis())
            param("device", Build.MODEL)
        }
        setContent {
            LearningAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LearningApp()
                }
            }
        }
    }
}

@Composable
fun LearningApp() {
    val navController = rememberNavController()

    val user = remember {
        User(
            name = "Abel Huallullo Matos  ",
            photoUrl = "",
            coursesInProgress = 3,
            completedCourses = 12,
            totalPoints = 2450
        )
    }

    val courses = remember {
        listOf(
            Course(
                id = 1,
                title = "Kotlin Avanzado",
                category = "Programación",
                progress = 65,
                duration = "12 horas",
                rating = 4.8f,
                students = 15420,
                isPopular = true,
                description = "Domina Kotlin con patrones avanzados, corrutinas y programación funcional.",
                modules = 8,
                lessons = 45
            ),
            Course(
                id = 2,
                title = "Jetpack Compose",
                category = "Android",
                progress = 40,
                duration = "10 horas",
                rating = 4.9f,
                students = 12350,
                isPopular = true,
                description = "Crea interfaces modernas para Android con el toolkit declarativo de Google.",
                modules = 6,
                lessons = 38
            ),
            Course(
                id = 3,
                title = "Firebase & Cloud",
                category = "Backend",
                progress = 20,
                duration = "15 horas",
                rating = 4.7f,
                students = 18900,
                isPopular = true,
                description = "Implementa servicios en la nube con Firebase y Google Cloud Platform.",
                modules = 10,
                lessons = 52
            ),
            Course(
                id = 4,
                title = "Clean Architecture",
                category = "Arquitectura",
                progress = 0,
                duration = "8 horas",
                rating = 4.6f,
                students = 9800,
                isPopular = false,
                description = "Aprende a estructurar aplicaciones escalables y mantenibles.",
                modules = 5,
                lessons = 30
            )
        )
    }

    val achievements = remember {
        listOf(
            Achievement(1, "Primera Clase", "🎯", true, "15 Enero 2025"),
            Achievement(2, "Estudiante Dedicado", "📚", true, "20 Enero 2025"),
            Achievement(3, "Maratón de Código", "💻", true, "5 Febrero 2025"),
            Achievement(4, "Maestro Android", "🤖", false, "Próximamente"),
            Achievement(5, "Experto en Kotlin", "🚀", false, "Próximamente"),
            Achievement(6, "Completado 10 Cursos", "🎓", true, "10 Febrero 2025"),
            Achievement(7, "Racha de 7 Días", "🔥", false, "Próximamente"),
            Achievement(8, "Mentor Comunitario", "🌟", false, "Próximamente")
        )
    }

    val certificates = remember {
        listOf(
            Certificate(1, "Kotlin Básico", "Dic 2024", true),
            Certificate(2, "Android Fundamentals", "Oct 2024", true),
            Certificate(3, "Java avanzado", "Ene 2025", true)
        )
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                user = user,
                courses = courses,
                onCourseClick = { course ->
                    navController.navigate(Screen.CourseDetails.createRoute(course.id))
                },
                onNavigateToProgress = {
                    navController.navigate(Screen.Progress.route)
                },
                onNavigateToAchievements = {
                    navController.navigate(Screen.Achievements.route)
                }
            )
        }

        composable(
            route = Screen.CourseDetails.route,
            arguments = listOf(
                navArgument("courseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 1
            val course = courses.find { it.id == courseId } ?: courses[0]

            CourseDetailsScreen(
                course = course,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(
                user = user,
                courses = courses,
                achievements = achievements,
                certificates = certificates,
                onBack = { navController.popBackStack() }
            )
        }


        composable(Screen.Achievements.route) {
            AchievementsScreen(
                user = user,
                achievements = achievements,
                onBack = { navController.popBackStack() }
            )
        }
    }
}