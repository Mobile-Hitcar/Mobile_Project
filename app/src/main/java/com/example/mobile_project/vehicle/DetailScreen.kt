package com.example.mobile_project.vehicle

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.mobile_project.firebaseDB.VehicleViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DetailScreen(vehicleId: String, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val vehicleViewModel: VehicleViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()

    val vehicles by vehicleViewModel.allVehicles.collectAsState(initial = emptyList())
    val vehicle = vehicles.find { it.id == vehicleId }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var isOrdering by remember { mutableStateOf(false) }

    // entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(vehicleId) { visible = true }
    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, easing = EaseOutCubic), label = "da"
    )

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        if (vehicle == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppDarkBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(contentAlpha)
                    .verticalScroll(rememberScrollState())
            ) {
                // รูปรถ + gradient overlay
                Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                    AsyncImage(
                        model = vehicle.imageUrl,
                        contentDescription = "Car Image",
                        modifier = Modifier.fillMaxSize().background(Color.LightGray.copy(0.2f)),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.BottomStart)
                            .background(
                                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.5f)))
                            )
                    )
                    // ชื่อรถ + ราคา บนรูป
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)
                    ) {
                        Text(
                            "${vehicle.brand} ${vehicle.model}",
                            fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White
                        )
                        Text(
                            "฿ ${NumberFormat.getNumberInstance(Locale.US).format(vehicle.price)}",
                            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7DC8F5)
                        )
                    }
                }

                // Content panel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(Color.White)
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    // Section: Specifications
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(4.dp).height(20.dp)
                                .background(AppLightBlue, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Specifications",
                            fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AppDarkBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2x3 spec grid
                    val specs = listOf(
                        Triple(Icons.Default.DirectionsCar, "Type", vehicle.segment),
                        Triple(Icons.Default.MeetingRoom, "Doors", "${vehicle.door} doors"),
                        Triple(Icons.Default.AirlineSeatReclineNormal, "Seats", "${vehicle.seats} seats"),
                        Triple(Icons.Default.LocalGasStation, "Fuel", vehicle.energy),
                        Triple(Icons.Default.Settings, "Transmission", vehicle.gear),
                        Triple(
                            if (vehicle.status == "Available") Icons.Default.CheckCircle else Icons.Default.Cancel,
                            "Status",
                            vehicle.status
                        )
                    )

                    specs.chunked(2).forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            row.forEach { (icon, label, value) ->
                                val isStatus = label == "Status"
                                val valueColor = when {
                                    isStatus && value == "Available" -> Color(0xFF2E7D32)
                                    isStatus -> Color(0xFFD32F2F)
                                    else -> AppDarkBlue
                                }
                                SpecDetailItem(
                                    icon = icon, label = label, value = value,
                                    valueColor = valueColor,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size < 2) Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(24.dp))

                    // ปุ่มสั่งซื้อ
                    Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppDarkBlue,
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(28.dp),
                        enabled = vehicle.status == "Available" && !isOrdering,
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        AnimatedContent(
                            targetState = isOrdering,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "btn"
                        ) { loading ->
                            if (loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Order This Car", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    if (vehicle.status != "Available") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "This car is currently unavailable",
                            fontSize = 12.sp, color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Popup Confirmสั่งซื้อ
            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, null, tint = AppDarkBlue, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirm Order", fontWeight = FontWeight.Bold, color = AppDarkBlue, fontSize = 17.sp)
                        }
                    },
                    text = {
                        Column {
                            Text("Would you like to order this car?", color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BgColor, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text("${vehicle.brand} ${vehicle.model}", fontWeight = FontWeight.Bold, color = AppDarkBlue, fontSize = 15.sp)
                                    Text("฿ ${NumberFormat.getNumberInstance(Locale.US).format(vehicle.price)}", color = AppLightBlue, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, null, tint = Color(0xFFE65100), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Statusเริ่มต้น: รอการPay Now", fontSize = 12.sp, color = Color(0xFFE65100))
                            }
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
                                        Toast.makeText(context, "Order placed! Please proceed to payment.", Toast.LENGTH_SHORT).show()
                                        onBackClick()
                                    } else {
                                        Toast.makeText(context, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppDarkBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Confirm", fontWeight = FontWeight.Bold) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SpecDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = AppDarkBlue,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(AppLightBlue.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = AppLightBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}