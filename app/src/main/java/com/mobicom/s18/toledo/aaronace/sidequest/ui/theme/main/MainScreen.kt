package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mobicom.s18.toledo.aaronace.sidequest.navigation.NavItem
import com.mobicom.s18.toledo.aaronace.sidequest.R
import com.mobicom.s18.toledo.aaronace.sidequest.data.sampleQuests
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.home.HomePage
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.map.MapPage
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.profile.ProfilePage
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.rankup.RankUpScreen

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val navItemList = listOf(
        NavItem("Home", painterResource(R.drawable.home_inactive)),
        NavItem("Map", painterResource(R.drawable.map_inactive)),
        NavItem("Profile", painterResource(R.drawable.profile_inactive)),
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = Color.White,
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
        ContentScreen(modifier = Modifier.padding(innerPadding), selectedIndex)
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int) {
    when(selectedIndex) {
        0 -> HomePage(quests = sampleQuests)
        1 -> {
            var showRankUp by remember { mutableStateOf(false) }
            if (showRankUp) {
                RankUpScreen(onConfirm = { showRankUp = false })
            } else {
                MapPage(onShowRankUp = { showRankUp = true })
            }
        }
        2 -> ProfilePage()
    }
}
