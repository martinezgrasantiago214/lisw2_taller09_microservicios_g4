# Taller 09 – Comunicación síncrona y asíncrona de microservicios

**Laboratorio de Ingeniería de Software II – Universidad del Cauca (2026.2)**
Proyecto de curso: *Banco de Preguntas Saber Pro* · Spring Boot 3.3.4 · REST · RabbitMQ · Postman

Equipo (grupo B): Cristian Camilo Ramírez Camacho · Juliana Andrea Salas Tapia · Víctor Manuel Ortiz Rojas

## Microservicios

| Microservicio | Puerto | Responsabilidad | Comunicación |
|---|---|---|---|
| `user-service` | 8081 | Autenticar usuarios y verificar roles | REST (`POST /auth/login`, `GET /auth/validate`, `GET /users/{id}`) |
| `validation-service` | 8082 | Validar enunciado, 4 opciones y clave | REST (`POST /validate`) |
| `question-service` | 8083 | Contenido de la pregunta; coordina el flujo | REST + publica `QuestionCreated` y `SubmitQuestionForReview` |
| `lifecycle-service` | 8084 | Autoridad única sobre estados y transiciones | `@RabbitListener` + REST (`/lifecycle/...`) |
| `review-service` | 8085 | Asignación de revisores y dictámenes | REST + publica `ReviewerAssigned` y `QuestionReviewed` |
| `notification-service` | 8086 | Notificaciones sin bloquear el flujo | `@RabbitListener` (asíncrono puro) + `GET /notifications` |

Cada servicio tiene su propia base de datos H2 en memoria (*database per service*).

## Topología RabbitMQ

Exchange directo `saberpro.direct.exchange` y Dead Letter Exchange `saberpro.dlx` → `saberpro.dead-letter.queue`.

| Mensaje (tipo) | Routing key | Cola | Productor → Consumidor |
|---|---|---|---|
| `QuestionCreated` (evento) | `question.created` | `lifecycle.commands.queue` | Question → Lifecycle |
| `SubmitQuestionForReview` (comando) | `question.submit` | `lifecycle.commands.queue` | Question → Lifecycle |
| `OperationResult` (evento) | `question.operation-result` | `question.operation-result.queue` | Lifecycle → Question |
| `QuestionStateChanged` (evento) | `question.state-changed` | `question.state-changed.queue` | Lifecycle → Question |
| `QuestionSubmittedForReview` (evento) | `question.submitted` | `notification.question-submitted.queue` | Lifecycle → Notification |
| `ReviewerAssigned` (evento) | `reviewer.assigned` | `notification.reviewer-assigned.queue` | Review → Notification |
| `QuestionReviewed` (evento) | `question.reviewed` | `notification.question-reviewed.queue` | Review → Notification |
| `ValidationFailed` (evento) | `validation.failed` | `notification.validation-failed.queue` | Question → Notification |

## Cómo ejecutar

Requisitos: JDK 17+, Maven 3.9+, Docker Desktop (o RabbitMQ instalado localmente) y Postman.

```bash
# 1. RabbitMQ (consola en http://localhost:15672, guest/guest)
docker compose up -d

# 2. Microservicios (cada uno en su terminal), o en Windows: start-all.bat
cd user-service && mvn spring-boot:run
# ... igual para validation-, lifecycle-, notification-, question- y review-service

# Compilar todo y correr pruebas unitarias desde la raíz
mvn clean package
```

Si no tienes Docker, instala RabbitMQ para Windows (requiere Erlang) y habilita la consola con
`rabbitmq-plugins enable rabbitmq_management`. Los servicios se conectan por defecto a `localhost:5672`;
se puede cambiar con las variables `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USER` y `RABBITMQ_PASS`.

## Usuarios de prueba

| Usuario | Contraseña | Roles |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `docente1` | `docente1123` | TEACHER, AUTHOR |
| `revisor1` | `revisor1123` | TEACHER, REVIEWER |
| `revisor2` | `revisor2123` | REVIEWER |
| `estudiante1` | `estudiante1123` | STUDENT |

`POST /auth/login` devuelve un token opaco (UUID) que se envía como `Authorization: Bearer <token>`.
No es un JWT: los tokens firmados se implementan en el corte 3.

## Pruebas con Postman

1. Importar `postman/SaberPro_Microservices.postman_collection.json` y `postman/SaberPro_Local.postman_environment.json`.
2. Seleccionar el entorno **SaberPro Local**.
3. Ejecutar la colección completa con el *Collection Runner* (en orden). Las carpetas están organizadas por
   microservicio; la carpeta 7 cubre el flujo de corrección de una pregunta rechazada y la carpeta 8
   el flujo de aprobación (incluida una aprobación sin observaciones).

Las consultas de resultados asíncronos (`GET /operations/{id}`, `GET /notifications`) se repiten
automáticamente cada 500 ms mientras la operación siga en `PROCESSING` (máximo 20 intentos), igual
que el bucle "consultar hasta completar" del diagrama de secuencia. Esta repetición usa
`postman.setNextRequest`, por lo que funciona en el *Collection Runner* y en Newman.

También se puede ejecutar por consola con Newman:

```bash
npx newman run postman/SaberPro_Microservices.postman_collection.json -e postman/SaberPro_Local.postman_environment.json
```

## Estados de la pregunta

```
DRAFT ──► PENDING_REVIEW ──► IN_REVIEW ──► APPROVED ──► PUBLISHED
  ▲             │                  │
  │             ▼                  ▼
  └──────── (DRAFT)  ◄──────── REJECTED
```

Solo el `lifecycle-service` cambia el estado. Las transiciones no permitidas responden `409 Conflict`
(REST) o quedan registradas como `FAILED` (comandos asíncronos), consultables con `GET /operations/{operationId}`.

## Resiliencia ante fallos

- **Asignación de revisor (saga con compensación):** si el Lifecycle Service rechaza la transición
  a `IN_REVIEW`, la asignación pasa a `CANCELLED`. Si no responde (timeout), no se compensa a ciegas:
  se consulta el estado real; si la transición sí se aplicó, la asignación se confirma. Si el
  Lifecycle sigue caído, la asignación queda `PENDING` y una tarea periódica (`@Scheduled`, cada
  30 s) la reconcilia.
- **Comandos con error técnico:** si `SubmitQuestionForReview` falla tras los 3 reintentos, un
  `MessageRecoverer` registra la operación como `FAILED` (y publica el resultado) antes de enviar el
  mensaje a la DLQ, de modo que `GET /operations/{id}` nunca queda en `PROCESSING` indefinidamente.
- **Errores no previstos:** responden 500 con el formato común `ApiError` en lugar de la página por
  defecto de Spring.
