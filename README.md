# Rescatta — Backend API REST

API REST en **Java 17 + Spring Boot 3.3** para el sistema de reporte, rescate y adopción
de mascotas callejeras de Rescatta.

La autenticación la maneja **Firebase Authentication** desde la app móvil y el panel web.
El backend valida el **ID Token** de Firebase en cada request con Firebase Admin SDK y
guarda el rol del usuario en su propia base de datos. No existe un login propio ni un
header `X-User-Uid`.

## Stack

- Java 17, Spring Boot 3.3.4
- MySQL + Spring Data JPA / Hibernate
- Firebase Admin SDK (verificación de ID Token)
- MapStruct, Lombok, springdoc-openapi
- WebSocket STOMP/SockJS para el chat
- Almacenamiento de imágenes: **Cloudinary**, **Cloudflare R2** o disco local

## Arquitectura

Organizado por **bounded contexts** (un paquete por módulo de negocio) y, dentro de cada
uno, capas claras:

```
com.rescatta.backend
├── common/          → respuestas, excepciones, seguridad, storage y utilidades compartidas
├── config/          → CORS, recursos estáticos, Swagger
├── firebase/        → inicialización de Firebase Admin SDK
├── shared/          → enums de dominio usados por más de un módulo
├── user/            → AppUser (uid + rol), /auth/me y bootstrap de SUPERADMIN
├── organization/    → veterinarias/refugios: perfil, logo, registro y verificación
├── report/          → reportes de animales en la calle
├── catalog/         → catálogo de mascotas en adopción
├── breed/           → catálogo de razas
├── chat/            → conversaciones y mensajes (REST + WebSocket)
└── home/            → datos agregados para la pantalla de inicio
```

Cada módulo de negocio sigue el mismo patrón:

```
domain/       → entidades JPA
dto/          → contratos de entrada/salida (nunca se exponen entidades)
mapper/       → MapStruct: Entity ↔ DTO
repository/   → Spring Data JPA + Specifications para filtros dinámicos
service/      → interfaz + implementación (lógica de negocio y transacciones)
controller/   → capa HTTP delgada, solo delega al service
```

### Decisiones de diseño

- **DTOs, nunca entidades, en los controllers.** El contrato de la API no queda acoplado
  a la estructura de la base de datos.
- **`Specification` para filtros dinámicos.** Cada filtro es independiente y se combina
  con `.and(...)`.
- **`FileStorageService` como abstracción.** Hay tres implementaciones:
  `LocalFileStorageService`, `R2FileStorageService` y `CloudinaryFileStorageService`.
  Cambiar de proveedor es configuración, no código de controllers/services.
- **`CurrentUserProvider` desacoplado.** La implementación actual lee el UID verificado
  por Firebase desde la request.
- **`ApiResponse<T>` y `PageResponse<T>` uniformes.** Todas las respuestas mantienen la
  misma forma para que el cliente tenga un solo tipo de parseo genérico.

## Requisitos

- JDK 17 o superior
- Maven 3.8+
- MySQL 8+ local (o un MySQL externo para producción)
- Una cuenta de Firebase con Authentication habilitado
- Un **JSON de cuenta de servicio** de Firebase

## Configuración local

### 1. Crear la base de datos

```sql
CREATE DATABASE IF NOT EXISTS rescatta;
```

Hibernate crea/actualiza las tablas con `ddl-auto: update` en el perfil `dev`.

> **Importante:** `data.sql` **no se ejecuta** actualmente porque
> `spring.sql.init.mode` está en `never`. No hay seed automático de razas,
> organizaciones ni mascotas. Para una base nueva, o cargas esos datos manualmente o
> implementas migraciones/seed idempotente (recomendado: Flyway o Liquibase).

### 2. Configurar MySQL

En `src/main/resources/application.yml`, perfil `dev`, ajusta el usuario y la contraseña:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rescatta?useSSL=false&serverTimezone=America/Lima&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true
    username: root
    password: tu_password_local
