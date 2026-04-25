package com.example.serviceapp.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.serviceapp.ui.screens.dashboard.DashboardScreen
import com.example.serviceapp.ui.screens.jobs.JobListScreen
import com.example.serviceapp.ui.screens.profile.ProfileScreen
import com.example.serviceapp.ui.theme.AppColors
import com.example.serviceapp.utils.AppStrings
import com.example.serviceapp.viewmodel.MainViewModel

private data class Tab(val label: @Composable () -> String, val icon: ImageVector)

@Composable
fun MainScreen(vm: MainViewModel, nav: NavController) {

    var selected by remember { mutableStateOf(0) }
    val pendingCount = vm.jobs.count { it.status == "pending" }

    val tabs = listOf(
        Tab({ AppStrings.dashboard }, Icons.Default.Home),
        Tab({ AppStrings.availableJobs }, Icons.Default.List),
        Tab({ AppStrings.profile }, Icons.Default.Person),
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = AppColors.Primary,
                modifier = Modifier.navigationBarsPadding()
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selected == index,
                        onClick = { selected = index },
                        icon = {
                            if (index == 1 && pendingCount > 0) {
                                BadgedBox(badge = {
                                    Badge(containerColor = AppColors.Accent) {
                                        Text("$pendingCount", color = Color.White)
                                    }
                                }) {
                                    Icon(tab.icon, contentDescription = tab.label())
                                }
                            } else {
                                Icon(tab.icon, contentDescription = tab.label())
                            }
                        },
                        label = { Text(tab.label()) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            indicatorColor = AppColors.PrimaryVar,
                            unselectedIconColor = Color.White.copy(alpha = 0.55f),
                            unselectedTextColor = Color.White.copy(alpha = 0.55f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (selected) {
                0 -> DashboardScreen(vm)
                1 -> JobListScreen(vm, nav)
                2 -> ProfileScreen(vm, nav)
            }
        }
    }
}
