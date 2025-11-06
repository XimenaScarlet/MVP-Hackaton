package com.example.univapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.univapp.transporter.TransporterScanScreen
import com.example.univapp.ui.*
import com.example.univapp.ui.admin.*

/* ====== MANTÉN ESTE SOLO UNA VEZ EN TODO EL PROYECTO ====== */
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val GRADES = "grades"
    const val PROFILE = "profile"
    const val TIMETABLE = "timetable"
    const val ROUTES = "routes"
    const val ROUTE_MAP = "routeMap/{routeId}"
    const val HEALTH = "health"
    const val SUBJECTS = "subjects"
    const val SUBJECT_DETAIL = "subjectDetail/{term}/{subjectId}"
    const val SETTINGS = "settings"
    const val ANNOUNCEMENTS = "announcements"
    const val PSYCH_SUPPORT = "psychSupport"
    const val MEDICAL_SUPPORT = "medicalSupport"
    const val PSYCH_APPOINT = "psychSupport/appointment"
    // Admin
    const val ADMIN_ALUMNOS = "admin_alumnos"
    const val ADMIN_MATERIAS = "admin_materias"
    const val ADMIN_GRUPOS   = "admin_grupos"
    const val ADMIN_PROFESORES = "admin_profesores"
    const val ADMIN_HORARIOS   = "admin_horarios"
    // Transportista
    const val TRANSPORTER_SCAN = "transporter_scan/{routeId}/{busName}/{phone}"
}
/* =========================================================== */

@Composable
fun UnivApp() {
    val nav = rememberNavController()
    val authVM: AuthViewModel = viewModel()

    val user by authVM.user.collectAsState()
    val adminFlag by authVM.isAdmin.collectAsState()
    val start = if (user == null) Routes.LOGIN else Routes.HOME

    NavHost(navController = nav, startDestination = start) {

        /* ------------ LOGIN ------------ */
        composable(Routes.LOGIN) {
            val vm: AuthViewModel = viewModel()
            val u by vm.user.collectAsState()
            val err by vm.error.collectAsState()
            val loading by vm.loading.collectAsState()

            LaunchedEffect(u) {
                if (u != null && nav.currentDestination?.route != Routes.HOME) {
                    nav.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            LoginScreen(
                errorText = if (loading) null else err,
                onLogin = { idOrEmail, pass, _ ->
                    // Atajo para transportista
                    if (idOrEmail.equals("transporte", true) && pass.equals("transporte", true)) {
                        val routeId = "Ramos"
                        val busName = "Camión 1"
                        val phone   = "5218440000000"
                        nav.navigate("transporter_scan/$routeId/$busName/$phone") {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        vm.login(idOrEmail, pass)
                    }
                },
                onForgot = { who, after ->
                    vm.sendReset(who) { ok, _ -> if (ok) after() }
                },
                onDismissError = { vm.clearError() }
            )
        }

        /* ------------ HOME ------------ */
        composable(Routes.HOME) {
            val userName = remember(user) {
                user?.displayName?.takeIf { it.isNotBlank() }
                    ?: user?.email?.substringBefore('@')?.replaceFirstChar { it.uppercase() }
                    ?: "Usuario"
            }

            when (adminFlag) {
                null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                true -> AdminHomeScreen(
                    onGoAlumnos       = { nav.navigate(Routes.ADMIN_ALUMNOS) },
                    onGoMaterias      = { nav.navigate(Routes.ADMIN_MATERIAS) },
                    onGoGrupos        = { nav.navigate(Routes.ADMIN_GRUPOS) },
                    onGoHorarios      = { nav.navigate(Routes.ADMIN_HORARIOS) },
                    onGoProfesores    = { nav.navigate(Routes.ADMIN_PROFESORES) },
                    onGoSettings      = { nav.navigate(Routes.SETTINGS) },
                    onGoProfile       = { nav.navigate(Routes.PROFILE) },
                    onGoAnnouncements = { nav.navigate(Routes.ANNOUNCEMENTS) },
                    onLogout          = { authVM.logout() },
                    userName          = userName
                )
                false -> HomeScreen(
                    onGoGrades        = { nav.navigate(Routes.GRADES) },
                    onGoProfile       = { nav.navigate(Routes.PROFILE) },
                    onGoRoutes        = { nav.navigate(Routes.ROUTES) },
                    onGoHealth        = { nav.navigate(Routes.HEALTH) },
                    onGoSettings      = { nav.navigate(Routes.SETTINGS) },
                    onGoSubjects      = { nav.navigate(Routes.SUBJECTS) },
                    onGoAnnouncements = { nav.navigate(Routes.ANNOUNCEMENTS) },
                    onGoTimetable     = { nav.navigate(Routes.TIMETABLE) },
                    onLogout          = { authVM.logout() },
                    userName          = userName
                )
            }
        }

        /* ------------ Admin ------------ */
        composable(Routes.ADMIN_ALUMNOS)    { AdminAlumnosScreen   (onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_MATERIAS)   { AdminMateriasScreen  (onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_GRUPOS)     { AdminGruposScreen    (onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_PROFESORES) { AdminProfesoresScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_HORARIOS)   { AdminHorariosScreen  (onBack = { nav.popBackStack() }) }

        /* ------------ Alumno ------------ */
        composable(Routes.GRADES)    { GradesScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.PROFILE)   { ProfileScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.TIMETABLE) { TimetableScreen(onBack = { nav.popBackStack() }) }

        /* ------------ Configuración (logout OK) ------------ */
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { nav.popBackStack() },
                onLogout = {
                    authVM.logout()
                    nav.navigate(Routes.LOGIN) { popUpTo(0) } // limpia back stack
                }
            )
        }

        /* ------------ Avisos ------------ */
        composable(Routes.ANNOUNCEMENTS) { AnnouncementsScreen() }

        /* ------------ Rutas (mapa) ------------ */
        composable(Routes.ROUTES) {
            CampusRoutesScreen(
                onTapSaltillo = { nav.navigate("routeMap/SALTILLO") },
                onTapRamos    = { nav.navigate("routeMap/RAMOS") }
            )
        }
        composable(Routes.ROUTE_MAP) { back ->
            val routeId = back.arguments?.getString("routeId") ?: "R5"
            RouteMapScreen(routeId = routeId, onBack = { nav.popBackStack() })
        }

        /* ------------ Transportista ------------ */
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
