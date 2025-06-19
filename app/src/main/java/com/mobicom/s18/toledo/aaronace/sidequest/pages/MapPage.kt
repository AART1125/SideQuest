package com.mobicom.s18.toledo.aaronace.sidequest.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.mobicom.s18.toledo.aaronace.sidequest.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.config.Configuration.*
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

private lateinit var fusedLocationClient : FusedLocationProviderClient

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OpenMaps(
    modifier: Modifier = Modifier,
    zoom: Double = 18.0,
    stringLocation: String? = null,
    onMapTap: (GeoPoint) -> Unit
) {

    val permsState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationVar by remember { mutableStateOf(Location("manual").apply {
        latitude = 14.5585560991
        longitude = 120.989571042
    }) }

    var mapView: MapView? by remember { mutableStateOf(null) }

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

                mapView = this

                val initmarker = Marker(mapView).apply {
                    position = GeoPoint(locationVar.latitude, locationVar.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Current Location"
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
                            }
                            overlays.add(marker)
                            invalidate()
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


@Preview
@Composable
fun MapPage(modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf("") }
    var submittedSearch by remember { mutableStateOf<String?>(null) }
    var tappedPoint by remember { mutableStateOf<GeoPoint?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OpenMaps(
            modifier = Modifier.matchParentSize(),
            stringLocation = submittedSearch,
            onMapTap = { tappedPoint=it }
        )

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, top = 32.dp),
            verticalArrangement = Arrangement.Top
        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search Location") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedLabelColor = Color.LightGray,
                    focusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp)
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, Color.Black, RoundedCornerShape(50)),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    submittedSearch = searchText
                }
                )
            )
        }

        tappedPoint?.let { point ->
            var titleInput = ""
            var detailInput = ""

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

                        Spacer(modifier = Modifier.height(10.dp))

                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 20.dp)
                                .border(1.dp, Color.Black, RoundedCornerShape(10)),
                            value = titleInput,
                            onValueChange = { titleInput = it },
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
                            value = detailInput,
                            onValueChange = { detailInput = it },
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
                            onClick = { tappedPoint = null },
                            modifier = Modifier
                                .align(Alignment.End)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                Color(0xFF509A72)
                            )
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}