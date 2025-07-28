package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.map

import android.Manifest
import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
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
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.fontFamily
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OpenMaps(
    modifier: Modifier = Modifier,
    zoom: Double = 20.0
) {
    val permsState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationVar by remember { mutableStateOf(Location("manual").apply {
        latitude = 14.56476
        longitude = 120.99384
    }) }

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

    AndroidView(
        modifier = modifier,
        factory = {
            val pref = context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)

            getInstance().load(context, pref)
            getInstance().userAgentValue = context.packageName

            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setZoomRounding(true)
                setMultiTouchControls(true)
                controller.setZoom(zoom)
                controller.setCenter(GeoPoint(locationVar.latitude, locationVar.longitude))

                val initmarker = Marker(this).apply {
                    position = GeoPoint(locationVar.latitude, locationVar.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Current Location"
                    icon = customMarkerDesign
                }

                overlays.add(initmarker)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    onShowRankUp: () -> Unit,
    showCreateQuestSheet: Boolean = false,
    onDismissCreateQuest: () -> Unit,
    viewModel: MapViewModel = viewModel()
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val newQuestTitle by viewModel.newQuestTitle
    val newQuestDetails by viewModel.newQuestDetails

    val context = LocalContext.current
    val errorMessage by viewModel.errorMessage
    val successMessage by viewModel.successMessage

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

    Box(modifier = Modifier.fillMaxSize()) {
        OpenMaps(
            modifier = Modifier.matchParentSize()
        )
    }

    if (showCreateQuestSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.resetQuestCreation()
                onDismissCreateQuest()
            },
            sheetState = sheetState,
        ) {
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

                Text(text = "Title",
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp))

                // Title input
                OutlinedTextField(
                    value = newQuestTitle,
                    onValueChange = viewModel::updateNewQuestTitle,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24),
                    placeholder = { Text("Enter Title") },
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Details",
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp))

                // Details input
                OutlinedTextField(
                    value = newQuestDetails,
                    onValueChange = viewModel::updateNewQuestDetails,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(10),
                    placeholder = { Text("Enter Details") },
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Create Quest button
                Button(
                    onClick = {
                        if (viewModel.canCreateQuest()) {
                            viewModel.createQuest(
                                onSuccess = {
                                    viewModel.resetQuestCreation()
                                    onDismissCreateQuest()
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF509A72)
                    ),
                    enabled = viewModel.canCreateQuest()
                ) {
                    Text(
                        text = "Confirm",
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp)) // Bottom padding
            }
        }
    }
}