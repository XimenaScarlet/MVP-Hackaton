package com.example.univapp.ui.admin

data class ImportResult(val count: Int, val rowErrors: List<String>)

sealed class ImportState {
    object Idle : ImportState()
    object Loading : ImportState()
    data class Success(val count: Int, val errors: List<String> = emptyList()) : ImportState()
    data class Error(val message: String) : ImportState()
}
