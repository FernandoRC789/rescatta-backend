# Rescatta — Backend API REST

API REST en **Java + Spring Boot** para el sistema de reporte y adopción de mascotas
callejeras de Rescatta. La autenticación de usuarios (login, registro) se maneja
**enteramente en la app con Firebase Authentication** — este backend no expone APIs de
usuario, solo recibe el UID ya autenticado.

## Arquitectura

Organizado por **bounded contexts** (un paquete por módulo de negocio), y dentro de cada
uno, capas claras:

```
com.rescatta.backend
├── common/          → wrappers de respuesta, excepciones, storage, seguridad, utilidades
│                       compartidas por todos los módulos
├── config/          → configuración de Spring (CORS, recursos estáticos, Swagger)
├── shared/          → enums de dominio usados por más de un módulo (Species, Size, etc.)
├── breed/           → catálogo de razas (dropdown de formularios)
├── organization/    → refugios/rescatistas (solo atribución, no autenticación)
├── report/          → reporte de animales en la calle (módulo principal de esta entrega)
├── catalog/          → catálogo de mascotas listas para adopción (solo lectura por ahora)
└── home/            → datos agregados para pantallas de inicio
```

Cada módulo de negocio sigue el mismo patrón interno:

```
domain/       → entidades JPA
dto/          → contratos de entrada/salida (nunca se exponen las entidades directamente)
mapper/       → MapStruct: Entity ↔ DTO
repository/   → Spring Data JPA + Specifications para filtros dinámicos
service/      → interfaz + implementación (lógica de negocio, transacciones)
controller/   → capa HTTP, delgada — solo delega al service
```

### Decisiones de diseño relevantes

- **DTOs, nunca entidades, en los controllers.** Evita acoplar el contrato de la API a la
  estructura de la base de datos.
- **`Specification` en vez de métodos con muchos parámetros opcionales.** Cada filtro
  (`hasSpecies`, `hasAdoptionStatus`, etc.) es independiente y se combina con `.and(...)` —
  agregar un filtro nuevo no obliga a tocar los demás.
- **`FileStorageService` como interfaz.** Hoy hay una implementación en disco local
  (`LocalFileStorageService`); el día que se necesite Firebase Storage o S3, se crea una
  nueva implementación y no se toca ni un controller ni un service.
- **`CurrentUserProvider` como interfaz.** Hoy lee un header simple `X-User-Uid` (ver más
  abajo); el día que se conecte Firebase Admin SDK para verificar el ID Token real, se
  reemplaza la implementación sin tocar los controllers.
- **`ApiResponse<T>` y `PageResponse<T>` uniformes.** Todas las respuestas de la API
  tienen la misma forma, así el cliente (la app iOS) puede tener un solo tipo de parseo
  genérico.

## Cómo correrlo

Requisitos: JDK 17+, Maven, y **MySQL corriendo localmente** (el que administras con
MySQL Workbench).

### 1. Crea la base de datos en Workbench

Abre una nueva pestaña de consulta SQL en Workbench, conectado a tu servidor local, y
ejecuta:

```sql
CREATE DATABASE IF NOT EXISTS rescatta;
```

(No necesitas crear tablas a mano — Hibernate las genera solo gracias a
`ddl-auto: update`, y `data.sql` las llena con datos de ejemplo automáticamente al
arrancar la app.)

### 2. Ajusta el usuario/contraseña

En `src/main/resources/application.yml`, dentro del perfil `dev`, edita:

```yaml
datasource:
  url: jdbc:mysql://localhost:3306/rescatta?useSSL=false&serverTimezone=America/Lima&createDatabaseIfNotExist=true
  username: root
  password: root   # ← cambia esto por tu contraseña real de MySQL Workbench
```

Si en Workbench usas otro usuario (no `root`) o el puerto no es el `3306` por defecto,
ajústalo aquí también.

### 3. Levanta el proyecto

