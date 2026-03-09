package com.example.mobile_project.vehicle

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_project.firebaseDB.OrderViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PaymentScreen(
    orderId: String,
    vehicleBrand: String,
    vehicleModel: String,
    vehiclePrice: Int,
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val orderViewModel: OrderViewModel = viewModel()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var selectedMethod by remember { mutableStateOf("Bank Transfer") }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400, easing = EaseOutCubic), label = "pa"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .background(BgColor)
            .verticalScroll(rememberScrollState())
    ) {
        // ── สรุปOrders ──
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp).height(18.dp)
                            .background(AppLightBlue, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Order Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppDarkBlue)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "$vehicleBrand $vehicleModel",
                            fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AppDarkBlue
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            "Order ID: #${orderId.takeLast(6).uppercase()}",
                            fontSize = 12.sp, color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.LightGray.copy(0.4f))
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Amount", fontSize = 15.sp, color = Color.DarkGray)
                    Text(
                        "฿${NumberFormat.getNumberInstance(Locale.US).format(vehiclePrice)}",
                        fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = AppDarkBlue
                    )
                }
            }
        }

        // ── เลือกวิธีPay Now ──
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp).height(18.dp)
                            .background(AppLightBlue, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Payment Method", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppDarkBlue)
                }
                Spacer(modifier = Modifier.height(16.dp))

                listOf(
                    Triple(Icons.Default.QrCode, "QR Code / PromptPay", "Instant scan & pay"),
                    Triple(Icons.Default.CreditCard, "Credit / Debit Card", "Visa, Mastercard, JCB"),
                    Triple(Icons.Default.AccountBalance, "Bank Transfer", "All major banks")
                ).forEach { (icon, label, sub) ->
                    val selected = selectedMethod == label
                    Surface(
                        onClick = { selectedMethod = label },
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) AppDarkBlue.copy(alpha = 0.07f) else Color(0xFFF8FAFB),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        border = if (selected) ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(AppDarkBlue),
                            width = 2.dp
                        ) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        if (selected) AppDarkBlue.copy(0.12f) else Color.White,
                                        RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    icon, null,
                                    tint = if (selected) AppDarkBlue else Color.Gray,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    label, fontSize = 14.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) AppDarkBlue else Color.DarkGray
                                )
                                Text(sub, fontSize = 11.sp, color = Color.Gray)
                            }
                            AnimatedVisibility(visible = selected) {
                                Icon(Icons.Default.CheckCircle, null, tint = AppDarkBlue, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── ปุ่มConfirm ──
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(28.dp),
                enabled = !isProcessing,
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                AnimatedContent(
                    targetState = isProcessing,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "pb"
                ) { loading ->
                    if (loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ConfirmการPay Now", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payment, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ConfirmการPay Now", fontWeight = FontWeight.Bold, color = AppDarkBlue, fontSize = 17.sp)
                }
            },
            text = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgColor, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text("$vehicleBrand $vehicleModel", fontWeight = FontWeight.Bold, color = AppDarkBlue)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "฿${NumberFormat.getNumberInstance(Locale.US).format(vehiclePrice)}",
                                fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalance, null, tint = AppLightBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Via: $selectedMethod", fontSize = 13.sp, color = Color.DarkGray)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        isProcessing = true
                        orderViewModel.updateOrderStatus(orderId, "Payment Successful") { ok ->
                            isProcessing = false
                            if (ok) onPaymentSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("ConfirmPay Now", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }
}