-- =============================================================
-- AUTODEV - Gestión de Licencias
-- Diseño físico de la base de datos (MySQL 8)
-- Ejecutar con: mysql -u root -p < sql/01_esquema.sql
-- =============================================================

-- Asegura que las tildes (Renovación, María, ...) se guarden bien
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS autodev_licencias
    CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE autodev_licencias;

-- Usuarios del sistema. La clave se guarda como hash PBKDF2 con sal, nunca en texto plano.
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario   INT PRIMARY KEY AUTO_INCREMENT,
    usuario      VARCHAR(30)  NOT NULL UNIQUE,
    clave_hash   VARCHAR(200) NOT NULL,
    nombre       VARCHAR(100) NOT NULL,
    rol          VARCHAR(20)  NOT NULL,              -- ADMIN o VENDEDOR
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_usuario_rol CHECK (rol IN ('ADMIN', 'VENDEDOR'))
);

CREATE TABLE IF NOT EXISTS cliente (
    id_cliente   INT PRIMARY KEY AUTO_INCREMENT,
    nombre       VARCHAR(150) NOT NULL,
    ruc          VARCHAR(11)  UNIQUE,                -- opcional, 11 dígitos
    contacto     VARCHAR(100)                        -- celular o correo
);

CREATE TABLE IF NOT EXISTS programa (
    id_programa  INT PRIMARY KEY AUTO_INCREMENT,
    nombre       VARCHAR(50)   NOT NULL UNIQUE,
    precio       DECIMAL(10,2) NOT NULL,
    CONSTRAINT chk_programa_precio CHECK (precio > 0)
);

CREATE TABLE IF NOT EXISTS licencia (
    id_licencia        INT PRIMARY KEY AUTO_INCREMENT,
    id_cliente         INT           NOT NULL,
    id_programa        INT           NOT NULL,
    tipo               VARCHAR(20)   NOT NULL,       -- Nueva o Renovación
    fecha_venta        DATE          NOT NULL,
    monto              DECIMAL(10,2) NOT NULL,
    descuento          DECIMAL(5,4)  NOT NULL DEFAULT 0,  -- 0.10 = 10 %
    medio_pago         VARCHAR(30)   NOT NULL,       -- Yape, Plin, Transferencia, ...
    fecha_emision      DATE          NOT NULL,
    fecha_vencimiento  DATE          NOT NULL,       -- emisión + 1 año
    estado             VARCHAR(20)   NOT NULL DEFAULT 'Vigente',
    notas              VARCHAR(255),
    CONSTRAINT fk_licencia_cliente  FOREIGN KEY (id_cliente)  REFERENCES cliente(id_cliente),
    CONSTRAINT fk_licencia_programa FOREIGN KEY (id_programa) REFERENCES programa(id_programa),
    CONSTRAINT chk_licencia_monto     CHECK (monto > 0),
    CONSTRAINT chk_licencia_descuento CHECK (descuento BETWEEN 0 AND 1),
    CONSTRAINT chk_licencia_tipo      CHECK (tipo IN ('Nueva', 'Renovación')),
    CONSTRAINT chk_licencia_estado    CHECK (estado IN ('Vigente', 'Por vencer', 'Vencida', 'Renovada')),
    INDEX idx_licencia_cliente (id_cliente),
    INDEX idx_licencia_programa (id_programa),
    INDEX idx_licencia_vencimiento (fecha_vencimiento)
);

CREATE TABLE IF NOT EXISTS alerta (
    id_alerta       INT PRIMARY KEY AUTO_INCREMENT,
    id_licencia     INT      NOT NULL,
    fecha_generada  DATETIME NOT NULL,
    dias_restantes  INT      NOT NULL,
    atendida        BOOLEAN  NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_alerta_licencia FOREIGN KEY (id_licencia) REFERENCES licencia(id_licencia),
    INDEX idx_alerta_licencia (id_licencia)
);

-- Programas que vende AUTODEV (precios referenciales)
INSERT IGNORE INTO programa (nombre, precio) VALUES
    ('AUTODEV', 250.00),
    ('AUTOSIRE', 180.00),
    ('AUTOGUIAS', 150.00),
    ('AUTORHE', 120.00),
    ('AUTOBUZON', 100.00),
    ('AUTOVALIDADOR', 90.00);
