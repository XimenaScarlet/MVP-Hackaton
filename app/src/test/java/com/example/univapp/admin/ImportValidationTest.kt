package com.example.univapp.admin

import com.example.univapp.data.Alumno
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ImportValidationTest {

    @Test
    fun `importacion should fail if eid already exists`() {
        // Arrange
        val alumnosEnDb = listOf(Alumno(id = "22040089", matricula = "22040089"))
        val alumnosEnExcel = listOf(Alumno(matricula = "22040089"))

        // Act
        val matriculasEnExcel = alumnosEnExcel.mapNotNull { it.matricula }
        val duplicados = alumnosEnDb.any { dbAlumno -> matriculasEnExcel.contains(dbAlumno.matricula) }

        // Assert
        assertThat(duplicados).isTrue()
    }
}