```

Si usas otro usuario o puerto, cámbialo ahí.

### 3. Configurar Firebase

El backend necesita la variable de entorno:

```env
FIREBASE_SERVICE_ACCOUNT_JSON={"type":"service_account","project_id":"...","private_key":"...","client_email":"..."}
```

Debe contener el JSON completo de la cuenta de servicio **en una sola línea**. Si falta,
la aplicación falla al arrancar.

En IntelliJ IDEA puedes agregarla en:

`Run → Edit Configurations → Environment variables`

En PowerShell:

```powershell
$env:FIREBASE_SERVICE_ACCOUNT_JSON = '{"type":"service_account",...}'
```

En Bash:

```bash
export FIREBASE_SERVICE_ACCOUNT_JSON='{"type":"service_account",...}'
```

### 4. Levantar el proyecto

```bash
mvn spring-boot:run
```

La API queda en:

- API: `http://localhost:8081`
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/api-docs`

### 5. Ejecutar tests

```bash
mvn test
```

Actualmente hay tests de almacenamiento: validación por magic bytes, borrado seguro en
local y construcción de clientes de Cloudinary/R2.

## Autenticación

Todas las peticiones autenticadas deben enviar:

```http
Authorization: Bearer <FIREBASE_ID_TOKEN>
```

El filtro `FirebaseAuthenticationFilter` verifica el token con Firebase Admin SDK y deja
disponibles en la request el UID, email y nombre del usuario.

Los roles viven en la tabla `app_users`:

| Rol | Cómo se obtiene | Uso |
|---|---|---|
| `CIUDADANO` | Cualquier usuario nuevo de Firebase | Reportar animales, ver sus reportes y chatear |
| `ADMINISTRADOR` | Registrando una organización | Gestionar reportes, organización y chats |
| `SUPERADMIN` | Bootstrap con secreto | Verificar organizaciones |

### Bootstrap del primer SUPERADMIN

1. Define `RESCATTA_SUPERADMIN_SECRET` en el entorno.
2. Inicia sesión con Firebase y obtén un ID Token.
3. Llama:

```bash
curl -X POST "http://localhost:8081/api/v1/superadmin/bootstrap?secret=TU_SECRETO" \
  -H "Authorization: Bearer $TOKEN"
```

Después del bootstrap, **rota o elimina** `RESCATTA_SUPERADMIN_SECRET`. El endpoint
actualmente recibe el secreto por query param; como mejora pendiente conviene moverlo a
un header y hacerlo de un solo uso.

### Registro de organizaciones

`POST /api/v1/organizations/register` crea la organización y asigna el rol
`ADMINISTRADOR` al usuario autenticado. La organización queda pendiente de verificación
por un `SUPERADMIN`.

> **Pendiente de seguridad:** hoy el backend no bloquea todas las operaciones
> administrativas mientras la organización no esté verificada. Ese gating debe
> implementarse antes de producción.

## Almacenamiento de imágenes

Las fotos de reportes, logos de organizaciones y fotos del catálogo pasan por
`FileStorageService`. El proveedor se elige con `STORAGE_PROVIDER`:

| Valor | Uso | Persistencia |
|---|---|---|
| `local` | Desarrollo local (`./uploads`) | Solo en tu máquina |
| `cloudinary` | Producción / free tier | CDN de Cloudinary |
| `r2` | Producción / Cloudflare R2 | Bucket R2 (10 GB free, sin egress) |

> En Render Free **no uses `local`**: el filesystem del contenedor es efímero y las
> imágenes desaparecen en cada deploy/reinicio. Render Free tampoco soporta discos
> persistentes.

### Opción A: Cloudinary (recomendada para empezar)

1. Crea una cuenta en <https://cloudinary.com>.
2. Copia `Cloud Name`, `API Key` y `API Secret` desde el dashboard.
3. Configura:

```env
STORAGE_PROVIDER=cloudinary
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret
CLOUDINARY_FOLDER=rescatta
```

No necesitas `STORAGE_PUBLIC_BASE_URL` para Cloudinary. El backend sube la imagen y
guarda el `secure_url` que devuelve Cloudinary.

### Opción B: Cloudflare R2

1. Crea un bucket en Cloudflare R2.
2. Activa el acceso público del bucket (`r2.dev`) o conecta un dominio custom.
3. Crea un API Token con permisos de lectura/escritura/borrado sobre el bucket.
4. Configura:

```env
STORAGE_PROVIDER=r2
STORAGE_PUBLIC_BASE_URL=https://pub-xxxxxxxx.r2.dev
R2_ENDPOINT=https://<account-id>.r2.cloudflarestorage.com
R2_BUCKET=rescatta-images
R2_ACCESS_KEY=tu_access_key
R2_SECRET_KEY=tu_secret_key
R2_REGION=auto
```

`STORAGE_PUBLIC_BASE_URL` es la URL con la que el navegador/app accede a las imágenes.
Si el bucket es privado, las URLs devolverán `403`: activa el acceso público o usa un
dominio custom.

### Opción C: local

```env
STORAGE_PROVIDER=local
```

Opcionalmente, para que la API devuelva URLs absolutas en vez de `/uploads/...`:

```env
STORAGE_PUBLIC_BASE_URL=http://localhost:8081
```

### Comportamiento y límites

- Máximo **10 MB por foto**.
- Máximo **55 MB por request** (5 fotos de 10 MB + campos del formulario).
- Se valida el contenido real por **magic bytes** (JPEG, PNG, WebP, HEIC/HEIF), no solo
  el `Content-Type`.
- La extensión se deriva del contenido, nunca del nombre original.
- Si falla el guardado en base de datos, las imágenes recién subidas se borran.
- Al reemplazar el logo de una organización, el logo anterior se borra después del commit.
- Las URLs se guardan en la base de datos tal como las devuelve el proveedor.

### Integración con la app y el panel web

Los clientes deben usar la URL devuelta por la API tal cual. Si la URL empieza con
`http`, **no** deben anteponerle el dominio del backend:

```ts
// Correcto
const imageUrl = report.photos[0].url;

