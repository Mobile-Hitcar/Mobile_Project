package com.example.mobile_project.vehicle

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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

val AppLightBlue = Color(0xFF2FA2E9)
val AppDarkBlue = Color(0xFF1B3B6F)
val BgColor = Color(0xFFF6F8FA)

@Composable
fun HomeScreen(navController: NavController) {
    val vehicleViewModel: VehicleViewModel = viewModel()
    val vehicles by vehicleViewModel.allVehicles.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().background(BgColor)) {
        if (vehicles.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppDarkBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(vehicles) { index, vehicle ->
                    // staggered entrance animation
                    var appeared by remember { mutableStateOf(false) }
                    LaunchedEffect(vehicle.id) {
                        kotlinx.coroutines.delay(index * 60L)
                        appeared = true
                    }
                    val itemAlpha by animateFloatAsState(
                        targetValue = if (appeared) 1f else 0f,
                        animationSpec = tween(350, easing = EaseOutCubic), label = "ia$index"
                    )
                    val itemSlide by animateFloatAsState(
                        targetValue = if (appeared) 0f else 24f,
                        animationSpec = tween(350, easing = EaseOutCubic), label = "is$index"
                    )
                    Box(
                        modifier = Modifier
                            .alpha(itemAlpha)
                            .offset(y = itemSlide.dp)
                    ) {
                        CarItemCard(vehicle = vehicle, onDetailClick = {
                            navController.navigate("detail/${vehicle.id}")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun CustomTopAppBar(
    onBackClick: () -> Unit,
    showBackButton: Boolean = false,
    onLogoutClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, spotColor = AppLightBlue.copy(alpha = 0.3f))
            .background(
                Brush.horizontalGradient(listOf(Color(0xFF2FA2E9), Color(0xFF1B3B6F))),
                RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
    ) {
        Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
        ) {
            if (showBackButton) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.hitcar_template),
                contentDescription = "HitCar Logo",
                modifier = Modifier.height(28.dp).align(Alignment.Center),
                contentScale = ContentScale.Fit
            )

            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .clickable { showMenu = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null) },
                        onClick = { showMenu = false; showLogoutDialog = true }
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Logout", fontWeight = FontWeight.Bold, color = AppDarkBlue) },
            text = { Text("Are you sure you want to logout?", color = Color.DarkGray) },
            confirmButton = {
                Button(
                    onClick = { showLogoutDialog = false; onLogoutClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Logout", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun CarItemCard(vehicle: VehicleEntity, onDetailClick: () -> Unit = {}) {
    var pressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "cs"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp, pressedElevation = 1.dp)
    ) {
        Column {
            // รูปรถ — full width บนสุด
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                AsyncImage(
                    model = vehicle.imageUrl,
                    contentDescription = "Car Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(Color.LightGray.copy(alpha = 0.2f)),
                    contentScale = ContentScale.Crop
                )
                // Status badge บนรูป
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(
                            if (vehicle.status == "Available") Color(0xFF2E7D32) else Color(0xFFD32F2F),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = vehicle.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // ชื่อรถ + ราคา
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${vehicle.brand} ${vehicle.model}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppDarkBlue
                        )
                        Text(
                            text = vehicle.segment,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "฿${NumberFormat.getNumberInstance(Locale.US).format(vehicle.price)}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AppLightBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // สเปค chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SpecChip(icon = Icons.Default.Settings, text = vehicle.gear)
                    SpecChip(icon = Icons.Default.LocalGasStation, text = vehicle.energy)
                    SpecChip(icon = Icons.Default.AirlineSeatReclineNormal, text = "${vehicle.seats} seats")
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ปุ่มView Details — full width
                Button(
                    onClick = {
                        pressed = true
                        onDetailClick()
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppDarkBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(text = "View Details", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun SpecChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .background(BgColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = AppLightBlue, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
    }
}

// SpecItem เดิม — เผื่อใช้ที่อื่น
@Composable
fun SpecItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 12.sp, color = Color.DarkGray)
    }
}

@Composable
fun CustomBottomNavBar(navController: NavController, currentRoute: String?) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier
            .shadow(elevation = 12.dp, spotColor = Color.Black.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true; restoreState = true
                }
            },
            icon = {
                Icon(
                    if (currentRoute == "home") Icons.Default.Home else Icons.Default.Home,
                    contentDescription = null
                )
            },
            label = { Text("Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AppDarkBlue,
                selectedTextColor = AppDarkBlue,
                indicatorColor = AppLightBlue.copy(alpha = 0.15f),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = currentRoute == "history",
            onClick = {
                navController.navigate("history") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true; restoreState = true
                }
            },
            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
            label = { Text("Orders", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AppDarkBlue,
                selectedTextColor = AppDarkBlue,
                indicatorColor = AppLightBlue.copy(alpha = 0.15f),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true; restoreState = true
                }
            },
            icon = { Icon(Icons.Default.PersonOutline, contentDescription = null) },
            label = { Text("Profile", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AppDarkBlue,
                selectedTextColor = AppDarkBlue,
                indicatorColor = AppLightBlue.copy(alpha = 0.15f),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}

// เก็บ BottomNavItem ไว้เผื่อใช้
@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AppDarkBlue else Color.Gray,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label, fontSize = 11.sp,
            color = if (isSelected) AppDarkBlue else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}