// CourseDetailsScreen.kt
package com.tuapp.learning.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.tuapp.learning.models.Course

data class Module(
    val number: Int,
    val title: String,
    val lessons: Int,
    val completed: Boolean,
    val current: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailsScreen(
    course: Course,
    onBack: () -> Unit
) {
    val analytics = Firebase.analytics

    LaunchedEffect(course.id) {
        analytics.logEvent("course_details_viewed") {
            param("course_id", course.id.toLong())
            param("course_name", course.title)
        }
    }

    val modules = remember {
        listOf(
            Module(1, "Introducción", 5, true),
            Module(2, "Fundamentos", 8, true),
            Module(3, "Conceptos Avanzados", 10, false, true),
            Module(4, "Proyecto Final", 12, false)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Curso") },
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
            // Información principal
            item {
                CourseInfoCard(course)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Estadísticas del curso
            item {
                CourseStatsRow(course)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Contenido del curso
            item {
                Text(
                    text = "Contenido del Curso",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            itemsIndexed(modules) { index, module ->
                ModuleCard(module)
                if (index < modules.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        analytics.logEvent("continue_learning_clicked") {
                            param("course_id", course.id.toLong())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C27B0)
                    )
                ) {
                    Text(
                        text = "Continuar Aprendiendo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CourseInfoCard(course: Course) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = course.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${course.rating} rating",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${course.students} estudiantes",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progreso
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF5F3FF),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tu progreso",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${course.progress}%",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9C27B0)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = course.progress / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFF9C27B0),
                        trackColor = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CourseStatsRow(course: Course) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CourseStatCard(
            modifier = Modifier.weight(1f),
            icon = "📚",
            value = course.modules.toString(),
            label = "Módulos"
        )
        CourseStatCard(
            modifier = Modifier.weight(1f),
            icon = "📝",
            value = course.lessons.toString(),
            label = "Lecciones"
        )
        CourseStatCard(
            modifier = Modifier.weight(1f),
            icon = "⏱️",
            value = course.duration,
            label = "Duración"
        )
    }
}

@Composable
fun CourseStatCard(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
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
fun ModuleCard(module: Module) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de estado
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            module.completed -> Color(0xFF4CAF50)
                            module.current -> Color(0xFF9C27B0)
                            else -> Color(0xFFBDBDBD)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (module.completed) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = module.number.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = module.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${module.lessons} lecciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (module.current) {
                Surface(
                    color = Color(0xFFF3E5F5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Actual",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9C27B0),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}