// Incorrecto si la URL ya es absoluta
const imageUrl = `${API_BASE_URL}${report.photos[0].url}`;
```

Mostrar imágenes de Cloudinary no requiere CORS. Si el panel web usa un componente que
exige dominios remotos permitidos (por ejemplo, `next/image`), agrega
`res.cloudinary.com` a su lista de dominios.

### Limitaciones actuales de imágenes

- **HEIC/HEIF:** se acepta y se sube, pero el backend no lo convierte. Muchos navegadores
  no muestran HEIC. Para el panel web conviene que la app suba JPEG/WebP o que Cloudinary
  convierta el formato al subir.
- **Imágenes antiguas:** las que ya estaban en `uploads/` y las URLs `/uploads/...` no se
  migran automáticamente. Hay que re-subirlas o hacer una migración puntual.
- **Cloudinary free tier:** tiene una cuota mensual de créditos. Sirve para desarrollo y
  proyectos pequeños, pero hay que monitorear el consumo.
- **URLs vs. claves:** hoy se guarda la URL completa. A futuro sería más flexible guardar
  el `public_id`/`storage_key` y construir la URL en el DTO, para poder cambiar de
  proveedor o CDN sin migrar datos.

## Endpoints principales

### Públicos y ciudadano

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/reports` | Crea un reporte (multipart: `report` JSON + `photos` 1-5 archivos) |
| GET | `/api/v1/reports/{id}` | Detalle de un reporte |
| GET | `/api/v1/reports` | Lista con filtros `species`, `status` + paginación |
| GET | `/api/v1/reports/nearby` | Reportes cercanos a `latitude`/`longitude` |
| GET | `/api/v1/reports/mine` | Reportes del usuario autenticado |
| GET | `/api/v1/reports/mine/count` | Cantidad de reportes del usuario autenticado |
| GET | `/api/v1/breeds?species=PERRO` | Razas para el dropdown |
| GET | `/api/v1/pets` | Catálogo con filtros y paginación |
| GET | `/api/v1/pets/{id}` | Ficha de una mascota |
| GET | `/api/v1/home/citizen-summary` | Resumen para la pantalla Home Ciudadano |
| GET | `/api/v1/organizations` | Directorio de organizaciones |
| GET | `/api/v1/organizations/{id}` | Detalle de una organización |

> `POST /reports`, `GET /reports/mine` y `GET /reports/mine/count` requieren
> `Authorization: Bearer <token>`.

### Autenticados / administración

