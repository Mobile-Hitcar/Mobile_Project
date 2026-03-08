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

import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import com.example.mobile_project.firebaseDB.VehicleEntity
import com.example.mobile_project.firebaseDB.VehicleViewModel
import coil.compose.AsyncImage

// สีตาม Design
val AppLightBlue = Color(0xFF2FA2E9) // สีฟ้าสว่าง (Top Bar / Bottom Bar)
val AppDarkBlue = Color(0xFF1B3B6F) // สีน้ำเงินเข้ม (ปุ่มดูรายละเอียด)
val BgColor = Color(0xFFF6F8FA) // สีพื้นหลัง

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val vehicleViewModel: VehicleViewModel = viewModel()

    val vehicles by vehicleViewModel.allVehicles.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {

        // ปุ่มเสกข้อมูล (เพิ่มการส่ง context ไปด้วยเพื่อใช้โชว์แจ้งเตือน)
        Button(
            onClick = { vehicleViewModel.addMockData(context) },
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Text("เสกข้อมูลจำลอง (Mock Data)")
        }

        // รายการรถ
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(vehicles) { vehicle ->
                CarItemCard(vehicle = vehicle)
            }
        }
    }

    // ❌ โค้ด LazyColumn อันล่างสุดที่เคยอยู่ตรงนี้ถูกลบทิ้งไปแล้ว เพื่อไม่ให้มันบังหน้าจอครับ ❌
}


@Composable
fun CustomTopAppBar(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit = {} // ✅ 1. เพิ่มพารามิเตอร์นี้สำหรับสั่ง Logout
) {
    // ✅ 2. สร้าง State สำหรับควบคุมการแสดงผล เมนู และ กล่องยืนยัน
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
            // โลโก้ HitCar (ตรงกลาง)
            Image(
                painter = painterResource(id = R.drawable.hitcar_template),
                contentDescription = "HitCar Logo",
                modifier = Modifier
                    .height(32.dp)
                    .align(Alignment.Center),
                contentScale = ContentScale.Fit
            )

            // ✅ 3. ส่วนของไอคอนโปรไฟล์ (มุมขวา) ที่กดแล้วมีเมนูโผล่มา
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black, CircleShape)
                        .clickable { showMenu = true } // พอกดแล้วให้โชว์เมนู
                )

                // เมนู Dropdown
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        onClick = {
                            showMenu = false
                            showLogoutDialog = true // กด Logout ให้ปิดเมนู แล้วโชว์ Dialog
                        }
                    )
                }
            }
        }
    }

    // ✅ 4. กล่อง Dialog ถามยืนยันการออกจากระบบ
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(text = "ยืนยันการออกจากระบบ", fontWeight = FontWeight.Bold)
            },
            text = { Text("คุณต้องการออกจากระบบใช่หรือไม่?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogoutClick() // เรียกใช้ฟังก์ชัน Logout ที่ส่งมาจาก MainActivity
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
                // ✅ เปลี่ยนตรงนี้เป็น AsyncImage
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
        modifier = Modifier
            .clickable { onClick() } // ✅ 1. เพิ่มบรรทัดนี้เข้าไปเพื่อให้กดได้
            .padding(8.dp)           // (ต้องวาง clickable ไว้ก่อน padding นะครับ เพื่อให้พื้นที่กดกว้างขึ้น)
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