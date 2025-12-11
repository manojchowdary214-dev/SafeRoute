package com.example.saferoute.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saferoute.ViewModels.HomeViewModel
import com.example.saferoute.data.RouteEntity
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val recentJourneys by homeViewModel.recentJourneys.collectAsState()
    val username = homeViewModel.username ?: "User"

    val gradient = Brush.horizontalGradient(
        listOf(Color(0xFF6A1B9A), Color(0xFFAB47BC))
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) homeViewModel.fetchLocation()
        else homeViewModel.setLocationStatus("Location Denied")
    }

    LaunchedEffect(Unit) { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }

    val listState = rememberLazyListState()

    // Bottombar
    Scaffold(bottomBar = { HomeBottomBar(navController) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { GreetingCard(username, homeViewModel, navController, gradient) }
                item { Spacer(Modifier.height(20.dp)) }
                item { FeatureRow(navController) }
                item { Spacer(Modifier.height(16.dp)) }
                item { Text("Recent Journeys", fontSize = 20.sp) }
                item { Spacer(Modifier.height(12.dp)) }

                items(recentJourneys) { route ->
                    RecentJourneyItem(route) {
                        navController.navigate("feedback/${route.id}")
                    }
                }

                item { Spacer(Modifier.height(50.dp)) }
            }

            // Invisible Scrollbar
            CustomVerticalScrollbar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 2.dp),
                listState = listState,
                thumbColor = Color.Transparent,
                trackColor = Color.Transparent,
                minThumbHeightDp = 0.dp,
                width = 0.dp
            )
        }
    }
}

@Composable
fun CustomVerticalScrollbar(
    modifier: Modifier = Modifier,
    listState: androidx.compose.foundation.lazy.LazyListState,
    thumbColor: Color,
    trackColor: Color,
    minThumbHeightDp: androidx.compose.ui.unit.Dp = 20.dp,
    width: androidx.compose.ui.unit.Dp = 6.dp
) {
    var trackHeightPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val layoutInfo = listState.layoutInfo
    val totalItems = layoutInfo.totalItemsCount
    val visibleItems = layoutInfo.visibleItemsInfo.size

    if (totalItems == 0 || visibleItems == 0) {
        Box(
            modifier = modifier
                .width(width)
                .fillMaxHeight()
                .onGloballyPositioned { coords -> trackHeightPx = coords.size.height }
        )
        return
    }

    val firstVisibleIndex = listState.firstVisibleItemIndex
    val visibleFraction = (visibleItems.toFloat() / max(1, totalItems).toFloat()).coerceIn(0f, 1f)
    val thumbHeightPx = remember(trackHeightPx, visibleFraction) {
        if (trackHeightPx == 0) 0 else max(
            (with(density) { minThumbHeightDp.toPx() }).roundToInt(),
            (visibleFraction * trackHeightPx).roundToInt()
        )
    }

    val maxScrollIndex = max(0, totalItems - visibleItems)
    val scrollProgress = if (maxScrollIndex == 0) 0f else (firstVisibleIndex.toFloat() / maxScrollIndex.toFloat())
    val thumbOffsetPx = remember(trackHeightPx, thumbHeightPx, scrollProgress) {
        if (trackHeightPx == 0) 0 else ((trackHeightPx - thumbHeightPx) * scrollProgress).roundToInt()
    }

    Box(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .onGloballyPositioned { coords -> trackHeightPx = coords.size.height }
            .background(trackColor, shape = RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.TopCenter
    ) {
        if (thumbHeightPx > 0 && trackHeightPx > 0) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, thumbOffsetPx) }
                    .width(width)
                    .height(with(density) { thumbHeightPx.toDp() })
                    .background(thumbColor, shape = RoundedCornerShape(4.dp))
                    .pointerInput(Unit) {}
            )
        }
    }
}

@Composable
fun GreetingCard(
    username: String,
    homeViewModel: HomeViewModel,
    navController: NavController,
    gradient: Brush
) {
    // greet card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // location
                    Text(
                        text = "Location: ${homeViewModel.currentLocation ?: "Unknown"}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                    // logout
                    Text(
                        text = "Logout",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            homeViewModel.signOut()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }
                // hi username
                Text("Hi $username!", color = Color.White, fontSize = 26.sp)
                Text(
                    "Ready to travel safely today?",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun FeatureRow(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Feature Cards
        FeatureCard(
            modifier = Modifier.weight(1f),
            title = "Plan Journey",
            subtitle = "Safe Routes",
            onClick = { navController.navigate("journeyPlanner") },
            color = Color(0xFF8E24AA)
        )
        FeatureCard(
            modifier = Modifier.weight(1f),
            title = "Emergency SOS",
            subtitle = "Quick Help",
            onClick = { navController.navigate("sos") },
            color = Color(0xFFD32F2F)
        )
    }
}

@Composable
fun FeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    color: Color
) {
    Card(
        modifier = modifier.height(120.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, color = Color.White, fontSize = 16.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RecentJourneyItem(route: RouteEntity, onClick: () -> Unit) {
    val scoreColor = when {
        route.safetyScore >= 80 -> Color(0xFF4CAF50)
        route.safetyScore >= 50 -> Color(0xFFFFA000)
        else -> Color(0xFFE53935)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${route.start} → ${route.end}", style = MaterialTheme.typography.titleMedium)
                // Distance
                Text(
                    "Distance: ${route.distance} km | Duration: ${route.duration} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .background(scoreColor, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("${route.safetyScore}/100", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun HomeBottomBar(navController: NavController) {
    NavigationBar {
        // Home
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        //sos history
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("sosHistory") },
            icon = { Icon(Icons.Default.History, contentDescription = "SOS History") },
            label = { Text("SOS History") }
        )
        //feedback text
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("feedbackList") { launchSingleTop = true; restoreState = true } },
            icon = { Icon(Icons.Default.Star, contentDescription = "Feedback") },
            label = { Text("Feedback") }
        )
        // Report text
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("reports") },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Reports") },
            label = { Text("Reports") }
        )
    }
}