| Método | Endpoint | Rol |
|---|---|---|
| GET | `/api/v1/auth/me` | Cualquier usuario autenticado |
| POST | `/api/v1/organizations/register` | Ciudadano autenticado → ADMINISTRADOR |
| GET/PUT | `/api/v1/organizations/me` | ADMINISTRADOR |
| POST | `/api/v1/organizations/me/logo` | ADMINISTRADOR |
| GET | `/api/v1/admin/reports`, `/{id}`, `/stats` | ADMINISTRADOR |
| PATCH | `/api/v1/admin/reports/{id}/status` | ADMINISTRADOR |
| GET/POST | `/api/v1/chats`, `/report/{reportId}`, `/{id}/messages`, `/{id}/read` | Participante del chat |
| WS | `/ws` (STOMP/SockJS) | Participante del chat |

### SUPERADMIN

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v1/superadmin/organizations/pending` | Organizaciones pendientes |
| PATCH | `/api/v1/superadmin/organizations/{id}/verify` | Verificar organización |
| POST | `/api/v1/superadmin/bootstrap?secret=...` | Convertir al usuario autenticado en SUPERADMIN |

> El WebSocket recibe el token de Firebase como `?token=`. Como mejora pendiente, hay que
> autorizar la suscripción a `/topic/conversations/{id}` para que un usuario no pueda
> escuchar conversaciones ajenas.

### Ciclo de vida esperado de un reporte

```
PENDIENTE ──(admin aprueba)──> APROBADO ──> EN_PROCESO ──> RESCATADO
    │
    └──(admin rechaza)──> RECHAZADO
```

Al pasar a `APROBADO`, el reporte se asigna a la organización del administrador y se crea
el `Conversation` para chatear con el ciudadano.

> **Pendiente:** el backend todavía no valida todas las transiciones de estado. Hay que
> implementar una máquina de estados y auditoría de cambios.

## Probar rápido con cURL

```bash
TOKEN="pega_aqui_tu_firebase_id_token"
BASE="http://localhost:8081"

# Catálogo completo
curl "$BASE/api/v1/pets"

# Catálogo filtrado: perros cerca de San Isidro
curl "$BASE/api/v1/pets?species=PERRO&latitude=-12.0995&longitude=-77.0369&radiusKm=10"

# Ficha de una mascota
curl "$BASE/api/v1/pets/1"

# Home Ciudadano
curl "$BASE/api/v1/home/citizen-summary?latitude=-12.0980&longitude=-77.0350"

# Razas de perro
curl "$BASE/api/v1/breeds?species=PERRO"

# Crear un reporte autenticado (multipart con una foto)
curl -X POST "$BASE/api/v1/reports" \
  -H "Authorization: Bearer $TOKEN" \
  -F 'report={"species":"PERRO","condition":"HERIDO","description":"Perrito herido cerca del parque","latitude":-12.10,"longitude":-77.03,"address":"Parque Kennedy","reporterCanStay":true};type=application/json' \
  -F "photos=@/ruta/a/una/foto.jpg"
