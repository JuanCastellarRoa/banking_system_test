# Frontend — Docker

Instrucciones para construir y ejecutar la SPA (Angular) empaquetada con Nginx.

Requisitos: Docker instalado.

Construir la imagen (desde la carpeta `banking-frontend`):

```bash
cd banking-frontend
docker build -t banking-frontend:latest -f Dockerfile .
```

Ejecutar (modo standalone):

> Nota: el `nginx.conf` incluido hace `proxy_pass` a `http://backend:8080` internamente. Para ejecutar el frontend aislado debes conectarlo a la misma red Docker donde corre el backend.

```bash
# Crear red compartida (si no existe)
docker network create banking-net

# Asumiendo que ya tienes el backend corriendo en la misma red
docker run -d --name banking-frontend \
  --network banking-net \
  -p 4200:80 \
  banking-frontend:latest

# Ahora la UI estará disponible en http://localhost:4200
```

Ejecutar con Docker Compose (recomendado, comparte red con backend y DB):

```bash
# Desde la raíz del proyecto (donde está docker-compose.yml)
# Recomendado (Compose V2 integrado):
docker compose up --build frontend
# Si tu instalación usa la versión legacy, funciona también `docker-compose up --build frontend`
```

Notas:
- Si ejecutas el frontend fuera de Docker Compose y sin `--link`, edita `nginx.conf` para apuntar al host/URL correcto del backend.
- Imagen expone el contenido estático en el puerto `80` dentro del contenedor; en el compose hemos mapeado `4200:80` para que el servicio quede en `http://localhost:4200`.
