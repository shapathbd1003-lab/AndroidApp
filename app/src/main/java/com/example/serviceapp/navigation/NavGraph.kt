package com.example.serviceapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.serviceapp.ui.screens.client.*
import com.example.serviceapp.ui.screens.entry.EntryScreen
import com.example.serviceapp.ui.screens.jobs.JobDetailScreen
import com.example.serviceapp.ui.screens.login.LoginScreen
import com.example.serviceapp.ui.screens.main.MainScreen
import com.example.serviceapp.ui.screens.pending.PendingScreen
import com.example.serviceapp.ui.screens.profile.EditProfileScreen
import com.example.serviceapp.ui.screens.register.RegisterScreen
import com.example.serviceapp.ui.screens.role.RoleSelectionScreen
import com.example.serviceapp.viewmodel.ClientViewModel
import com.example.serviceapp.viewmodel.MainViewModel

@Composable
fun NavGraph(vm: MainViewModel, cvm: ClientViewModel) {

    val nav = rememberNavController()

    NavHost(nav, startDestination = Screen.RoleSelection.route) {

        // ── Role selection ────────────────────────────────────────────────────
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(vm, nav)
        }

        // ── Provider flow ─────────────────────────────────────────────────────
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

        // ── Client flow ───────────────────────────────────────────────────────
        composable(Screen.ClientEntry.route) {
            ClientEntryScreen(cvm, nav)
        }
        composable(Screen.ClientRegister.route) {
            ClientRegisterScreen(cvm, nav)
        }
        composable(Screen.ClientLogin.route) {
            ClientLoginScreen(cvm, nav)
        }
        composable(Screen.ClientDashboard.route) {
            ClientDashboardScreen(cvm, nav)
        }
        composable(Screen.ClientNewRequest.route) {
            ClientNewRequestScreen(cvm, nav)
        }
        composable(Screen.ClientRequestDetail.route) {
            val id = it.arguments?.getString("id") ?: ""
            ClientRequestDetailScreen(id, cvm, nav)
        }
    }
}
