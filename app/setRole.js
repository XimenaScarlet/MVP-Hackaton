const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccount.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// 🔽 Cambia esto por el UID del usuario admin (lo copias de Firebase Authentication)
const uid = 'PegaAquíElUID';

admin.auth().setCustomUserClaims(uid, { role: 'admin' })
  .then(() => {
    console.log(`✅ Rol 'admin' asignado al usuario ${uid}`);
    process.exit(0);
  })
  .catch(err => {
    console.error('❌ Error asignando rol:', err);
    process.exit(1);
  });
