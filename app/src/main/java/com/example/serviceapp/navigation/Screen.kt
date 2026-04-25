package com.example.serviceapp.navigation

sealed class Screen(val route: String, val title: String) {
    object Entry      : Screen("entry", "Entry")
    object Register   : Screen("register", "Register")
    object Login      : Screen("login", "Login")
    object Main       : Screen("main", "Main")
    object JobDetail  : Screen("job_detail/{id}", "Job Detail") {
        fun createRoute(id: String) = "job_detail/$id"
    }
    object EditProfile      : Screen("edit_profile", "Edit Profile")
    object Pending          : Screen("pending", "Pending Approval")
    object RoleSelection    : Screen("role_selection", "Role Selection")
    object ClientDemo       : Screen("client_demo", "Client Demo")
}
