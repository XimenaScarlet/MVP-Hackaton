// setRoleByEmail.js
const admin = require('firebase-admin');
const path = require('path');

const credPath = process.argv[2] || './serviceAccount.json'; // ruta al serviceAccount si quieres pasarla
const email    = process.argv[3];                             // correo del usuario en Auth
const role     = process.argv[4] || 'admin';                  // admin | professor | student

if (!email) {
  console.error('Uso: node setRoleByEmail.js <rutaServiceAccount.json(opcional)> <email> [rol]');
  process.exit(1);
}

admin.initializeApp({
  credential: admin.credential.cert(require(path.resolve(credPath)))
});

(async () => {
  try {
    const user = await admin.auth().getUserByEmail(email);
    await admin.auth().setCustomUserClaims(user.uid, { role });
    console.log(`✅ Rol '${role}' asignado a ${email} (uid: ${user.uid})`);
    process.exit(0);
  } catch (err) {
    console.error('❌ Error:', err.message);
    process.exit(1);
  }
})();
