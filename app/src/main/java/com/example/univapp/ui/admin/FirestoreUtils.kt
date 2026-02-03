package com.example.univapp.ui.admin

import android.util.Log
import com.google.firebase.firestore.QuerySnapshot

/**
 * A robust, reusable parser for converting Firestore snapshots to a list of data-class objects.
 * It processes documents one by one inside a try-catch block.
 * If a document is malformed or doesn't match the data class, it's skipped and an error is logged,
 * but the app WILL NOT CRASH.
 *
 * @param T The data class type to convert to (e.g., Materia, Grupo).
 * @param snapshot The QuerySnapshot from the Firestore listener.
 * @param logContext A string to identify which collection is being parsed in case of an error.
 * @return A list of successfully parsed objects.
 */
inline fun <reified T : Any> robustFirestoreParser(
    snapshot: QuerySnapshot?,
    logContext: String
): List<T> {
    if (snapshot == null) {
        Log.w("FirestoreParser", "Snapshot was null for '$logContext'")
        return emptyList()
    }

    return snapshot.documents.mapNotNull { doc ->
        try {
            // Attempt to convert the document to the specified class
            doc.toObject(T::class.java)?.apply {
                // Try to set the 'id' field on the object if it exists, using reflection for safety.
                try {
                    this::class.java.getMethod("setId", String::class.java).invoke(this, doc.id)
                } catch (e: NoSuchMethodException) {
                    // This is fine, the data class might not have a setId function (e.g. if 'id' is a val in constructor).
                }
            }
        } catch (e: Exception) {
            // Log the error with the document ID and context, then skip this document
            Log.e(
                "FirestoreParser",
                "Error parsing document ${doc.id} in '$logContext'. CHECK YOUR FIREBASE DATA. Skipping document.",
                e
            )
            null
        }
    }
}
