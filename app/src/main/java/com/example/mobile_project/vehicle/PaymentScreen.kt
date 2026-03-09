package com.example.mobile_project.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    var selectedMethod by remember { mutableStateOf("โอนเงินผ่านบัญชี") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // ── สรุปคำสั่งซื้อ ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp)
        ) {
            Text(
                text = "สรุปคำสั่งซื้อ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppDarkBlue
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$vehicleBrand $vehicleModel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "หมายเลขคำสั่งซื้อ: ...${orderId.takeLast(6)}", fontSize = 12.sp, color = Color.Gray)
                }
                Text(
                    text = "฿${NumberFormat.getNumberInstance(Locale.US).format(vehiclePrice)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "ยอดรวม", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(
                    text = "฿${NumberFormat.getNumberInstance(Locale.US).format(vehiclePrice)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── เลือกวิธีชำระเงิน ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp)
        ) {
            Text(
                text = "เลือกวิธีชำระเงิน",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppDarkBlue
            )
            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodOption(
                icon = Icons.Default.QrCode,
                label = "QR Code / พร้อมเพย์",
                isSelected = selectedMethod == "QR Code / พร้อมเพย์",
                onClick = { selectedMethod = "QR Code / พร้อมเพย์" }
            )
            Spacer(modifier = Modifier.height(10.dp))
            PaymentMethodOption(
                icon = Icons.Default.CreditCard,
                label = "บัตรเครดิต / เดบิต",
                isSelected = selectedMethod == "บัตรเครดิต / เดบิต",
                onClick = { selectedMethod = "บัตรเครดิต / เดบิต" }
            )
            Spacer(modifier = Modifier.height(10.dp))
            PaymentMethodOption(
                icon = Icons.Default.CheckCircle,
                label = "โอนเงินผ่านบัญชี",
                isSelected = selectedMethod == "โอนเงินผ่านบัญชี",
                onClick = { selectedMethod = "โอนเงินผ่านบัญชี" }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── ปุ่มยืนยันชำระเงิน ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp)
        ) {
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(30.dp),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("ยืนยันการชำระเงิน", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // ── Popup ยืนยันการชำระเงิน ──
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("ยืนยันการชำระเงิน", fontWeight = FontWeight.Bold, color = AppDarkBlue, fontSize = 18.sp)
            },
            text = {
                Column {
                    Text("คุณต้องการชำระเงินสำหรับ", color = Color.DarkGray)
                    Text(
                        "$vehicleBrand $vehicleModel",
                        fontWeight = FontWeight.Bold,
                        color = AppDarkBlue,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "ยอดชำระ: ฿${NumberFormat.getNumberInstance(Locale.US).format(vehiclePrice)}",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "ผ่าน: $selectedMethod",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        isProcessing = true
                        orderViewModel.updateOrderStatus(
                            orderId = orderId,
                            newStatus = "ชำระเงินสำเร็จ"
                        ) { isSuccess ->
                            isProcessing = false
                            if (isSuccess) {
                                onPaymentSuccess()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
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

@Composable
fun PaymentMethodOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) AppDarkBlue else Color.LightGray
    val bgColor = if (isSelected) AppDarkBlue.copy(alpha = 0.06f) else Color.White

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = if (isSelected) 2.dp else 1.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) AppDarkBlue else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) AppDarkBlue else Color.DarkGray,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AppDarkBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}