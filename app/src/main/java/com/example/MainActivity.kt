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
fun LaunchAnimationScreen(onFinished: () -> Unit) {
    var startAnim by remember { mutableStateOf(false) }
    val progress = animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 2600, easing = LinearEasing),
        label = "vine_growth"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(3000) // 2.6s growth + 400ms steady appreciation
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2419), // Lush deep evergreen forest gradient
                        Color(0xFF07110B)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                VineGrowthCanvas(progressState = progress)
            }

            Spacer(modifier = Modifier.height(28.dp))

            val textAlpha = animateFloatAsState(
                targetValue = if (startAnim) 1f else 0f,
                animationSpec = tween(durationMillis = 800, delayMillis = 390),
                label = "text_fade"
            )

            Text(
                text = "SPROUT",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp,
                    color = Color(0xFFE8F5E9)
                ),
                modifier = Modifier.graphicsLayer { alpha = textAlpha.value }
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sprouting your sanctuary...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFFA5D6A7)
                ),
                modifier = Modifier.graphicsLayer { alpha = textAlpha.value }
            )
        }
    }
}

private val centralLeaves = listOf(
    Triple(0.12f, 1f, 1.0f),
    Triple(0.24f, -1f, 0.95f),
    Triple(0.38f, 1f, 0.9f),
    Triple(0.48f, -1f, 0.85f),
    Triple(0.62f, 1f, 0.8f),
    Triple(0.74f, -1f, 0.75f),
    Triple(0.86f, 1f, 0.7f),
    Triple(0.97f, -0.4f, 0.65f),
    Triple(0.97f, 0.4f, 0.65f)
)

private val leftBranchLeaves = listOf(
    Triple(0.2f, -1f, 0.8f),
    Triple(0.45f, 1f, 0.75f),
    Triple(0.7f, -1f, 0.7f),
    Triple(0.94f, -0.4f, 0.55f),
    Triple(0.94f, 0.4f, 0.55f)
)

private val rightBranchLeaves = listOf(
    Triple(0.2f, 1f, 0.8f),
    Triple(0.45f, -1f, 0.75f),
    Triple(0.7f, 1f, 0.7f),
    Triple(0.94f, -0.4f, 0.55f),
    Triple(0.94f, 0.4f, 0.55f)
)

private val sparkleData = listOf(
    Pair(0.32f, 0.74f) to 0.18f,
    Pair(0.68f, 0.65f) to 0.30f,
    Pair(0.25f, 0.52f) to 0.42f,
    Pair(0.75f, 0.38f) to 0.58f,
    Pair(0.42f, 0.24f) to 0.72f,
    Pair(0.58f, 0.15f) to 0.85f
)

