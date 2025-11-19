// HomeScreen.kt
package com.tuapp.learning.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.tuapp.learning.models.Course
import com.tuapp.learning.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: User,
    courses: List<Course>,
    onCourseClick: (Course) -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToAchievements: () -> Unit
) {
    val analytics = Firebase.analytics

    LaunchedEffect(Unit) {
        analytics.logEvent("screen_view") {
            param("screen_name", "home_screen")
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Cursos") },
                    label = { Text("Cursos") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProgress,
                    icon = { Icon(Icons.Default.TrendingUp, contentDescription = "Progreso") },
                    label = { Text("Progreso") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToAchievements,
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Logros") },
                    label = { Text("Logros") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF5F3FF),
                            Color(0xFFEBF4FF)
                        )
                    )
                )
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header con foto de usuario
            item {
                UserHeader(user)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Estadísticas
            item {
                StatsRow(user)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Mis Cursos
            item {
                Text(
                    text = "Mis Cursos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(courses.filter { it.progress > 0 }) { course ->
                MyCourseCard(
                    course = course,
                    onClick = {
                        analytics.logEvent("course_selected") {
                            param("course_id", course.id.toLong())
                            param("course_name", course.title)
                        }
                        onCourseClick(course)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Cursos Populares
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFFFF9800)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cursos Populares",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(courses.filter { it.isPopular }) { course ->
                PopularCourseCard(
                    course = course,
                    onClick = {
                        analytics.logEvent("popular_course_clicked") {
                            param("course_id", course.id.toLong())
                            param("course_name", course.title)
                        }
                        onCourseClick(course)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun UserHeader(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto de usuario (emoji o imagen)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF9C27B0),
                                Color(0xFF2196F3)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👩‍🎓",
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¡Hola, ${user.name}!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Continúa aprendiendo hoy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${user.totalPoints}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9C27B0)
                )
                Text(
                    text = "puntos",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun StatsRow(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatsCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Book,
            iconTint = Color(0xFF2196F3),
            value = user.coursesInProgress.toString(),
            label = "En progreso"
        )
        StatsCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.CheckCircle,
            iconTint = Color(0xFF4CAF50),
            value = user.completedCourses.toString(),
            label = "Completados"
        )
    }
}

@Composable
fun StatsCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun MyCourseCard(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = course.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progreso",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "${course.progress}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = course.progress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF9C27B0),
                trackColor = Color(0xFFE0E0E0)
            )
        }
    }
}

@Composable
fun PopularCourseCard(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (course.isPopular) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFFFE0B2),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Popular",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = course.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = course.rating.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${course.students}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = course.duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}