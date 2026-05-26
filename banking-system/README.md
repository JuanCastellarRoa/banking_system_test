# Banking System

Sistema bancario REST construido con **Java 17 + Spring Boot 3.2 + PostgreSQL**.

---

## Levantar con Docker (recomendado)

Desde la **raíz** del repositorio (`FullStackTest/`):

```bash
# Construye y levanta todos los servicios (DB + Backend + Frontend)
docker compose up --build -d
```

Servicios que se levantan:

- **Backend (Spring Boot)**: `http://localhost:8080`
- **Frontend (Angular / Nginx)**: `http://localhost:4200`
- **PostgreSQL**: `localhost:5432` (DB: `banco`, user: `banco_user`)

Notas:
- Hibernate crea/actualiza el esquema automáticamente (`ddl-auto=update`) en la primera ejecución.
- El `wait-for-db.sh` del backend espera a que PostgreSQL esté listo antes de arrancar Spring Boot.
- Los datos persisten en el volumen Docker `db-data`.

---

## Endpoints

| Recurso      | Métodos               | Path                   |
|--------------|-----------------------|------------------------|
| Clientes     | GET / POST / PUT / PATCH / DELETE | `/clientes` |
| Cuentas      | GET / POST / PUT / PATCH / DELETE | `/cuentas`  |
| Movimientos  | GET / POST / PUT / PATCH / DELETE | `/movimientos` |
| Reporte      | GET                   | `/reportes?fechaInicio=yyyy-MM-dd&fechaFin=yyyy-MM-dd&cliente={id}` |

El reporte incluye el PDF codificado en Base64 en el campo `pdfBase64` del JSON.

---

## Reglas de negocio

- **Crédito**: valor positivo → saldo aumenta.
- **Débito**: valor negativo → saldo disminuye.
- Saldo 0 + intento de débito → `400 Saldo no disponible`.
- Más de **$1,000** de débitos en el mismo día → `400 Cupo diario Excedido`.

---

## Arquitectura

```
bankingsystem/
├── domain/          # Entidades JPA (Persona, Cliente, Cuenta, Movimiento)
├── repository/      # Spring Data JPA Repositories
├── dto/             # DTOs de entrada/salida
├── service/         # Interfaces de servicio
│   └── impl/        # Implementaciones (lógica de negocio)
├── strategy/        # Patrón Strategy (CreditoStrategy, DebitoStrategy, Factory)
├── controller/      # REST Controllers
└── exception/       # Excepciones + @RestControllerAdvice global
```

**Herencia JPA**: `JOINED` — `persona` y `cliente` son tablas separadas vinculadas por PK.

**Strategy**: `MovimientoStrategyFactory` resuelve dinámicamente si aplicar `CreditoStrategy` o `DebitoStrategy` según el signo del valor, sin `if/else` en el servicio.

---

## Ejecutar tests

```bash
mvn test
```

---

## Colección Postman

Importar `BankingSystem.postman_collection.json` en Postman. La variable `baseUrl` apunta a `http://localhost:8080`.
