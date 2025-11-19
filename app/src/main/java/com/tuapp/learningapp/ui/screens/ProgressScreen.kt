// ProgressScreen.kt
package com.tuapp.learning.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.tuapp.learning.models.Achievement
import com.tuapp.learning.models.Certificate
import com.tuapp.learning.models.Course
import com.tuapp.learning.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    user: User,
    courses: List<Course>,
    achievements: List<Achievement>,
    certificates: List<Certificate>,
    onBack: () -> Unit
) {
    val analytics = Firebase.analytics

    LaunchedEffect(Unit) {
        analytics.logEvent("screen_view") {
            param("screen_name", "progress_screen")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Progreso") },
                navigationIcon = {IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
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
            // Resumen de puntos
            item {
                PointsSummaryCard(user)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Cursos activos
            item {
                SectionHeader(
                    icon = Icons.Default.Book,
                    iconTint = Color(0xFF9C27B0),
                    title = "Cursos Activos"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(courses.filter { it.progress > 0 && it.progress < 100 }) { course ->
                ActiveCourseCard(course)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Logros
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    icon = Icons.Default.EmojiEvents,
                    iconTint = Color(0xFFFFC107),
                    title = "Logros Desbloqueados"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                AchievementsGrid(achievements)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Certificados
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    icon = Icons.Default.WorkspacePremium,
                    iconTint = Color(0xFF2196F3),
                    title = "Certificados"
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(certificates) { certificate ->
                CertificateCard(certificate)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun PointsSummaryCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF9C27B0),
                            Color(0xFF2196F3)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "${user.totalPoints}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Puntos Totales",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    Text(
                        text = "🏆",
                        style = MaterialTheme.typography.displayMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProgressStatCard(
                        modifier = Modifier.weight(1f),
                        value = user.completedCourses.toString(),
                        label = "Completados"
                    )
                    ProgressStatCard(
                        modifier = Modifier.weight(1f),
                        value = user.coursesInProgress.toString(),
                        label = "En curso"
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressStatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ActiveCourseCard(course: Course) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
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
                Text(
                    text = "${course.progress}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9C27B0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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
fun AchievementsGrid(achievements: List<Achievement>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        achievements.chunked(2).forEach { row ->
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { achievement ->
                    AchievementCard(achievement)
                }
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlocked) Color.White else Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = achievement.icon,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.alpha(if (achievement.unlocked) 1f else 0.4f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (achievement.unlocked) Color.Black else Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = achievement.date,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CertificateCard(certificate: Certificate) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFBBDEFB))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📜",
                style = MaterialTheme.typography.displaySmall
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = certificate.course,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Completado: ${certificate.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (certificate.verified) {
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = androidx.compose.foundation.shape.CircleShape
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Verificado",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
            }
        }
    }
}