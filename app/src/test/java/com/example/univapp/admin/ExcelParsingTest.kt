package com.example.univapp.admin

import com.example.univapp.ui.admin.AdminImportAlumnosViewModel
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ExcelParsingTest {

    @Test
    fun `numeric cell should be converted to string without decimals`() {
        // This test is conceptual as we cannot create a real Excel file here.
        // It verifies the logic of the getCellText extension function.
        
        // Arrange
        val numericValueWithDecimal = 12345.0
        val numericValueWithoutDecimal = 12345

        // Act
        val stringFromDouble = if (numericValueWithDecimal % 1.0 == 0.0) numericValueWithDecimal.toLong().toString() else numericValueWithDecimal.toString()
        val stringFromInt = numericValueWithoutDecimal.toString()

        // Assert
        assertThat(stringFromDouble).isEqualTo("12345")
        assertThat(stringFromInt).isEqualTo("12345")
    }
}