```bash
cd rescatta-backend
mvn spring-boot:run
```

La API queda en `http://localhost:8080`. Puedes ver las tablas creadas y los datos de
ejemplo directamente en MySQL Workbench (refresca el esquema `rescatta` en el panel de
la izquierda).

- Swagger UI (probar todo desde el navegador): `http://localhost:8080/swagger-ui.html`

Para producción (perfil `prod`), se usan variables de entorno en vez de valores fijos:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod \
  -DDB_URL="jdbc:mysql://tu-servidor:3306/rescatta?useSSL=false&serverTimezone=America/Lima" \
  -DDB_USERNAME=rescatta -DDB_PASSWORD=tu_password_real
```

## Autenticación temporal (mientras no se conecta Firebase Admin SDK)

Los endpoints que necesitan saber "quién es el usuario" (crear reporte, contar mis
reportes) leen un header simple:

```
X-User-Uid: demo-uid-ciudadano-1
```

Esto es un **stub deliberado** (ver `HeaderBasedCurrentUserProvider`) para poder construir
y probar el resto de la API sin bloquear el desarrollo en la verificación de tokens. La
migración a Firebase Admin SDK real está documentada como TODO en esa misma clase.

## Endpoints de esta entrega

### Reportes

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/v1/reports` | Crea un reporte (multipart: `report` JSON + `photos` 1-5 archivos) |
| GET | `/api/v1/reports/{id}` | Detalle completo de un reporte |
| GET | `/api/v1/reports` | Lista con filtros `species`, `status` + paginación |
| GET | `/api/v1/reports/nearby` | Reportes activos más cercanos a `latitude`/`longitude` |
| GET | `/api/v1/reports/mine/count` | Cantidad de reportes del usuario autenticado |

### Razas

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v1/breeds?species=PERRO` | Razas disponibles para el dropdown |

### Catálogo de adopción

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v1/pets` | Búsqueda con filtros `species`, `ageGroup`, `size`, `adoptionStatus`, `latitude`/`longitude`/`radiusKm` |
| GET | `/api/v1/pets/{id}` | Ficha completa de una mascota |

### Home

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v1/home/citizen-summary?latitude=&longitude=` | Urgencias cercanas + reportes cercanos, para la pantalla Home Ciudadano |

## Probar rápido con cURL

```bash
# Catálogo completo
curl "http://localhost:8080/api/v1/pets"

# Catálogo filtrado: perros, cerca de San Isidro
curl "http://localhost:8080/api/v1/pets?species=PERRO&latitude=-12.0995&longitude=-77.0369&radiusKm=10"

# Ficha de una mascota
curl "http://localhost:8080/api/v1/pets/1"

# Home Ciudadano
curl "http://localhost:8080/api/v1/home/citizen-summary?latitude=-12.0980&longitude=-77.0350"

# Razas de perro
curl "http://localhost:8080/api/v1/breeds?species=PERRO"

# Crear un reporte (multipart con una foto de ejemplo)
curl -X POST "http://localhost:8080/api/v1/reports" \
  -H "X-User-Uid: demo-uid-ciudadano-1" \
  -F 'report={"species":"PERRO","condition":"HERIDO","description":"Perrito herido cerca del parque","latitude":-12.10,"longitude":-77.03,"address":"Parque Kennedy","reporterCanStay":true};type=application/json' \
  -F "photos=@/ruta/a/una/foto.jpg"
```

## Pendiente para la siguiente entrega

- Módulo de gestión del Rescatista/Refugio: publicar/editar mascota, atender reportes
  (transición de estado `PENDIENTE → EN_PROCESO → RESCATADO`), solicitudes de adopción.
- Verificación real de Firebase ID Token (reemplazar `HeaderBasedCurrentUserProvider`).
- Módulo de donaciones.
- Tests unitarios de los `*ServiceImpl` (la arquitectura ya está lista para esto: cada
  service depende de interfaces, fácil de mockear con Mockito).
