@echo off
REM Arranca los 6 microservicios, cada uno en su propia ventana.
REM Requisitos: RabbitMQ en marcha (docker compose up -d) y Maven en el PATH.
start "user-service :8081"         cmd /k "cd user-service && mvn spring-boot:run"
start "validation-service :8082"   cmd /k "cd validation-service && mvn spring-boot:run"
start "lifecycle-service :8084"    cmd /k "cd lifecycle-service && mvn spring-boot:run"
start "notification-service :8086" cmd /k "cd notification-service && mvn spring-boot:run"
start "question-service :8083"     cmd /k "cd question-service && mvn spring-boot:run"
start "review-service :8085"       cmd /k "cd review-service && mvn spring-boot:run"
