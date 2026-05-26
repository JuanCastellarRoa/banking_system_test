# Backend — Docker

Este documento explica cómo construir y arrancar el servicio backend (`banking-system`) usando Docker.

Requisitos previos:
- Docker instalado
- (Opcional) Docker Compose

Construir la imagen (desde la carpeta `banking-system`):

```bash
cd banking-system
docker build -t banking-system:latest -f Dockerfile .
```

Ejecutar el backend junto a una base de datos Postgres (modo manual):

```bash
# Crear red compartida
docker network create banking-net

# Iniciar Postgres
docker run -d --name banco-postgres \
  --network banking-net \
  -e POSTGRES_DB=banco \
  -e POSTGRES_USER=banco_user \
  -e POSTGRES_PASSWORD=banco_pass \
  -p 5432:5432 \
  postgres:15-alpine

# Ejecutar backend (misma red, alias 'db' para el host de la DB)
docker run -d --name banking-backend \
  --network banking-net \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://banco-postgres:5432/banco \
  -e SPRING_DATASOURCE_USERNAME=banco_user \
  -e SPRING_DATASOURCE_PASSWORD=banco_pass \
  -p 8080:8080 \
  banking-system:latest
```

Ver logs del backend:

```bash
docker logs -f banking-backend
```

Usar Docker Compose (recomendado — arranca DB + Backend + Frontend juntos):

```bash
# Desde la raíz del proyecto (donde está docker-compose.yml)
docker compose up --build -d

# Solo el backend (sin reconstruir frontend/db):
docker compose up --build backend

# Ver logs en tiempo real:
docker compose logs -f backend
```

Notas:
- Por defecto `application.properties` usa `spring.jpa.hibernate.ddl-auto=validate`. En el `docker-compose.yml` (`backend` service) se ha incluido `SPRING_JPA_HIBERNATE_DDL_AUTO=update` para entornos de desarrollo; puedes quitar/ajustar esa variable si prefieres gestionar el esquema manualmente.
- El puerto expuesto del backend es `8080`.
