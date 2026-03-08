package com.example.mobile_project.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_project.R
import com.example.mobile_project.firebaseDB.UserViewModel // เช็ค import ให้ตรงกับโปรเจกต์คุณ
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.util.Log

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit, // ✅ เพิ่มพารามิเตอร์นี้สำหรับไปหน้า Home
    userViewModel: UserViewModel = viewModel()
) {
    // ✅ สร้าง State เก็บอีเมลและรหัสผ่าน
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current // เอาไว้ใช้โชว์ข้อความแจ้งเตือน

    val auth = FirebaseAuth.getInstance()
    // เอา Web client ID ที่ก๊อปปี้มาจาก Firebase Console มาใส่ตรงนี้
    val webClientId = "เอา key ใส่"

    // ตัวเรียกหน้าต่าง Login ของ Google และรับผลลัพธ์กลับมา
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        Toast.makeText(context, "Google Login สำเร็จ!", Toast.LENGTH_SHORT).show()
                        onNavigateToHome()
                    } else {
                        // ✅ ถ้า Firebase ปฏิเสธ จะแสดงข้อความตรงนี้
                        val errorMsg = authTask.exception?.message ?: "Unknown Error"
                        Toast.makeText(context, "Firebase Error: $errorMsg", Toast.LENGTH_LONG).show()
                        Log.e("LoginError", "Firebase Error: $errorMsg")
                    }
                }
            } catch (e: ApiException) {
                // ✅ ถ้าดึงข้อมูลจาก Google ไม่สำเร็จ จะโชว์รหัส Error ตรงนี้
                Toast.makeText(context, "Google Error Code: ${e.statusCode}", Toast.LENGTH_LONG).show()
                Log.e("LoginError", "Google API Error: ${e.statusCode}")
            }
        } else {
            Toast.makeText(context, "ยกเลิกการล็อกอิน", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- ส่วนของโลโก้ ---
        Icon(
            painter = painterResource(id = R.drawable.hitcar_template),
            contentDescription = "Hitcar Logo",
            modifier = Modifier.size(120.dp),
            tint = Color(0xFF00337C)
        )

        Text(
            text = "hitcar",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00337C),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "Login to your Account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00337C),
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // --- ส่วนของฟอร์ม ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xFFE1F1FA),
                    shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp)
                )
                .padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = email, // ✅ ผูกค่ากับ State
                    onValueChange = { email = it }, // ✅ อัปเดตค่าเมื่อพิมพ์
                    placeholder = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF00337C),
                        unfocusedBorderColor = Color(0xFF00337C)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password, // ✅ ผูกค่ากับ State
                    onValueChange = { password = it }, // ✅ อัปเดตค่าเมื่อพิมพ์
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
                    onClick = {
                        // ✅ เช็คการล็อกอินเมื่อกดปุ่ม
                        if(email.isNotEmpty() && password.isNotEmpty()){
                            userViewModel.loginUser(email, password) { isSuccess ->
                                if (isSuccess) {
                                    Toast.makeText(context, "Login สำเร็จ!", Toast.LENGTH_SHORT).show()
                                    onNavigateToHome() // ไปหน้า Home
                                } else {
                                    Toast.makeText(context, "Email หรือ Password ไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "กรุณากรอกข้อมูลให้ครบ", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00337C)),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { }) {
                    Text(
                        text = "Forget Password ?",
                        color = Color(0xFF00337C),
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
                    Text(
                        text = " or ",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = Color(0xFF00337C)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ✅ ปุ่ม Google ที่แก้ไขแล้วให้ใช้รูปภาพ
                Surface(
                    onClick = {
                        // สร้าง Options สำหรับขอข้อมูลอีเมลและ Token จาก Google
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(webClientId)
                            .requestEmail()
                            .build()

                        // สั่งให้ Google Client ทำงาน
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)

                        // บางครั้งอาจมีบัญชีค้างอยู่ สั่ง sign out ก่อนเพื่อความชัวร์ (ให้ผู้ใช้เลือกบัญชีใหม่ได้)
                        googleSignInClient.signOut().addOnCompleteListener {
                            // เปิดหน้าต่างให้ผู้ใช้เลือกบัญชี Google
                            launcher.launch(googleSignInClient.signInIntent)
                        }
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .size(50.dp)
                        .border(1.dp, Color.LightGray, CircleShape),
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.google_logo),
                            contentDescription = "Google Login"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.clickable { onNavigateToRegister() }) {
                    Text(text = "Don't have an account? ", color = Color(0xFF00337C))
                    Text(
                        text = "Register",
                        color = Color(0xFF00337C),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}