package com.mobicom.s18.toledo.aaronace.sidequest.screens

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mobicom.s18.toledo.aaronace.sidequest.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration.*
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobicom.s18.toledo.aaronace.sidequest.viewmodels.MapViewModel

private lateinit var fusedLocationClient : FusedLocationProviderClient

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OpenMaps(
    modifier: Modifier = Modifier,
    zoom: Double = 20.0,
    stringLocation: String? = null,
    onMapTap: (GeoPoint) -> Unit,
    onDismissQuestPopup: () -> Unit
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

    LaunchedEffect(stringLocation) {
        if (!stringLocation.isNullOrBlank()){
            val geocoder = Geocoder(context, java.util.Locale.getDefault())
            val results = withContext(Dispatchers.IO){
                geocoder.getFromLocationName(stringLocation, 1)
            }
            if (!results.isNullOrEmpty() && mapView == null){
                val loc = results[0]
                val point = GeoPoint(loc.latitude, loc.longitude)
                mapView?.controller?.setCenter(point)
                mapView?.overlays?.clear()

                val marker = Marker(mapView).apply {
                    position = point
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = stringLocation
                    icon = customMarkerDesign
                }

                Log.d("MapChecker", "Current Point: " + point.latitude.toString() + " : " + point.longitude.toString())
                mapView?.overlays?.add(marker)
                mapView?.invalidate()

            } else {
                Toast.makeText(
                    context,
                    "Location Not Found!",
                    Toast.LENGTH_SHORT
                )
            }
        } else {
            mapView?.controller?.setCenter(GeoPoint(locationVar.latitude, locationVar.longitude))
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
                controller.setCenter(GeoPoint(14.56476, 120.99384))

                mapView = this

                val initmarker = Marker(mapView).apply {
                    position = GeoPoint(locationVar.latitude, locationVar.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Current Location"
                    icon = customMarkerDesign
                }

                overlays.add(initmarker)

                val mapEventsReceiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        if (p != null) {
                            overlays.removeAll { it is Marker }

                            val marker = Marker(this@apply).apply {
                                position = p
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Tapped: %.5f, %.5f".format(p.latitude, p.longitude)
                                icon = customMarkerDesign
                            }
                            overlays.add(marker)
                            invalidate()
                            onDismissQuestPopup()
                            onMapTap(p)
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        return false
                    }
                }

                overlays.add(MapEventsOverlay(mapEventsReceiver))
            }
        }
    )

}


@Composable
fun QuestNotification(
    modifier: Modifier = Modifier,
    locationName : String,
    questCount: Int = 3,
    onViewDetailsClick: () -> Unit
){
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.location_icon),
                    contentDescription = "Location Icon",
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$questCount Quests available",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFFFA500) // Orange color
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onViewDetailsClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32), // Green
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Details")
            }
        }
    }

}

@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    onShowRankUp: () -> Unit,
    viewModel: MapViewModel = viewModel()
) {
    val searchText by viewModel.searchText
    val submittedSearch by viewModel.submittedSearch
    val tappedPoint by viewModel.tappedPoint
    val showQuestNotification by viewModel.showQuestNotification
    val newQuestTitle by viewModel.newQuestTitle
    val newQuestDetails by viewModel.newQuestDetails

    Box(modifier = Modifier.fillMaxSize()) {
        OpenMaps(
            modifier = Modifier.matchParentSize(),
            stringLocation = submittedSearch,
            onMapTap = viewModel::onMapTap,
            onDismissQuestPopup = viewModel::dismissQuestPopup
        )

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, top = 32.dp),
            verticalArrangement = Arrangement.Top
        ) {
            TextField(
                value = searchText,
                onValueChange = viewModel::updateSearchText,
                label = { Text("Search Location") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedLabelColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp)
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, Color.Black, RoundedCornerShape(50)),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.submitSearch() }
                )
            )
        }

        if(showQuestNotification){
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -(100).dp)
            ) {
                QuestNotification(
                    locationName = "De La Salle University",
                    onViewDetailsClick = {},
                    modifier = Modifier
                )
            }
        }


        tappedPoint?.let { point ->
            AnimatedVisibility(
                visible = tappedPoint != null,
                enter = slideInVertically (
                    initialOffsetY = { fullHeight -> fullHeight }
                ),
                exit = slideOutVertically (
                    targetOffsetY = { fullHeight -> fullHeight } // Slide out to bottom
                ),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Surface (
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .offset(y = -(95).dp)
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(5)),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Create New Quest", fontWeight = FontWeight.Bold, fontSize = 28.sp, modifier = Modifier.padding(bottom = 5.dp))

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Title: ", modifier = Modifier.padding(bottom = 5.dp))

                        Spacer(modifier = Modifier.height(20.dp))

                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 20.dp)
                                .border(1.dp, Color.Black, RoundedCornerShape(10)),
                            value = newQuestTitle,
                            onValueChange = viewModel::updateNewQuestTitle,
                            label = { Text("Enter Title") },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedLabelColor = Color.LightGray,
                                focusedContainerColor = Color.White
                            ),
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Details: ", modifier = Modifier.padding(bottom = 5.dp))

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 20.dp)
                                .border(1.dp, Color.Black, RoundedCornerShape(10))
                                .height(100.dp),
                            value = newQuestDetails,
                            onValueChange = viewModel::updateNewQuestDetails,
                            label = { Text("Enter Details") },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedLabelColor = Color.LightGray,
                                focusedContainerColor = Color.White
                            ),
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (viewModel.canCreateQuest()) {
                                    viewModel.resetQuestCreation()
                                    onShowRankUp()
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.End)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                Color(0xFF509A72)
                            ),
                            enabled = viewModel.canCreateQuest()
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}