@Composable
fun VineGrowthCanvas(progressState: State<Float>) {
    val stemPath = remember { Path() }
    val leftPath = remember { Path() }
    val rightPath = remember { Path() }
    
    val stemPoints = remember { mutableListOf<Offset>() }
    val leftPoints = remember { mutableListOf<Offset>() }
    val rightPoints = remember { mutableListOf<Offset>() }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vine_launch_canvas")
    ) {
        val progress = progressState.value
        val w = size.width
        val h = size.height

        // 1. Central Vine endpoints
        val cP0 = Offset(w / 2f, h)
        val cP1 = Offset(w / 2f - w * 0.18f, h * 0.7f)
        val cP2 = Offset(w / 2f + w * 0.18f, h * 0.4f)
        val cP3 = Offset(w / 2f, h * 0.1f)

        // Generate Central stem points
        val steps = 100
        stemPoints.clear()
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            stemPoints.add(getCubicBezierPoint(cP0, cP1, cP2, cP3, t))
        }

        // Draw Central Stem
        val activeStemLength = (progress * steps).toInt().coerceIn(0, steps)
        if (activeStemLength > 1) {
            stemPath.reset()
            stemPath.moveTo(stemPoints[0].x, stemPoints[0].y)
            for (i in 1..activeStemLength) {
                stemPath.lineTo(stemPoints[i].x, stemPoints[i].y)
            }
            drawPath(
                path = stemPath,
                color = Color(0xFF4CAF50), // Vibrant leafy green
                style = Stroke(
                    width = 6.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // 2. Draw Left Branch splitting at t = 0.35
        val leftSteps = 50
        leftPoints.clear()
        val lpProgress = if (progress > 0.35f) (progress - 0.35f) / 0.65f else 0f

        if (progress > 0.35f) {
            val splitIdx = (0.35f * steps).toInt()
            val bp0 = stemPoints[splitIdx]
            val bp1 = bp0 + Offset(-w * 0.2f, -h * 0.05f)
            val bp2 = bp0 + Offset(-w * 0.3f, -h * 0.2f)
            for (i in 0..leftSteps) {
                val u = i.toFloat() / leftSteps
                leftPoints.add(getQuadraticBezierPoint(bp0, bp1, bp2, u))
            }

            val activeLeftLength = (lpProgress * leftSteps).toInt().coerceIn(0, leftSteps)
            if (activeLeftLength > 1) {
                leftPath.reset()
                leftPath.moveTo(leftPoints[0].x, leftPoints[0].y)
                for (i in 1..activeLeftLength) {
                    leftPath.lineTo(leftPoints[i].x, leftPoints[i].y)
                }
                drawPath(
                    path = leftPath,
                    color = Color(0xFF388E3C), // Muted dark green
                    style = Stroke(
                        width = 4.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        // 3. Draw Right Branch splitting at t = 0.55
        val rightSteps = 50
        rightPoints.clear()
        val rpProgress = if (progress > 0.55f) (progress - 0.55f) / 0.45f else 0f

        if (progress > 0.55f) {
            val splitIdx = (0.55f * steps).toInt()
            val bp0 = stemPoints[splitIdx]
            val bp1 = bp0 + Offset(w * 0.2f, -h * 0.05f)
            val bp2 = bp0 + Offset(w * 0.3f, -h * 0.2f)
            for (i in 0..rightSteps) {
                val u = i.toFloat() / rightSteps
                rightPoints.add(getQuadraticBezierPoint(bp0, bp1, bp2, u))
            }

            val activeRightLength = (rpProgress * rightSteps).toInt().coerceIn(0, rightSteps)
            if (activeRightLength > 1) {
                rightPath.reset()
                rightPath.moveTo(rightPoints[0].x, rightPoints[0].y)
                for (i in 1..activeRightLength) {
                    rightPath.lineTo(rightPoints[i].x, rightPoints[i].y)
                }
                drawPath(
                    path = rightPath,
                    color = Color(0xFF388E3C), // Muted dark green
                    style = Stroke(
                        width = 4.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        // 4. Draw Leaves on Central Vine
        val leafPrimaryColor = Color(0xFF81C784)
        val leafVeinColor = Color(0xFF1B5E20)

        centralLeaves.forEach { (tSpawn, side, scaleMult) ->
            if (progress > tSpawn) {
                val idx = (tSpawn * steps).toInt().coerceIn(0, steps)
                val pos = stemPoints[idx]
                val nextIdx = (idx + 1).coerceAtMost(steps)
                val prevIdx = (idx - 1).coerceAtLeast(0)
                val tangent = stemPoints[nextIdx] - stemPoints[prevIdx]
                val baseAngle = Math.toDegrees(Math.atan2(tangent.y.toDouble(), tangent.x.toDouble())).toFloat()

                val leafAngle = baseAngle + (side * 65f)
                val leafScale = ((progress - tSpawn) / 0.12f).coerceIn(0f, 1f) * scaleMult

                drawLeaf(
                    origin = pos,
                    angleDegrees = leafAngle,
                    scale = leafScale,
                    primaryColor = leafPrimaryColor,
                    veinColor = leafVeinColor
                )
            }
        }

        // 5. Draw Leaves on Left Branch
        if (progress > 0.35f && leftPoints.size > 0) {
            leftBranchLeaves.forEach { (uSpawn, side, scaleMult) ->
                if (lpProgress > uSpawn) {
                    val idx = (uSpawn * leftSteps).toInt().coerceIn(0, leftSteps)
                    val pos = leftPoints[idx]
                    val nextIdx = (idx + 1).coerceAtMost(leftSteps)
                    val prevIdx = (idx - 1).coerceAtLeast(0)
                    val tangent = leftPoints[nextIdx] - leftPoints[prevIdx]
                    val baseAngle = Math.toDegrees(Math.atan2(tangent.y.toDouble(), tangent.x.toDouble())).toFloat()

                    val leafAngle = baseAngle + (side * 65f)
                    val leafScale = ((lpProgress - uSpawn) / 0.12f).coerceIn(0f, 1f) * scaleMult

                    drawLeaf(
                        origin = pos,
                        angleDegrees = leafAngle,
                        scale = leafScale,
                        primaryColor = Color(0xFFA5D6A7),
                        veinColor = leafVeinColor
                    )
                }
            }
        }

        // 6. Draw Leaves on Right Branch
        if (progress > 0.55f && rightPoints.size > 0) {
            rightBranchLeaves.forEach { (uSpawn, side, scaleMult) ->
                if (rpProgress > uSpawn) {
                    val idx = (uSpawn * rightSteps).toInt().coerceIn(0, rightSteps)
                    val pos = rightPoints[idx]
                    val nextIdx = (idx + 1).coerceAtMost(rightSteps)
                    val prevIdx = (idx - 1).coerceAtLeast(0)
                    val tangent = rightPoints[nextIdx] - rightPoints[prevIdx]
                    val baseAngle = Math.toDegrees(Math.atan2(tangent.y.toDouble(), tangent.x.toDouble())).toFloat()

                    val leafAngle = baseAngle + (side * 65f)
                    val leafScale = ((rpProgress - uSpawn) / 0.12f).coerceIn(0f, 1f) * scaleMult

                    drawLeaf(
                        origin = pos,
                        angleDegrees = leafAngle,
                        scale = leafScale,
                        primaryColor = Color(0xFFA5D6A7),
                        veinColor = leafVeinColor
                    )
                }
            }
        }

        // 7. Draw magical glowing gold pollen sparkles
        sparkleData.forEach { (relPos, spawnT) ->
            if (progress > spawnT) {
                val age = progress - spawnT
                val sizeAnim = (age / 0.1f).coerceIn(0f, 1f) * (1f - (age / 0.4f).coerceIn(0f, 1f))
                if (sizeAnim > 0f) {
                    drawSparkle(Offset(w * relPos.first, h * relPos.second), sizeAnim * 10.dp.toPx())
                }
            }
        }
    }
}

fun getQuadraticBezierPoint(p0: Offset, p1: Offset, p2: Offset, t: Float): Offset {
    val u = 1 - t
    val tt = t * t
    val uu = u * u
    return Offset(
        x = uu * p0.x + 2 * u * t * p1.x + tt * p2.x,
        y = uu * p0.y + 2 * u * t * p1.y + tt * p2.y
    )
}

fun getCubicBezierPoint(p0: Offset, p1: Offset, p2: Offset, p3: Offset, t: Float): Offset {
    val u = 1 - t
    val tt = t * t
    val uu = u * u
    val uuu = uu * u
    val ttt = tt * t
    return Offset(
        x = uuu * p0.x + 3 * uu * t * p1.x + 3 * u * tt * p2.x + ttt * p3.x,
        y = uuu * p0.y + 3 * uu * t * p1.y + 3 * u * tt * p2.y + ttt * p3.y
    )
}

fun DrawScope.drawLeaf(
    origin: Offset,
    angleDegrees: Float,
    scale: Float,
    primaryColor: Color,
    veinColor: Color
) {
    if (scale <= 0f) return

    val leafLength = 26.dp.toPx() * scale
    val leafWidth = 13.dp.toPx() * scale

    withTransform({
        translate(origin.x, origin.y)
        rotate(angleDegrees, pivot = Offset.Zero)
    }) {
        val path = Path().apply {
            moveTo(0f, 0f)
            quadraticTo(
                -leafWidth / 2f, leafLength / 3f,
                0f, leafLength
            )
            quadraticTo(
                leafWidth / 2f, leafLength / 3f,
                0f, 0f
            )
            close()
        }
        drawPath(path, color = primaryColor)

        // Draw leaf vein
        drawLine(
            color = veinColor,
            start = Offset(0f, 0f),
            end = Offset(0f, leafLength * 0.85f),
            strokeWidth = 1.2.dp.toPx() * scale
        )
    }
}

fun DrawScope.drawSparkle(center: Offset, size: Float) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        quadraticTo(center.x, center.y, center.x + size, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + size)
        quadraticTo(center.x, center.y, center.x - size, center.y)
        quadraticTo(center.x, center.y, center.x, center.y - size)
        close()
    }
    drawPath(path, color = Color(0xFFFFF176)) // Brilliant glowing gold-yellow
}

@Composable
fun SproutApp(viewModel: SproutViewModel) {
    val currentScreen = viewModel.currentScreen
    val profile by viewModel.userProfile.collectAsState()
    val plants by viewModel.plants.collectAsState()
    val alerts by viewModel.plantAlerts.collectAsState()
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _ -> }
        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    var toastMessage by remember { mutableStateOf<String?>(null) }
    toastMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        toastMessage = null
    }

    if (profile == null) {
        LoginScreen(viewModel)
    } else {
        Scaffold(
            bottomBar = {
                val showBar = when (currentScreen) {
                    is Screen.Home, is Screen.Nursery, is Screen.MyGarden, is Screen.Stats, is Screen.Friends -> true
                    else -> false
                }
                if (showBar) {
                    SproutBottomNavigation(currentScreen) { selectedScreen ->
                        viewModel.navigateTo(selectedScreen)
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    label = "ScreenTransition",
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300)) + slideInVertically(
                            animationSpec = tween(400),
                            initialOffsetY = { it / 16 }
                        )).togetherWith(
                            fadeOut(animationSpec = tween(300))
                        )
                    }
                ) { targetScreen ->
                    when (targetScreen) {
                        is Screen.Home -> HomeScreen(viewModel)
                        is Screen.Nursery -> NurseryScreen(viewModel)
                        is Screen.MyGarden -> GardenScreen(viewModel)
                        is Screen.Stats -> StatsScreen(viewModel)
                        is Screen.Settings -> SettingsScreen(viewModel)
                        is Screen.PurchaseSeeds -> PurchaseSeedsScreen(viewModel)
                        is Screen.Friends -> FriendsScreen(viewModel)
                        is Screen.Login -> LoginScreen(viewModel)
                        is Screen.PlantDetail -> PlantDetailScreen(
                            viewModel = viewModel,
                            plantId = targetScreen.plantId
                        )
                        is Screen.CareGuides -> CareGuidesScreen(viewModel)
                    }
                }
                
                if (alerts.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 32.dp)
                            .windowInsetsPadding(WindowInsets.statusBars),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        alerts.take(2).forEach { alert ->
                            key(alert.id) {
                                LaunchedEffect(alert.id) {
                                    delay(4000)
                                    viewModel.dismissAlert(alert.id)
                                }
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(android.graphics.Color.parseColor(alert.type.colorHex))
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = alert.message,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.weight(1f).padding(end = 12.dp)
                                        )
                                        IconButton(
                                            onClick = { viewModel.dismissAlert(alert.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Dismiss",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Custom Bottom Navigation matching Sprout branding
@Composable
fun SproutBottomNavigation(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 24.dp, vertical = 16.dp), // Floating style
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.95f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                .height(72.dp)
        ) {
            NavigationBarItem(
                selected = currentScreen is Screen.Home,
                onClick = { onTabSelected(Screen.Home) },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
            NavigationBarItem(
                selected = currentScreen is Screen.Nursery,
                onClick = { onTabSelected(Screen.Nursery) },
                icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Nursery") },
                label = { Text("Nursery", style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
            NavigationBarItem(
                selected = currentScreen is Screen.MyGarden,
                onClick = { onTabSelected(Screen.MyGarden) },
                icon = { Icon(Icons.Default.LocalFlorist, contentDescription = "Garden") },
                label = { Text("Garden", style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
            NavigationBarItem(
                selected = currentScreen is Screen.Friends,
                onClick = { onTabSelected(Screen.Friends) },
                icon = { Icon(Icons.Default.People, contentDescription = "Companions") },
                label = { Text("Friends", style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
            NavigationBarItem(
                selected = currentScreen is Screen.Stats,
                onClick = { onTabSelected(Screen.Stats) },
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profile", style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

// --- ORGANIC WATER WIDGET ANIMATION HELPERS ---
data class WaterDroplet(
    val id: Int,
    val initialX: Float,
    val initialY: Float,
    val floatSpeedX: Float,
    val floatSpeedY: Float,
    val size: Float
)

@Composable
fun WaterWaveProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "WaterProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnimation")
    
    val wavePhase = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    val wavePhase2 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -(2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase2"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val baseHeight = height * (1f - animatedProgress.value)
            val amplitude = 3.dp.toPx()

            // Back support wave
            val path2 = Path().apply {
                moveTo(0f, height)
                for (x in 0..width.toInt() step 6) {
                    val angle = (x.toFloat() / width) * 2 * Math.PI + wavePhase2.value
                    val y = baseHeight + Math.sin(angle).toFloat() * amplitude
                    lineTo(x.toFloat(), y.coerceIn(0f, height))
                }
                lineTo(width, height)
                close()
            }
            drawPath(path2, color = Color(0xFF64B5F6).copy(alpha = 0.4f))

            // Front primary wave
            val path1 = Path().apply {
                moveTo(0f, height)
                for (x in 0..width.toInt() step 6) {
                    val angle = (x.toFloat() / width) * 2 * Math.PI + wavePhase.value
                    val y = baseHeight + Math.sin(angle).toFloat() * amplitude
                    lineTo(x.toFloat(), y.coerceIn(0f, height))
                }
                lineTo(width, height)
                close()
            }
            drawPath(path1, color = Color(0xFF2196F3).copy(alpha = 0.85f))
        }
    }
}

@Composable
fun BreathingAura(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AuraAnimation")
    
    val scale1 = infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Scale1"
    )
    val alpha1 = infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Alpha1"
    )

    val scale2 = infiniteTransition.animateFloat(
        initialValue = 1.02f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale2"
    )
    val alpha2 = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Alpha2"
    )

    val auraColor = if (progress >= 0.99f) {
        Color(0xFF81C784) // Soft flourishing lush green
    } else if (progress < 0.3f) {
        Color(0xFFFFB74D) // Thirsty warm orange
    } else {
        Color(0xFF64B5F6) // Hydrating blue
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    scaleX = scale1.value
                    scaleY = scale1.value
                    alpha = alpha1.value
                }
                .background(auraColor.copy(alpha = 0.35f), shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    scaleX = scale2.value
                    scaleY = scale2.value
                    alpha = alpha2.value
                }
                .border(2.5.dp, auraColor.copy(alpha = 0.45f), shape = CircleShape)
        )
    }
}

data class CelebrationItem(
    val emoji: String,
    val alignment: Alignment,
    val offset: androidx.compose.ui.unit.Dp
)

@Composable
fun CelebrationDecorations(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "Celebration")
    
    val angle = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RotateAngle"
    )

    val bobOffset = infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BobOffset"
    )

    Box(
        modifier = modifier
            .size(270.dp)
            .graphicsLayer { rotationZ = angle.value },
        contentAlignment = Alignment.Center
    ) {
        val elements = listOf(
            CelebrationItem("✨", Alignment.TopCenter, 0.dp),
            CelebrationItem("🍃", Alignment.BottomCenter, 0.dp),
            CelebrationItem("🌸", Alignment.CenterStart, 8.dp),
            CelebrationItem("✨", Alignment.CenterEnd, (-8).dp),
            CelebrationItem("💖", Alignment.TopStart, 12.dp),
            CelebrationItem("🌱", Alignment.BottomEnd, (-12).dp)
        )

        elements.forEach { item ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = item.alignment
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = bobOffset.value.dp.toPx() + item.offset.toPx()
                            rotationZ = -angle.value // Keep emojis upright
                        }
                )
            }
        }
    }
}

@Composable
fun WaterDropletsOverlay(waterTrigger: Int) {
    var droplets by remember { mutableStateOf<List<WaterDroplet>>(emptyList()) }
    var dropletIdCounter by remember { mutableStateOf(0) }
    
    LaunchedEffect(waterTrigger) {
        if (waterTrigger > 0) {
             val newParticles = (1..10).map { i ->
                 WaterDroplet(
                     id = dropletIdCounter + i,
                     initialX = (Math.random() * 180 - 90).toFloat(),
                     initialY = 160f,
                     floatSpeedX = (Math.random() * 4 - 2).toFloat(),
                     floatSpeedY = (Math.random() * 6 + 5).toFloat(),
                     size = (Math.random() * 5 + 5).toFloat()
                 )
             }
             dropletIdCounter += 10
             droplets = droplets + newParticles
        }
    }

    LaunchedEffect(droplets) {
        if (droplets.isNotEmpty()) {
            delay(16)
            droplets = droplets.mapNotNull { droplet ->
                val nextY = droplet.initialY - droplet.floatSpeedY
                if (nextY < -240f) {
                    null
                } else {
                    droplet.copy(
                        initialY = nextY,
                        initialX = droplet.initialX + droplet.floatSpeedX
                    )
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        droplets.forEach { droplet ->
            // calculate progress 0f to 1f over the vertical travel distance
            val progressVal = ((160f - droplet.initialY) / 400f).coerceIn(0f, 1f)
            val alphaVal = (1f - progressVal) * 0.75f
            val colorVal = Color(0xFF64B5F6) // light blue
            
            drawCircle(
                color = colorVal,
                radius = droplet.size.dp.toPx() * (1f - progressVal * 0.3f),
                center = Offset(
                    x = size.width / 2 + droplet.initialX.dp.toPx() + (Math.sin(droplet.initialY.toDouble() / 15.0).toFloat() * 12f),
                    y = size.height / 2 + droplet.initialY.dp.toPx()
                ),
                alpha = alphaVal
            )
        }
    }
}

// --- SCREEN 1: HOME / WIDGET FOCUS ---
@Composable
fun HomeScreen(viewModel: SproutViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val plants by viewModel.plants.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterChip by remember { mutableStateOf("All") }

    var waterTrigger by remember { mutableIntStateOf(0) }
    var isWateredTrigger by remember { mutableStateOf(false) }

    val activePlant = plants.firstOrNull { it.isWidgetActive } ?: plants.firstOrNull { it.isCollected }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        // App top header block
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SproutAdaptiveLogo()
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Sprout",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.CareGuides) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("care_guides_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = "Care Almanac",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Settings) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        if (activePlant != null) {
            val plantInfiniteTransition = rememberInfiniteTransition(label = "PlantFloating")
            val plantFloatingOffset = plantInfiniteTransition.animateFloat(
                initialValue = -7f,
                targetValue = 7f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "PlantFloatOffset"
            )

            val plantImageScale = animateFloatAsState(
                targetValue = if (isWateredTrigger) 1.15f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "PlantImageScale",
                finishedListener = {
                    if (isWateredTrigger) {
                        isWateredTrigger = false
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Focus title
                Text(
                    text = "Active Widget Plant",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // The Widget organic card container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadowAmbient(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "DAILY TASK",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = activePlant.name,
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val now = System.currentTimeMillis()
                                    val diffMs = now - activePlant.lastWatered
                                    val activeLastWateredText = if (activePlant.lastWatered == 0L) {
                                        if (activePlant.waterProgress > 0.85f) "Just now" else "2 hours ago"
                                    } else {
                                        val diffSecs = diffMs / 1000
                                        val diffMins = diffSecs / 60
                                        val diffHours = diffMins / 60
                                        when {
                                            diffSecs < 10 -> "Just now"
                                            diffSecs < 60 -> "$diffSecs secs ago"
                                            diffMins < 60 -> "$diffMins mins ago"
                                            diffHours < 24 -> "$diffHours hours ago"
                                            else -> {
                                                val days = diffHours / 24
                                                "$days days ago"
                                            }
                                        }
                                    }
                                    Text(
                                        text = "Last watered: $activeLastWateredText",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val nextWateringInfo = com.example.data.PlantUtils.getNextWateringInfo(activePlant, activePlant.careInstructions)
                                    if (nextWateringInfo.daysRemaining == 0) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = "Water Today",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.error,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else if (nextWateringInfo.daysRemaining <= 2) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.tertiaryContainer,
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = "Due in ${nextWateringInfo.daysRemaining}d",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.tertiary,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.navigateTo(Screen.PlantDetail(activePlant.id)) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Go to Details", tint = MaterialTheme.colorScheme.onPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Plant avatar frame with breathing aura background ring and floating rotation stars
                            Box(
                                modifier = Modifier
                                    .size(280.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Breathing dynamic glow rings
                                BreathingAura(progress = activePlant.waterProgress)

                                // Sparkles and leaves spinning for happy flourishing plants
                                if (activePlant.waterProgress >= 0.99f) {
                                    CelebrationDecorations()
                                }

                                // Interactive bouncing/floating plant picture
                                Box(
                                    modifier = Modifier
                                        .size(190.dp)
                                        .graphicsLayer {
                                            scaleX = plantImageScale.value
                                            scaleY = plantImageScale.value
                                            translationY = plantFloatingOffset.value.dp.toPx()
                                        }
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .clickable { viewModel.navigateTo(Screen.PlantDetail(activePlant.id)) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = com.example.data.PlantUtils.getPlantImageUrl(activePlant, profile?.defaultEnvironment ?: "Tropical"),
                                        contentDescription = activePlant.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Organic sloshing wave progress bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Hydration Level",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "${(activePlant.waterProgress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Liquid Sloshing animated progress bar (feels like real CSS waves)
                            WaterWaveProgressIndicator(
                                progress = activePlant.waterProgress,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Large bouncing action button with soft feedback
                            var isButtonPressed by remember { mutableStateOf(false) }
                            val buttonScale = animateFloatAsState(
                                targetValue = if (isButtonPressed) 0.93f else 1.0f,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                label = "ButtonPressScale",
                                finishedListener = {
                                    if (isButtonPressed) {
                                        isButtonPressed = false
                                    }
                                }
                            )

                            Button(
                                onClick = {
                                    isButtonPressed = true
                                    viewModel.waterPlant(activePlant.id)
                                    isWateredTrigger = true

                                    // Spontaneous splash droplets rise
                                    waterTrigger++
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .graphicsLayer {
                                        scaleX = buttonScale.value
                                        scaleY = buttonScale.value
                                    }
                                    .shadowAmbientActive(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(28.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.WaterDrop, contentDescription = "Water", tint = MaterialTheme.colorScheme.onPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (activePlant.waterProgress >= 0.99f) "Flourishing!" else "Water Me",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Canvas particle overlay showing beautiful physical bubbles floating above
                        WaterDropletsOverlay(waterTrigger = waterTrigger)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = "https://image.pollinations.ai/prompt/soft_organic_illustration_of_empty_plant_pot_with_glowing_magical_soil_pastel_colors_vector_minimalist?width=800&height=800&nologo=true",
                    contentDescription = "Empty Garden",
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "No plants in your garden yet. Head over to the Nursery to add some!",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Quick advice card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .shadowAmbient(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Advice Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Daily Botanical Tip",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Plants respond wonderfully to natural daylight. Avoid turning on harsh overhead fluorescent light during high-noon sunlight gaps.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // --- DASHBOARD CARD GRID SECTION ---
        val collectedPlants = plants.filter { it.isCollected }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Collection Dashboard",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                val thirstyCount = collectedPlants.count { it.waterProgress < 0.40f }
                if (thirstyCount > 0) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$thirstyCount DUE FOR WATER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                } else if (collectedPlants.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ALL HYDRATED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            Text(
                text = "Grid view of added species & hydration frequencies.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            if (collectedPlants.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .shadowAmbient(),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = "https://image.pollinations.ai/prompt/soft_organic_illustration_of_empty_plant_pot_with_glowing_magical_soil_pastel_colors_vector_minimalist?width=800&height=800&nologo=true",
                            contentDescription = "Empty Garden",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No plants added yet!",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Discover seeds & plants in the Nursery to build your dashboard grid.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.navigateTo(Screen.Nursery) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Visit Nursery", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            } else {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("search_bar_input"),
                    placeholder = { Text("Search species name or properties...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
                )

                // Horizontally Scrollable Filter Chips
                val filterChips = listOf("All", "Tropical", "Desert", "Nebula", "Indoor", "Outdoor", "Succulent")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    filterChips.forEach { chip ->
                        val isSelected = selectedFilterChip == chip
                        val containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        }
                        val contentColor = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(
                                    color = containerColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedFilterChip = chip }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .testTag("filter_chip_$chip")
                        ) {
                            Text(
                                text = chip,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = contentColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Apply filtering logic
                val filteredPlants = collectedPlants.filter { plant ->
                    val matchesSearch = searchQuery.trim().isEmpty() ||
                        plant.name.contains(searchQuery, ignoreCase = true) ||
                        plant.scientificName.contains(searchQuery, ignoreCase = true) ||
                        plant.about.contains(searchQuery, ignoreCase = true)
                        
                    val matchesChip = when (selectedFilterChip) {
                        "All" -> true
                        "Tropical" -> plant.category.equals("Tropical", ignoreCase = true)
                        "Desert" -> plant.category.equals("Desert", ignoreCase = true)
                        "Nebula" -> plant.category.equals("Nebula", ignoreCase = true)
                        "Indoor" -> plant.about.contains("indoor", ignoreCase = true) || 
                                     plant.light.contains("indoor", ignoreCase = true) || 
                                     plant.name.contains("indoor", ignoreCase = true)
                        "Outdoor" -> plant.about.contains("outdoor", ignoreCase = true) || 
                                      plant.light.contains("outdoor", ignoreCase = true) || 
                                      plant.name.contains("outdoor", ignoreCase = true)
                        "Succulent" -> plant.about.contains("succulent", ignoreCase = true) || 
                                        plant.name.contains("succulent", ignoreCase = true) || 
                                        plant.category.equals("Desert", ignoreCase = true)
                        else -> true
                    }
                    matchesSearch && matchesChip
                }

                if (filteredPlants.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .shadowAmbient(),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No matches found",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "No plants match your search/filter parameters.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedFilterChip = "All"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Reset Filters", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                } else {
                    val rows = filteredPlants.chunked(2)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rows.forEach { rowPairs ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                rowPairs.forEach { plant ->
                                    val isDue = plant.waterProgress < 0.40f
                                    val frequencyText = when (plant.waterLevel.lowercase()) {
                                        "high" -> "Every 2 Days"
                                        "medium" -> "Every 5 Days"
                                        "low" -> "Every 10 Days"
                                        else -> "Every 7 Days"
                                    }
                                    
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .shadowAmbient()
                                            .clickable { viewModel.navigateTo(Screen.PlantDetail(plant.id)) },
                                        shape = RoundedCornerShape(24.dp),
                                        border = BorderStroke(
                                            width = if (isDue) 2.dp else 1.dp,
                                            color = if (isDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isDue) {
                                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                                            } else {
                                                MaterialTheme.colorScheme.surfaceContainerLowest
                                            }
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(1.2f)
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AsyncImage(
                                                    model = com.example.data.PlantUtils.getPlantImageUrl(plant, viewModel.userProfile.value?.defaultEnvironment ?: "Tropical"),
                                                    contentDescription = plant.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                                
                                                if (isDue) {
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.TopEnd)
                                                            .padding(6.dp)
                                                            .background(
                                                                color = MaterialTheme.colorScheme.error,
                                                                shape = RoundedCornerShape(8.dp)
                                                            )
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "DUE",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.Bold
                                                            ),
                                                            color = MaterialTheme.colorScheme.onError
                                                        )
                                                    }
                                                }
                                            }
                                            
                                            Spacer(modifier = Modifier.height(10.dp))
                                            
                                            Text(
                                                text = plant.name,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            
                                            Text(
                                                text = plant.scientificName,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.outline,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            
                                            val now = System.currentTimeMillis()
                                            val diffMs = now - plant.lastWatered
                                            val lastWateredText = if (plant.lastWatered == 0L) {
                                                "Never"
                                            } else {
                                                val diffSecs = diffMs / 1000
                                                val diffMins = diffSecs / 60
                                                val diffHours = diffMins / 60
                                                when {
                                                    diffSecs < 10 -> "Just now"
                                                    diffSecs < 60 -> "$diffSecs secs ago"
                                                    diffMins < 60 -> "$diffMins min${if (diffMins > 1) "s" else ""} ago"
                                                    diffHours < 24 -> "$diffHours hr${if (diffHours > 1) "s" else ""} ago"
                                                    else -> {
                                                        val days = diffHours / 24
                                                        "$days day${if (days > 1) "s" else ""} ago"
                                                    }
                                                }
                                            }
                                            
                                            Text(
                                                text = "Last: $lastWateredText",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.outline,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            
                                            Spacer(modifier = Modifier.height(6.dp))
                                            
                                            Text(
                                                text = "Need: $frequencyText",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isDue) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (isDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            
                                            Spacer(modifier = Modifier.height(6.dp))
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = if (isDue) "Thirsty" else "Hydrated",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    color = if (isDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = "${(plant.waterProgress * 100).toInt()}%",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    color = if (isDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            
                                            Spacer(modifier = Modifier.height(4.dp))
                                            
                                            LinearProgressIndicator(
                                                progress = { plant.waterProgress },
                                                color = if (isDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                            )
                                            
                                            Spacer(modifier = Modifier.height(10.dp))
                                            
                                            Button(
                                                onClick = { viewModel.waterPlant(plant.id) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                                ),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.fillMaxWidth().height(32.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        Icons.Default.WaterDrop,
                                                        contentDescription = "Water plant",
                                                        tint = if (isDue) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = if (isDue) "Water Due" else "Mark as Watered",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                        color = if (isDue) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                if (rowPairs.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- SCREEN 2: NURSERY / MARKETPLACE ---
@Composable
fun NurseryScreen(viewModel: SproutViewModel) {
    val plants by viewModel.plants.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeTab by viewModel.nurseryTab.collectAsState()
    val realOrMakeBelieve by viewModel.realOrMakeBelieveTab.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Upper block: title, search catalog, seed balance tracker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "The Nursery",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Buy seeds & add beautiful green life.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Interactive header buttons and seed wallet
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.CareGuides) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = "Care Almanac",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Card(
                    onClick = { viewModel.navigateTo(Screen.PurchaseSeeds) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.shadowAmbient()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Seeds Wallet", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${profile?.seedBalance ?: 0}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Nursery Hero Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 20.dp)
                .height(160.dp)
                .shadowAmbient(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = "https://image.pollinations.ai/prompt/beautiful_lush_greenhouse_botanical_garden_nursery_with_glowing_light_rays_vector_art_warm_aesthetic?width=800&height=400&nologo=true",
                    contentDescription = "Nursery Hero",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                )
                Text(
                    text = "Discover New Species",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search plant species...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent
            ),
            singleLine = true
        )

        // Segment Tabs: Real vs Make-Believe
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            TabButton(
                text = "Real Plants",
                isSelected = realOrMakeBelieve == "Real",
                onClick = { viewModel.setRealOrMakeBelieveTab("Real") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TabButton(
                text = "Make-Believe",
                isSelected = realOrMakeBelieve == "Make-Believe",
                onClick = { viewModel.setRealOrMakeBelieveTab("Make-Believe") },
                modifier = Modifier.weight(1f)
            )
        }

        // Category Filter scrollable pill row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categories = listOf("All", "Tropical", "Desert", "Nebula", "Tundra")
            categories.forEach { cat ->
                val isSelected = activeTab == cat
                InputChip(
                    selected = isSelected,
                    onClick = { viewModel.setNurseryTab(cat) },
                    label = { Text(cat) },
                    colors = InputChipDefaults.inputChipColors(
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedContainerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // Plants Catalog list scroll
        val filteredPlants = plants.filter { plant ->
            val matchesSearch = plant.name.contains(searchQuery, ignoreCase = true) ||
                    plant.scientificName.contains(searchQuery, ignoreCase = true)

            val matchesCategory = activeTab == "All" || plant.category == activeTab

            // Determine if Real or Make-Believe matches
            val matchesType = if (realOrMakeBelieve == "Real") {
                plant.category != "Nebula" // Nebula and other fantasy are make-believe
            } else {
                plant.category == "Nebula"
            }

            matchesSearch && matchesCategory && matchesType
        }

        if (filteredPlants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Info, contentDescription = "None", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No matching species found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredPlants) { plant ->
                    NurseryPlantItem(
                        plant = plant,
                        environment = viewModel.userProfile.value?.defaultEnvironment ?: "Tropical",
                        onViewDetails = { viewModel.navigateTo(Screen.PlantDetail(plant.id)) },
                        onAddOrBuy = {
                            if (plant.isCollected) {
                                Toast.makeText(context, "${plant.name} is already in your Garden!", Toast.LENGTH_SHORT).show()
                            } else {
                                // Purchase logic mapping seed balance
                                viewModel.purchasePlant(
                                    plant.id,
                                    onSuccess = { Toast.makeText(context, "${plant.name} added to Garden successfully!", Toast.LENGTH_SHORT).show() },
                                    onFailure = { viewModel.navigateTo(Screen.PurchaseSeeds) }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NurseryPlantItem(
    plant: Plant,
    environment: String = "Tropical",
    onViewDetails: () -> Unit,
    onAddOrBuy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() }
            .shadowAmbient(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = com.example.data.PlantUtils.getPlantImageUrl(plant, environment),
                    contentDescription = plant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Rarity Tag Badge
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                        .background(
                            color = when (plant.rarity) {
                                "Legendary" -> Color(0xFFEFB8C8)
                                "Epic" -> Color(0xFFB2AD7D)
                                "Rare" -> Color(0xFF9DD090)
                                else -> MaterialTheme.colorScheme.primaryContainer
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = plant.rarity,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = plant.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = plant.scientificName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontStyle = FontStyle.Italic
                        )
                    }
                    // Price tag
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Seed Cost",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "${plant.seedPrice} Seeds",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewDetails,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("About Info", style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = onAddOrBuy,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (plant.isCollected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (plant.isCollected) "In Garden" else "Buy & Plant",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// --- SCREEN 3: MY GARDEN ---
@Composable
fun GardenScreen(viewModel: SproutViewModel) {
    val plants by viewModel.plants.collectAsState()
    val collectedPlants = plants.filter { it.isCollected }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // My Garden Header block & Stats
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = "My Garden",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Collected Species: ${collectedPlants.size} / ${plants.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Grid Stats bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .shadowAmbient(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.WaterDrop, contentDescription = "Water drop", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Collection Care",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = collectedPlants.firstOrNull { it.waterProgress < 0.40f }?.let { "Needs Water" } ?: "Hydrated",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .shadowAmbient(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "Favs", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Collected Ratio",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "${((collectedPlants.size.toFloat() / plants.size) * 100).toInt()}% Discovery",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (collectedPlants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = "https://image.pollinations.ai/prompt/soft_organic_illustration_of_empty_plant_pot_with_glowing_magical_soil_pastel_colors_vector_minimalist?width=800&height=800&nologo=true",
                        contentDescription = "Empty Garden",
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "No plants saved. Purchase seeds or grab plants inside the Nursery to start!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(collectedPlants) { plant ->
                    GardenPlantItem(
                        plant = plant,
                        environment = viewModel.userProfile.value?.defaultEnvironment ?: "Tropical",
                        onViewDetails = { viewModel.navigateTo(Screen.PlantDetail(plant.id)) },
                        onSetAsWidget = {
                            viewModel.setAsWidget(plant.id)
                            Toast.makeText(context, "${plant.name} is now your active focus widget plant!", Toast.LENGTH_SHORT).show()
                        },
                        onToggleNotification = { isEnabled ->
                            viewModel.updatePlant(plant.copy(isNotificationEnabled = isEnabled))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GardenPlantItem(
    plant: Plant,
    environment: String = "Tropical",
    onViewDetails: () -> Unit,
    onSetAsWidget: () -> Unit,
    onToggleNotification: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() }
            .shadowAmbient(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(80.dp)) {
                AsyncImage(
                    model = com.example.data.PlantUtils.getPlantImageUrl(plant, environment),
                    contentDescription = plant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )
                
                // Overdue Task Badge Counter
                if (plant.waterProgress < 0.4f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(20.dp)
                            .background(MaterialTheme.colorScheme.error, shape = CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.surface, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "1",
                            color = MaterialTheme.colorScheme.onError,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = plant.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = plant.scientificName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Per-plant care reminder notification toggle
                        IconButton(
                            onClick = {
                                onToggleNotification(!plant.isNotificationEnabled)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (plant.isNotificationEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                contentDescription = "Toggle Reminders",
                                tint = if (plant.isNotificationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        val nextWateringInfo = com.example.data.PlantUtils.getNextWateringInfo(plant, plant.careInstructions)
                        if (nextWateringInfo.daysRemaining == 0) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(
                                        "Water Today",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else if (nextWateringInfo.daysRemaining <= 2) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(
                                        "Due in ${nextWateringInfo.daysRemaining}d",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (plant.isWidgetActive) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(
                                        "Widget Active",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Water Meter progress bar details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Water Level: " + if (plant.waterProgress < 0.4f) "Thirsty" else "Good",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (plant.waterProgress < 0.4f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "${(plant.waterProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { plant.waterProgress },
                    color = if (plant.waterProgress < 0.4f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                if (!plant.isWidgetActive) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onSetAsWidget,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Pin to Widget",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// --- SCREEN 4: PROFILE & STATS ---
data class SproutAchievement(
    val id: String,
    val title: String,
    val description: String,
    val requirementText: String,
    val rewardSeeds: Int,
    val unlockTheme: String? = null,
    val unlockFont: String? = null,
    val icon: ImageVector,
    val isCompleted: (UserProfile?, List<Plant>) -> Boolean
)

val achievementsList = listOf(
    SproutAchievement(
        id = "eco_novice",
        title = "Eco Novice",
        description = "Grow your private plant sanctuary.",
        requirementText = "Own 3 or more collected plants",
        rewardSeeds = 200,
        icon = Icons.Default.Spa,
        isCompleted = { profile, plants -> plants.filter { it.isCollected }.size >= 3 }
    ),
    SproutAchievement(
        id = "desert_survivor",
        title = "Desert Nomad",
        description = "Cultivate the dry sands.",
        requirementText = "Own 1 or more Desert category plants",
        rewardSeeds = 250,
        unlockTheme = "Golden Desert",
        icon = Icons.Default.WbSunny,
        isCompleted = { profile, plants -> plants.any { it.isCollected && it.category.equals("Desert", ignoreCase = true) } }
    ),
    SproutAchievement(
        id = "galactic_botanist",
        title = "Space Botanist",
        description = "Discover stellar gas-dwellers.",
        requirementText = "Own 1 or more Nebula category plants",
        rewardSeeds = 400,
        unlockTheme = "Neon Cyberpunk",
        icon = Icons.Default.Public,
        isCompleted = { profile, plants -> plants.any { it.isCollected && it.category.equals("Nebula", ignoreCase = true) } }
    ),
    SproutAchievement(
        id = "water_champion",
        title = "Hydration Hero",
        description = "Ensure perfect hydration status.",
        requirementText = "Water plants 120 or more times total",
        rewardSeeds = 150,
        unlockTheme = "Cozy Forest",
        icon = Icons.Default.WaterDrop,
        isCompleted = { profile, plants -> (profile?.totalWaterings ?: 0) >= 120 }
    ),
    SproutAchievement(
        id = "streak_legend",
        title = "Streak Legend",
        description = "Keep up the watering routine.",
        requirementText = "Watering streak of 14 days or more",
        rewardSeeds = 300,
        unlockFont = "Monospace",
        icon = Icons.Default.Eco,
        isCompleted = { profile, plants -> (profile?.wateringStreak ?: 0) >= 14 }
    ),
    SproutAchievement(
        id = "mature_master",
        title = "Mature Master",
        description = "Develop an elite nursery.",
        requirementText = "Reach 18 or more matured flora count",
        rewardSeeds = 500,
        unlockFont = "Serif",
        unlockTheme = "Pastel Sakura",
        icon = Icons.Default.Star,
        isCompleted = { profile, plants -> (profile?.plantsMatured ?: 0) >= 18 }
    ),
    SproutAchievement(
        id = "cursive_unlocked",
        title = "Artisanal Scroll",
        description = "Save up seeds to show your balance.",
        requirementText = "Accumulate 2,500 or more seeds",
        rewardSeeds = 100,
        unlockFont = "Cursive",
        icon = Icons.Default.Edit,
        isCompleted = { profile, plants -> (profile?.seedBalance ?: 0) >= 2500 }
    ),
    SproutAchievement(
        id = "fibonacci_wizard",
        title = "Fibonacci Maestro",
        description = "Harmonize with sacred organic shapes.",
        requirementText = "Set active profile growth preference to Fibonacci Spiral",
        rewardSeeds = 350,
        unlockTheme = "Golden Hour",
        icon = Icons.Default.FilterVintage,
        isCompleted = { profile, _ -> profile?.growthPattern == "Fibonacci Spiral" }
    ),
    SproutAchievement(
        id = "elite_hybrid",
        title = "Legendary Grower",
        description = "Cultivate extremely rare botanical specimen.",
        requirementText = "Own at least one Legendary plant",
        rewardSeeds = 600,
        unlockTheme = "Obsidian Dark",
        icon = Icons.Default.AutoAwesome,
        isCompleted = { _, plants -> plants.any { it.isCollected && it.rarity == "Legendary" } }
    ),
    SproutAchievement(
        id = "nurture_guru",
        title = "Nurture Guru",
        description = "Nurture plants to their peak majestic stage.",
        requirementText = "Mature 20 or more plants",
        rewardSeeds = 500,
        unlockFont = "Space Grotesk",
        icon = Icons.Default.Park,
        isCompleted = { profile, _ -> (profile?.plantsMatured ?: 0) >= 20 }
    )
)

@Composable
fun StatsScreen(viewModel: SproutViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val plants by viewModel.plants.collectAsState()
    val collectedPlants = plants.filter { it.isCollected }

    // Interactive chart values based on user's plants
    var selectedBarLabel by remember { mutableStateOf<String?>("") }
    var selectedBarProgress by remember { mutableStateOf<Int>(0) }
    
    // Update default selection when plants load
    LaunchedEffect(collectedPlants) {
        if (collectedPlants.isNotEmpty() && selectedBarLabel.isNullOrEmpty()) {
            val firstPlant = collectedPlants.first()
            selectedBarLabel = firstPlant.name
            selectedBarProgress = (firstPlant.waterProgress * 100).toInt()
        }
    }

    var activeTab by remember { mutableStateOf("Stats") } // "Stats", "Achievements", "Custom"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Profile Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(profile?.avatarColor ?: 0xFF4CAF50.toInt())),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile?.name?.take(1)?.uppercase() ?: "S",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = profile?.name ?: "Jane Doe",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Level ${profile?.userLevel ?: 1} Botanist Explorer (XP: ${profile?.xp ?: 0})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            IconButton(onClick = { viewModel.navigateTo(Screen.Settings) }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }

        // Segmented Tab bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf("Stats", "Achievements", "Custom")
            tabs.forEach { tab ->
                val isSelected = activeTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { activeTab = tab }
                        .padding(vertical = 10.dp)
                        .testTag("stats_tab_$tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (tab) {
                            "Custom" -> "Customize"
                            else -> tab
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeTab) {
            "Stats" -> {
                // Bento Grid Stats
                Text(
                    text = "Activity Stats",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // First Row of Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Total Waterings",
                            value = "${profile?.totalWaterings ?: 0}",
                            icon = Icons.Default.WaterDrop,
                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "Watering Streak",
                            value = "${profile?.wateringStreak ?: 0} Days",
                            icon = Icons.Default.LocalFlorist,
                            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Second Row of Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Seeds Planted",
                            value = "${profile?.seedsPlanted ?: 0}",
                            icon = Icons.Default.Eco,
                            backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "Matured Flora",
                            value = "${profile?.plantsMatured ?: 0}",
                            icon = Icons.Default.Star,
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // interactive Visual Chart Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .shadowAmbient(),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Hydration Levels",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Your active garden",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${selectedBarLabel ?: "None"}: $selectedBarProgress%",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Bar charts layout
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val chartPlants = collectedPlants.take(7)
                            if (chartPlants.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Plant seeds to see hydration levels!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                }
                            } else {
                                chartPlants.forEach { plant ->
                                    val level = (plant.waterProgress * 100).toInt().coerceIn(5, 100) // min 5% for visual
                                    val isSelected = selectedBarLabel == plant.name
                                    val shortName = if (plant.name.length > 5) plant.name.take(3) + ".." else plant.name
                                    
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedBarLabel = plant.name
                                                selectedBarProgress = (plant.waterProgress * 100).toInt()
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(16.dp)
                                                .fillMaxHeight(fraction = level / 100f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isSelected) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = shortName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Environmental Diversity report box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .shadowAmbient(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Environmental Diversity Status",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (collectedPlants.isEmpty()) {
                                "Your ecosystem is empty. Head to the Nursery and plant some species to analyze diversity!"
                            } else {
                                "Your botanical garden is currently thriving with a balance of ${collectedPlants.groupBy { it.category }.keys.joinToString(", ") { it }} species. Maintain high watering streaks for continued ecological equilibrium."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            "Achievements" -> {
                AchievementsTabContent(viewModel, profile, plants)
            }
            "Custom" -> {
                CustomizationTabContent(viewModel, profile)
            }
        }
    }
}

@Composable
fun AchievementsTabContent(
    viewModel: SproutViewModel,
    profile: UserProfile?,
    plants: List<Plant>
) {
    val completedIds = profile?.completedAchievements?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()

    // Title / Completed Summary Bar
    val totalAchievements = achievementsList.size
    val completedCount = achievementsList.count { it.isCompleted(profile, plants) }
    val claimedCount = achievementsList.count { completedIds.contains(it.id) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .shadowAmbient(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Trophy Room Objectives",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$completedCount of $totalAchievements Achieved ($claimedCount Claimed)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { if (totalAchievements > 0) completedCount.toFloat() / totalAchievements else 0f },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // List of achievements
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        achievementsList.forEach { achievement ->
            val hasMetRequirement = achievement.isCompleted(profile, plants)
            val isClaimed = completedIds.contains(achievement.id)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadowAmbient()
                    .testTag("achievement_card_${achievement.id}"),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isClaimed) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            else if (hasMetRequirement) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isClaimed) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                     else if (hasMetRequirement) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                                     else MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Icon Circle
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                if (isClaimed) MaterialTheme.colorScheme.surfaceVariant
                                else if (hasMetRequirement) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = achievement.icon,
                            contentDescription = achievement.title,
                            tint = if (isClaimed) MaterialTheme.colorScheme.outline
                                   else if (hasMetRequirement) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Center text details
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = achievement.title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isClaimed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        else MaterialTheme.colorScheme.onSurface
                            )
                            if (isClaimed) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Claimed",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = achievement.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Req: ${achievement.requirementText}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (hasMetRequirement && !isClaimed) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline
                        )
                        
                        // Show rewards
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = if (isClaimed) 0.3f else 1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "+${achievement.rewardSeeds} Seeds",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            if (achievement.unlockTheme != null) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isClaimed) 0.3f else 1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "🎨 ${achievement.unlockTheme}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            if (achievement.unlockFont != null) {
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = if (isClaimed) 0.3f else 1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "🔤 ${achievement.unlockFont}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Right column: Claim action / state
                    if (isClaimed) {
                        Text(
                            text = "Claimed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    } else if (hasMetRequirement) {
                        Button(
                            onClick = {
                                if (profile != null) {
                                    val completedIdsList = if (profile.completedAchievements.isEmpty()) {
                                        achievement.id
                                    } else {
                                        "${profile.completedAchievements},${achievement.id}"
                                    }

                                    var unlockedThemes = profile.unlockedThemes
                                    if (achievement.unlockTheme != null) {
                                        val themesList = unlockedThemes.split(",")
                                        if (!themesList.contains(achievement.unlockTheme)) {
                                            unlockedThemes = "$unlockedThemes,${achievement.unlockTheme}"
                                        }
                                    }

                                    var unlockedFonts = profile.unlockedFonts
                                    if (achievement.unlockFont != null) {
                                        val fontsList = unlockedFonts.split(",")
                                        if (!fontsList.contains(achievement.unlockFont)) {
                                            unlockedFonts = "$unlockedFonts,${achievement.unlockFont}"
                                        }
                                    }

                                    val updatedProfile = profile.copy(
                                        seedBalance = profile.seedBalance + achievement.rewardSeeds,
                                        completedAchievements = completedIdsList,
                                        unlockedThemes = unlockedThemes,
                                        unlockedFonts = unlockedFonts
                                    )
                                    viewModel.updateProfile(updatedProfile)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("claim_button_${achievement.id}")
                        ) {
                            Text(
                                "CLAIM",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun CustomizationTabContent(
    viewModel: SproutViewModel,
    profile: UserProfile?
) {
    val unlockedThemesList = profile?.unlockedThemes?.split(",")?.filter { it.isNotEmpty() } ?: listOf("Default")
    val unlockedFontsList = profile?.unlockedFonts?.split(",")?.filter { it.isNotEmpty() } ?: listOf("System")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // App Theme Selector Section
        Text(
            text = "App Color Themes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        val themes = listOf(
            Triple("Default", "Standard Sprout green hues", 1),
            Triple("Cozy Forest", "Deep organic moss green & oak wood", 3),
            Triple("Pastel Sakura", "Soft cherry blossom ivory & petal pinks", 5),
            Triple("Golden Desert", "Terracotta clay & high-sunbaked sand tan", 10),
            Triple("Neon Cyberpunk", "Futuristic high-contrast neon cyan & magenta", 15),
            Triple("Golden Hour", "Sunset orange and twilight blues", 20),
            Triple("Obsidian Dark", "Pitch black with neon accents", 25)
        )

        val userLevel = profile?.userLevel ?: 1

        themes.forEach { (themeName, description, requiredLevel) ->
            val isUnlocked = userLevel >= requiredLevel
            val isSelected = profile?.selectedTheme == themeName

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isUnlocked) {
                        if (profile != null) {
                            viewModel.updateProfile(profile.copy(selectedTheme = themeName))
                        }
                    }
                    .shadowAmbient()
                    .testTag("theme_card_$themeName"),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                                     else if (!isUnlocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                     else MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circle indicator representation of the colors
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                when (themeName) {
                                    "Cozy Forest" -> Color(0xFF2E5A36)
                                    "Neon Cyberpunk" -> Color(0xFF00F0FF)
                                    "Pastel Sakura" -> Color(0xFFE899A5)
                                    "Golden Desert" -> Color(0xFFCD7F32)
                                    "Golden Hour" -> Color(0xFFFF9800)
                                    "Obsidian Dark" -> Color(0xFF121212)
                                    else -> Color(0xFF386B40) // Default sprout green
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active Theme",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else if (!isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked Theme",
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = themeName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) MaterialTheme.colorScheme.onSurface 
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = if (isUnlocked) description else "🔒 Locked: Reach Level $requiredLevel to unlock.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // App Typography Font Selector Section
        Text(
            text = "App Text Fonts",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        val fonts = listOf(
            Triple("System", "Standard modern sans-serif", "Default Font"),
            Triple("Monospace", "Retro tech typewriter style", "Streak Legend"),
            Triple("Serif", "Classic elegant botanist journal style", "Mature Master"),
            Triple("Cursive", "Creative artistic cursive styling", "Artisanal Scroll")
        )

        fonts.forEach { (fontName, description, requiredAchievement) ->
            val isUnlocked = unlockedFontsList.contains(fontName)
            val isSelected = profile?.selectedFont == fontName

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isUnlocked) {
                        if (profile != null) {
                            viewModel.updateProfile(profile.copy(selectedFont = fontName))
                        }
                    }
                    .shadowAmbient()
                    .testTag("font_card_$fontName"),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                                     else if (!isUnlocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                     else MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else org.jetbrains.annotations.Nullable::class.java.let { MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active Font",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        } else if (!isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked Font",
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                "Aa",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = when (fontName) {
                                        "Monospace" -> FontFamily.Monospace
                                        "Serif" -> FontFamily.Serif
                                        "Cursive" -> FontFamily.Cursive
                                        else -> FontFamily.SansSerif
                                    }
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = fontName,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = when (fontName) {
                                    "Monospace" -> FontFamily.Monospace
                                    "Serif" -> FontFamily.Serif
                                    "Cursive" -> FontFamily.Cursive
                                    else -> FontFamily.SansSerif
                                }
                            ),
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) MaterialTheme.colorScheme.onSurface 
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = if (isUnlocked) description else "🔒 Locked: claim \"$requiredAchievement\" to unlock.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // App Botanist Growth Focus Selector Section
        Text(
            text = "Botanist Growth Focus Pattern",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        val focuses = listOf(
            "S-Curve Sigmoid" to "Adapts to physical space limits with classic logistic acceleration curves.",
            "Fibonacci Spiral" to "Models fluid growth in sacred organic spiral golden arrangements.",
            "Fractal Branching" to "Spreads dendritic branches recursively, maximizing ambient light."
        )

        focuses.forEach { (focusName, desc) ->
            val isSelected = profile?.growthPattern == focusName

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (profile != null) {
                            viewModel.updateProfile(profile.copy(growthPattern = focusName))
                        }
                    }
                    .shadowAmbient()
                    .testTag("focus_card_$focusName"),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                                     else MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Check else Icons.Default.FilterVintage,
                            contentDescription = focusName,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = focusName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadowAmbient(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Column {
                Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

// --- SCREEN 5: SETTINGS ---
@Composable
fun SettingsScreen(viewModel: SproutViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Custom Top Settings Header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Settings",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Group 1: General Preferences
            Card(
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                modifier = Modifier.shadowAmbient()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Theme & Aesthetics",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Switch 1: Dark Mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DarkMode, contentDescription = "Dark Mode", tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Dark Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Eye-safe dark slate palette", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = profile?.isDarkMode ?: false,
                            onCheckedChange = { viewModel.toggleDarkMode(it) }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Switch 2: Notifications
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Watering Alerts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Remind me when leaves are dry", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = profile?.notificationEnabled ?: true,
                            onCheckedChange = { viewModel.toggleNotification(it) }
                        )
                    }
                }
            }

            // Group 2: Account details
            Card(
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                modifier = Modifier.shadowAmbient()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Gardening Environment",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val environments = listOf("Tundra" to Icons.Default.AcUnit, "Desert" to Icons.Default.WbSunny, "Nebula" to Icons.Default.AutoAwesome, "Tropical" to Icons.Default.Park)
                    environments.forEachIndexed { index, (env, icon) ->
                        val isSelected = profile?.defaultEnvironment == env
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.updateDefaultEnvironment(env) }
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    icon, 
                                    contentDescription = env, 
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    env, 
                                    style = MaterialTheme.typography.titleMedium, 
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                            }
                        }
                        if (index < environments.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Logout/Wipe button
            Button(
                onClick = {
                    viewModel.logout()
                    Toast.makeText(context, "Logged out successfully.", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadowAmbient(),
                shape = RoundedCornerShape(32.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.onErrorContainer)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Log Out Sprout", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// --- SCREEN 6: PLANT DETAIL ---
@Composable
fun PlantDetailScreen(
    viewModel: SproutViewModel,
    plantId: String
) {
    val plants by viewModel.plants.collectAsState()
    val plant = plants.firstOrNull { it.id == plantId }
    val profile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    if (plant != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Image Box with overlay
            val plantScale = when(plant.stage.lowercase()) {
                "seed", "spore", "cosmic seed" -> 0.35f
                "seedling", "sprout" -> 0.5f
                "juvenile", "small plant", "cutting", "shoot", "bulb" -> 0.75f
                "mature" -> 1.0f
                "flowering", "majestic" -> 1.15f
                else -> 1.0f
            }
            
            val animatedPlantScale by animateFloatAsState(
                targetValue = plantScale, 
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "plant_growth_scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp) // created space for the image to change and grow
                    .clickable {
                        viewModel.nurturePlant(plant.id) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = com.example.data.PlantUtils.getPlantImageUrl(plant, profile?.defaultEnvironment ?: "Tropical"),
                    contentDescription = plant.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(animatedPlantScale)
                )

                // Navigation overlay actions buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 40.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }

                    IconButton(
                        onClick = {
                            viewModel.toggleFavorite(plantId)
                            val statusMsg = if (!plant.isWishlist) "${plant.name} added to Wishlist!" else "Removed ${plant.name} from Wishlist."
                            Toast.makeText(context, statusMsg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                    ) {
                        Icon(
                            imageVector = if (plant.isWishlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (plant.isWishlist) Color.Red else Color.Black
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Intro Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = plant.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = plant.scientificName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = "Origin", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = plant.origin,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // About section
                Column {
                    Text(
                        "About Sprout",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = plant.about,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )
                }

                // Environmental Reaction Section
                val reactionText = com.example.data.PlantUtils.getEnvironmentReactionText(plant, profile?.defaultEnvironment ?: "Tropical")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "🌍 Environmental Reaction",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = profile?.defaultEnvironment ?: "Tropical",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = reactionText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        lineHeight = 20.sp
                    )
                }

                // Care Needs Bento Layout Grid
                Column {
                    Text(
                        "Care Requirements",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CareRequirementCard(
                            label = "Light Needs",
                            value = plant.light,
                            icon = Icons.Default.Star,
                            modifier = Modifier.weight(1f)
                        )
                        CareRequirementCard(
                            label = "Water Meter",
                            value = plant.waterLevel,
                            icon = Icons.Default.WaterDrop,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    CareRequirementCard(
                        label = "Soil / Medium Recommended",
                        value = plant.soil,
                        icon = Icons.Default.Eco,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Custom steppy Growth Path layout
                Column {
                    Text(
                        "Growth Stepper Timeline",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    StepperItem(step = "1", title = "Seedling / Spore", timeline = "0-6 Months", isActive = plant.stage == "Seedling" || plant.stage == "Spore")
                    StepperItem(step = "2", title = "Juvenile (Current Stage)", timeline = "6-18 Months", isActive = plant.stage == "Juvenile" || plant.stage == "Cosmic Seed")
                    StepperItem(step = "3", title = "Mature", timeline = "18+ Months", isActive = plant.stage == "Mature" || plant.stage == "Galactic Bloom")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mathematical Growth Focus Pattern
                if (plant.isCollected) {
                    Column {
                        Text(
                            text = "Growth Pattern (${profile?.growthPattern ?: "S-Curve Sigmoid"})",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Visualize the active mathematical shape tracing cell division for this floral node.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        GrowthPatternCanvas(
                            progress = plant.waterProgress,
                            patternType = profile?.growthPattern ?: "S-Curve Sigmoid"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action panel
                if (plant.isCollected) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Care Notification toggle for this specific plant
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (plant.isNotificationEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                        contentDescription = "Notification Status",
                                        tint = if (plant.isNotificationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "Care Reminders",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            "Notify when leaves are dry",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                                Switch(
                                    checked = plant.isNotificationEnabled,
                                    onCheckedChange = { isChecked ->
                                        viewModel.updatePlant(plant.copy(isNotificationEnabled = isChecked))
                                        val message = if (isChecked) "Enabled notifications for ${plant.name}" else "Disabled notifications for ${plant.name}"
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.waterPlant(plant.id)
                                    Toast.makeText(context, "${plant.name} watered successfully!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .shadowAmbientActive(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(28.dp)
                            ) {
                                Text(
                                    "Water Plant 💧",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.nurturePlant(plant.id) { success, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .shadowAmbientActive(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(28.dp)
                            ) {
                                Text(
                                    "Nurture 🌱",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.resetPlantGrowth(plant.id)
                                Toast.makeText(context, "${plant.name} reset to Seed stage!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadowAmbientActive(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text(
                                "Replant (Reset to Seed Stage) 🔄",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.purchasePlant(
                                plant.id,
                                onSuccess = { Toast.makeText(context, "${plant.name} purchased and added to your Garden!", Toast.LENGTH_SHORT).show() },
                                onFailure = {
                                    Toast.makeText(context, "Insufficient seeds. Opening shop!", Toast.LENGTH_SHORT).show()
                                    viewModel.navigateTo(Screen.PurchaseSeeds)
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadowAmbientActive(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = "Plant Seed for ${plant.seedPrice} Seeds",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CareRequirementCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadowAmbient(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun StepperItem(step: String, title: String, timeline: String, isActive: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                step,
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                timeline,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

// --- SCREEN 7: PURCHASE SEEDS / REFILL SHOP ---
@Composable
fun PurchaseSeedsScreen(viewModel: SproutViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Shop header top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Purchase Seeds",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Seed Wallet Card balance report
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadowAmbient(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Seeds Shop", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(28.dp))
                    }
                    Column {
                        Text(
                            text = "Seed Credit Tracker",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${profile?.seedBalance ?: 0} Seeds",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Text(
                text = "Refill Packages",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Packages Bento Grid choices
            SeedPackageCard(
                title = "Small Pouch",
                seeds = 500,
                price = "$4.99",
                onBuy = {
                    viewModel.buySeedPackage(500)
                    Toast.makeText(context, "Added 500 seed credits to Sprout Account balance!", Toast.LENGTH_SHORT).show()
                }
            )

            SeedPackageCard(
                title = "Gardener's Sack",
                seeds = 1200,
                price = "$9.99",
                tag = "Best Value",
                onBuy = {
                    viewModel.buySeedPackage(1200)
                    Toast.makeText(context, "Added 1200 seed credits to Sprout Account balance!", Toast.LENGTH_SHORT).show()
                }
            )

            SeedPackageCard(
                title = "Botanist's Crate",
                seeds = 3000,
                price = "$19.99",
                onBuy = {
                    viewModel.buySeedPackage(3000)
                    Toast.makeText(context, "Added 3000 seed credits to Sprout Account balance!", Toast.LENGTH_SHORT).show()
                }
            )

            // Terms disclaimer
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Sprout Seed Terms of Service",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Seed credits are virtual local values and are entirely free from physical cash conversion. All simulated seed additions help fund virtual growth.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun SeedPackageCard(
    title: String,
    seeds: Int,
    price: String,
    tag: String? = null,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadowAmbient(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Eco,
                        contentDescription = "Seeds pack icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "+$seeds Seed Credits",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                if (tag != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Refill wallet ($price)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Minimalist generic shadows helpers matching Sprout requirements
fun Modifier.shadowAmbient(cornerRadius: androidx.compose.ui.unit.Dp = 24.dp): Modifier = this.shadowAmbientCustom(alpha = 0.08f, cornerRadius = cornerRadius)
fun Modifier.shadowAmbientActive(cornerRadius: androidx.compose.ui.unit.Dp = 24.dp): Modifier = this.shadowAmbientCustom(alpha = 0.12f, cornerRadius = cornerRadius)

fun Modifier.shadowAmbientCustom(alpha: Float, cornerRadius: androidx.compose.ui.unit.Dp = 24.dp): Modifier = this
    .shadow(
        elevation = 16.dp,
        shape = RoundedCornerShape(cornerRadius),
        clip = false,
        ambientColor = Color(0xFF2E5A36).copy(alpha = alpha),
        spotColor = Color(0xFF2E5A36).copy(alpha = 0.5f)
    )


@Composable
fun SproutAdaptiveLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(38.dp).testTag("sprout_adaptive_logo")) {
        val w = size.width
        val h = size.height
        
        // Double botanical curved nodes!
        val leftNode = Path().apply {
            moveTo(w / 2f, h * 0.8f)
            cubicTo(w * 0.12f, h * 0.5f, w * 0.22f, h * 0.2f, w / 2f, h * 0.25f)
            cubicTo(w * 0.32f, h * 0.42f, w * 0.42f, h * 0.65f, w / 2f, h * 0.8f)
        }
        val rightNode = Path().apply {
            moveTo(w / 2f, h * 0.8f)
            cubicTo(w * 0.88f, h * 0.5f, w * 0.78f, h * 0.2f, w / 2f, h * 0.25f)
            cubicTo(w * 0.68f, h * 0.42f, w * 0.58f, h * 0.65f, w / 2f, h * 0.8f)
        }
        
        // Cozy forest green and primary dynamic gradients
        drawPath(leftNode, color = Color(0xFF2E5A36))
        drawPath(rightNode, color = Color(0xFF4CAF50))
        
        // Concentric geometric thin circle to look super app-store ready
        drawCircle(
            color = Color(0xFF81C784).copy(alpha = 0.6f),
            radius = w * 0.46f,
            style = Stroke(width = 1.5.dp.toPx())
        )
        // Center glowing seedling golden bud
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = w * 0.1f,
            center = Offset(w / 2f, h * 0.45f)
        )
    }
}

@Composable
fun GrowthPatternCanvas(
    progress: Float,
    patternType: String
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f))
            .padding(16.dp)
    ) {
        val w = size.width
        val h = size.height
        val mainColor = Color(0xFF4CAF50)
        
        when (patternType) {
            "Fibonacci Spiral" -> {
                // Draw spiral mathematical spiral shell path
                val path = Path()
                val r = 10f
                val centerX = w / 2f
                val centerY = h / 2f
                path.moveTo(centerX, centerY)
                val totalSteps = (180 * progress).toInt()
                for (i in 0..totalSteps) {
                    val theta = Math.toRadians(i.toDouble() * 3.0).toFloat()
                    val dist = r * Math.exp(0.12 * theta).toFloat()
                    val x = centerX + dist * Math.cos(theta.toDouble()).toFloat()
                    val y = centerY + dist * Math.sin(theta.toDouble()).toFloat()
                    if (x in 0f..w && y in 0f..h) {
                        path.lineTo(x, y)
                    }
                }
                drawPath(path, color = Color(0xFFFFB300), style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                // Draw little seeds along spiral
                if (progress > 0.5f) {
                    drawCircle(Color(0xFF81C784), radius = 6.dp.toPx(), center = Offset(centerX + 30f, centerY - 25f))
                }
                if (progress > 0.8f) {
                    drawCircle(Color(0xFF81C784), radius = 8.dp.toPx(), center = Offset(centerX - 60f, centerY + 40f))
                }
            }
            "Fractal Branching" -> {
                // Branching structure
                val startY = h
                val startX = w / 2f
                val trunkLen = h * 0.45f * progress
                val activeTrunkEnd = Offset(startX, startY - trunkLen)
                
                // Draw main trunk
                drawLine(
                    color = mainColor,
                    start = Offset(startX, startY),
                    end = activeTrunkEnd,
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                
                if (progress > 0.5f) {
                    val leftEnd = Offset(startX - (w * 0.18f * progress), activeTrunkEnd.y - (h * 0.15f * progress))
                    val rightEnd = Offset(startX + (w * 0.18f * progress), activeTrunkEnd.y - (h * 0.15f * progress))
                    drawLine(color = mainColor, start = activeTrunkEnd, end = leftEnd, strokeWidth = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    drawLine(color = mainColor, start = activeTrunkEnd, end = rightEnd, strokeWidth = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    
                    if (progress > 0.8f) {
                        val subLeft = Offset(leftEnd.x - (w * 0.08f), leftEnd.y - (h * 0.1f))
                        val subRight = Offset(rightEnd.x + (w * 0.08f), rightEnd.y - (h * 0.1f))
                        drawLine(color = Color(0xFFC8E6C9), start = leftEnd, end = subLeft, strokeWidth = 2.dp.toPx())
                        drawLine(color = Color(0xFFC8E6C9), start = rightEnd, end = subRight, strokeWidth = 2.dp.toPx())
                    }
                }
            }
            else -> {
                // "S-Curve Sigmoid"
                val path = Path()
                path.moveTo(0f, h * 0.95f)
                val totalSteps = (w * progress).toInt()
                for (x in 0..totalSteps) {
                    val xPct = x.toFloat() / w
                    val k = 10f
                    val x0 = 0.5f
                    val yPct = 1f / (1f + Math.exp(-k * (xPct - x0).toDouble())).toFloat()
                    val y = h * 0.95f - (yPct * h * 0.8f)
                    path.lineTo(x.toFloat(), y)
                }
                drawPath(path, color = Color(0xFF00F0FF), style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
                
                drawLine(
                    color = Color.White.copy(alpha = 0.3f),
                    start = Offset(0f, h * 0.15f),
                    end = Offset(w, h * 0.15f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: SproutViewModel) {
    var username by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    
    val registeredProfiles by viewModel.registeredProfiles.collectAsState()
    var isRegisterMode by remember(registeredProfiles.isEmpty()) { mutableStateOf(registeredProfiles.isEmpty()) }
    var selectedAvatarColor by remember { mutableStateOf(0xFF4CAF50.toInt()) } // Green default
    var selectedGrowthPref by remember { mutableStateOf("S-Curve Sigmoid") }
    val context = LocalContext.current
    
    val avatarColors = listOf(
        0xFF4CAF50.toInt(), // Green
        0xFFCD7F32.toInt(), // Terracotta Tan
        0xFFE899A5.toInt(), // Sakura Pink
        0xFF00F0FF.toInt(), // Neon Cyan
        0xFFFFB300.toInt()  // Amber Golden
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        
        SproutAdaptiveLogo(modifier = Modifier.size(90.dp))
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Welcome to Sprout",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "The Mathematical Botanical Sanctum",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 32.dp),
            textAlign = TextAlign.Center
        )
        
        if (!isRegisterMode) {
            if (registeredProfiles.isNotEmpty()) {
                Text(
                    text = "Select Botanic Profile",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
                )
                
                registeredProfiles.forEach { prof ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                username = prof.username
                                passcode = prof.passcode
                            }
                            .shadowAmbient()
                            .testTag("profile_shortcut_${prof.username}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (username == prof.username) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                             else MaterialTheme.colorScheme.surfaceContainerLowest
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(prof.avatarColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prof.name.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(prof.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text("@${prof.username}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Botanic Username") },
                placeholder = { Text("e.g. carllinnaeus") },
                modifier = Modifier.fillMaxWidth().testTag("login_username_input"),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = passcode,
                onValueChange = { passcode = it },
                label = { Text("Secure Pin Passcode") },
                placeholder = { Text("e.g. 1234") },
                modifier = Modifier.fillMaxWidth().testTag("login_passcode_input"),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (username.isEmpty() || passcode.isEmpty()) {
                        Toast.makeText(context, "Please key in both username and pin.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.loginToProfile(username, passcode,
                        onSuccess = { Toast.makeText(context, "Access success, botanical vault unlocked!", Toast.LENGTH_SHORT).show() },
                        onFailure = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).testTag("login_submit_button"),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Enter Botanic Vault", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = { isRegisterMode = true }) {
                Text("New Botanist? Create Profile Segment", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
            }
            
        } else {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Botanist Display Name") },
                placeholder = { Text("e.g. Carl Linnaeus") },
                modifier = Modifier.fillMaxWidth().testTag("register_name_input"),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Botanic Username Segment") },
                placeholder = { Text("e.g. carl_bot") },
                modifier = Modifier.fillMaxWidth().testTag("register_username_input"),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = passcode,
                onValueChange = { passcode = it },
                label = { Text("Profile Pin Passcode") },
                placeholder = { Text("e.g. 5678") },
                modifier = Modifier.fillMaxWidth().testTag("register_passcode_input"),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            )
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Select Avatar Theme Color",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                avatarColors.forEach { colorVal ->
                    val isChecked = selectedAvatarColor == colorVal
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(colorVal))
                            .clickable { selectedAvatarColor = colorVal }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isChecked) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Select Initial Growth Pattern Focus",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )
            
            val focuses = listOf("S-Curve Sigmoid", "Fibonacci Spiral", "Fractal Branching")
            focuses.forEach { focus ->
                val isSelectedFocus = selectedGrowthPref == focus
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedGrowthPref = focus }
                        .shadowAmbient(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelectedFocus) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                         else MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = isSelectedFocus, onClick = { selectedGrowthPref = focus })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(focus, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (name.isEmpty() || username.isEmpty() || passcode.isEmpty()) {
                        Toast.makeText(context, "All segment parameters are required.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.registerNewProfile(name, username, passcode, selectedAvatarColor, selectedGrowthPref,
                        onSuccess = {
                            Toast.makeText(context, "Registration complete! Logging you in...", Toast.LENGTH_SHORT).show()
                            viewModel.loginToProfile(username, passcode, {}, {})
                        },
                        onFailure = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).testTag("register_submit_button"),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Sow Profile Seeds", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = { isRegisterMode = false }) {
                Text("Already registered? Open vault", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun FriendsScreen(viewModel: SproutViewModel) {
    val friends by viewModel.friends.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    
    var activeTab by remember { mutableStateOf("Companions") } // "Companions", "AddFriend"
    
    var visitGardenFriendName by remember { mutableStateOf<String?>(null) }
    var visitGardenFriendPlant by remember { mutableStateOf<String?>(null) }
    
    // Register friend form fields
    var newNickname by remember { mutableStateOf("") }
    var newHandle by remember { mutableStateOf("") }
    var newFavPlant by remember { mutableStateOf("Valkyrie Pitcher") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    text = "Companion Sanctuaries",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Connect, transfer seed bundles, and visit neighboring botanist yards.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Companions", "Add Botanist Friend").forEach { tab ->
                val isSelected = (tab == "Companions" && activeTab == "Companions") || (tab != "Companions" && activeTab != "Companions")
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { activeTab = if (tab == "Companions") "Companions" else "AddFriend" }
                        .padding(vertical = 10.dp)
                        .testTag("friend_tab_$tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeTab == "Companions") {
            if (friends.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.PeopleOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No neighboring sanctuaries found. Sow companionship in the register tab!", textAlign = TextAlign.Center)
                }
            } else {
                Text(
                    text = "Active Neighbors (${friends.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                friends.forEach { fr ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 6.dp)
                            .shadowAmbient(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(fr.name.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(fr.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                        Text("@${fr.username}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    }
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        "LVL ${fr.level}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("FAVORITE SPECIMEN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
                                    Text(fr.favoritePlantName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("ECO SEEDS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
                                    Text("${fr.seedBalance} 🪙", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.sendSeedsToFriend(fr.username) { success ->
                                            if (success) {
                                                Toast.makeText(context, "Sent 50 Seeds package successfully! New balance: ${profile?.seedBalance ?: 0}", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Insufficient seeds. Water more plants to accumulate balance!", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Text("Gift 50 Seeds 🪙", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        visitGardenFriendName = fr.name
                                        visitGardenFriendPlant = fr.favoritePlantName
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text("Visit Sanctuary 🏡", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Form New Botanic Compact",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = newNickname,
                    onValueChange = { newNickname = it },
                    label = { Text("Friend Nickname") },
                    placeholder = { Text("e.g. Marie Curie") },
                    modifier = Modifier.fillMaxWidth().testTag("add_friend_name"),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = newHandle,
                    onValueChange = { newHandle = it },
                    label = { Text("Friend Botanic username ID") },
                    placeholder = { Text("e.g. curie_flora") },
                    modifier = Modifier.fillMaxWidth().testTag("add_friend_username"),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = newFavPlant,
                    onValueChange = { newFavPlant = it },
                    label = { Text("Favorite Specimen") },
                    placeholder = { Text("e.g. Midnight Iris") },
                    modifier = Modifier.fillMaxWidth().testTag("add_friend_favorite"),
                    shape = RoundedCornerShape(16.dp)
                )

                Button(
                    onClick = {
                        if (newNickname.isEmpty() || newHandle.isEmpty() || newFavPlant.isEmpty()) {
                            Toast.makeText(context, "Please complete all registration variables.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.addFriend(newHandle, newNickname, newFavPlant) { success ->
                            if (success) {
                                Toast.makeText(context, "$newNickname added as neighboring companion!", Toast.LENGTH_LONG).show()
                                newNickname = ""
                                newHandle = ""
                                activeTab = "Companions"
                            } else {
                                Toast.makeText(context, "Username already in files.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("add_friend_submit"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Register Garden Companion", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(30.dp))
    }

    visitGardenFriendName?.let { name ->
        val favoritePlant = visitGardenFriendPlant ?: "Botanical Sprout"
        AlertDialog(
            onDismissRequest = {
                visitGardenFriendName = null
                visitGardenFriendPlant = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SproutAdaptiveLogo()
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Sanctuary: $name")
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Viewing neighbor's virtual sanctuary! This botanist centers their ecological pattern around the rare: $favoritePlant.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                    ) {
                        val w = size.width
                        val h = size.height
                        
                        val stemPath = Path().apply {
                            moveTo(w / 2f, h)
                            cubicTo(w / 2f - 30f, h * 0.6f, w / 2f + 40f, h * 0.3f, w / 2f, h * 0.15f)
                        }
                        drawPath(stemPath, color = Color(0xFF2E5A36), style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
                        
                        drawCircle(color = Color(0xFFE899A5), radius = 10.dp.toPx(), center = Offset(w / 2f, h * 0.15f))
                        drawCircle(color = Color(0xFFFFD54F), radius = 6.dp.toPx(), center = Offset(w / 2f - 24f, h * 0.5f))
                        drawCircle(color = Color(0xFF00F0FF), radius = 6.dp.toPx(), center = Offset(w / 2f + 20f, h * 0.35f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Eco-vibe factor: Pure tranquility.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        visitGardenFriendName = null
                        visitGardenFriendPlant = null
                    }
                ) {
                    Text("Return to my sanctuary", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

sealed class CareGuideLoadState {
    object Initial : CareGuideLoadState()
    object Loading : CareGuideLoadState()
    data class Success(
        val careInstructions: String,
        val lightingNeeds: String,
        val toxicityWarnings: String
    ) : CareGuideLoadState()
    data class Error(val message: String) : CareGuideLoadState()
}

@Composable
fun CareGuidesScreen(viewModel: SproutViewModel) {
    val plants by viewModel.plants.collectAsState()
    val context = LocalContext.current
    
    var searchQuery by remember { mutableStateOf("") }
    val discoveredPlants = remember(plants) {
        plants.filter { it.isCollected }
    }
    
    val filteredPlants = remember(discoveredPlants, searchQuery) {
        discoveredPlants.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.scientificName.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }
    
    val careGuidesCache = remember { mutableStateMapOf<String, CareGuideLoadState>() }
    val customQACache = remember { mutableStateMapOf<String, List<Pair<String, String>>>() }
    
    var selectedPlantId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Custom Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Flora Almanac",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "AI botany intelligence for unlocked houseplants.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Stats card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = "Almanac stats",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    val total = plants.size
                    val discovered = discoveredPlants.size
                    Text(
                        text = "Unlocked: $discovered / $total Species",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Guides are accessible only for fully discovered green plants.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Search panel
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .testTag("care_guide_search_input"),
            placeholder = { Text("Search discovered care guides...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.outline)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (filteredPlants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Empty guides",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (discoveredPlants.isEmpty()) "No plant species discovered yet!" else "No care guides matched search.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (discoveredPlants.isEmpty()) "Germinate and collect seeds in the Nursery to build your private almanac." else "Check typo, or try searching another plant category.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPlants, key = { it.id }) { plant ->
                    val isExpanded = selectedPlantId == plant.id
                    
                    CareGuideItemCard(
                        plant = plant,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            selectedPlantId = if (isExpanded) null else plant.id
                            if (!isExpanded && !careGuidesCache.containsKey(plant.id)) {
                                careGuidesCache[plant.id] = CareGuideLoadState.Loading
                                coroutineScope.launch {
                                    val res = GeminiService.getCareGuide(plant.name, plant.scientificName)
                                    when (res) {
                                        is CareGuideResult.Success -> {
                                            careGuidesCache[plant.id] = CareGuideLoadState.Success(
                                                careInstructions = res.careInstructions,
                                                lightingNeeds = res.lightingNeeds,
                                                toxicityWarnings = res.toxicityWarnings
                                            )
                                            viewModel.updatePlant(plant.copy(careInstructions = res.careInstructions))
                                        }
                                        is CareGuideResult.Error -> {
                                            careGuidesCache[plant.id] = CareGuideLoadState.Error(res.message)
                                        }
                                    }
                                }
                            }
                        },
                        loadState = careGuidesCache[plant.id] ?: CareGuideLoadState.Initial,
                        qaList = customQACache[plant.id] ?: emptyList(),
                        onAskQuestion = { question ->
                            val currentList = customQACache[plant.id] ?: emptyList()
                            customQACache[plant.id] = currentList + (question to "Consulting our botanical knowledge database...")
                            coroutineScope.launch {
                                val ansRes = GeminiService.askCustomQuestion(plant.name, plant.scientificName, question)
                                val answerTxt = when (ansRes) {
                                    is CustomQuestionResult.Success -> ansRes.answer
                                    is CustomQuestionResult.Error -> "Unable to fetch botanist advice: ${ansRes.message}"
                                }
                                customQACache[plant.id] = currentList + (question to answerTxt)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CareGuideItemCard(
    plant: Plant,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    loadState: CareGuideLoadState,
    qaList: List<Pair<String, String>>,
    onAskQuestion: (String) -> Unit
) {
    val rarityColor = when (plant.rarity.lowercase()) {
        "legendary" -> Color(0xFFFFD54F)
        "epic" -> Color(0xFFBA68C8)
        "rare" -> Color(0xFF4FC3F7)
        else -> Color(0xFF81C784)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("care_guide_card_${plant.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.20f)
            }
        ),
        border = BorderStroke(
            1.dp,
            if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = com.example.data.PlantUtils.getPlantImageUrl(plant, "Tropical"),
                    contentDescription = plant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, rarityColor, CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plant.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = plant.scientificName,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = rarityColor.copy(alpha = 0.15f),
                        ) {
                            Text(
                                text = plant.rarity,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = rarityColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        ) {
                            Text(
                                text = plant.category,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand/Collapse details",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    when (loadState) {
                        is CareGuideLoadState.Initial -> {
                            // Waiting
                        }
                        is CareGuideLoadState.Loading -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(36.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 3.5.dp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Consulting botanist Gemini AI...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        is CareGuideLoadState.Success -> {
                            CareGuideContentBody(
                                plant = plant,
                                careInstructions = loadState.careInstructions,
                                lightingNeeds = loadState.lightingNeeds,
                                toxicityWarnings = loadState.toxicityWarnings,
                                qaList = qaList,
                                onAskQuestion = onAskQuestion,
                                isFallback = false
                            )
                        }
                        is CareGuideLoadState.Error -> {
                            val fallbackCare = "Watering level requirement is: ${plant.waterLevel}. Recommended soil: ${plant.soil}. Originating region is: ${plant.origin}. Description: ${plant.about}"
                            val fallbackLighting = "Best lighting is ${plant.light}. Avoid intense midday radiation if grown outside."
                            val fallbackToxicity = "Standard species classification. For safety, keep out of reach of curious family pets and dogs."

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Error notification",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Offline datasheet (Add GEMINI_API_KEY to secrets for live botanist advice)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                CareGuideContentBody(
                                    plant = plant,
                                    careInstructions = fallbackCare,
                                    lightingNeeds = fallbackLighting,
                                    toxicityWarnings = fallbackToxicity,
                                    qaList = emptyList(),
                                    onAskQuestion = {},
                                    isFallback = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CareGuideContentBody(
    plant: Plant,
    careInstructions: String,
    lightingNeeds: String,
    toxicityWarnings: String,
    qaList: List<Pair<String, String>>,
    onAskQuestion: (String) -> Unit,
    isFallback: Boolean
) {
    var userQuestion by remember { mutableStateOf("") }

    val nextWateringInfo = remember(plant, careInstructions) {
        com.example.data.PlantUtils.getNextWateringInfo(plant, careInstructions)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Suggested Watering Utility Card
        Text(
            text = "📅 NEXT SUGGESTED WATERING",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Water calendar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = nextWateringInfo.dateString,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val remainingText = if (nextWateringInfo.daysRemaining == 0) {
                            "💧 Watering is due today!"
                        } else {
                            "⏳ ${nextWateringInfo.daysRemaining} days remaining till next hydration"
                        }
                        Text(
                            text = remainingText,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (nextWateringInfo.daysRemaining == 0) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${nextWateringInfo.intervalDaysUsed}d interval",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = nextWateringInfo.calculationReasoning,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }

        Text(
            text = "💡 LIGHTING PROFILE",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = lightingNeeds,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "📋 CARE SHEET DIRECTIVES",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = careInstructions,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "⚠️ ECO & PET SAFETY",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        val isPetFriendly = !toxicityWarnings.contains("toxic", ignoreCase = true) && !toxicityWarnings.contains("poison", ignoreCase = true)
        val safetyThemeColor = if (isPetFriendly) Color(0xFF4CAF50) else Color(0xFFF44336)
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(containerColor = safetyThemeColor.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, safetyThemeColor.copy(alpha = 0.25f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isPetFriendly) Icons.Default.Pets else Icons.Default.Warning,
                    contentDescription = "Safety level icon",
                    tint = safetyThemeColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = toxicityWarnings,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (!isFallback) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "🌿 VIRTUAL BOTANIST DESK",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (qaList.isEmpty()) {
                Text(
                    text = "No custom questions proposed yet. Inquire about symptoms, yellowing foliage, or humidity needs.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    qaList.forEach { qa ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = qa.first,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        val isLoadingAns = qa.second == "Consulting our botanical knowledge database..."
                        Box(
                            modifier = Modifier
                                .align(Alignment.Start)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                                .background(
                                    if (isLoadingAns) {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                                .padding(10.dp)
                        ) {
                            Column {
                                if (isLoadingAns) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(12.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 1.5.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = qa.second,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontStyle = FontStyle.Italic,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                } else {
                                    Text(
                                        text = qa.second,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userQuestion,
                    onValueChange = { userQuestion = it },
                    placeholder = { Text("Ask anything about this plant...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("botanist_question_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (userQuestion.isNotBlank()) {
                            onAskQuestion(userQuestion)
                            userQuestion = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .testTag("consult_botanist_button"),
                    enabled = userQuestion.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send question",
                        tint = if (userQuestion.isNotBlank()) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

