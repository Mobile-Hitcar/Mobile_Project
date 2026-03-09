package com.example.mobile_project.login

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_project.R
import com.example.mobile_project.firebaseDB.UserSession
import com.example.mobile_project.firebaseDB.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Forgot Password states
    var showForgotDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var isSendingReset by remember { mutableStateOf(false) }
    var resetSent by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // เอา Web client ID ที่ก๊อปปี้มาจาก Firebase Console มาใส่ตรงนี้
    val webClientId = ""


    // Google Sign-In launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                isLoading = true
                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    isLoading = false
                    if (authTask.isSuccessful) {
                        UserSession.currentUserEmail = account.email ?: ""
                        onNavigateToHome()
                    } else {
                        val errorMsg = authTask.exception?.message ?: "Unknown error"
                        Toast.makeText(context, "Google sign-in failed: $errorMsg", Toast.LENGTH_LONG).show()
                        Log.e("LoginError", errorMsg)
                    }
                }
            } catch (e: ApiException) {
                isLoading = false
                Toast.makeText(context, "Google error: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = EaseOutCubic), label = "ca"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .alpha(contentAlpha)
            .padding(top = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        // --- ส่วนของโลโก้ ---
        Image(
            painter = painterResource(id = R.drawable.hitcar_template),
            contentDescription = "Hitcar Logo",
            modifier = Modifier.size(180.dp) // ขยายจาก 140.dp เป็น 180.dp
        )
        Text(
            text = "HitCar",
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF00337C)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Login to Your Account",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF00337C),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Form panel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xFFE1F1FA),
                    shape = RoundedCornerShape(topStart = 56.dp, topEnd = 56.dp)
                )
                .padding(horizontal = 32.dp, vertical = 32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF00337C)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF00337C),
                        unfocusedBorderColor = Color(0xFF00337C)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF00337C)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF00337C),
                        unfocusedBorderColor = Color(0xFF00337C)
                    )
                )

                // Forgot Password link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            resetEmail = email // pre-fill ด้วย email ที่พิมพ์ไว้แล้ว
                            resetSent = false
                            showForgotDialog = true
                        }
                    ) {
                        Text(
                            text = "Forgot Password?",
                            color = Color(0xFF00337C),
                            fontSize = 13.sp,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Login button
                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            isLoading = true
                            userViewModel.loginUser(email, password) { isSuccess, errorMsg ->
                                isLoading = false
                                if (isSuccess) {
                                    onNavigateToHome()
                                } else {
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00337C)),
                    shape = RoundedCornerShape(27.dp),
                    enabled = !isLoading
                ) {
                    AnimatedContent(
                        targetState = isLoading,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "loginBtn"
                    ) { loading ->
                        if (loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Login", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.4f))
                    Text(
                        " or continue with ",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.4f))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Google Sign-In button
                Surface(
                    onClick = {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(webClientId)
                            .requestEmail()
                            .build()
                        val client = GoogleSignIn.getClient(context, gso)
                        client.signOut().addOnCompleteListener {
                            launcher.launch(client.signInIntent)
                        }
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .size(52.dp)
                        .border(1.dp, Color.LightGray, CircleShape),
                    color = Color.White
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize().padding(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google_logo),
                            contentDescription = "Google Login"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Register link
                Row(modifier = Modifier.clickable { onNavigateToRegister() }) {
                    Text("Don't have an account? ", color = Color(0xFF00337C), fontSize = 14.sp)
                    Text(
                        "Register",
                        color = Color(0xFF00337C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // ── Forgot Password Dialog ──
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isSendingReset) {
                    showForgotDialog = false
                    resetSent = false
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LockReset,
                        contentDescription = null,
                        tint = Color(0xFF00337C),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Reset Password",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00337C),
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column {
                    AnimatedContent(
                        targetState = resetSent,
                        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                        label = "resetContent"
                    ) { sent ->
                        if (sent) {
                            // หลังส่ง email สำเร็จ
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Icon(
                                    Icons.Default.MarkEmailRead,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Reset link sent!",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Check your inbox at\n$resetEmail\nand follow the instructions.",
                                    color = Color.DarkGray,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        } else {
                            // ฟอร์มกรอก email
                            Column {
                                Text(
                                    "Enter your registered email and we'll send you a link to reset your password.",
                                    color = Color.DarkGray,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = resetEmail,
                                    onValueChange = { resetEmail = it },
                                    label = { Text("Email Address") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Email, null, tint = Color(0xFF00337C))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF00337C),
                                        unfocusedBorderColor = Color.LightGray
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (resetSent) {
                    Button(
                        onClick = { showForgotDialog = false; resetSent = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00337C)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            if (resetEmail.isNotEmpty()) {
                                isSendingReset = true
                                userViewModel.sendPasswordReset(resetEmail) { success, errorMsg ->
                                    isSendingReset = false
                                    if (success) {
                                        resetSent = true
                                    } else {
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please enter your email.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00337C)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSendingReset
                    ) {
                        AnimatedContent(
                            targetState = isSendingReset,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "sendBtn"
                        ) { sending ->
                            if (sending) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Send Reset Link", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            dismissButton = {
                if (!resetSent) {
                    TextButton(
                        onClick = { showForgotDialog = false },
                        enabled = !isSendingReset
                    ) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            }
        )
    }
}