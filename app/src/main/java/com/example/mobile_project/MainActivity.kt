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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobile_project.editprofile.ProfileScreen
import com.example.mobile_project.login.LoginScreen
import com.example.mobile_project.register.RegisterScreen
import com.example.mobile_project.vehicle.CustomBottomNavBar
import com.example.mobile_project.vehicle.CustomTopAppBar
import com.example.mobile_project.vehicle.DetailScreen
import com.example.mobile_project.vehicle.HistoryScreen
import com.example.mobile_project.vehicle.HomeScreen
import com.example.mobile_project.vehicle.PaymentScreen
import com.example.mobile_project.firebaseDB.UserSession
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val screensWithBars = listOf("home", "history", "profile")

            // หน้าที่มีปุ่มย้อนกลับ (TopBar) แต่ไม่มี BottomBar
            val isDetailScreen = currentRoute?.startsWith("detail/") == true
            val isPaymentScreen = currentRoute?.startsWith("payment/") == true
            val needsBackButton = isDetailScreen || isPaymentScreen

            val showBottomBar = currentRoute in screensWithBars
            val showTopBar = currentRoute in screensWithBars || needsBackButton

            Scaffold(
                topBar = {
                    if (showTopBar) {
                        CustomTopAppBar(
                            onBackClick = { navController.popBackStack() },
                            showBackButton = needsBackButton,
                            onLogoutClick = {
                                UserSession.currentUserEmail = ""
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("welcome") {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            }
                        )
                    }
                },
                bottomBar = {
                    if (showBottomBar) {
                        CustomBottomNavBar(navController, currentRoute)
                    }
                },
                containerColor = Color(0xFFF6F8FA)
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "welcome",
                    modifier = if (showTopBar) Modifier.padding(innerPadding) else Modifier
                ) {
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
                            },
                            onNavigateToHome = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
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

                    composable("home") {
                        HomeScreen(navController = navController)
                    }

                    composable("detail/{vehicleId}") { backStackEntry ->
                        val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
                        DetailScreen(
                            vehicleId = vehicleId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable("history") {
                        HistoryScreen(navController = navController)
                    }

                    // ✅ route ชำระเงิน — รับข้อมูลจาก URL
                    composable("payment/{orderId}/{brand}/{model}/{price}") { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                        val brand = backStackEntry.arguments?.getString("brand") ?: ""
                        val model = backStackEntry.arguments?.getString("model") ?: ""
                        val price = backStackEntry.arguments?.getString("price")?.toIntOrNull() ?: 0
                        PaymentScreen(
                            orderId = orderId,
                            vehicleBrand = brand,
                            vehicleModel = model,
                            vehiclePrice = price,
                            onBackClick = { navController.popBackStack() },
                            onPaymentSuccess = {
                                // กลับไปหน้า history และล้าง payment stack ออก
                                navController.navigate("history") {
                                    popUpTo("history") { inclusive = false }
                                }
                            }
                        )
                    }

                    composable("profile") {
                        ProfileScreen()
                    }
                }
            }
        }
    }
}

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
                Text(text = "Hello", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Welcome To HitCar, where\nyou can find your perfect car",
                    fontSize = 16.sp, color = Color.White,
                    textAlign = TextAlign.Center, lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = { onLoginClick() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkBlue),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(text = "Login", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onRegisterClick() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(text = "Register", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                }
            }
        }
    }
}