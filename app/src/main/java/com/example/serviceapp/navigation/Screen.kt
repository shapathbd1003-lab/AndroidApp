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
    object ClientEntry      : Screen("client_entry", "Client Entry")
    object ClientRegister   : Screen("client_register", "Client Register")
    object ClientLogin      : Screen("client_login", "Client Login")
    object ClientDashboard  : Screen("client_dashboard", "Client Dashboard")
    object ClientNewRequest : Screen("client_new_request", "New Request")
    object ClientProfile    : Screen("client_profile", "Client Profile")
    object ClientRequestDetail : Screen("client_request_detail/{id}", "Request Detail") {
        fun createRoute(id: String) = "client_request_detail/$id"
    }
}
