package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JobFitAnalysisResult
import com.example.ui.RTViewModel
import com.example.ui.components.TagChip
import kotlinx.coroutines.delay

@Composable
fun JobFitAiScreen(
    viewModel: RTViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resumeInput by viewModel.resumeInput.collectAsState()
    val jobDescInput by viewModel.jobDescInput.collectAsState()
    val jobTitleInput by viewModel.jobTitleInput.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingJobFit.collectAsState()
    val result by viewModel.jobFitResult.collectAsState()
    val errorMessage by viewModel.jobFitError.collectAsState()

    var activeInputTab by remember { mutableIntStateOf(0) } // 0: Resume, 1: Job Description

    fun pasteToResume() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString() ?: ""
            viewModel.resumeInput.value = text
            Toast.makeText(context, "Teks resume ditempel!", Toast.LENGTH_SHORT).show()
        }
    }

    fun pasteToJobDesc() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString() ?: ""
            viewModel.jobDescInput.value = text
            Toast.makeText(context, "Teks lowongan ditempel!", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareResult(res: JobFitAnalysisResult) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "📊 *Hasil Analisis JobFit AI - Resume vs Lowongan*\n\n" +
                    "Posisi: ${res.jobTitle}\n" +
                    "Skor Kesesuaian: ${res.matchScore}% (${res.matchLevel})\n\n" +
                    "*Ringkasan:*\n${res.summary}\n\n" +
                    "*Keterampilan Cocok:* ${res.matchingSkills.joinToString(", ")}\n" +
                    "*Area Perlu Diperkuat:* ${res.missingSkills.joinToString(", ")}\n\n" +
                    "*Elevator Pitch:*\n\"${res.interviewPitch}\""
            )
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Bagikan Hasil Analisis JobFit"))
    }

    fun copyPitch(pitch: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Interview Pitch", pitch)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Interview Pitch disalin!", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("jobfit_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("jobfit_hero_card"),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4338CA))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF6366F1),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "JobFit AI Analyzer",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Analisis Kesesuaian Resume & Lowongan Kerja",
                                    color = Color(0xFFC7D2FE),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TagChip(text = "Gemini 3.5 Flash", color = Color(0xFFA5B4FC), bgColor = Color(0xFF312E81))
                            TagChip(text = "ATS Skill Match", color = Color(0xFF6EE7B7), bgColor = Color(0xFF064E3B))
                            TagChip(text = "Cloud Firestore", color = Color(0xFFFDE68A), bgColor = Color(0xFF78350F))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Masukkan teks resume (CV) Anda dan deskripsi pekerjaan untuk mendapatkan skor kesesuaian, kecocokan keahlian, rekomendasi ATS, serta interview pitch siap pakai.",
                            color = Color(0xFFE0E7FF),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Active Analysis State Indicator
        if (isAnalyzing) {
            item {
                AnalyzingProgressCard()
            }
        }

        // If Analysis Result is Present, show Result Dashboard
        if (result != null && !isAnalyzing) {
            val res = result!!
            item {
                JobFitResultDashboard(
                    result = res,
                    onReanalyze = { viewModel.analyzeJobFit() },
                    onEditInputs = { viewModel.jobFitResult.value = null },
                    onShare = { shareResult(res) },
                    onCopyPitch = { copyPitch(res.interviewPitch) }
                )
            }
        } else if (!isAnalyzing) {
            // Main Input Form Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("jobfit_input_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Informasi Posisi (Opsional)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = jobTitleInput,
                            onValueChange = { viewModel.jobTitleInput.value = it },
                            placeholder = { Text("Contoh: Senior Android Developer / Staff Administrasi") },
                            leadingIcon = { Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("job_title_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tab selector between Resume and Job Description
                        TabRow(
                            selectedTabIndex = activeInputTab,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        ) {
                            Tab(
                                selected = activeInputTab == 0,
                                onClick = { activeInputTab = 0 },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Resume / CV (${resumeInput.length} kar)")
                                    }
                                }
                            )
                            Tab(
                                selected = activeInputTab == 1,
                                onClick = { activeInputTab = 1 },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Lowongan (${jobDescInput.length} kar)")
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (activeInputTab == 0) {
                            // Resume Text Box
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Isi / Teks Resume Anda:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.clickable { pasteToResume() }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Tempel (Paste)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = resumeInput,
                                onValueChange = { viewModel.resumeInput.value = it },
                                placeholder = {
                                    Text(
                                        "Salin dan tempel ringkasan pengalaman kerja, riwayat pendidikan, keahlian teknis (skills), dan proyek dari CV Anda ke sini..."
                                    )
                                },
                                minLines = 8,
                                maxLines = 14,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("resume_text_input")
                            )
                        } else {
                            // Job Description Text Box
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Deskripsi Lowongan (Job Description):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.clickable { pasteToJobDesc() }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Tempel (Paste)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = jobDescInput,
                                onValueChange = { viewModel.jobDescInput.value = it },
                                placeholder = {
                                    Text(
                                        "Salin dan tempel uraian tugas, kualifikasi minimum, persyaratan teknis, dan tanggung jawab dari pengumuman lowongan kerja ke sini..."
                                    )
                                },
                                minLines = 8,
                                maxLines = 14,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("job_desc_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Actions & Samples
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.loadSampleResumeAndJob() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("sample_data_button"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Muat Contoh", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.clearJobFit() },
                                modifier = Modifier.weight(0.7f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bersihkan", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Primary Action: Analyze with AI
                        Button(
                            onClick = { viewModel.analyzeJobFit() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("analyze_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA))
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mulai Analisis JobFit AI",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyzingProgressCard() {
    var stepIndex by remember { mutableIntStateOf(0) }
    val steps = listOf(
        "Membaca resume & keahlian kandidat...",
        "Mengekstrak kualifikasi teknis dari lowongan...",
        "Menghitung skor kecocokan algoritma ATS...",
        "Merumuskan strategi wawancara & saran perbaikan..."
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            stepIndex = (stepIndex + 1) % steps.size
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("analyzing_progress_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
        border = BorderStroke(1.5.dp, Color(0xFF6366F1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF4338CA),
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sedang Menganalisis dengan Gemini AI...",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF312E81)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = steps[stepIndex],
                fontSize = 11.sp,
                color = Color(0xFF4338CA),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF4338CA),
                trackColor = Color(0xFFC7D2FE)
            )
        }
    }
}

@Composable
fun JobFitResultDashboard(
    result: JobFitAnalysisResult,
    onReanalyze: () -> Unit,
    onEditInputs: () -> Unit,
    onShare: () -> Unit,
    onCopyPitch: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = result.matchScore / 100f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "score"
    )

    val scoreColor = when {
        result.matchScore >= 80 -> Color(0xFF15803D)
        result.matchScore >= 65 -> Color(0xFFB45309)
        else -> Color(0xFFB91C1C)
    }

    val scoreBgColor = when {
        result.matchScore >= 80 -> Color(0xFFDCFCE7)
        result.matchScore >= 65 -> Color(0xFFFEF3C7)
        else -> Color(0xFFFEE2E2)
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Overall Match Score Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("match_score_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = result.jobTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (result.isFromGemini) "Dianalisis oleh Gemini 3.5 Flash" else "Analisis NLP Heuristik Cerdas",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = scoreBgColor,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = result.matchLevel,
                            color = scoreColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Score Bar & Percentage
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${result.matchScore}%",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp,
                        color = scoreColor
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tingkat Keselarasan Kualifikasi (Match Index)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = scoreColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                // Executive Summary
                Text(
                    text = "Ringkasan Evaluasi AI:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.summary,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }
        }

        // Skills Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Matching Skills
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Keahlian yang Sudah Cocok (${result.matchingSkills.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF15803D)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(result.matchingSkills) { skill ->
                        TagChip(text = "✓ $skill", color = Color(0xFF15803D), bgColor = Color(0xFFDCFCE7))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                // Missing Skills / Gaps
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Area / Keterampilan yang Perlu Diperkuat (${result.missingSkills.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFFB45309)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(result.missingSkills) { skill ->
                        TagChip(text = "! $skill", color = Color(0xFFB45309), bgColor = Color(0xFFFEF3C7))
                    }
                }
            }
        }

        // Actionable Recommendations Card
        if (result.recommendations.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF4338CA), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Rekomendasi Optimasi Resume (Lolos ATS)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF4338CA)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    result.recommendations.forEachIndexed { index, rec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFEEF2FF),
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4338CA)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = rec,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Tailored Interview Pitch Card
        if (result.interviewPitch.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Elevator Pitch Wawancara",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF0F172A)
                            )
                        }

                        IconButton(onClick = onCopyPitch, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Salin Pitch", modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${result.interviewPitch}\"",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Serif,
                        color = Color(0xFF334155),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Bottom Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onShare,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Bagikan Hasil", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = onEditInputs,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ubah Teks", fontSize = 12.sp)
            }
        }
    }
}
