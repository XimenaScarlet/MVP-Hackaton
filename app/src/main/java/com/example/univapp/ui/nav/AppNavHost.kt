@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui.nav

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.univapp.location.LocationHelper
import com.example.univapp.navigation.Routes
import com.example.univapp.ui.*
import com.example.univapp.ui.admin.*
import com.example.univapp.ui.routesel.RoutesSelectorScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    authVM: AuthViewModel = viewModel(),
    adminGruposViewModel: AdminGruposViewModel = viewModel()
) {
    val nav = rememberNavController()
    val scope = rememberCoroutineScope()
    val user by authVM.user.collectAsState()
    val offlineSession by authVM.offlineSession.collectAsState()
    val isAdmin by authVM.isAdmin.collectAsState()
    val isLoggedIn = user != null || offlineSession != null
    val authError by authVM.error.collectAsState()
    val context = LocalContext.current

    // --- SHARED VIEWMODELS ---
    val locationHelper = remember { LocationHelper(context) }
    val sosViewModel: SOSViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SOSViewModel(locationHelper) as T
        }
    })
    
    // SINCRONIZAR SESIÓN CON SOS
    LaunchedEffect(user, offlineSession) {
        sosViewModel.setOfflineSession(offlineSession)
    }

    val adminSosViewModel: AdminSosViewModel = viewModel()
    val proceduresVM: StudentProceduresViewModel = viewModel()
    val healthVM: HealthViewModel = viewModel()
    val medicalVm: MedicalAppointmentViewModel = viewModel()
    val settingsVM: SettingsViewModel = viewModel()
    // -----------------------

    NavHost(navController = nav, startDestination = Routes.LOGIN) {

        /* ------------ Auth ------------ */
        composable(Routes.LOGIN) {
            LoginScreen(
                vm = authVM,
                errorText = authError,
                onLogin = { id, pass, _ -> 
                    authVM.login(id, pass) { error -> }
                },
                onDismissError = { authVM.clearError() }
            )

            LaunchedEffect(isLoggedIn, isAdmin) {
                if (!isLoggedIn) return@LaunchedEffect
                when (isAdmin) {
                    true -> nav.navigate(Routes.ADMIN_HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                    false -> nav.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                    null -> Unit
                }
            }

            if (isLoggedIn && isAdmin == null) {
                Box(Modifier.fillMaxSize()) { CircularProgressIndicator() }
            }
        }

        composable(Routes.HOME) {
            val displayName = when {
                user?.email != null -> user!!.email!!.substringBefore("@")
                offlineSession?.email != null -> offlineSession!!.email.substringBefore("@")
                else -> "Alumno"
            }
            HomeScreen(
                userName          = displayName,
                onGoProfile       = { nav.navigate(Routes.PROFILE) },
                onGoStudentServices = { nav.navigate(Routes.STUDENT_SERVICES) },
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
                },
                settingsVm = settingsVM
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
                onOpenAlumnos = { nav.navigate(Routes.ADMIN_ALUMNOS) },
                onOpenMaterias = { nav.navigate(Routes.ADMIN_MATERIAS) },
                onOpenGrupos = { nav.navigate(Routes.ADMIN_GRUPOS) },
                onOpenHorarios = { nav.navigate(Routes.ADMIN_HORARIOS) },
                onOpenProfesores = { nav.navigate(Routes.ADMIN_PROFESORES) },
                onOpenActivity = { nav.navigate(Routes.ADMIN_ACTIVITY) },
                onOpenAnnouncements = { nav.navigate(Routes.ADMIN_ANNOUNCEMENTS) },
                onOpenSosMap = { nav.navigate(Routes.ADMIN_SOS_MAP) }
            )
        }
        
        composable(Routes.ADMIN_SOS_MAP) {
            AdminSosMapScreen(
                viewModel = adminSosViewModel,
                onBack = { nav.popBackStack() }
            )
        }

        composable(Routes.ADMIN_ANNOUNCEMENTS) { AdminAnnouncementsScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_ALUMNOS) {
            AdminAlumnosScreen(
                onBack = { nav.popBackStack() },
                onEdit = { id -> nav.navigate("admin_edit_alumno/$id") },
                onAddManually = { nav.navigate(Routes.ADMIN_ADD_ALUMNO) },
                onImportExcel = { nav.navigate(Routes.ADMIN_IMPORT_ALUMNOS) }
            )
        }
        composable(Routes.ADMIN_MATERIAS) {
            val vm: AdminMateriasViewModel = viewModel()
            val uiState by vm.uiState.collectAsState()
            AdminMateriasScreen(
                onBack = { nav.popBackStack() },
                onOpenMateria = { id -> nav.navigate("admin_materia_detail/$id") },
                onAddManually = {
                    val c = uiState.selectedCarrera?.id ?: return@AdminMateriasScreen
                    val g = uiState.selectedGrupo?.id ?: return@AdminMateriasScreen
                    nav.navigate("admin_add_materia/$c/$g")
                },
                onImportExcel = { nav.navigate(Routes.ADMIN_IMPORT_MATERIAS) },
                vm = vm
            )
        }
        composable(Routes.ADMIN_GRUPOS) {
            val uiState by adminGruposViewModel.uiState.collectAsState()
            AdminGruposScreen(
                onBack = { nav.popBackStack() },
                onGroupClick = { id -> nav.navigate("admin_group_detail/$id") },
                onAddManually = {
                    val c = uiState.selectedCarrera?.id ?: return@AdminGruposScreen
                    nav.navigate("admin_add_group_details/$c")
                },
                onImportExcel = { },
                uiState = uiState,
                onCarreraSelected = { adminGruposViewModel.onCarreraSelected(it) }
            )
        }
        composable(Routes.ADMIN_PROFESORES) { backStackEntry ->
            val vm: AdminProfesoresViewModel = viewModel(backStackEntry)
            val uiState by vm.uiState.collectAsState()
            AdminProfesoresScreen(
                onBack = { nav.popBackStack() },
                onAddManually = {
                    val c = uiState.selectedCarrera?.id ?: return@AdminProfesoresScreen
                    nav.navigate("admin_add_profesor/$c")
                },
                onImportExcel = { },
                vm = vm
            )
        }
        composable(Routes.ADMIN_HORARIOS) {
            val vm: AdminHorariosViewModel = viewModel()
            val uiState by vm.uiState.collectAsState()
            AdminHorariosScreen(
                onBack = { nav.popBackStack() },
                onAddManually = {
                    val c = uiState.selectedCarrera?.id ?: return@AdminHorariosScreen
                    val g = uiState.selectedGrupo?.id ?: return@AdminHorariosScreen
                    nav.navigate("admin_add_horario/$c/$g")
                },
                onImportExcel = { nav.navigate(Routes.ADMIN_IMPORT_HORARIOS) },
                vm = vm
            )
        }
        composable(Routes.ADMIN_ACTIVITY) { AdminActivityScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_IMPORT_ALUMNOS) { ImportAlumnosScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_ADD_ALUMNO) { AdminAddAlumnoScreen(onBack = { nav.popBackStack() }) }

        composable(
            route = Routes.ADMIN_ADD_PROFESOR,
            arguments = listOf(navArgument("carreraId") { type = NavType.StringType })
        ) { back ->
            val carreraId = back.arguments?.getString("carreraId") ?: return@composable
            AddProfesorScreen(carreraId = carreraId, onBack = { nav.popBackStack() })
        }

        /* ------------ Alumno ------------ */
        composable(Routes.PROFILE) { ProfileScreen(onBack = { nav.popBackStack() }, settingsVm = settingsVM) }
        composable(Routes.STUDENT_SERVICES) {
            StudentServicesScreen(
                onBack = { nav.popBackStack() },
                onOpenInfo = { nav.navigate(Routes.SCHOOL_INFO) },
                onOpenProcedures = { nav.navigate(Routes.STUDENT_PROCEDURES) },
                onOpenRequests = { nav.navigate(Routes.STUDENT_REQUESTS) },
                onOpenDocuments = { nav.navigate(Routes.STUDENT_DOCUMENTS) },
                onOpenDigitalID = { nav.navigate(Routes.DIGITAL_ID) },
                settingsVm = settingsVM
            )
        }
        composable(Routes.DIGITAL_ID) { DigitalIDScreen(onBack = { nav.popBackStack() }, settingsVm = settingsVM) }
        composable(Routes.STUDENT_DOCUMENTS) {
            StudentDocumentsScreen(onBack = { nav.popBackStack() }, vm = proceduresVM)
        }
        composable(Routes.STUDENT_PROCEDURES) {
            StudentProceduresScreen(
                onBack = { nav.popBackStack() },
                onOpenEnrollmentCertificate = { nav.navigate(Routes.STUDENT_ENROLLMENT_CERTIFICATE) },
                onOpenKardex = { nav.navigate(Routes.KARDEX_SELECTION) },
                onOpenIDReplacement = { nav.navigate(Routes.ID_REPLACEMENT) },
                onOpenInternshipCertificate = { nav.navigate(Routes.INTERNSHIP_CERTIFICATE) },
                onOpenBajaTemporal = { nav.navigate(Routes.BAJA_TEMPORAL) },
                settingsVm = settingsVM
            )
        }
        composable(Routes.STUDENT_REQUESTS) {
            StudentRequestsScreen(
                onBack = { nav.popBackStack() },
                onStartProcedure = { nav.navigate(Routes.STUDENT_PROCEDURES) },
                vm = proceduresVM
            )
        }
        composable(Routes.KARDEX_SELECTION) {
            KardexSelectionScreen(
                onBack = { nav.popBackStack() },
                onViewHistory = { nav.navigate(Routes.ACADEMIC_HISTORY) },
                onRequestOfficial = { nav.navigate(Routes.KARDEX_REQUEST) },
                settingsVm = settingsVM
            )
        }
        composable(Routes.ACADEMIC_HISTORY) { AcademicHistoryScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.KARDEX_REQUEST) {
            KardexRequestScreen(
                onBack = { nav.popBackStack() },
                vm = proceduresVM,
                onFinish = { isDigital ->
                    if (isDigital) {
                        nav.navigate(Routes.STUDENT_DOCUMENTS) {
                            popUpTo(Routes.KARDEX_SELECTION) { inclusive = true }
                        }
                    } else {
                        nav.navigate(Routes.STUDENT_ENROLLMENT_CERTIFICATE_SUCCESS) {
                            popUpTo(Routes.KARDEX_SELECTION) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Routes.STUDENT_ENROLLMENT_CERTIFICATE) {
            StudentEnrollmentCertificateScreen(
                onBack = { nav.popBackStack() },
                onRequest = { nav.navigate(Routes.STUDENT_ENROLLMENT_FORM) },
                settingsVm = settingsVM
            )
        }
        composable(Routes.STUDENT_ENROLLMENT_FORM) {
            StudentEnrollmentFormScreen(
                onBack = { nav.popBackStack() },
                vm = proceduresVM,
                onSubmit = { isDigital ->
                    if (isDigital) {
                        nav.navigate(Routes.STUDENT_DOCUMENTS) {
                            popUpTo(Routes.STUDENT_PROCEDURES) { inclusive = false }
                        }
                    } else {
                        nav.navigate(Routes.STUDENT_ENROLLMENT_CERTIFICATE_SUCCESS)
                    }
                },
                settingsVm = settingsVM
            )
        }
        composable(Routes.STUDENT_ENROLLMENT_CERTIFICATE_SUCCESS) {
            StudentEnrollmentSuccessScreen(
                onClose = { nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                onGoHome = { nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                onViewRequests = { nav.navigate(Routes.STUDENT_REQUESTS) { popUpTo(Routes.STUDENT_SERVICES) { inclusive = true } } }
            )
        }
        composable(Routes.SCHOOL_INFO) { SchoolInfoScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.TIMETABLE) { TimetableScreen(onBack = { nav.popBackStack() }, settingsVm = settingsVM) }
        composable(Routes.HEALTH) {
            HealthScreen(
                onBack = { nav.popBackStack() },
                onOpenMedicalSupport = { 
                    medicalVm.service.value = "Médico General"
                    medicalVm.location.value = "Consultorio A-102"
                    nav.navigate("medicalSupport") 
                },
                onOpenPsychSupport = { 
                    medicalVm.service.value = "Psicología"
                    medicalVm.location.value = "Edificio D, Cubículo 202"
                    nav.navigate(Routes.PSYCHOLOGIST_DETAIL) 
                },
                onTriggerSOS = { nav.navigate(Routes.SOS_ACTIVE) },
                onViewAppointments = { nav.navigate("my_appointments") },
                settingsVm = settingsVM,
                medicalVm = medicalVm
            )
        }

        /* ------------ Salud ------------ */
        composable(Routes.SOS_ACTIVE) {
            SOSActiveScreen(
                viewModel = sosViewModel,
                onCancel = { nav.popBackStack() },
                onCallEmergencies = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:911")
                    }
                    context.startActivity(intent)
                }
            )
        }

        composable(Routes.PSYCHOLOGIST_DETAIL) {
            PsychologistDetailScreen(
                onBack = { nav.popBackStack() }, 
                onBookAppointment = { nav.navigate(Routes.MEDICAL_APPOINTMENT_FORM) }
            )
        }
        composable("medicalSupport") {
            MedicalSupportScreen(onBack = { nav.popBackStack() }, onBook = { nav.navigate(Routes.MEDICAL_APPOINTMENT_FORM) })
        }
        composable(Routes.MEDICAL_APPOINTMENT_FORM) {
            MedicalAppointmentFormScreen(
                onBack = { nav.popBackStack() }, 
                onContinue = { nav.navigate(Routes.MEDICAL_SCHEDULE_SELECTION) },
                vm = medicalVm,
                settingsVm = settingsVM
            )
        }
        composable(Routes.MEDICAL_SCHEDULE_SELECTION) {
            MedicalScheduleSelectionScreen(
                onBack = { nav.popBackStack() }, 
                onContinue = { nav.navigate(Routes.MEDICAL_APPOINTMENT_SUMMARY) },
                vm = medicalVm,
                healthVm = healthVM,
                settingsVm = settingsVM
            )
        }
        composable(Routes.MEDICAL_APPOINTMENT_SUMMARY) {
            MedicalAppointmentSummaryScreen(
                onBack = { nav.popBackStack() },
                onConfirm = { nav.navigate(Routes.MEDICAL_APPOINTMENT_SUCCESS) },
                onEdit = { nav.popBackStack() },
                vm = medicalVm,
                settingsVm = settingsVM
            )
        }
        composable(Routes.MEDICAL_APPOINTMENT_SUCCESS) {
            MedicalAppointmentSuccessScreen(
                onGoHome = { nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                vm = medicalVm
            )
        }
        
        composable("my_appointments") {
            MyAppointmentsScreen(
                onBack = { nav.popBackStack() },
                medicalVm = medicalVm,
                settingsVm = settingsVM
            )
        }

        composable(Routes.ID_REPLACEMENT) {
            IDReplacementScreen(
                onBack = { nav.popBackStack() },
                onFinish = { nav.popBackStack() },
                vm = proceduresVM,
                settingsVm = settingsVM
            )
        }
        composable(Routes.BAJA_TEMPORAL) {
            BajaTemporalScreen(
                onBack = { nav.popBackStack() },
                onFinish = { nav.popBackStack() },
                vm = proceduresVM,
                settingsVm = settingsVM
            )
        }
        composable(Routes.INTERNSHIP_CERTIFICATE) {
            InternshipCertificateScreen(
                onBack = { nav.popBackStack() },
                onFinish = { nav.popBackStack() },
                vm = proceduresVM,
                settingsVm = settingsVM
            )
        }

        /* ------------ Académico ------------ */
        composable(Routes.SUBJECTS) {
            SubjectsScreen(
                onBack = { nav.popBackStack() }, 
                onOpenSubject = { t, id -> nav.navigate("subjectDetail/$t/$id") }, 
                onGoGrades = { nav.navigate(Routes.GRADES) },
                settingsVm = settingsVM
            )
        }
        composable(Routes.GRADES) { GradesScreen(onBack = { nav.popBackStack() }) }
        composable(
            route = Routes.SUBJECT_DETAIL,
            arguments = listOf(navArgument("term") { type = NavType.IntType }, navArgument("subjectId") { type = NavType.StringType })
        ) { back ->
            val t = back.arguments?.getInt("term") ?: 1
            val id = back.arguments?.getString("subjectId") ?: ""
            SubjectDetailScreen(subjectId = id, term = t, onBack = { nav.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { nav.popBackStack() }, onLogout = { authVM.logout(); nav.navigate(Routes.LOGIN) { popUpTo(0) } }, vm = settingsVM)
        }

        composable(Routes.ANNOUNCEMENTS) { AnnouncementsScreen(onBack = { nav.popBackStack() }, settingsVm = settingsVM) }
        composable(Routes.ROUTES) {
            RoutesSelectorScreen(onBack = { nav.popBackStack() }, onOpenSaltillo = { nav.navigate("routeMap/Saltillo") }, onOpenRamos = { nav.navigate("routeMap/Ramos") })
        }
        composable(
            route = Routes.ROUTE_MAP,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { back ->
            val id = back.arguments?.getString("id") ?: "R5"
            RouteMapScreen(routeId = id, onBack = { nav.popBackStack() })
        }
    }
}
