package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.map

import android.Manifest
import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.mobicom.s18.toledo.aaronace.sidequest.R
import com.mobicom.s18.toledo.aaronace.sidequest.data.GeocodingResult
import com.mobicom.s18.toledo.aaronace.sidequest.model.QuestModel
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.fontFamily
import com.mobicom.s18.toledo.aaronace.sidequest.utils.toDateString
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OpenMaps(
    modifier: Modifier = Modifier,
    zoom: Double = 17.0,
    selectedLocation: GeoPoint? = null,
    questLocations: List<QuestLocation> = emptyList(),
    onQuestMarkerClick: (QuestLocation) -> Unit = {},
    centerLocation: GeoPoint? = null,
    forceZoom: Boolean = false
) {
    val permsState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationVar by remember { mutableStateOf(Location("manual").apply {
        latitude = 14.56476
        longitude = 120.99384
    }) }

    var mapView: MapView? by remember { mutableStateOf(null) }

    val customMarkerDesign = ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.custom_marker,
        null
    )

    LaunchedEffect(Unit) {
        if (!permsState.status.isGranted) {
            permsState.launchPermissionRequest()
        }
    }

    LaunchedEffect(permsState.status.isGranted) {
        if (permsState.status.isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    locationVar = location
                }
            }
        }
    }

    // Center map on target location when provided with consistent zoom
    LaunchedEffect(centerLocation, forceZoom, mapView) {
        centerLocation?.let { location ->
            // Wait for map to be initialized
            while (mapView == null) {
                kotlinx.coroutines.delay(50)
            }
            mapView?.let { map ->
                map.controller.setCenter(location)
                map.controller.setZoom(17.0) // Slightly closer zoom for target locations
                map.invalidate()
            }
        }
    }

    // Update marker when selectedLocation changes (for quest creation)
    LaunchedEffect(selectedLocation, mapView) {
        selectedLocation?.let { newLocation ->
            // Wait for map to be initialized
            while (mapView == null) {
                kotlinx.coroutines.delay(50)
            }
            mapView?.let { map ->
                // Remove previous selected location marker
                map.overlays.removeAll { overlay ->
                    overlay is Marker && overlay.title == "Selected Location"
                }

                val marker = Marker(map).apply {
                    position = newLocation
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Selected Location"
                    icon = customMarkerDesign
                }

                map.overlays.add(marker)
                map.controller.setCenter(newLocation)
                map.controller.setZoom(17.0)
                map.invalidate()
            }
        }
    }

    // Update markers when quest locations change
    LaunchedEffect(questLocations, mapView) {
        // Wait for map to be initialized
        while (mapView == null) {
            kotlinx.coroutines.delay(50)
        }
        mapView?.let { map ->
            // Remove only quest markers, keep the selected location marker if it exists
            map.overlays.removeAll { overlay ->
                overlay is Marker && overlay.title?.startsWith("Quest:") == true
            }

            // Add quest markers
            questLocations.forEach { questLocation ->
                val questMarker = Marker(map).apply {
                    position = GeoPoint(questLocation.latitude, questLocation.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Quest: ${questLocation.quests.size} quest(s) available"
                    icon = customMarkerDesign

                    setOnMarkerClickListener { marker, mapView ->
                        onQuestMarkerClick(questLocation)
                        true
                    }
                }
                map.overlays.add(questMarker)
            }

            map.invalidate()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val pref = context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)

            getInstance().load(context, pref)
            getInstance().userAgentValue = context.packageName

            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setZoomRounding(true)
                setMultiTouchControls(true)

                controller.setZoom(17.0)
                minZoomLevel = 4.0
                maxZoomLevel = 21.0

                val mapCenter = centerLocation ?: selectedLocation ?: GeoPoint(locationVar.latitude, locationVar.longitude)
                controller.setCenter(mapCenter)

                if (centerLocation != null) {
                    controller.setZoom(17.0)
                } else if (selectedLocation != null) {
                    controller.setZoom(16.0)
                }

                mapView = this

                // Add selected location marker if provided
                selectedLocation?.let { location ->
                    val marker = Marker(this).apply {
                        position = location
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Selected Location"
                        icon = customMarkerDesign
                    }
                    overlays.add(marker)
                }

                // Add quest markers
                questLocations.forEach { questLocation ->
                    val questMarker = Marker(this).apply {
                        position = GeoPoint(questLocation.latitude, questLocation.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Quest: ${questLocation.quests.size} quest(s) available"
                        icon = customMarkerDesign

                        setOnMarkerClickListener { marker, mapView ->
                            onQuestMarkerClick(questLocation)
                            true
                        }
                    }
                    overlays.add(questMarker)
                }

                postInvalidate()
            }
        }
    )
}

