# Levantar todo con Docker Compose

Este README describe cómo arrancar toda la plataforma (Postgres + Backend + Frontend) con un solo comando.

Requisitos:
- Docker (y opcionalmente Docker Compose v1/v2)

Desde la raíz del repositorio `FullStackTest`:

> **Windows (PowerShell):** si `docker` no se reconoce como comando, agrégalo al PATH de la sesión:
> ```powershell
> $env:PATH += ";$env:ProgramFiles\Docker\Docker\resources\bin"
> ```

```bash
# Construye y levanta todos los servicios en segundo plano
# Recomendado: Docker Compose V2 (integrado) usa `docker compose` (sin guion)
docker compose up --build -d

# Monitorizar logs
docker compose logs -f backend
# o
docker compose logs -f frontend
# o todos
docker compose logs -f

# Detener y eliminar contenedores + volúmenes de datos
docker compose down -v
# (si tu instalación es legacy aún puedes usar `docker-compose down -v`)
```

URLs:
- Frontend: http://localhost:4200
- Backend: http://localhost:8080
- Postgres: disponible en el host `localhost:5432` (si lo expones)

Credenciales Postgres (definidas en `docker-compose.yml`):
- DB: `banco`
- USER: `banco_user`
- PASSWORD: `banco_pass`

Notas importantes:
- En `docker-compose.yml` el servicio `backend` recibe la variable `SPRING_JPA_HIBERNATE_DDL_AUTO=update` para crear/actualizar el esquema en entornos de desarrollo.
- Si tienes datos previos o quieres persistencia, el volumen `db-data` mantiene los datos de Postgres.
- En la carpeta banking-system la cual pertenece al backend se encuentra la coleción BankingSystem.postman_collection.json para probar los endpoints

