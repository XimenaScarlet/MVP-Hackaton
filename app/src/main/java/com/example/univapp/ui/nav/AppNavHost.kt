package com.example.univapp.ui.nav

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Auth / Login
import com.example.univapp.ui.AuthViewModel
import com.example.univapp.ui.LoginScreen

// Admin
import com.example.univapp.ui.admin.AdminDashboard
import com.example.univapp.ui.admin.AdminHorariosScreen
import com.example.univapp.ui.admin.AdminMateriasScreen
import com.example.univapp.ui.admin.AdminAlumnosScreen
import com.example.univapp.ui.admin.AdminGruposScreen
import com.example.univapp.ui.admin.AdminProfesoresScreen

// Mapas
import com.example.univapp.ui.RouteMapScreen
import com.example.univapp.ui.routesel.RoutesSelectorScreen

// Alumno
import com.example.univapp.ui.HomeScreen
import com.example.univapp.ui.GradesScreen
import com.example.univapp.ui.ProfileScreen
import com.example.univapp.ui.HealthScreen
import com.example.univapp.ui.SettingsScreen
import com.example.univapp.ui.SubjectsScreen
import com.example.univapp.ui.SubjectDetailScreen
import com.example.univapp.ui.AnnouncementsScreen
import com.example.univapp.ui.TimetableScreen

// Transportista
import com.example.univapp.transporter.TransporterScanScreen

object Routes {
    const val LOGIN          = "login"
    const val ADMIN_HOME     = "admin_home"
    const val STUDENT_HOME   = "student_home"

    // Admin
    const val ADMIN_ALUMNOS      = "admin_alumnos"
    const val ADMIN_MATERIAS     = "admin_materias"
    const val ADMIN_GRUPOS       = "admin_grupos"
    const val ADMIN_PROFESORES   = "admin_profesores"
    const val ADMIN_HORARIOS     = "admin_horarios"

    // Alumno
    const val GRADES         = "grades"
    const val PROFILE        = "profile"
    const val HEALTH         = "health"
    const val SETTINGS       = "settings"
    const val SUBJECTS       = "subjects"
    const val SUBJECT_DETAIL = "subject_detail/{id}/{term}"
    const val ANNOUNCEMENTS  = "announcements"
    const val TIMETABLE      = "timetable"

    // Rutas / Mapas
    const val SELECTOR       = "routes_selector"
    const val ROUTE_MAP      = "routeMap/{id}"

    // Transportista
    const val TRANSPORTER_SCAN = "transporter_scan/{routeId}/{busName}/{phone}"
}

