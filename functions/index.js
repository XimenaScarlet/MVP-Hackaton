const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

/**
 * Cloud Function que se dispara cuando un documento en `sos_alerts` es creado o actualizado.
 * Si la alerta está activa, envía notificaciones push a todos los administradores.
 */
exports.sendSosNotification = functions.firestore
    .document("sos_alerts/{alertId}")
    .onWrite(async (change, context) => {
        const alertId = context.params.alertId;
        const newData = change.after.data();

        // Solo enviar si es una alerta activa
        if (!newData || !newData.active || newData.status !== "active") {
            console.log(`Alerta ${alertId} no está activa. No se envía notificación.`);
            return null;
        }

        // Evitar duplicados: Si la notificación ya fue enviada para esta alerta, no reenviar.
        // (Asumimos que el status pasará a "handled" o "ended" para evitar re-envíos)
        const oldData = change.before.data();
        if (oldData && oldData.active) {
            console.log(`Alerta ${alertId} ya estaba activa. Evitando notificación duplicada.`);
            return null;
        }

        console.log(`Alerta SOS activa: ${alertId}. Buscando tokens de administradores...`);

        const db = admin.firestore();
        const tokensSnapshot = await db.collection("admin_tokens").get();

        if (tokensSnapshot.empty) {
            console.log("No se encontraron tokens de administradores.");
            return null;
        }

        const tokens = tokensSnapshot.docs.map(doc => doc.data().token);
        const studentName = newData.alumnoNombre || "un estudiante";

        const payload = {
            notification: {
                title: "¡ALERTA DE EMERGENCIA SOS!",
                body: `El estudiante ${studentName} ha activado el botón de pánico.`
            },
            data: {
                type: "sos",
                alertId: alertId,
                studentId: newData.alumnoId || "",
                studentName: studentName,
                click_action: "FLUTTER_NOTIFICATION_CLICK" // Standard for many clients
            }
        };

        console.log(`Enviando notificación a ${tokens.length} administradores.`);

        // Enviar a todos los tokens de administradores
        const response = await admin.messaging().sendToDevice(tokens, payload);
        
        // Limpiar tokens inválidos de la base de datos para futuras notificaciones
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
            const error = result.error;
            if (error) {
                console.error("Fallo al enviar notificación a", tokens[index], error);
                if (["messaging/invalid-registration-token", "messaging/registration-token-not-registered"].includes(error.code)) {
                    tokensToRemove.push(tokensSnapshot.docs[index].ref.delete());
                }
            }
        });

        return Promise.all(tokensToRemove);
    });
