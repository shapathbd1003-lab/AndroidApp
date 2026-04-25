package com.example.serviceapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.serviceapp.ui.screens.client.ClientDemoScreen
import com.example.serviceapp.ui.screens.entry.EntryScreen
import com.example.serviceapp.ui.screens.jobs.JobDetailScreen
import com.example.serviceapp.ui.screens.login.LoginScreen
import com.example.serviceapp.ui.screens.main.MainScreen
import com.example.serviceapp.ui.screens.pending.PendingScreen
import com.example.serviceapp.ui.screens.profile.EditProfileScreen
import com.example.serviceapp.ui.screens.register.RegisterScreen
import com.example.serviceapp.ui.screens.role.RoleSelectionScreen
import com.example.serviceapp.viewmodel.MainViewModel

@Composable
fun NavGraph(vm: MainViewModel) {

    val nav = rememberNavController()

    NavHost(nav, startDestination = Screen.Entry.route) {

        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(vm, nav)
        }

        composable(Screen.ClientDemo.route) {
            ClientDemoScreen(nav)
        }

        composable(Screen.Entry.route) {
            EntryScreen(vm, nav)
        }

        composable(Screen.Register.route) {
            RegisterScreen(vm, nav)
        }

        composable(Screen.Login.route) {
            LoginScreen(vm, nav)
        }

        composable(Screen.Pending.route) {
            PendingScreen(vm, nav)
        }

        composable(Screen.Main.route) {
            MainScreen(vm, nav)
        }

        composable(Screen.JobDetail.route) {
            val id = it.arguments?.getString("id") ?: ""
            JobDetailScreen(id, vm, nav)
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(vm, nav)
        }
    }
}
