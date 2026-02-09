package com.example.univapp.navigation

/**
 * Nombres únicos de rutas para Navigation Compose.
 */
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val ADMIN_HOME = "admin_dashboard"
    const val PROFILE = "profile"
    const val ROUTES = "routes"
    const val HEALTH = "health"
    const val SETTINGS = "settings"
    const val SUBJECTS = "subjects"
    const val GRADES = "grades"
    const val ANNOUNCEMENTS = "announcements"
    const val TIMETABLE = "timetable"
    
    // Alumno
    const val STUDENT_SERVICES = "student_services"
    const val STUDENT_PROCEDURES = "student_procedures"
    const val STUDENT_REQUESTS = "student_requests"
    const val STUDENT_DOCUMENTS = "student_documents"
    const val STUDENT_ENROLLMENT_CERTIFICATE = "student_enrollment_certificate"
    const val STUDENT_ENROLLMENT_FORM = "student_enrollment_form"
    const val STUDENT_ENROLLMENT_CERTIFICATE_SUCCESS = "student_enrollment_certificate_success"
    const val KARDEX_SELECTION = "kardex_selection"
    const val KARDEX_REQUEST = "kardex_request"
    const val ACADEMIC_HISTORY = "academic_history"
    const val SCHOOL_INFO = "school_info"
    const val MEDICAL_APPOINTMENT_FORM = "medical_appointment_form"
    const val MEDICAL_SCHEDULE_SELECTION = "medical_schedule_selection"
    const val MEDICAL_APPOINTMENT_SUMMARY = "medical_appointment_summary"
    const val MEDICAL_APPOINTMENT_SUCCESS = "medical_appointment_success"
    const val PSYCHOLOGIST_DETAIL = "psychologist_detail"
    const val SOS_ACTIVE = "sos_active"
    const val ID_REPLACEMENT = "id_replacement"
    const val ID_REPLACEMENT_SUCCESS = "id_replacement_success"
    const val INTERNSHIP_CERTIFICATE = "internship_certificate"
    const val BAJA_TEMPORAL = "baja_temporal"
    const val PAYMENTS = "payments"
    const val DIGITAL_ID = "digital_id"
    const val MEDICAL_SUPPORT = "medical_support"
    const val MY_APPOINTMENTS = "my_appointments"
    
    // Admin
    const val ADMIN_ALUMNOS = "admin_alumnos"
    const val ADMIN_MATERIAS = "admin_materias"
    const val ADMIN_GRUPOS = "admin_grupos"
    const val ADMIN_HORARIOS = "admin_horarios"
    const val ADMIN_PROFESORES = "admin_profesores"
    const val ADMIN_ACTIVITY = "admin_activity"
    const val ADMIN_ANNOUNCEMENTS = "admin_avisos"
    const val ADMIN_SOS_MAP = "admin_sos_map"
    
    const val ADMIN_ADD_ALUMNO = "admin_add_alumno"
    const val ADMIN_EDIT_ALUMNO = "admin_edit_alumno/{alumnoId}"
    const val ADMIN_IMPORT_ALUMNOS = "admin_import_alumnos"
    
    const val ADMIN_ADD_PROFESOR = "admin_add_profesor/{carreraId}"
    
    const val ADMIN_ADD_MATERIA = "admin_add_materia/{carreraId}/{grupoId}"
    const val ADMIN_IMPORT_MATERIAS = "admin_import_materias"
    const val ADMIN_MATERIA_DETAIL = "admin_materia_detail/{materiaId}"
    const val ADMIN_EDIT_MATERIA = "admin_edit_materia/{materiaId}"
    
    const val ADMIN_ADD_HORARIO = "admin_add_horario/{carreraId}/{grupoId}"
    const val ADMIN_IMPORT_HORARIOS = "admin_import_horarios"
    
    const val ADMIN_GROUP_DETAIL = "admin_group_detail/{groupId}"
    const val ADMIN_ADD_GROUP_DETAILS = "admin_add_group_details/{carreraId}"
    const val ADMIN_ASSIGN_STUDENTS = "admin_assign_students/{groupName}/{programType}/{tutorId}/{carreraId}"
    
    const val SUBJECT_DETAIL = "subjectDetail/{term}/{subjectId}"
    const val ROUTE_MAP = "routeMap/{id}"
}