@Composable
fun LocationSearchScreen(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery
    val searchResults by viewModel.searchResults
    val isSearching by viewModel.isSearching

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Where is your quest?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Search input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            label = { Text("Search for a location") },
            placeholder = { Text("e.g. DLSU, SM Mall of Asia") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.location),
                    contentDescription = "Search"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search results
        when {
            isSearching -> {
                // Show loading indicator
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF40916C)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Searching...",
                            color = Color(0xFF71727A),
                            fontFamily = fontFamily,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            searchResults.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(searchResults) { result ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.selectLocation(result) },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.location),
                                    contentDescription = null,
                                    tint = Color(0xFF40916C),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = result.shortName,
                                        fontFamily = fontFamily,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                    if (result.shortName != result.displayName) {
                                        Text(
                                            text = result.displayName,
                                            fontFamily = fontFamily,
                                            fontSize = 12.sp,
                                            color = Color(0xFF71727A),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            searchQuery.length > 2 && !isSearching -> {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No results found",
                        color = Color(0xFF71727A),
                        fontSize = 16.sp,
                        fontFamily = fontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun QuestCard(
    quest: QuestModel,
    onClick: () -> Unit,
    showCompletionDate: Boolean = false
) {
    ElevatedCard(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        if (quest.completed) Color(0xFF40916C) else Color(0xFFF9A620)
                    )
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = quest.title,
                    fontFamily = fontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.location),
                        contentDescription = null,
                        tint = Color(0xFF40916C),
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = quest.location,
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF71727A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = quest.details,
                    fontFamily = fontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF71727A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AvailableQuestsBottomSheet(
    questLocation: QuestLocation,
    onViewQuests: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.location),
                contentDescription = "Location Icon",
                tint = Color(0xFF40916C),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = questLocation.locationName,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${questLocation.quests.size} Quest${if (questLocation.quests.size != 1) "s" else ""} available",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFFFFA500)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // View quests button
        Button(
            onClick = onViewQuests,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF40916C)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "View Quests",
                fontFamily = fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun QuestListBottomSheet(
    questLocation: QuestLocation,
    onQuestClick: (QuestModel) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Back button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onBack)
        ) {
            Icon(
                painter = painterResource(R.drawable.back_icon),
                contentDescription = null,
                tint = Color(0xFF40916C)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Back",
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color(0xFF40916C)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // Header
        Text(
            text = "Quests at this location",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Quest list
        LazyColumn {
            items(questLocation.quests) { quest ->
                QuestCard(
                    quest = quest,
                    onClick = { onQuestClick(quest) },
                    showCompletionDate = false
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun QuestDetailsBottomSheet(
    quest: QuestModel,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight(0.5f)
    ) {
        // Back button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onBack) {
                Text("← Back", color = Color(0xFF40916C))
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.padding(8.dp))

        // Title
        Text(
            text = quest.title,
            fontFamily = fontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Location
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.location),
                contentDescription = null,
                tint = Color(0xFF40916C),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.location,
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF71727A)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Details
        Text(
            text = quest.details,
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF71727A),
            modifier = Modifier
                .padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Complete button
        if (!quest.completed) {
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF40916C)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Complete Quest",
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun QuestCreationForm(
    viewModel: MapViewModel,
    selectedLocation: GeocodingResult?,
    onCreateQuest: () -> Unit,
    onChangeLocation: () -> Unit
) {
    val newQuestTitle by viewModel.newQuestTitle
    val newQuestDetails by viewModel.newQuestDetails

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Create New Quest",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Selected location
        Text("Selected Location",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onChangeLocation() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.location),
                    contentDescription = null,
                    tint = Color(0xFF40916C),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedLocation?.shortName ?: "Select location",
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                TextButton(onClick = onChangeLocation) {
                    Text(
                        text = "Change",
                        fontFamily = fontFamily,
                        color = Color(0xFF40916C))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title input
        Text(
            text = "Title",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = newQuestTitle,
            onValueChange = viewModel::updateNewQuestTitle,
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Enter Title") },
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Details input
        Text(
            text = "Details",
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = newQuestDetails,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            onValueChange = viewModel::updateNewQuestDetails,
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Enter Details") },
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Create button
        Button(
            onClick = onCreateQuest,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF509A72)
            ),
            enabled = viewModel.canCreateQuest()
        ) {
            Text("Create Quest", modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    showCreateQuestSheet: Boolean = false,
    onDismissCreateQuest: () -> Unit = {},
    targetQuestLocation: GeoPoint? = null,
    viewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val uiState by viewModel.uiState
    val selectedLocationResult by viewModel.selectedLocationResult
    val selectedQuestLocation by viewModel.selectedQuestLocation
    val userQuests by viewModel.userQuests.collectAsState()
    val questLocations by viewModel.questLocations.collectAsState()
    val errorMessage by viewModel.errorMessage
    val successMessage by viewModel.successMessage
    val lastCreatedQuestLocation by viewModel.lastCreatedQuestLocation

    // Local state for quest details view
    var showQuestList by remember { mutableStateOf(false) }
    var selectedQuest by remember { mutableStateOf<QuestModel?>(null) }

    // Toast messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }
    }
    // Ensure quest data is loaded when MapPage is displayed (especially from notifications)
    LaunchedEffect(Unit) {

    }

    // Determine the center location for the map
    val mapCenterLocation = when {
        targetQuestLocation != null -> targetQuestLocation
        lastCreatedQuestLocation != null -> GeoPoint(
            lastCreatedQuestLocation!!.latitude,
            lastCreatedQuestLocation!!.longitude
        )
        else -> null
    }

    if (showCreateQuestSheet) {
        when (uiState) {
            LocationSelectionState.SEARCHING -> {
                // Show search interface
                LocationSearchScreen(viewModel = viewModel)
            }
            LocationSelectionState.MAP_WITH_FORM -> {
                // Show map with quest form
                Box(modifier = Modifier.fillMaxSize()) {
                    // Show map with selected location marker and quest markers
                    selectedLocationResult?.let { location ->
                        OpenMaps(
                            modifier = Modifier.matchParentSize(),
                            selectedLocation = GeoPoint(location.latitude, location.longitude),
                            questLocations = questLocations,
                            onQuestMarkerClick = viewModel::onQuestMarkerClick,
                            forceZoom = true
                        )
                    }
                }

                // Show quest creation form as bottom sheet
                ModalBottomSheet(
                    onDismissRequest = {
                        viewModel.resetQuestCreation()
                        onDismissCreateQuest()
                    },
                    sheetState = sheetState,
                ) {
                    QuestCreationForm(
                        viewModel = viewModel,
                        selectedLocation = selectedLocationResult,
                        onCreateQuest = {
                            viewModel.createQuest(
                                onSuccess = {
                                    onDismissCreateQuest()
                                }
                            )
                        },
                        onChangeLocation = { viewModel.goBackToSearch() }
                    )
                }
            }
        }
    } else {
        // Regular map view when not creating quest
        Box(modifier = Modifier.fillMaxSize()) {
            OpenMaps(
                modifier = Modifier.matchParentSize(),
                questLocations = questLocations,
                onQuestMarkerClick = viewModel::onQuestMarkerClick,
                centerLocation = mapCenterLocation,
                forceZoom = targetQuestLocation != null || lastCreatedQuestLocation != null
            )
        }

        // Clear the last created quest location after the map has been shown
        LaunchedEffect(mapCenterLocation) {
            if (lastCreatedQuestLocation != null && !showCreateQuestSheet) {
                kotlinx.coroutines.delay(1000)
                viewModel.clearLastCreatedQuestLocation()
            }
        }
    }

    // Quest details bottom sheet
    selectedQuestLocation?.let { questLocation ->
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.dismissAvailableQuests()
                showQuestList = false
                selectedQuest = null
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            when {
                selectedQuest != null -> {
                    // Show individual quest details
                    QuestDetailsBottomSheet(
                        quest = selectedQuest!!,
                        onComplete = {
                            viewModel.completeQuest(selectedQuest!!)
                            selectedQuest = null
                            showQuestList = false
                        },
                        onBack = {
                            selectedQuest = null  // Go back to quest list
                        }
                    )
                }
                showQuestList -> {
                    // Show quest list
                    QuestListBottomSheet(
                        questLocation = questLocation,
                        onQuestClick = { quest ->
                            selectedQuest = quest  // Set selected quest to show details
                        },
                        onBack = {
                            showQuestList = false  // Go back to available quests
                        }
                    )
                }
                else -> {
                    // Show available quests
                    AvailableQuestsBottomSheet(
                        questLocation = questLocation,
                        onViewQuests = { showQuestList = true },
                        onDismiss = { viewModel.dismissAvailableQuests() }
                    )
                }
            }
        }
    }
}