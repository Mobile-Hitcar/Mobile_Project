package com.example.mobile_project.editprofile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.mobile_project.firebaseDB.UserViewModel // เช็ค import ด้วยนะครับ
import com.example.mobile_project.firebaseDB.UserSession

@Composable
fun ProfileScreen(userViewModel: UserViewModel = viewModel()) {
    val context = LocalContext.current

    // ✅ ดึงอีเมลจาก Session ของเราเอง แทนที่จะใช้ FirebaseAuth
    val loggedInEmail = UserSession.currentUserEmail

    // สร้าง State สำหรับเก็บข้อมูลในฟอร์ม (รวมถึงอีเมลด้วย)
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // ดึงข้อมูลจาก Firebase อัตโนมัติเมื่อเปิดหน้านี้
    LaunchedEffect(loggedInEmail) {
        if (loggedInEmail.isNotEmpty()) {
            userViewModel.fetchUserData(loggedInEmail) { user ->
                if (user != null) {
                    email = user.email // ดึงอีเมลมาใส่ช่อง
                    firstName = user.firstName
                    lastName = user.lastName
                    phone = user.phone
                }
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF6F8FA))
    ) {
        // --- ส่วนหัว (Profile Icon) ---
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(shape = CircleShape, color = Color(0xFFE1F1FA), modifier = Modifier.size(120.dp)) {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF00337C), modifier = Modifier.padding(24.dp))
            }
        }

        // --- ส่วนฟอร์มแก้ไขข้อมูล ---
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f).background(color = Color.White, shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp).verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    Text(text = "Edit Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00337C), modifier = Modifier.padding(bottom = 24.dp))

                    // ✅ ปลดล็อคให้แก้ Email ได้แล้ว!
                    EditTextField(label = "Email", value = email, onValueChange = { email = it })
                    Spacer(modifier = Modifier.height(16.dp))

                    EditTextField(label = "First Name", value = firstName, onValueChange = { firstName = it })
                    Spacer(modifier = Modifier.height(16.dp))

                    EditTextField(label = "Last Name", value = lastName, onValueChange = { lastName = it })
                    Spacer(modifier = Modifier.height(16.dp))

                    EditTextField(label = "Phone Number", value = phone, onValueChange = { phone = it })
                    Spacer(modifier = Modifier.height(40.dp))

                    // ปุ่มกดบันทึก
                    Button(
                        onClick = {
                            userViewModel.updateUserData(
                                oldEmail = loggedInEmail, // ใช้อีเมลเดิมค้นหา
                                newEmail = email,         // อีเมลใหม่ที่พิมพ์ลงไป
                                newFirstName = firstName,
                                newLastName = lastName,
                                newPhone = phone
                            ) { isSuccess ->
                                if (isSuccess) {
                                    UserSession.currentUserEmail = email // ✅ อัปเดต Session ให้จำอีเมลใหม่ด้วย
                                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to update profile.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00337C)),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

// UI Component สำหรับช่องกรอกข้อความ (เหมือนเดิม)
@Composable
fun EditTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF00337C), unfocusedBorderColor = Color.Gray
        )
    )
}