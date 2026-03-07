package com.example.mobile_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobile_project.login.LoginScreen
import com.example.mobile_project.register.RegisterScreen
import com.example.mobile_project.vehicle.CustomBottomNavBar
import com.example.mobile_project.vehicle.CustomTopAppBar
import com.example.mobile_project.vehicle.HomeScreen
import com.example.mobile_project.vehicle.VehicleTestScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // กำหนดรายชื่อหน้าจอที่ "ต้องการให้แสดง" TopBar และ BottomBar
            val screensWithBars = listOf("home")

            // ตัวแปรเช็คว่าควรโชว์บาร์ไหม (ถ้า currentRoute อยู่ในลิสต์ด้านบน จะเป็น true)
            val showBars = currentRoute in screensWithBars

            // โครงสร้างหลักของแอป
            // โครงสร้างหลักของแอป
            Scaffold(
                topBar = {
                    // โชว์ TopBar เฉพาะหน้าที่กำหนด
                    if (showBars) {
                        CustomTopAppBar(
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                },
                bottomBar = {
                    // โชว์ BottomBar เฉพาะหน้าที่กำหนด
                    if (showBars) {
                        CustomBottomNavBar(navController, currentRoute)
                    }
                },
                containerColor = Color(0xFFF6F8FA) // ใส่สีพื้นหลังหลักของแอปที่นี่
            ) { innerPadding ->

                NavHost(
                    navController = navController,
                    startDestination = "home",
                    // ✅ แก้ไขตรงนี้: เช็คว่าถ้าเป็นหน้าที่มี Bar ค่อยใส่ padding ถ้าไม่มีให้ทะลุขอบจอ (Modifier เปล่าๆ)
                    modifier = if (showBars) Modifier.padding(innerPadding) else Modifier
                ) {
                    // ... (composable หน้าอื่นๆ ด้านในคงไว้เหมือนเดิม) ...
                    composable("welcome") {
                        WelcomeScreen(
                            onLoginClick = { navController.navigate("login") },
                            onRegisterClick = { navController.navigate("register") }
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            onNavigateToRegister = {
                                navController.navigate("register") { popUpTo("welcome") }
                            }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onNavigateToLogin = {
                                navController.navigate("login") { popUpTo("welcome") }
                            }
                        )
                    }

                    // หน้า Home ของเรา
                    composable("home") {
                        HomeScreen()
                    }
                    composable("test-vehicle") {
                        VehicleTestScreen()
                    }
                }
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
        // --- ส่วนบน (โลโก้ และ ข้อความ) ---
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

                // ปุ่ม Login
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