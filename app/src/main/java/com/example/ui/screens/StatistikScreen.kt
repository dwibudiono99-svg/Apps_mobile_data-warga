package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RTViewModel

@Composable
fun StatistikScreen(
    viewModel: RTViewModel,
    modifier: Modifier = Modifier
) {
    val wargaList by viewModel.wargaList.collectAsState()
    val kkList by viewModel.kkList.collectAsState()
    val pendukungList by viewModel.pendukungList.collectAsState()

    val totalWarga = wargaList.size.coerceAtLeast(1)
    val males = wargaList.count { it.gender == "L" }
    val females = wargaList.count { it.gender == "P" }

    // Age calculation
    val currentYear = 2026
    val ages = wargaList.map { w ->
        val year = w.birth.split("-").firstOrNull()?.toIntOrNull() ?: 1990
        currentYear - year
    }

    val balita = ages.count { it in 0..5 }
    val anak = ages.count { it in 6..12 }
    val remaja = ages.count { it in 13..17 }
    val dewasa = ages.count { it in 18..59 }
    val lansia = ages.count { it >= 60 }

    // Aid breakdown
    val bansos = pendukungList.count { it.aid != "Tidak Ada / Mandiri" }
    val mandiri = pendukungList.count { it.aid == "Tidak Ada / Mandiri" }

    // House breakdown
    val milikSendiri = pendukungList.count { it.houseStatus == "Milik Sendiri" }
    val sewaKontrak = pendukungList.count { it.houseStatus.contains("Kontrak") || it.houseStatus.contains("Sewa") }
    val menumpang = pendukungList.count { it.houseStatus.contains("Menumpang") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("statistik_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Grafik & Statistik Demografi RT",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Analisis demografi kependudukan RT 003 / RW 007 Sukamaju",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Gender Distribution Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Distribusi Jenis Kelamin",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Laki-laki: $males Jiwa (${males * 100 / totalWarga}%)", fontSize = 11.sp, color = Color(0xFF0369A1), fontWeight = FontWeight.SemiBold)
                        Text(text = "Perempuan: $females Jiwa (${females * 100 / totalWarga}%)", fontSize = 11.sp, color = Color(0xFFBE185D), fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight((males.toFloat() / totalWarga).coerceAtLeast(0.05f))
                                .background(Color(0xFF0369A1))
                        )
                        Box(
                            modifier = Modifier
                                .weight((females.toFloat() / totalWarga).coerceAtLeast(0.05f))
                                .background(Color(0xFFBE185D))
                        )
                    }
                }
            }
        }

        // Age Pyramid Groups
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Kelompok Usia Penduduk",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    BarMetricRow("Balita (0 - 5 Thn)", balita, totalWarga, Color(0xFF10B981))
                    BarMetricRow("Usia Sekolah (6 - 17 Thn)", anak + remaja, totalWarga, Color(0xFF3B82F6))
                    BarMetricRow("Usia Produktif / Dewasa (18 - 59 Thn)", dewasa, totalWarga, Color(0xFFF59E0B))
                    BarMetricRow("Lansia (>= 60 Thn)", lansia, totalWarga, Color(0xFFEC4899))
                }
            }
        }

        // Socio-Economic Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Status Bantuan Sosial & Tempat Tinggal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val totalPendukung = pendukungList.size.coerceAtLeast(1)
                    BarMetricRow("Penerima Bansos (PKH / BPNT / SKTM)", bansos, totalPendukung, Color(0xFFBE185D))
                    BarMetricRow("Keluarga Mandiri", mandiri, totalPendukung, Color(0xFF15803D))
                    Spacer(modifier = Modifier.height(6.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(6.dp))
                    BarMetricRow("Rumah Milik Sendiri", milikSendiri, totalPendukung, Color(0xFF0F766E))
                    BarMetricRow("Kontrak / Sewa", sewaKontrak, totalPendukung, Color(0xFF6366F1))
                    BarMetricRow("Menumpang / Mengontrak Bersama", menumpang, totalPendukung, Color(0xFFD97706))
                }
            }
        }
    }
}

@Composable
fun BarMetricRow(label: String, count: Int, total: Int, color: Color) {
    val pct = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "$count Jiwa (${(pct * 100).toInt()}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { pct },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
