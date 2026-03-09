package com.example.mobile_project.vehicle

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobile_project.firebaseDB.Order
import com.example.mobile_project.firebaseDB.OrderViewModel
import com.example.mobile_project.firebaseDB.UserSession
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(navController: NavController) {
    val orderViewModel: OrderViewModel = viewModel()
    val userEmail = UserSession.currentUserEmail

    val orders by orderViewModel
        .getOrdersByUser(userEmail)
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().background(BgColor)
    ) {
        AnimatedContent(
            targetState = orders.isEmpty(),
            transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
            label = "history_content"
        ) { isEmpty ->
            if (isEmpty) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = Color(0xFFCDD5DD),
                            modifier = Modifier.size(90.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No orders yet",
                            fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Browse cars and place your first order",
                            fontSize = 13.sp, color = Color.LightGray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(orders, key = { _, o -> o.orderId }) { index, order ->
                        var appeared by remember { mutableStateOf(false) }
                        LaunchedEffect(order.orderId) {
                            kotlinx.coroutines.delay(index * 70L)
                            appeared = true
                        }
                        val alpha by animateFloatAsState(
                            targetValue = if (appeared) 1f else 0f,
                            animationSpec = tween(350, easing = EaseOutCubic), label = "oa$index"
                        )
                        val slide by animateFloatAsState(
                            targetValue = if (appeared) 0f else 20f,
                            animationSpec = tween(350, easing = EaseOutCubic), label = "os$index"
                        )
                        Box(modifier = Modifier.alpha(alpha).offset(y = slide.dp)) {
                            OrderHistoryCard(
                                order = order,
                                onPayClick = {
                                    navController.navigate(
                                        "payment/${order.orderId}/${order.vehicleBrand}/${order.vehicleModel}/${order.vehiclePrice}"
                                    )
                                },
                                onDeleteClick = {
                                    orderViewModel.deleteOrder(order.orderId) {}
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(
    order: Order,
    onPayClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy  •  HH:mm น.", Locale("th"))
    val dateString = dateFormat.format(Date(order.timestamp))

    val isPaid = order.status == "Payment Successful"
    val statusBg = if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val statusColor = if (isPaid) Color(0xFF2E7D32) else Color(0xFFE65100)
    val statusIcon = if (isPaid) Icons.Default.CheckCircle else Icons.Default.AccessTime

    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // รูปรถ + gradient overlay
            Box(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                AsyncImage(
                    model = order.vehicleImageUrl,
                    contentDescription = "Car",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
                // gradient overlay ด้านล่างรูป
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                            )
                        )
                )
                // ชื่อรถบนรูป
                Text(
                    text = "${order.vehicleBrand} ${order.vehicleModel}",
                    modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White
                )
                // Badge Status
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(statusBg, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(order.status, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ข้อมูลด้านล่าง
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "฿ ${NumberFormat.getNumberInstance(Locale.US).format(order.vehiclePrice)}",
                        fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = AppDarkBlue
                    )
                    Text(text = dateString, fontSize = 11.sp, color = Color.Gray)
                }

                // ปุ่ม — แสดงเฉพาะยังไม่ชำระ
                if (!isPaid) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // ปุ่มลบ
                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(21.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD32F2F))
                            )
                        ) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        // ปุ่มPay Now
                        Button(
                            onClick = onPayClick,
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(21.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Icon(Icons.Default.Payment, null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pay Now", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeleteForever, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Delete", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 17.sp)
                }
            },
            text = {
                Column {
                    Text("Are you sure you want to delete this order?", color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("${order.vehicleBrand} ${order.vehicleModel}", fontWeight = FontWeight.Bold, color = AppDarkBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("This action cannot be undone.", fontSize = 12.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false; onDeleteClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Delete", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}