@Composable
fun AppNavHost(nav: NavHostController) {
    val authVM: AuthViewModel = viewModel()
    val user    by authVM.user.collectAsState()
    val isAdmin by authVM.isAdmin.collectAsState()
    val errText by authVM.error.collectAsState()

    NavHost(navController = nav, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                errorText      = errText,
                onLogin        = { id, pass, _ ->
                    if (id.equals("transporte", true) && pass.equals("transporte", true)) {
                        val routeId = "Ramos"
                        val busName = Uri.encode("Camión 1")
                        val phone   = "5218440000000"
                        nav.navigate("transporter_scan/$routeId/$busName/$phone") {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        authVM.login(id, pass)
                    }
                },
                onForgot       = { who, done -> authVM.sendReset(who) { _, _ -> done() } },
                onDismissError = { authVM.clearError() }
            )

            LaunchedEffect(user, isAdmin) {
                if (user == null) return@LaunchedEffect
                when (isAdmin) {
                    true -> nav.navigate(Routes.ADMIN_HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                    false -> nav.navigate(Routes.STUDENT_HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                    null -> Unit
                }
            }

            if (user != null && isAdmin == null) {
                Box(Modifier.fillMaxSize()) { CircularProgressIndicator() }
            }
        }

        composable(Routes.STUDENT_HOME) {
            val rawName = remember(user) {
                val dn = user?.displayName?.trim().orEmpty()
                if (dn.isNotBlank()) dn else user?.email?.substringBefore('@').orEmpty()
            }
            HomeScreen(
                userName          = rawName,
                onGoGrades        = { nav.navigate(Routes.GRADES) },
                onGoProfile       = { nav.navigate(Routes.PROFILE) },
                onGoRoutes        = { nav.navigate(Routes.SELECTOR) },
                onGoHealth        = { nav.navigate(Routes.HEALTH) },
                onGoSettings      = { nav.navigate(Routes.SETTINGS) },
                onGoSubjects      = { nav.navigate(Routes.SUBJECTS) },
                onGoAnnouncements = { nav.navigate(Routes.ANNOUNCEMENTS) },
                onGoTimetable     = { nav.navigate(Routes.TIMETABLE) },
                onLogout          = {
                    authVM.logout()
                    nav.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.ADMIN_HOME) {
            AdminDashboard(
                onLogout = {
                    authVM.logout()
                    nav.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOpenAlumnos    = { nav.navigate(Routes.ADMIN_ALUMNOS) },
                onOpenMaterias   = { nav.navigate(Routes.ADMIN_MATERIAS) },
                onOpenGrupos     = { nav.navigate(Routes.ADMIN_GRUPOS) },
                onOpenProfesores = { nav.navigate(Routes.ADMIN_PROFESORES) },
                onOpenSettings   = { nav.navigate(Routes.SETTINGS) },
                onOpenPerfil     = { nav.navigate(Routes.PROFILE) },
                onOpenHorarios   = { nav.navigate(Routes.ADMIN_HORARIOS) }
            )
        }
        composable(Routes.ADMIN_ALUMNOS)    { AdminAlumnosScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_MATERIAS)   { AdminMateriasScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_GRUPOS)     { AdminGruposScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_PROFESORES) { AdminProfesoresScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_HORARIOS)   { AdminHorariosScreen(onBack = { nav.popBackStack() }) }

        composable(Routes.GRADES)  { GradesScreen() }
        composable(Routes.PROFILE) { ProfileScreen() }
        composable(Routes.HEALTH)  { HealthScreen() }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { nav.popBackStack() },
                onLogout = {
                    authVM.logout()
                    nav.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Lista de materias
        composable(Routes.SUBJECTS) {
            SubjectsScreen(
                onBack = { nav.popBackStack() },
                onOpenSubject = { term: Int, subjectId: Long ->
                    nav.navigate("subject_detail/$subjectId/$term")
                }
            )
        }

        // Detalle de materia (tipos correctos)
        composable(
            route = Routes.SUBJECT_DETAIL,
            arguments = listOf(
                navArgument("id")   { type = NavType.LongType },
                navArgument("term") { type = NavType.IntType  }
            )
        ) { back ->
            val subjectId: Long = back.arguments?.getLong("id") ?: 0L
            val term: Int       = back.arguments?.getInt("term") ?: 1
            SubjectDetailScreen(
                subjectId = subjectId,
                term      = term,
                onBack    = { nav.popBackStack() }
            )
        }

        composable(Routes.ANNOUNCEMENTS) { AnnouncementsScreen() }

        // Horario
        composable(Routes.TIMETABLE) {
            TimetableScreen(
                term = 1,
                onBack = { nav.popBackStack() },
                onOpenSubject = { subjectId, t ->
                    nav.navigate("subject_detail/$subjectId/$t")
                }
            )
        }

        composable(Routes.SELECTOR) {
            RoutesSelectorScreen(
                onBack         = { nav.popBackStack() },
                onOpenSaltillo = { nav.navigate("routeMap/Saltillo") },
                onOpenRamos    = { nav.navigate("routeMap/Ramos") }
            )
        }
        composable(
            route = Routes.ROUTE_MAP,
            arguments = listOf(navArgument("id") { type = NavType.StringType; defaultValue = "R5" })
        ) { back ->
            val id = back.arguments?.getString("id") ?: "R5"
            RouteMapScreen(routeId = id, onBack = { nav.popBackStack() })
        }

        composable(
            route = Routes.TRANSPORTER_SCAN,
            arguments = listOf(
                navArgument("routeId") { type = NavType.StringType },
                navArgument("busName") { type = NavType.StringType },
                navArgument("phone")   { type = NavType.StringType }
            )
        ) { back ->
            val routeId = back.arguments?.getString("routeId") ?: "Ramos"
            val busName = back.arguments?.getString("busName") ?: "Camión 1"
            val phone   = back.arguments?.getString("phone")   ?: "5218440000000"
            TransporterScanScreen(
                routeId = routeId,
                busName = busName,
                notifyPhoneNumber = phone,
                onBack = { nav.navigate(Routes.LOGIN) { popUpTo(0) } }
            )
        }
    }
}
