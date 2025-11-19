// AchievementsScreen.kt
package com.tuapp.learning.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tuapp.learning.models.Achievement
import com.tuapp.learning.models.User
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    user: User,
    achievements: List<Achievement>,
    onBack: () -> Unit
) {
    val analytics = Firebase.analytics

    LaunchedEffect(Unit) {
        analytics.logEvent("screen_view") {
            param("screen_name", "achievements_screen")
        }
    }

    val unlockedCount = achievements.count { it.unlocked }
    val totalCount = achievements.size
    val completionPercentage = (unlockedCount.toFloat() / totalCount.toFloat() * 100).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Logros") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            // Header con resumen
            item {
                AchievementSummaryCard(
                    unlockedCount = unlockedCount,
                    totalCount = totalCount,
                    completionPercentage = completionPercentage,
                    userPoints = user.totalPoints
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Título de sección
            item {
                Text(
                    text = "Todos los Logros",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Lista de logros
            items(achievements) { achievement ->
                AchievementDetailCard(
                    achievement = achievement,
                    onClick = {
                        if (achievement.unlocked) {
                            analytics.logEvent("achievement_clicked") {
                                param("achievement_id", achievement.id.toLong())
                                param("achievement_name", achievement.title)
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Espacio al final
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AchievementSummaryCard(
    unlockedCount: Int,
    totalCount: Int,
    completionPercentage: Int,
    userPoints: Int
) {
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
                            Color(0xFFFFB74D),
                            Color(0xFFFF9800)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🏆",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$unlockedCount / $totalCount",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Logros Desbloqueados",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Barra de progreso
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progreso Total",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                        Text(
                            text = "$completionPercentage%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = completionPercentage / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Puntos totales
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⭐",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "$userPoints Puntos Totales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementDetailCard(
    achievement: Achievement,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlocked) Color.White else Color(0xFFF5F5F5)
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del logro
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.unlocked) {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFD700),
                                    Color(0xFFFFA500)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFE0E0E0),
                                    Color(0xFFBDBDBD)
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (achievement.unlocked) {
                    Text(
                        text = achievement.icon,
                        style = MaterialTheme.typography.displaySmall
                    )
                } else {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del logro
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(if (achievement.unlocked) 1f else 0.6f)
            ) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.unlocked) Color.Black else Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = achievement.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (achievement.unlocked) Color(0xFF4CAF50) else Color.Gray
                )

                if (!achievement.unlocked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sigue aprendiendo para desbloquear",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // Badge si está desbloqueado
            if (achievement.unlocked) {
                Surface(
                    color = Color(0xFF4CAF50),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "✓",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}