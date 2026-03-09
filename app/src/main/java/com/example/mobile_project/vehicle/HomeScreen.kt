package com.example.mobile_project.vehicle

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobile_project.R
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.mobile_project.firebaseDB.VehicleEntity
import com.example.mobile_project.firebaseDB.VehicleViewModel
import coil.compose.AsyncImage

// สีตาม Design
val AppLightBlue = Color(0xFF2FA2E9)
val AppDarkBlue = Color(0xFF1B3B6F)
val BgColor = Color(0xFFF6F8FA)

@Composable
fun HomeScreen(navController: NavController) {
    val vehicleViewModel: VehicleViewModel = viewModel()
    val vehicles by vehicleViewModel.allVehicles.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(vehicles) { vehicle ->
                CarItemCard(
                    vehicle = vehicle,
                    onDetailClick = {
                        navController.navigate("detail/${vehicle.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun CustomTopAppBar(
    onBackClick: () -> Unit,
    showBackButton: Boolean = false, // ✅ เพิ่มพารามิเตอร์ควบคุมปุ่มย้อนกลับ
    onLogoutClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF2FA2E9),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
    ) {
        Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
        ) {
            // ✅ ปุ่มย้อนกลับ (มุมซ้ายบน) — แสดงเฉพาะเมื่อ showBackButton = true
            if (showBackButton) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(28.dp)
                        .clickable { onBackClick() }
                )
            }

            // โลโก้ HitCar (ตรงกลาง)
            Image(
                painter = painterResource(id = R.drawable.hitcar_template),
                contentDescription = "HitCar Logo",
                modifier = Modifier
                    .height(32.dp)
                    .align(Alignment.Center),
                contentScale = ContentScale.Fit
            )

            // ไอคอนโปรไฟล์ (มุมขวา)
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black, CircleShape)
                        .clickable { showMenu = true }
                )

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        onClick = {
                            showMenu = false
                            showLogoutDialog = true
                        }
                    )
                }
            }
        }
    }

    // Dialog ยืนยัน Logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "ยืนยันการออกจากระบบ", fontWeight = FontWeight.Bold) },
            text = { Text("คุณต้องการออกจากระบบใช่หรือไม่?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogoutClick()
                }) {
                    Text("ออกจากระบบ", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("ยกเลิก", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun CarItemCard(vehicle: VehicleEntity, onDetailClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AsyncImage(
                    model = vehicle.imageUrl,
                    contentDescription = "Car Image",
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f)),
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.width(130.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpecItem(icon = Icons.Default.Settings, text = vehicle.gear)
                    SpecItem(icon = Icons.Default.LocalGasStation, text = vehicle.energy)
                    SpecItem(icon = Icons.Default.AirlineSeatReclineNormal, text = "${vehicle.seats} ที่นั่ง")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "${vehicle.brand} ${vehicle.model}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "฿ ${NumberFormat.getNumberInstance(Locale.US).format(vehicle.price)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }

                Button(
                    onClick = onDetailClick, // ✅ เชื่อมกับ Navigation
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppDarkBlue),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(text = "ดูรายละเอียด", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun SpecItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 12.sp, color = Color.DarkGray)
    }
}

@Composable
fun CustomBottomNavBar(navController: NavController, currentRoute: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AppLightBlue,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(72.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "หน้าหลัก",
            isSelected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // ✅ ปุ่มประวัติการสั่งซื้อ
        BottomNavItem(
            icon = Icons.Default.ReceiptLong,
            label = "ประวัติการสั่งซื้อ",
            isSelected = currentRoute == "history",
            onClick = {
                navController.navigate("history") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        BottomNavItem(
            icon = Icons.Default.PersonOutline,
            label = "โปรไฟล์",
            isSelected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.6f),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.6f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}