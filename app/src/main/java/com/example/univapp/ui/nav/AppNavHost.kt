@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.univapp.ui.nav

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.univapp.navigation.Routes
import com.example.univapp.ui.*
import com.example.univapp.ui.admin.*
import com.example.univapp.ui.routes.RoutesSelectorScreen

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val authVM: AuthViewModel = hiltViewModel()
    
    val user by authVM.user.collectAsState()
    val offlineSession by authVM.offlineSession.collectAsState()
    val isAdmin by authVM.isAdmin.collectAsState()
    val isLoggedIn = user != null || offlineSession != null
    val authError by authVM.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            nav.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = nav, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                vm = authVM,
                errorText = authError,
                onLogin = { id, pass, _ -> authVM.login(id, pass) },
                onDismissError = { authVM.clearError() }
            )

            LaunchedEffect(isLoggedIn, isAdmin) {
                if (isLoggedIn && isAdmin != null) {
                    val target = if (isAdmin == true) Routes.ADMIN_HOME else Routes.HOME
                    nav.navigate(target) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            }
        }

        composable(Routes.HOME) {
            val settingsVM: SettingsViewModel = hiltViewModel()
            val displayName = (user?.email ?: offlineSession?.email ?: "Alumno").substringBefore("@")
            HomeScreen(
                userName = displayName,
                onGoProfile = { nav.navigate(Routes.PROFILE) },
                onGoStudentServices = { nav.navigate(Routes.STUDENT_SERVICES) },
                onGoHealth = { nav.navigate(Routes.HEALTH) },
                onGoSettings = { nav.navigate(Routes.SETTINGS) },
                onGoSubjects = { nav.navigate(Routes.SUBJECTS) },
                onGoAnnouncements = { nav.navigate(Routes.ANNOUNCEMENTS) },
                onGoTimetable = { nav.navigate(Routes.TIMETABLE) },
                onLogout = { authVM.logout() },
                settingsVm = settingsVM
            )
        }

        composable(Routes.ADMIN_HOME) {
            AdminDashboard(
                onLogout = { authVM.logout() },
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
            val vm: AdminSosViewModel = hiltViewModel()
            AdminSosMapScreen(viewModel = vm, onBack = { nav.popBackStack() })
        }

        composable(Routes.ADMIN_ANNOUNCEMENTS) { AdminAnnouncementsScreen(onBack = { nav.popBackStack() }) }
        
        composable(Routes.ADMIN_ALUMNOS) {
            AdminAlumnosScreen(
                onBack = { nav.popBackStack() },
                onEdit = { id -> nav.navigate(Routes.ADMIN_EDIT_ALUMNO.replace("{alumnoId}", id)) },
                onAddManually = { cid, gid -> 
                    nav.navigate(Routes.ADMIN_ADD_ALUMNO.replace("{carreraId}", cid).replace("{groupId}", gid)) 
                },
                onImportExcel = { nav.navigate(Routes.ADMIN_IMPORT_ALUMNOS) }
            )
        }

        composable(Routes.ADMIN_MATERIAS) {
            val vm: AdminMateriasViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()
            AdminMateriasScreen(
                onBack = { nav.popBackStack() },
                onOpenMateria = { id -> nav.navigate(Routes.ADMIN_MATERIA_DETAIL.replace("{materiaId}", id)) },
                onAddManually = {
                    val c = uiState.selectedCarrera?.id ?: return@AdminMateriasScreen
                    val g = uiState.selectedGrupo?.id ?: return@AdminMateriasScreen
                    nav.navigate(Routes.ADMIN_ADD_MATERIA.replace("{carreraId}", c).replace("{grupoId}", g))
                },
                onImportExcel = { nav.navigate(Routes.ADMIN_IMPORT_MATERIAS) },
                vm = vm
            )
        }

        composable(Routes.ADMIN_GRUPOS) {
            val vm: AdminGruposViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()
            AdminGruposScreen(
                onBack = { nav.popBackStack() },
                onGroupClick = { id -> nav.navigate(Routes.ADMIN_GROUP_DETAIL.replace("{groupId}", id)) },
                onAddManually = {
                    val c = uiState.selectedCarrera?.id ?: return@AdminGruposScreen
                    nav.navigate(Routes.ADMIN_ADD_GROUP_DETAILS.replace("{carreraId}", c))
                },
                onImportExcel = { nav.navigate(Routes.ADMIN_IMPORT_GRUPOS) },
                uiState = uiState,
                onCarreraSelected = { vm.onCarreraSelected(it) }
            )
        }

        composable(Routes.ADMIN_PROFESORES) {
            val vm: AdminProfesoresViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()
            AdminProfesoresScreen(
                onBack = { nav.popBackStack() },
                onAddManually = {
                    val c = uiState.selectedCarrera?.id ?: return@AdminProfesoresScreen
                    nav.navigate(Routes.ADMIN_ADD_PROFESOR.replace("{carreraId}", c))
                },
                onImportExcel = { nav.navigate(Routes.ADMIN_IMPORT_PROFESORES) },
                vm = vm
            )
        }

        composable(Routes.ADMIN_HORARIOS) {
            val vm: AdminHorariosViewModel = hiltViewModel()
            val uiState by vm.uiState.collectAsState()
            AdminHorariosScreen(
                onBack = { nav.popBackStack() },
                onAddManually = {
                    val c = uiState.selectedCarrera?.id ?: return@AdminHorariosScreen
                    val g = uiState.selectedGrupo?.id ?: return@AdminHorariosScreen
                    nav.navigate(Routes.ADMIN_ADD_HORARIO.replace("{carreraId}", c).replace("{grupoId}", g))
                },
                onImportExcel = { nav.navigate(Routes.ADMIN_IMPORT_HORARIOS) },
                vm = vm
            )
        }

        composable(Routes.ADMIN_ACTIVITY) { AdminActivityScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_IMPORT_ALUMNOS) { ImportAlumnosScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_IMPORT_MATERIAS) { ImportMateriasScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_IMPORT_PROFESORES) { ImportProfesoresScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.ADMIN_IMPORT_GRUPOS) { ImportGruposScreen(onBack = { nav.popBackStack() }) }
        
        composable(
            route = Routes.ADMIN_ADD_ALUMNO,
            arguments = listOf(
                navArgument("carreraId") { type = NavType.StringType },
                navArgument("groupId") { type = NavType.StringType }
            )
        ) { back ->
            val cId = back.arguments?.getString("carreraId") ?: ""
            val gId = back.arguments?.getString("groupId") ?: ""
            AdminAddAlumnoScreen(
                carreraId = cId,
                groupId = gId,
                onBack = { nav.popBackStack() }
            )
        }

        composable(
            route = Routes.ADMIN_EDIT_ALUMNO,
            arguments = listOf(navArgument("alumnoId") { type = NavType.StringType })
        ) { back ->
            val id = back.arguments?.getString("alumnoId") ?: ""
            AdminEditAlumnoScreen(alumnoId = id, onBack = { nav.popBackStack() })
        }

        composable(
            route = Routes.ADMIN_MATERIA_DETAIL,
            arguments = listOf(navArgument("materiaId") { type = NavType.StringType })
        ) { back ->
            val id = back.arguments?.getString("materiaId") ?: ""
            AdminMateriaDetailScreen(
                materiaId = id,
                onBack = { nav.popBackStack() },
                onEditMateria = { mid -> nav.navigate(Routes.ADMIN_EDIT_MATERIA.replace("{materiaId}", mid)) }
            )
        }

        composable(
            route = Routes.ADMIN_ADD_MATERIA,
            arguments = listOf(
                navArgument("carreraId") { type = NavType.StringType },
                navArgument("grupoId") { type = NavType.StringType }
            )
        ) { back ->
            val c = back.arguments?.getString("carreraId") ?: ""
            val g = back.arguments?.getString("grupoId") ?: ""
            AddMateriaScreen(carreraId = c, grupoId = g, onBack = { nav.popBackStack() })
        }

        composable(
            route = Routes.ADMIN_GROUP_DETAIL,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { back ->
            val id = back.arguments?.getString("groupId") ?: ""
            AdminGroupDetailScreen(
                groupId = id, 
                onBack = { nav.popBackStack() },
                onAddAlumno = { cid -> 
                    nav.navigate(Routes.ADMIN_ADD_ALUMNO.replace("{carreraId}", cid).replace("{groupId}", id))
                }
            )
        }

        composable(
            route = Routes.ADMIN_ADD_GROUP_DETAILS,
            arguments = listOf(navArgument("carreraId") { type = NavType.StringType })
        ) { back ->
            val c = back.arguments?.getString("carreraId") ?: ""
            AddGroupDetailsScreen(
                onBack = { nav.popBackStack() },
                onNext = { gName, pType, tId ->
                    nav.navigate(
                        Routes.ADMIN_ASSIGN_STUDENTS
                            .replace("{groupName}", gName)
                            .replace("{programType}", pType)
                            .replace("{tutorId}", tId)
                            .replace("{carreraId}", c)
                    )
                }
            )
        }

        composable(
            route = Routes.ADMIN_ASSIGN_STUDENTS,
            arguments = listOf(
                navArgument("groupName") { type = NavType.StringType },
                navArgument("programType") { type = NavType.StringType },
                navArgument("tutorId") { type = NavType.StringType },
                navArgument("carreraId") { type = NavType.StringType }
            )
        ) { back ->
            val gName = back.arguments?.getString("groupName") ?: ""
            val pType = back.arguments?.getString("programType") ?: ""
            val tId = back.arguments?.getString("tutorId") ?: ""
            val cId = back.arguments?.getString("carreraId") ?: ""
            val vm: AdminGruposViewModel = hiltViewModel()
            AssignStudentsScreen(
                onBack = { nav.popBackStack() },
                onSaveGroup = { studentIds ->
                    vm.createGroup(gName, tId, studentIds, cId)
                    nav.navigate(Routes.ADMIN_GRUPOS) {
                        popUpTo(Routes.ADMIN_GRUPOS) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.ADMIN_ADD_HORARIO,
            arguments = listOf(
                navArgument("carreraId") { type = NavType.StringType },
                navArgument("grupoId") { type = NavType.StringType }
            )
        ) { back ->
            val c = back.arguments?.getString("carreraId") ?: ""
            val g = back.arguments?.getString("grupoId") ?: ""
            AddHorarioScreen(carreraId = c, grupoId = g, onBack = { nav.popBackStack() })
        }

        composable(
            route = Routes.ADMIN_ADD_PROFESOR,
            arguments = listOf(navArgument("carreraId") { type = NavType.StringType })
        ) { back ->
            val carreraId = back.arguments?.getString("carreraId") ?: return@composable
            AddProfesorScreen(carreraId = carreraId, onBack = { nav.popBackStack() })
        }

        composable(Routes.PROFILE) { 
            val svm: SettingsViewModel = hiltViewModel()
            ProfileScreen(onBack = { nav.popBackStack() }, settingsVm = svm) 
        }
        composable(Routes.STUDENT_SERVICES) {
            val svm: SettingsViewModel = hiltViewModel()
            StudentServicesScreen(
                onBack = { nav.popBackStack() },
                onOpenInfo = { nav.navigate(Routes.SCHOOL_INFO) },
                onOpenProcedures = { nav.navigate(Routes.STUDENT_PROCEDURES) },
                onOpenRequests = { nav.navigate(Routes.STUDENT_REQUESTS) },
                onOpenDocuments = { nav.navigate(Routes.STUDENT_DOCUMENTS) },
                onOpenDigitalID = { nav.navigate(Routes.DIGITAL_ID) },
                settingsVm = svm
            )
        }
        composable(Routes.DIGITAL_ID) { 
            val svm: SettingsViewModel = hiltViewModel()
            DigitalIDScreen(onBack = { nav.popBackStack() }, settingsVm = svm) 
        }
        composable(Routes.STUDENT_DOCUMENTS) { 
            val pvm: StudentProceduresViewModel = hiltViewModel()
            StudentDocumentsScreen(onBack = { nav.popBackStack() }, vm = pvm) 
        }
        composable(Routes.STUDENT_PROCEDURES) {
            val svm: SettingsViewModel = hiltViewModel()
            StudentProceduresScreen(
                onBack = { nav.popBackStack() },
                onOpenEnrollmentCertificate = { nav.navigate(Routes.STUDENT_ENROLLMENT_CERTIFICATE) },
                onOpenIDReplacement = { nav.navigate(Routes.ID_REPLACEMENT) },
                onOpenInternshipCertificate = { nav.navigate(Routes.INTERNSHIP_CERTIFICATE) },
                onOpenBajaTemporal = { nav.navigate(Routes.BAJA_TEMPORAL) },
                settingsVm = svm
            )
        }
        composable(Routes.STUDENT_REQUESTS) {
            val pvm: StudentProceduresViewModel = hiltViewModel()
            StudentRequestsScreen(
                onBack = { nav.popBackStack() },
                onStartProcedure = { nav.navigate(Routes.STUDENT_PROCEDURES) },
                vm = pvm
            )
        }
        composable(Routes.ACADEMIC_HISTORY) { AcademicHistoryScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.STUDENT_ENROLLMENT_CERTIFICATE) {
            val svm: SettingsViewModel = hiltViewModel()
            StudentEnrollmentCertificateScreen(onBack = { nav.popBackStack() }, onRequest = { nav.navigate(Routes.STUDENT_ENROLLMENT_FORM) }, settingsVm = svm)
        }
        composable(Routes.STUDENT_ENROLLMENT_FORM) {
            val pvm: StudentProceduresViewModel = hiltViewModel()
            val svm: SettingsViewModel = hiltViewModel()
            StudentEnrollmentFormScreen(
                onBack = { nav.popBackStack() },
                vm = pvm,
                onSubmit = { isDigital ->
                    if (isDigital) nav.navigate(Routes.STUDENT_DOCUMENTS) { popUpTo(Routes.STUDENT_PROCEDURES) { inclusive = false } }
                    else nav.navigate(Routes.STUDENT_ENROLLMENT_CERTIFICATE_SUCCESS)
                },
                settingsVm = svm
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
        composable(Routes.TIMETABLE) { 
            val svm: SettingsViewModel = hiltViewModel()
            TimetableScreen(onBack = { nav.popBackStack() }, settingsVm = svm) 
        }
        composable(Routes.HEALTH) {
            val svm: SettingsViewModel = hiltViewModel()
            val mvm: MedicalAppointmentViewModel = hiltViewModel()
            HealthScreen(
                onBack = { nav.popBackStack() },
                onOpenMedicalSupport = { 
                    mvm.service.value = "Médico General"
                    mvm.location.value = "Consultorio A-102"
                    nav.navigate(Routes.MEDICAL_SUPPORT) 
                },
                onOpenPsychSupport = { 
                    mvm.service.value = "Psicología"
                    mvm.location.value = "Edificio D, Cubículo 202"
                    nav.navigate(Routes.PSYCHOLOGIST_DETAIL) 
                },
                onTriggerSOS = { nav.navigate(Routes.SOS_ACTIVE) },
                onViewAppointments = { nav.navigate(Routes.MY_APPOINTMENTS) },
                settingsVm = svm,
                medicalVm = mvm
            )
        }

        composable(Routes.SOS_ACTIVE) {
            val contextS = LocalContext.current
            val svm: SOSViewModel = hiltViewModel()
            SOSActiveScreen(
                viewModel = svm,
                onCancel = { nav.popBackStack() },
                onCallEmergencies = {
                    val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:911") }
                    contextS.startActivity(intent)
                }
            )
        }

        composable(Routes.PSYCHOLOGIST_DETAIL) { PsychologistDetailScreen(onBack = { nav.popBackStack() }, onBookAppointment = { nav.navigate(Routes.MEDICAL_APPOINTMENT_FORM) }) }
        composable(Routes.MEDICAL_SUPPORT) { MedicalSupportScreen(onBack = { nav.popBackStack() }, onBook = { nav.navigate(Routes.MEDICAL_APPOINTMENT_FORM) }) }
        composable(Routes.MEDICAL_APPOINTMENT_FORM) { 
            val mvm: MedicalAppointmentViewModel = hiltViewModel()
            val svm: SettingsViewModel = hiltViewModel()
            MedicalAppointmentFormScreen(onBack = { nav.popBackStack() }, onContinue = { nav.navigate(Routes.MEDICAL_SCHEDULE_SELECTION) }, vm = mvm, settingsVm = svm) 
        }
        composable(Routes.MEDICAL_SCHEDULE_SELECTION) { 
            val mvm: MedicalAppointmentViewModel = hiltViewModel()
            val hvm: HealthViewModel = hiltViewModel()
            val svm: SettingsViewModel = hiltViewModel()
            MedicalScheduleSelectionScreen(onBack = { nav.popBackStack() }, onContinue = { nav.navigate(Routes.MEDICAL_APPOINTMENT_SUMMARY) }, vm = mvm, healthVm = hvm, settingsVm = svm) 
        }
        composable(Routes.MEDICAL_APPOINTMENT_SUMMARY) { 
            val mvm: MedicalAppointmentViewModel = hiltViewModel()
            val svm: SettingsViewModel = hiltViewModel()
            MedicalAppointmentSummaryScreen(onBack = { nav.popBackStack() }, onConfirm = { nav.navigate(Routes.MEDICAL_APPOINTMENT_SUCCESS) }, onEdit = { nav.popBackStack() }, vm = mvm, settingsVm = svm) 
        }
        composable(Routes.MEDICAL_APPOINTMENT_SUCCESS) { 
            val mvm: MedicalAppointmentViewModel = hiltViewModel()
            MedicalAppointmentSuccessScreen(onGoHome = { nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } }, vm = mvm) 
        }
        composable(Routes.MY_APPOINTMENTS) { 
            val mvm: MedicalAppointmentViewModel = hiltViewModel()
            val svm: SettingsViewModel = hiltViewModel()
            MyAppointmentsScreen(onBack = { nav.popBackStack() }, medicalVm = mvm, settingsVm = svm) 
        }
        composable(Routes.ID_REPLACEMENT) { 
            val pvm: StudentProceduresViewModel = hiltViewModel()
            val svm: SettingsViewModel = hiltViewModel()
            IDReplacementScreen(onBack = { nav.popBackStack() }, onFinish = { nav.popBackStack() }, vm = pvm, settingsVm = svm) 
        }
        composable(Routes.BAJA_TEMPORAL) { 
            val pvm: StudentProceduresViewModel = hiltViewModel()
            val svm: SettingsViewModel = hiltViewModel()
            BajaTemporalScreen(onBack = { nav.popBackStack() }, onFinish = { nav.popBackStack() }, vm = pvm, settingsVm = svm) 
        }
        composable(Routes.INTERNSHIP_CERTIFICATE) { 
            val pvm: StudentProceduresViewModel = hiltViewModel()
            val svm: SettingsViewModel = hiltViewModel()
            InternshipCertificateScreen(onBack = { nav.popBackStack() }, onFinish = { nav.popBackStack() }, vm = pvm, settingsVm = svm) 
        }

        composable(Routes.SUBJECTS) { 
            val svm: SettingsViewModel = hiltViewModel()
            SubjectsScreen(onBack = { nav.popBackStack() }, onOpenSubject = { t, id -> nav.navigate("subjectDetail/$t/$id") }, onGoGrades = { nav.navigate(Routes.GRADES) }, settingsVm = svm) 
        }
        composable(Routes.GRADES) { GradesScreen(onBack = { nav.popBackStack() }) }
        composable(
            route = "subjectDetail/{term}/{subjectId}",
            arguments = listOf(navArgument("term") { type = NavType.IntType }, navArgument("subjectId") { type = NavType.StringType })
        ) { back ->
            val t = back.arguments?.getInt("term") ?: 1
            val id = back.arguments?.getString("subjectId") ?: ""
            SubjectDetailScreen(subjectId = id, term = t, onBack = { nav.popBackStack() })
        }
        
        composable(Routes.SETTINGS) { 
            val svm: SettingsViewModel = hiltViewModel()
            SettingsScreen(onBack = { nav.popBackStack() }, onLogout = { authVM.logout() }, vm = svm) 
        }
        
        composable(Routes.ANNOUNCEMENTS) { 
            val svm: SettingsViewModel = hiltViewModel()
            AnnouncementsScreen(onBack = { nav.popBackStack() }, settingsVm = svm) 
        }
        composable(Routes.ROUTES) { 
            RoutesSelectorScreen(onBack = { nav.popBackStack() }, onOpenSaltillo = { nav.navigate("routeMap/Saltillo") }, onOpenRamos = { nav.navigate("routeMap/Ramos") }) 
        }
        composable(route = Routes.ROUTE_MAP, arguments = listOf(navArgument("id") { type = NavType.StringType })) { back ->
            val id = back.arguments?.getString("id") ?: "R5"
            RouteMapScreen(routeId = id, onBack = { nav.popBackStack() })
        }
    }
}
