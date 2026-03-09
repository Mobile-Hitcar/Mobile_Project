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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_project.R
import com.example.mobile_project.firebaseDB.UserViewModel
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    userViewModel: UserViewModel = viewModel() // ✅ 1. รับ ViewModel เข้ามาใช้งาน
) {
    // ✅ 2. สร้างตัวแปร State เพื่อเก็บค่าที่ผู้ใช้พิมพ์
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // ✅ เพิ่มตัวแปรนี้เข้าไป
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- ส่วนของโลโก้ ---
        Image(
            painter = painterResource(id = R.drawable.hitcar_template),
            contentDescription = "Hitcar Logo",
            modifier = Modifier.size(140.dp)
        )

        Text(
            text = "Create Your Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00337C),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // --- ส่วนของฟอร์มพื้นหลังสีฟ้าอ่อน ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
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
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // ✅ 3. ส่งค่า state และฟังก์ชันอัปเดตค่าไปยัง TextField
                RegisterTextField(
                    label = "Name",
                    value = firstName,
                    onValueChange = { firstName = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                RegisterTextField(
                    label = "Surname",
                    value = lastName,
                    onValueChange = { lastName = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                RegisterTextField(
                    label = "Phone",
                    value = phone,
                    onValueChange = { phone = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                RegisterTextField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // ✅ 4. ผูก State กับช่องรหัสผ่าน
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    // ✅ สลับการแสดงผลตัวอักษร
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        // ✅ สลับไอคอนและทำให้กดได้
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF00337C),
                        unfocusedBorderColor = Color(0xFF00337C)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ✅ 5. สั่งบันทึกข้อมูลเมื่อกดปุ่ม Register
                Button(
                    onClick = {
                        // เรียกใช้ฟังก์ชันจาก ViewModel เพื่อบันทึกลง Firestore
                        userViewModel.registerUser(
                            email = email,
                            password = password,
                            firstName = firstName,
                            lastName = lastName,
                            phone = phone
                        )
                        // เมื่อสมัครเสร็จ สั่งให้เด้งกลับไปหน้า Login
                        onNavigateToLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00337C)),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.clickable { onNavigateToLogin() }) {
                    Text(text = "Already have an account? ", color = Color(0xFF00337C))
                    Text(
                        text = "Login",
                        color = Color(0xFF00337C),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// ✅ 6. แก้ไข UI Component ตัวนี้ให้รับค่า value และ onValueChange เพื่อให้มันอัปเดตข้อมูลได้จริง
@Composable
fun RegisterTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
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