package com.example.mobile_project.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "ยังไม่มีประวัติการสั่งซื้อ",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "เลือกรถที่ชื่นชอบแล้วทำรายการได้เลย",
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(orders, key = { it.orderId }) { order ->
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

@Composable
fun OrderHistoryCard(
    order: Order,
    onPayClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("th"))
    val dateString = dateFormat.format(Date(order.timestamp))

    val isPaid = order.status == "ชำระเงินสำเร็จ"
    val statusBgColor = if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val statusTextColor = if (isPaid) Color(0xFF2E7D32) else Color(0xFFE65100)
    val statusIcon = if (isPaid) Icons.Default.CheckCircle else Icons.Default.AccessTime

    // State สำหรับ popup ยืนยันการลบ
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // รูปรถ
                AsyncImage(
                    model = order.vehicleImageUrl,
                    contentDescription = "Car Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.2f)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${order.vehicleBrand} ${order.vehicleModel}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppDarkBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "฿ ${NumberFormat.getNumberInstance(Locale.US).format(order.vehiclePrice)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Badge สถานะ
                    Box(
                        modifier = Modifier
                            .background(color = statusBgColor, shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = statusIcon,
                                contentDescription = null,
                                tint = statusTextColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = order.status,
                                fontSize = 12.sp,
                                color = statusTextColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = dateString, fontSize = 11.sp, color = Color.Gray)
                }
            }

            // ปุ่มด้านล่าง — แสดงเฉพาะเมื่อยังไม่ได้ชำระ
            if (!isPaid) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ✅ ปุ่มลบ (ซ้าย)
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD32F2F))
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "ลบ",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "ลบรายการ", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // ปุ่มชำระเงิน (ขวา)
                    Button(
                        onClick = onPayClick,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(text = "ชำระเงิน", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // ✅ Popup ยืนยันการลบ
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "ยืนยันการลบรายการ",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F),
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text("คุณต้องการลบคำสั่งซื้อนี้ใช่หรือไม่?", color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${order.vehicleBrand} ${order.vehicleModel}",
                        fontWeight = FontWeight.Bold,
                        color = AppDarkBlue,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "การลบจะไม่สามารถกู้คืนได้",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ลบรายการ", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("ยกเลิก", color = Color.Gray)
                }
            }
        )
    }
}