# MarcApp Android conectado a Render / Supabase

## Antes de compilar
1. **Primero** despliega el ZIP del **BACKEND** en `YazidChahua/MarcApp-Backend`. Espera el estado **Live** en Render.
2. Comprueba `https://marcapp-backend.onrender.com/api/health` -> `{"status":"ok"}`.
3. El backend debe ofrecer `/api/marcapp/me` y `/api/marcapp/marcaciones`. Sin JWT válido esas rutas deben devolver 401 (correcto).
4. Actualiza este proyecto Android en el repositorio `YazidChahua/MarcApp`. **No mezcles el ZIP del backend en el repositorio Android**.
5. En GitHub abre Actions -> «Compilar MarcApp APK» y descarga el artefacto `MarcApp-APK-debug` tras compilar (o utiliza Android Studio).

## Qué funciona en la APK
- URL HTTPS configurada en `app/build.gradle.kts`: `https://marcapp-backend.onrender.com/`.
- Login real de colaborador/supervisor, nombre/correo desde endpoint `/api/marcapp/me` autenticado.
- Para colaboradores: pantalla Inicio, marcaciones de entrada, refrigerio, retorno y salida contra Supabase, con token, GPS nuevo en cada operación y biometría local obligatoria.
- Historial de los últimos 7 días consultado al servidor y visible tras un nuevo inicio de sesión.
- Botones `Demo trabajador` y `Demo admin` conservan el modo local sin servidor.

## Límites importantes
- El backend emite JWT de **15 minutos**. Si caduca, cierra sesión e ingresa otra vez antes de marcar. El token no se almacena en el teléfono de forma persistente.
- **GPS y lector biométrico no prueban identidad en el servidor**; BiometricPrompt protege el flujo local de la aplicación. Para uso laboral exigente hacen falta medidas antifraude adicionales (vinculación de dispositivo/atestation, controles de GPS falsificado y auditoría).
- El backend mantiene reglas preexistentes: sede `Sucursal` con ID 1 y geocerca configurada; entrada desde 08:00; refrigerio desde 13:00; retorno desde 14:00; salida Full desde 17:00 y Medio desde 13:00; sujeto a que existan tablas y trabajadores con contraseñas BCrypt.
- Las pantallas de administración, justificaciones y reportes **siguen siendo DEMO** y no deben usarse para datos reales.
- Render Free puede tardar >50 s en responder tras inactividad. Se configuraron esperas HTTP mayores, pero esto no elimina las suspensiones del servicio.
- Las contraseñas, cadena JDBC y JWT_SECRET no deben estar en el proyecto Android ni en GitHub.
- No se ejecutó compilación Android local en este entorno: verifica la compilación mediante GitHub Actions antes de instalar la APK.

## Cómo probar la conexión
1. Despliega **backend**, luego compila **Android**.
2. Instala la APK y pulsa Ingresar con un colaborador que ya exista en Supabase.
3. Se mostrará su **nombre real**. Si sale 403/404 al pedir `/api/marcapp/me`, aún no está actualizado el backend.
4. En la sede, con permiso de ubicación precisa y huella enrolada, marca una acción permitida por el horario.
5. Confirma la operación en Historial y, si puedes, consulta `detalle_asistencia` o `detalle_refrigerio` en Supabase.

## Si una prueba falla
- Login 401: verifica que el correo y la contraseña BCrypt sean correctos y que el colaborador esté Activo.
- Marca 403: podría ser fuera de horario o geocerca.
- Marca 404: verifica la sede ID 1 y los datos del trabajador.
- Marca 409: marcación duplicada o secuencia incorrecta.
- Marca 401 después de un rato: token expirado; vuelve a ingresar.
- Para localizar el motivo exacto, revisa Render -> Logs sin compartir claves.
