package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.rankup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobicom.s18.toledo.aaronace.sidequest.R
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.profile.ProfileViewModel

@Composable
fun RankUpScreen (
    onConfirm: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()) {
    val rank = profileViewModel.userRank.value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.rank_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(top = 75.dp, start = 40.dp)
            ){
                Row {
                    Text("You are now a...",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 30.sp,
                        modifier = Modifier.padding(8.dp, top = 6.dp))
                }

                Row {
                    Text("${rank as String}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 34.sp,
                        color = Color(0xFF3A8B74),
                        modifier = Modifier.padding(8.dp))
                }
            }

            val imageRes = when (rank){
                "Roamer" -> R.drawable.static_roamer
                "Ranger" -> R.drawable.static_ranger
                "Voyager" -> R.drawable.static_voyager
                "Pilot" -> R.drawable.static_pilot
                else -> R.drawable.static_roamer
            }

            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(400.dp)
                    .offset(y = 20.dp)
            )

        }

        // Put the Button outside the Column so we can align it to the bottom
        Button(
            onClick = onConfirm,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = -(100).dp)
                .fillMaxWidth()
                .padding(start = 40.dp, end = 40.dp, bottom = 15.dp, top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3A8B74)
            )
        ) {
            Text("Confirm")
        }
    }


}