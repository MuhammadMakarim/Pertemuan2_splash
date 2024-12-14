package com.example.pertemuan2_splash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.pertemuan2_splash.ui.theme.Pertemuan2_splashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pertemuan2_splashTheme {
                LoginOneNotePreview()
            }
        }
    }
}

@Composable
fun LoginOneNote() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .align(Alignment.BottomCenter),
            color = Color(0XFF7218A5),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(100.dp))

                Text(
                    text = "Log In",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                val textFieldHeight = 56.dp

                // State for Email and Password fields
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                var emailFocused by remember { mutableStateOf(false) }
                var passwordFocused by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // Email TextField
                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { /* Empty label as we are using custom label logic */ },
                            placeholder = { if (!emailFocused) Text("Masukkan email")},
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(textFieldHeight)
                                .padding(vertical = 8.dp)
                                .onFocusChanged {
                                    emailFocused = it.isFocused
                                },
                            shape = RoundedCornerShape(8.dp)
                        )
                        // Custom Label for Email
                        if (!emailFocused && email.isEmpty()) {
                            Text(
                                text = "Email",
                                color = Color.Gray,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 12.dp, top = 16.dp)
                                    .zIndex(1f) // Ensure label stays above the TextField
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password TextField
                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { /* Empty label as we are using custom label logic */ },
                            placeholder = { if (!passwordFocused) Text("Masukkan password") else null },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(textFieldHeight)
                                .padding(vertical = 8.dp)
                                .onFocusChanged {
                                    passwordFocused = it.isFocused
                                },
                            shape = RoundedCornerShape(8.dp)
                        )
                        // Custom Label for Password
                        if (!passwordFocused && password.isEmpty()) {
                            Text(
                                text = "Password",
                                color = Color.Gray,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 12.dp, top = 16.dp)
                                    .zIndex(1f) // Ensure label stays above the TextField
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Login Button
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            // Handle login logic here
                        },
                        colors = ButtonDefaults.buttonColors(Color.White),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "LOGIN",
                            fontSize = 18.sp,
                            color = Color(0XFF7218A5)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }

                Text(
                    textAlign = TextAlign.Center,
                    text = "Forgot Password?",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    textAlign = TextAlign.Center,
                    text = "or login with",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Google Button
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gmail_logo),
                            contentDescription = "Google",
                            modifier = Modifier.size(250.dp)
                        )
                    }

                    // Facebook Button
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.facebook_logo),
                            contentDescription = "Facebook",
                            modifier = Modifier.size(250.dp)
                        )
                    }

                    // Twitter Button
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.twitter_logo),
                            contentDescription = "Twitter",
                            modifier = Modifier.size(250.dp)
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(bottomStart = 70.dp, bottomEnd = 70.dp),
            color = Color(0xFFFFFFFF)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Image(
                        modifier = Modifier.size(150.dp),
                        painter = painterResource(id = R.drawable.onenote),
                        contentDescription = "picture"
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        modifier = Modifier.padding(horizontal = 50.dp),
                        text = "Microsoft OneNote",
                        fontSize = 20.sp,
                        color = Color(0XFF7218A5),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        modifier = Modifier.padding(horizontal = 50.dp),
                        text = "Log in to your account to access the app",
                        fontSize = 15.sp,
                        color = Color(0XFF7218A5),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginOneNotePreview() {
    Pertemuan2_splashTheme {
        LoginOneNote()
    }
}