```

## Deploy en Render

El archivo `render.yaml` ya define el servicio Docker, el health check y las variables de
entorno. Puntos importantes:

- **No se declara disco persistente**: Render Free no lo soporta.
- `STORAGE_PROVIDER` es obligatorio en producción (`cloudinary` o `r2`).
- `FIREBASE_SERVICE_ACCOUNT_JSON` es obligatorio.
- `RESCATTA_CORS_ORIGINS` debe apuntar al dominio real del panel web, no a `*`.
- El health check usa `/api-docs`.

### Base de datos en producción

Render Free no ofrece MySQL administrado (solo PostgreSQL). Para mantener MySQL puedes
usar un proveedor externo con plan gratuito, por ejemplo:

- Aiven
- TiDB Cloud Serverless
- Clever Cloud

Y configurar:

```env
DB_URL=jdbc:mysql://<host>:3306/rescatta?useSSL=true&serverTimezone=America/Lima
DB_USERNAME=...
DB_PASSWORD=...
```

Alternativa: migrar a PostgreSQL (Neon/Supabase free), cambiando la dependencia
`mysql-connector-j` por `postgresql` y el dialecto de Hibernate a
`org.hibernate.dialect.PostgreSQLDialect`.

## Variables de entorno

| Variable | Obligatoria | Uso |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Producción | `prod` |
| `DB_URL` | Producción | JDBC URL de MySQL |
| `DB_USERNAME` | Producción | Usuario de MySQL |
| `DB_PASSWORD` | Producción | Contraseña de MySQL |
| `FIREBASE_SERVICE_ACCOUNT_JSON` | Sí | JSON completo de la cuenta de servicio, en una línea |
| `RESCATTA_SUPERADMIN_SECRET` | Bootstrap | Secreto temporal para crear el primer SUPERADMIN |
| `RESCATTA_CORS_ORIGINS` | Producción | Dominios permitidos, separados por coma |
| `STORAGE_PROVIDER` | Sí en prod | `local`, `cloudinary` o `r2` |
| `STORAGE_PUBLIC_BASE_URL` | Solo R2 / opcional local | URL pública del bucket R2 o URL base para local |
| `R2_ENDPOINT` | Solo R2 | `https://<account-id>.r2.cloudflarestorage.com` |
| `R2_BUCKET` | Solo R2 | Nombre del bucket |
| `R2_ACCESS_KEY` | Solo R2 | Access key del API Token |
| `R2_SECRET_KEY` | Solo R2 | Secret key del API Token |
| `R2_REGION` | Solo R2 | `auto` |
| `CLOUDINARY_CLOUD_NAME` | Solo Cloudinary | Cloud name |
| `CLOUDINARY_API_KEY` | Solo Cloudinary | API key |
| `CLOUDINARY_API_SECRET` | Solo Cloudinary | API secret |
| `CLOUDINARY_FOLDER` | Opcional | Carpeta raíz, por defecto `rescatta` |
| `PORT` | Opcional | Puerto del servidor, por defecto `8081` |

## Estado actual y pendientes conocidos

### Implementado

- Autenticación con Firebase ID Token y roles en base de datos.
- Registro de organizaciones, verificación por SUPERADMIN y logo.
- Reportes, catálogo, razas, home y chat REST/WebSocket.
- Storage con Cloudinary, Cloudflare R2 y local.
- Validación de imágenes por magic bytes.
- Limpieza de imágenes si falla la transacción.
- Corrección del límite multipart a 55 MB.
- Tests de storage.

### Pendientes recomendados

**Seguridad**

- Bloquear operaciones administrativas hasta que la organización esté verificada.
- Separar DTO público y DTO administrativo de reportes (`reporterUid`, `adminNotes` y
  coordenadas exactas no deberían exponerse públicamente).
- Validar que el admin solo acceda a reportes, mascotas y chats de su organización.
- Autorizar suscripciones WebSocket por conversación.
- Hacer el bootstrap de SUPERADMIN de un solo uso y mover el secreto fuera del query param.
- Rate limiting para reportes, uploads, chats y registro de organizaciones.
- Restringir CORS y eliminar secretos hardcodeados.

**Funcional**

- Máquina de estados para reportes y auditoría.
- Excluir reportes `RECHAZADO` de `listNearby()`.
- Paginación y ordenamiento en base de datos para mascotas.
- Migraciones con Flyway/Liquibase y `ddl-auto: validate` en producción.
- Desactivar `open-in-view` y corregir lazy loading/N+1.
- Limpiar imágenes al eliminar reportes o mascotas.
- Paginación y validaciones de mensajes de chat.
- Convertir HEIC a JPEG/WebP para el panel web.
- Guardar `public_id`/`storage_key` en vez de la URL completa.

**Operación**

- Tests de integración con MySQL/Testcontainers, MockMvc y WebSocket.
- CI/CD con GitHub Actions.
- Actuator, métricas, logs estructurados y Sentry.
- Docker como usuario no root y con límites de memoria.
- Maven Wrapper (`mvnw`).

## Frontend

El panel web de administrador (React + TypeScript + Vite + Tailwind) vive en un proyecto
separado: `rescatta-admin-web/`. Ver su propio README para setup y deploy.

Recuerda que, con Cloudinary/R2, las URLs de imágenes son absolutas. El frontend debe
usarlas tal cual y no volver a prefijarlas con el dominio del backend.
