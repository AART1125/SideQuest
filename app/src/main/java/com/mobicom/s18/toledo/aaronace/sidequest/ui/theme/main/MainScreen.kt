package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.main

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mobicom.s18.toledo.aaronace.sidequest.navigation.NavItem
import com.mobicom.s18.toledo.aaronace.sidequest.R
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.home.HomePage
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.home.HomeViewModel
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.map.MapPage
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.map.MapViewModel
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.profile.ProfilePage
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.rankup.RankUpScreen
import com.mobicom.s18.toledo.aaronace.sidequest.tracking.LocationQuestManager
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.profile.ProfileViewModel
import org.osmdroid.util.GeoPoint

var showRankUp by mutableStateOf(false)

fun triggerRankUpPopup() {
    showRankUp = true
}

fun closeRankUpPopup() {
    showRankUp = false
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    shouldNavigateToMap: Boolean = false
) {
    val context = LocalContext.current

    val navItemList = listOf(
        NavItem("Home", painterResource(R.drawable.home_inactive)),
        NavItem("Map", painterResource(R.drawable.map_inactive)),
        NavItem("Profile", painterResource(R.drawable.profile_inactive)),
    )

    var selectedIndex by remember { mutableIntStateOf(if (shouldNavigateToMap) 1 else 0) }
    var showCreateQuestSheet by remember { mutableStateOf(false) }
    var targetQuestLocation by remember { mutableStateOf<GeoPoint?>(null) }
    val mapViewModel: MapViewModel = viewModel()

    val locationQuestManager = remember { LocationQuestManager(context) }

    // Permission states
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    // Handle navigation from notification
    LaunchedEffect(shouldNavigateToMap) {
        if (shouldNavigateToMap) {
            selectedIndex = 1
        }
    }

    // Request permissions and start location tracking
    LaunchedEffect(Unit) {
        // Request location permission if not granted
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }

        // Request notification permission on Android 13+
        notificationPermissionState?.let { permissionState ->
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
    }

    // Start location tracking when permissions are granted
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            locationQuestManager.startLocationTracking()
        }
    }

    fun handleCreateQuestClick() {
        selectedIndex = 1
        showCreateQuestSheet = true
        targetQuestLocation = null
    }

    fun navigateToQuestLocation(latitude: Double, longitude: Double) {
        selectedIndex = 1
        showCreateQuestSheet = false
        targetQuestLocation = GeoPoint(latitude, longitude)
        mapViewModel.clearLastCreatedQuestLocation()
    }

    fun handleDismissCreateQuestSheet() {
        showCreateQuestSheet = false
        targetQuestLocation = null
        mapViewModel.resetQuestCreation()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = Color.White,
        floatingActionButton = {
            if (selectedIndex == 0 && !showRankUp) {
                FloatingActionButton(
                    onClick = { handleCreateQuestClick() },
                    containerColor = Color(0xFF40916C),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, "Create Quest")
                }
            }
        },
        bottomBar = {
            NavigationBar (
                modifier = Modifier.drawBehind {
                    val strokeWidthPx = (1.dp).toPx()
                    drawLine(
                        color = Color(0xFFC5C6CC),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = strokeWidthPx
                    )
                },
                containerColor = Color.White
            ) {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            if (index != 1) {
                                targetQuestLocation = null
                                showCreateQuestSheet = false
                            }
                        },
                        icon = {
                            Icon(
                                painter = navItem.icon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(text = navItem.label)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color(0xFF40916C),
                            unselectedIconColor = Color(0xFF71727A),
                            selectedTextColor = Color(0xFF40916C),
                            unselectedTextColor = Color(0xFF71727A)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            showCreateQuestSheet = showCreateQuestSheet,
            onDismissCreateQuestSheet = ::handleDismissCreateQuestSheet,
            targetQuestLocation = targetQuestLocation,
            onNavigateToQuestLocation = ::navigateToQuestLocation,
            onLogout = onLogout,
            mapViewModel = mapViewModel,
            locationQuestManager = locationQuestManager
        )
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    showCreateQuestSheet: Boolean,
    onDismissCreateQuestSheet: () -> Unit,
    targetQuestLocation: GeoPoint?,
    onNavigateToQuestLocation: (Double, Double) -> Unit,
    onLogout: () -> Unit,
    mapViewModel: MapViewModel,
    locationQuestManager: LocationQuestManager
) {


    when(selectedIndex) {
        0 -> {
            val viewModel: HomeViewModel = viewModel()
            val profileViewModel: ProfileViewModel = viewModel()
            if (showRankUp){
                RankUpScreen(
                    onConfirm = ::closeRankUpPopup,
                    profileViewModel = profileViewModel
                )
            } else{
                HomePage(
                    showRankUp = showRankUp,
                    triggerRankUpPopup = ::triggerRankUpPopup,
                    viewModel = viewModel,
                    onNavigateToQuestLocation = onNavigateToQuestLocation
                )
            }
        }
        1 -> {
            MapPage(
                showCreateQuestSheet = showCreateQuestSheet,
                onDismissCreateQuest = onDismissCreateQuestSheet,
                targetQuestLocation = targetQuestLocation,
                viewModel = mapViewModel
            )
        }
        2 -> ProfilePage(onLogout = onLogout)
    }
}