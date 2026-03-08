package com.example.mobile_project.vehicle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobile_project.R
import com.example.mobile_project.vehicle.VehicleEntity
import com.example.mobile_project.vehicle.VehicleViewModel
import com.example.mobile_project.vehicle.VehicleViewModelFactory
import java.text.NumberFormat
import java.util.Locale

// สีตาม Design
val AppLightBlue = Color(0xFF2FA2E9) // สีฟ้าสว่าง (Top Bar / Bottom Bar)
val AppDarkBlue = Color(0xFF1B3B6F) // สีน้ำเงินเข้ม (ปุ่มดูรายละเอียด)
val BgColor = Color(0xFFF6F8FA) // สีพื้นหลัง

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val vehicleViewModel: VehicleViewModel = viewModel(
        factory = VehicleViewModelFactory(context)
    )

    // ดึงข้อมูลรถจาก Database
    val vehicles by vehicleViewModel.allVehicles.collectAsState(initial = emptyList())

    // ลบ Scaffold ออกไปเลย ใช้แค่ LazyColumn
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), // ระยะห่างขอบซ้ายขวาของลิสต์รถ
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(vehicles) { vehicle ->
            CarItemCard(vehicle = vehicle)
        }
    }
}


@Composable
fun CustomTopAppBar(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF2FA2E9), // สีฟ้าตามดีไซน์
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
    ) {
        // 1. ดันพื้นที่ Status Bar
        Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars))

        // 2. ใช้ Box เพื่อให้โลโก้อยู่ตรงกลางเป๊ะๆ
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // ลดความสูงลงมาเพื่อให้แถบดูเล็กลงและขยับขึ้นไปอีก
                .padding(horizontal = 16.dp)
        ) {
            // ส่วนที่ 1: มุมซ้าย (ไอคอน Back iOS)
//            IconButton(
//                onClick = { onBackClick() },
//                modifier = Modifier.align(Alignment.CenterStart)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.ArrowBackIosNew,
//                    contentDescription = "Back",
//                    tint = Color.White,
//                    modifier = Modifier.size(24.dp)
//                )
//            }

            // ส่วนที่ 2: ตรงกลาง (โลโก้ HitCar)
            Image(
                painter = painterResource(id = R.drawable.hitcar_template),
                contentDescription = "HitCar Logo",
                modifier = Modifier
                    .height(32.dp)
                    .align(Alignment.Center), // สั่งให้อยู่ตรงกลาง Box
                contentScale = ContentScale.Fit
            )

            // ส่วนที่ 3: มุมขวา (ไอคอนโปรไฟล์)
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterEnd) // สั่งให้อยู่ชิดขวา
                    .background(Color.Black, CircleShape)
            )
        }
    }
}

@Composable
fun CarItemCard(vehicle: VehicleEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ส่วนบน: รูปภาพ + ข้อมูลสเปค
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // รูปภาพรถ (ตอนนี้เป็นกล่องว่างๆ รองรับ null ไปก่อน)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Car Image",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // สเปครถด้านขวา
                Column(
                    modifier = Modifier.width(130.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpecItem(icon = Icons.Default.Speed, text = "130000 km") // TODO: ยังไม่มีใน DB
                    SpecItem(icon = Icons.Default.Settings, text = vehicle.gear)
                    SpecItem(icon = Icons.Default.LocalGasStation, text = vehicle.energy)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // ส่วนล่าง: ชื่อ, ราคา + ปุ่มดูรายละเอียด
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
                        // จัดฟอร์แมตราคาให้มีลูกน้ำ เช่น 390,000
                        text = "฿ ${NumberFormat.getNumberInstance(Locale.US).format(vehicle.price)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }

                Button(
                    onClick = { /* TODO: ไปหน้า Detail */ },
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
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}


@Composable
fun CustomBottomNavBar(navController: NavController, currentRoute: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // 1. ใส่สีและขอบโค้ง
            .background(
                color = AppLightBlue,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            // 2. สั่งให้พื้นหลังขยายลงไปทับ Navigation Bar ด้านล่างสุด
            .windowInsetsPadding(WindowInsets.navigationBars)
            // 3. กำหนดความสูงของเมนู
            .height(72.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ปุ่มหน้าหลัก (Home)
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "หน้าหลัก",
            isSelected = currentRoute == "home", // เช็คว่าตอนนี้อยู่หน้า home หรือเปล่า
            onClick = {
                navController.navigate("home") {
                    // ป้องกันการเปิดหน้าซ้อนกันหลายๆ ชั้นเวลาผู้ใช้กดเมนูเดิมย้ำๆ
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // ปุ่มประวัติการสั่งซื้อ (History)
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

        // ปุ่มโปรไฟล์ (Profile)
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
fun BottomNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector,
                  label: String,
                  isSelected: Boolean,
                  onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
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