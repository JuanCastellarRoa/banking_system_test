-- ============================================================
-- BaseDatos.sql — Sistema Bancario
-- DDL + datos de prueba (PostgreSQL)
-- ============================================================

DROP TABLE IF EXISTS movimiento CASCADE;
DROP TABLE IF EXISTS cuenta CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS persona CASCADE;

-- ============================================================
-- PERSONA (clase base)
-- ============================================================
CREATE TABLE persona (
    persona_id     BIGSERIAL PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    genero         VARCHAR(20)  NOT NULL,
    edad           INTEGER      NOT NULL,
    identificacion VARCHAR(20)  NOT NULL UNIQUE,
    direccion      VARCHAR(200) NOT NULL,
    telefono       VARCHAR(20)  NOT NULL
);

-- ============================================================
-- CLIENTE (hereda de Persona)
-- ============================================================
CREATE TABLE cliente (
    persona_id BIGINT       PRIMARY KEY REFERENCES persona(persona_id) ON DELETE CASCADE,
    cliente_id VARCHAR(50)  NOT NULL UNIQUE,
    contrasena VARCHAR(200) NOT NULL,
    estado     BOOLEAN      NOT NULL
);

-- ============================================================
-- CUENTA
-- ============================================================
CREATE TABLE cuenta (
    numero_cuenta   BIGINT         PRIMARY KEY,
    tipo_cuenta     VARCHAR(20)    NOT NULL,
    saldo_inicial   NUMERIC(19,2)  NOT NULL,
    estado          BOOLEAN        NOT NULL,
    cliente_id_fk   BIGINT         NOT NULL REFERENCES cliente(persona_id) ON DELETE CASCADE
);

-- ============================================================
-- MOVIMIENTO
-- ============================================================
CREATE TABLE movimiento (
    movimiento_id     BIGSERIAL     PRIMARY KEY,
    fecha             DATE          NOT NULL,
    tipo_movimiento   VARCHAR(20)   NOT NULL,
    valor             NUMERIC(19,2) NOT NULL,
    saldo             NUMERIC(19,2) NOT NULL,
    numero_cuenta_fk  BIGINT        NOT NULL REFERENCES cuenta(numero_cuenta) ON DELETE CASCADE
);

CREATE INDEX idx_movimiento_cuenta_fecha ON movimiento(numero_cuenta_fk, fecha);

-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================
INSERT INTO persona (nombre, genero, edad, identificacion, direccion, telefono) VALUES
 ('Jose Lema',    'Masculino', 35, '1700000001', 'Otavalo sn y principal', '098254785'),
 ('Marianela Montalvo', 'Femenino', 28, '1700000002', 'Amazonas y NNUU', '097548965'),
 ('Juan Osorio',  'Masculino', 40, '1700000003', '13 junio y Equinoccial', '098874587');

INSERT INTO cliente (persona_id, cliente_id, contrasena, estado) VALUES
 (1, 'jlema',      '1234', TRUE),
 (2, 'mmontalvo',  '5678', TRUE),
 (3, 'josorio',    '1245', TRUE);

INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id_fk) VALUES
 (478758, 'AHORRO',    2000.00, TRUE, 1),
 (225487, 'CORRIENTE',  100.00, TRUE, 2),
 (495878, 'AHORRO',     0.00,   TRUE, 3),
 (496825, 'AHORRO',   540.00,   TRUE, 2);

INSERT INTO movimiento (fecha, tipo_movimiento, valor, saldo, numero_cuenta_fk) VALUES
 ('2024-01-15', 'DEBITO',  -575.00, 1425.00, 478758),
 ('2024-01-15', 'CREDITO',  600.00,  700.00, 225487),
 ('2024-01-15', 'CREDITO',  150.00,  150.00, 495878),
 ('2024-01-15', 'DEBITO',  -540.00,   0.00, 496825);
