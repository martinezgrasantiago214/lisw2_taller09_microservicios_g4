#!/usr/bin/env bash
# Arranca los 6 microservicios en segundo plano (logs en ./logs). Requiere RabbitMQ y Maven.
mkdir -p logs
for s in user-service validation-service lifecycle-service notification-service question-service review-service; do
  (cd "$s" && mvn -q spring-boot:run > "../logs/$s.log" 2>&1 &)
  echo "Iniciando $s (log: logs/$s.log)"
done
