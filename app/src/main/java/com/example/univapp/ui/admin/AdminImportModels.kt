package com.example.univapp.ui.admin

data class ImportResult(
    val totalProcessed: Int,
    val createdInAuth: Int = 0,
    val existingInAuth: Int = 0,
    val authFailed: Int = 0,
    val skippedFirestore: Int = 0,
    val rowErrors: List<String>
)

sealed class ImportState {
    object Idle : ImportState()
    object Loading : ImportState()
    data class Success(val result: ImportResult) : ImportState()
    data class Error(val message: String) : ImportState()
}
