package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.Plant
import com.example.data.UserProfile
import com.example.data.GeminiService
import com.example.data.CareGuideResult
import com.example.data.CustomQuestionResult
import com.example.ui.Screen
import com.example.ui.SproutViewModel
import com.example.ui.theme.SproutTheme
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.animation.core.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.example.data.Friend

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ui.PlantAlert
import com.example.ui.AlertType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Force widget update to sync up launcher widgets immediately
        SproutWidgetProvider.triggerUpdate(this)
        LargeFlowerWidgetProvider.triggerUpdate(this)

        setContent {
            val viewModel: SproutViewModel = viewModel()
            val profile by viewModel.userProfile.collectAsState()

            val selectedTheme = profile?.selectedTheme ?: "Default"
            val selectedFont = profile?.selectedFont ?: "System"

            SproutTheme(
                darkTheme = profile?.isDarkMode ?: false,
                selectedTheme = selectedTheme,
                selectedFont = selectedFont
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplashScreen by remember { mutableStateOf(true) }
                    Crossfade(
                        targetState = showSplashScreen,
                        animationSpec = tween(durationMillis = 800),
                        label = "launch_crossfade"
                    ) { isSplash ->
                        if (isSplash) {
                            LaunchAnimationScreen(
                                onFinished = { showSplashScreen = false }
                            )
                        } else {
                            SproutApp(viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: SproutViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SproutAdaptiveLogo()
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Passcode") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                if (isRegistering) {
                    viewModel.registerNewProfile(
                        name = username,
                        username = username,
                        passcode = password,
                        avatarColor = 0xFF4CAF50.toInt(),
                        onSuccess = { viewModel.loginToProfile(username, password, onSuccess = {}, onFailure = {}) },
                        onFailure = { }
                    )
                } else {
                    viewModel.loginToProfile(username, password, onSuccess = {}, onFailure = {})
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isRegistering) "Register" else "Login")
        }
        
        TextButton(onClick = { isRegistering = !isRegistering }) {
            Text(if (isRegistering) "Already have an account? Login" else "New here? Register")
        }
    }
}
@Composable
fun LaunchAnimationScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) { onFinished() }
}

@Composable
fun SproutAdaptiveLogo(modifier: Modifier = Modifier) {
    Text("SPROUT", modifier = modifier, style = MaterialTheme.typography.displayLarge)
}

@Composable
fun SproutApp(viewModel: SproutViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    if (profile == null) {
        LoginScreen(viewModel)
    } else {
        var currentScreen by remember { mutableStateOf("Garden") }
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == "Garden",
                        onClick = { currentScreen = "Garden" },
                        icon = { Icon(Icons.Default.Park, contentDescription = "Garden") },
                        label = { Text("Garden") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "Profile",
                        onClick = { currentScreen = "Profile" },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                when (currentScreen) {
                    "Garden" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Welcome to your Garden, ${profile?.name}!")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.logout() }) {
                                Text("Logout")
                            }
                        }
                    }
                    "Profile" -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Profile Stats", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Seeds: ${profile?.seedBalance}", style = MaterialTheme.typography.bodyLarge)
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
                                Switch(
                                    checked = profile?.isDarkMode ?: false,
                                    onCheckedChange = { viewModel.toggleDarkMode(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
