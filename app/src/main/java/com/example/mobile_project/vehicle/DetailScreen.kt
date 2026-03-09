package com.example.mobile_project.vehicle

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mobile_project.firebaseDB.OrderViewModel
import com.example.mobile_project.firebaseDB.UserSession
import com.example.mobile_project.firebaseDB.VehicleEntity
import com.example.mobile_project.firebaseDB.VehicleViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DetailScreen(
    vehicleId: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val vehicleViewModel: VehicleViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()

    // ดึงรถทั้งหมดแล้วหาคันที่ตรงกับ vehicleId
    val vehicles by vehicleViewModel.allVehicles.collectAsState(initial = emptyList())
    val vehicle = vehicles.find { it.id == vehicleId }

    // State สำหรับ Popup ยืนยันสั่งซื้อ
    var showConfirmDialog by remember { mutableStateOf(false) }
    // State สำหรับ Loading ขณะบันทึกคำสั่งซื้อ
    var isOrdering by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        if (vehicle == null) {
            // Loading state
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppDarkBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // ── รูปรถ ──
                AsyncImage(
                    model = vehicle.imageUrl,
                    contentDescription = "Car Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(Color.LightGray.copy(alpha = 0.2f)),
                    contentScale = ContentScale.Crop
                )

                // ── ส่วนข้อมูล ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(Color.White)
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    // ชื่อรถ + ราคา
                    Text(
                        text = "${vehicle.brand} ${vehicle.model}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppDarkBlue
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "฿ ${NumberFormat.getNumberInstance(Locale.US).format(vehicle.price)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppLightBlue
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Grid สเปค ──
                    Text(
                        text = "ข้อมูลจำเพาะ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppDarkBlue
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            SpecDetailItem(icon = Icons.Default.DirectionsCar, label = "ประเภท", value = vehicle.segment)
                            SpecDetailItem(icon = Icons.Default.AirlineSeatReclineNormal, label = "จำนวนที่นั่ง", value = "${vehicle.seats} ที่นั่ง")
                            SpecDetailItem(icon = Icons.Default.Settings, label = "เกียร์", value = vehicle.gear)
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            SpecDetailItem(icon = Icons.Default.MeetingRoom, label = "จำนวนประตู", value = "${vehicle.door} ประตู")
                            SpecDetailItem(icon = Icons.Default.LocalGasStation, label = "พลังงาน", value = vehicle.energy)
                            SpecDetailItem(
                                icon = Icons.Default.CheckCircle,
                                label = "สถานะ",
                                value = vehicle.status,
                                valueColor = if (vehicle.status == "Available") Color(0xFF2E7D32) else Color.Red
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // ── ปุ่มสั่งซื้อ ──
                    Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppDarkBlue),
                        shape = RoundedCornerShape(30.dp),
                        enabled = vehicle.status == "Available" && !isOrdering
                    ) {
                        if (isOrdering) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "สั่งซื้อรถคันนี้",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // ── Popup ยืนยันการสั่งซื้อ ──
            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    title = {
                        Text(
                            text = "ยืนยันการสั่งซื้อ",
                            fontWeight = FontWeight.Bold,
                            color = AppDarkBlue,
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "คุณต้องการสั่งซื้อรถรุ่นนี้ใช่หรือไม่?",
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${vehicle.brand} ${vehicle.model}",
                                fontWeight = FontWeight.Bold,
                                color = AppDarkBlue,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "฿ ${NumberFormat.getNumberInstance(Locale.US).format(vehicle.price)}",
                                color = AppLightBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "⚠ สถานะ: รอการชำระเงิน",
                                color = Color(0xFFE65100),
                                fontSize = 13.sp
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showConfirmDialog = false
                                isOrdering = true
                                orderViewModel.placeOrder(
                                    userEmail = UserSession.currentUserEmail,
                                    vehicle = vehicle
                                ) { isSuccess ->
                                    isOrdering = false
                                    if (isSuccess) {
                                        Toast.makeText(context, "สั่งซื้อสำเร็จ! กรุณาชำระเงิน", Toast.LENGTH_SHORT).show()
                                        onBackClick()
                                    } else {
                                        Toast.makeText(context, "เกิดข้อผิดพลาด ลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppDarkBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("ยืนยัน", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text("ยกเลิก", color = Color.Gray)
                        }
                    }
                )
            }
        }
    }
}

// Component สำหรับแสดงสเปคแต่ละรายการ
@Composable
fun SpecDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = Color.Black
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(AppLightBlue.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppLightBlue,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = Color.Gray)
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
        }
    }
}