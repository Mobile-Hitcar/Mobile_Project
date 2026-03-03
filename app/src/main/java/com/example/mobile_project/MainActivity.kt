package com.example.mobile_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_project.login.LoginScreen
import com.example.mobile_project.register.RegisterScreen

// --- 1. สร้าง Enum สำหรับหน้าจอ ---
enum class Screen {
    Welcome, Login, Register
}

// --- 2. สร้าง ViewModel สำหรับจัดการ Navigation ---
class NavigationViewModel : ViewModel() {
    var currentScreen by mutableStateOf(Screen.Welcome)
        private set

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // เรียกใช้งาน ViewModel
            val navigationViewModel: NavigationViewModel = viewModel()

            // ตรวจสอบ State จาก ViewModel เพื่อแสดงหน้าจอ
            when (navigationViewModel.currentScreen) {
                Screen.Welcome -> WelcomeScreen(
                    onLoginClick = { navigationViewModel.navigateTo(Screen.Login) },
                    onRegisterClick = { navigationViewModel.navigateTo(Screen.Register) }
                )
                Screen.Login -> LoginScreen(
                    onNavigateToRegister = { navigationViewModel.navigateTo(Screen.Register) }
                )
                Screen.Register -> RegisterScreen(
                    onNavigateToLogin = { navigationViewModel.navigateTo(Screen.Login) }
                )
            }
        }
    }
}

// กำหนดสีตามภาพ Design
val TopBackgroundColor = Color(0xFFF7F8FA)
val PrimaryDarkBlue = Color(0xFF0D3D82)
val BottomPanelBlue = Color(0xFF2FA2E9)

@Composable
fun WelcomeScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TopBackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- ส่วนบน (โลโก้ และ ข้อความ) ใช้แบบเดิมตามที่คุณต้องการ ---
        Spacer(modifier = Modifier.height(80.dp))

        Image(
            painter = painterResource(id = R.drawable.hitcar_template),
            contentDescription = "HitCar Logo",
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Let's find your\nperfect car!",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryDarkBlue,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp
        )

        // Spacer ตัวนี้จะทำหน้าที่ดันกรอบสีฟ้าให้ลงไปอยู่ด้านล่างสุด
        Spacer(modifier = Modifier.weight(1f))

        // --- ส่วนล่าง (กล่องสีฟ้าและปุ่มกด) ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BottomPanelBlue,
            shape = RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // ปรับให้กรอบสีฟ้าสูงขึ้น เหมาะกับจอ Pixel 9a
                    // เพิ่ม top เป็น 72.dp และ bottom เป็น 110.dp (เดิม 56.dp)
                    .padding(top = 72.dp, bottom = 110.dp, start = 32.dp, end = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hello",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Welcome To HitCar, where\nyou can find your perfect car",
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(48.dp))

                // ปุ่ม Login - ปรับเป็น fillMaxWidth() ให้อยู่ในกรอบ padding จะได้สมมาตร
                Button(
                    onClick = { onLoginClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkBlue),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Login",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ปุ่ม Register
                Button(
                    onClick = { onRegisterClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Register",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkBlue
                    )
                }
            }
        }
    }
}