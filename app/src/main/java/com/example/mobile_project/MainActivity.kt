package com.example.mobile_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
                    modifier = if (showTopBar) Modifier.padding(innerPadding) else Modifier,
                    enterTransition = {
                        fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 5 }
                    },
                    exitTransition = { fadeOut(tween(200)) },
                    popEnterTransition = {
                        fadeIn(tween(280)) + slideInHorizontally(tween(280)) { -it / 5 }
                    },
                    popExitTransition = { fadeOut(tween(200)) }
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
                    composable("home") { HomeScreen(navController = navController) }
                    composable("detail/{vehicleId}") { back ->
                        val vehicleId = back.arguments?.getString("vehicleId") ?: ""
                        DetailScreen(vehicleId = vehicleId, onBackClick = { navController.popBackStack() })
                    }
                    composable("history") { HistoryScreen(navController = navController) }
                    composable("payment/{orderId}/{brand}/{model}/{price}") { back ->
                        val orderId = back.arguments?.getString("orderId") ?: ""
                        val brand = back.arguments?.getString("brand") ?: ""
                        val model = back.arguments?.getString("model") ?: ""
                        val price = back.arguments?.getString("price")?.toIntOrNull() ?: 0
                        PaymentScreen(
                            orderId = orderId, vehicleBrand = brand,
                            vehicleModel = model, vehiclePrice = price,
                            onBackClick = { navController.popBackStack() },
                            onPaymentSuccess = {
                                navController.navigate("history") {
                                    popUpTo("history") { inclusive = false }
                                }
                            }
                        )
                    }
                    composable("profile") { ProfileScreen() }
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
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val logoAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700, easing = EaseOutCubic), label = "la"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.88f,
        animationSpec = tween(700, easing = EaseOutBack), label = "ls"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, delayMillis = 200, easing = EaseOutCubic), label = "ta"
    )
    val panelAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, delayMillis = 350, easing = EaseOutCubic), label = "pa"
    )
    val panelSlide by animateFloatAsState(
        targetValue = if (visible) 0f else 60f,
        animationSpec = tween(600, delayMillis = 350, easing = EaseOutCubic), label = "ps"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFECF5FD), Color(0xFFF5F7FA))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(90.dp))

            Image(
                painter = painterResource(id = R.drawable.hitcar_template),
                contentDescription = "HitCar Logo",
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .padding(horizontal = 16.dp)
                    .alpha(logoAlpha)
                    .scale(logoScale)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.alpha(textAlpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Find Your",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2FA2E9),
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Perfect Car",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryDarkBlue
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .width(40.dp).height(4.dp)
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF2FA2E9), Color(0xFF0D3D82))),
                            RoundedCornerShape(2.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = panelSlide.dp)
                    .alpha(panelAlpha)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF2FA2E9), Color(0xFF0A3272))),
                        RoundedCornerShape(topStart = 44.dp, topEnd = 44.dp)
                    )
                    .padding(top = 48.dp, bottom = 72.dp, start = 32.dp, end = 32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Welcome to HitCar",
                        fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "The smartest platform\nto find your dream car",
                        fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center, lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(36.dp))

                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(27.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkBlue)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onRegisterClick,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.6f))
                        ),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Text("Register", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}