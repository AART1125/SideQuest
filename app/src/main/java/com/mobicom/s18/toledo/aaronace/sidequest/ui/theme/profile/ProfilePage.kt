package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.profile

import android.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobicom.s18.toledo.aaronace.sidequest.R
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.fontFamily

@Preview
@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val username by viewModel.username
    val userRank by viewModel.userRank
    val completedQuests by viewModel.completedQuests
    var showLogoutDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.profile_bg),
            contentDescription = "Background Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        Image(
            painter = painterResource(R.drawable.profile_character),
            contentDescription = "Sample Character",
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.Center)
                .offset(y = -(90).dp)
        )

        TextButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = 20.dp),
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            onClick = { showLogoutDialog = true }
        ) {

            Image(
                painter = painterResource(R.drawable.exit_img),
                contentDescription = null,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text("Logout")
        }
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Confirm Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF509A72),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showLogoutDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text("No")
                    }
                }
            )
        }


        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(10))
        ) {
            Column(
                modifier = Modifier
            ) {
                Text("$username", // change to "username"
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp),
                    fontFamily = fontFamily,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF52B788)
                )
                Spacer(
                    modifier = Modifier
                    .height(16.dp))
                Text("$userRank", // change to "userRank"
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontFamily = fontFamily,
                    fontSize = 20.sp)
                Spacer(
                    modifier = Modifier
                        .height(8.dp))
                HorizontalDivider(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally))
                Spacer(
                    modifier = Modifier
                        .height(16.dp))
                Text("$completedQuests", // change to "$completedQuests"
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontFamily = fontFamily,
                    fontSize = 100.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF52B788))
                Text("Quests Completed",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontFamily = fontFamily,
                    fontSize = 20.sp)
                Spacer(
                    modifier = Modifier
                        .height(160.dp)
                )
            }
        }

    }
}

