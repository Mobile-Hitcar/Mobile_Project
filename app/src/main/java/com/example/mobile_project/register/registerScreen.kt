package com.example.mobile_project.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_project.R

@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 40.dp),
        // ❌ เอา verticalScroll ออกจากตรงนี้
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- ส่วนของโลโก้ ---
        Image(
            painter = painterResource(id = R.drawable.hitcar_template),
            contentDescription = "Hitcar Logo",
            modifier = Modifier.size(140.dp)
        )

        Text(
            text = "Create your Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00337C),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // --- ส่วนของฟอร์มพื้นหลังสีฟ้าอ่อน ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // ✅ ใส่ weight(1f) เพื่อบังคับให้กล่องนี้ดันลงไปจนสุดขอบจอด้านล่าง
                .background(
                    color = Color(0xFFE1F1FA),
                    shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp)
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()) // ✅ ย้าย verticalScroll มาไว้ด้านในนี้
            ) {
                // เผื่อขอบด้านบนนิดหน่อยกันฟอร์มชิดขอบโค้งเกินไป
                Spacer(modifier = Modifier.height(40.dp))

                RegisterTextField(label = "Name")
                Spacer(modifier = Modifier.height(12.dp))

                RegisterTextField(label = "Surname")
                Spacer(modifier = Modifier.height(12.dp))

                RegisterTextField(label = "Phone")
                Spacer(modifier = Modifier.height(12.dp))

                RegisterTextField(label = "Email")
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF00337C),
                        unfocusedBorderColor = Color(0xFF00337C)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { /* TODO: Register Logic */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00337C)),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ปุ่มกดกลับไปหน้า Login
                Row(modifier = Modifier.clickable { onNavigateToLogin() }) {
                    Text(text = "Already have an account? ", color = Color(0xFF00337C))
                    Text(
                        text = "Login",
                        color = Color(0xFF00337C),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // เผื่อที่ว่างด้านล่างสุดตอนที่ผู้ใช้เลื่อนหน้าจอลงมาสุด
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun RegisterTextField(label: String) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF00337C),
            unfocusedBorderColor = Color(0xFF00337C)
        )
    )
}