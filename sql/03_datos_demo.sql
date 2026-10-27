-- =============================================================
-- Datos de demostración (clientes ficticios).
-- Las fechas se calculan desde hoy para que siempre haya licencias
-- vigentes, por vencer y vencidas al presentar el sistema.
-- Ejecutar después de 01_esquema.sql.
-- =============================================================

-- Asegura que las tildes (Renovación, María, ...) se guarden bien
SET NAMES utf8mb4;

USE autodev_licencias;

INSERT INTO cliente (nombre, ruc, contacto) VALUES
    ('Comercial Andina SAC',          '20601234561', '987654321'),
    ('Estudio Contable Rojas',        '10456789012', '912345678'),
    ('Inversiones Pacífico EIRL',     '20509876543', 'contabilidad@pacifico.pe'),
    ('Ferretería El Progreso',        '20487654321', '945112233'),
    ('Contadores Asociados Lima SAC', '20556677889', '956778899'),
    ('Transportes Sur SAC',           '20112233445', '934556677'),
    ('Distribuidora Norte SAC',       '20667788990', '923344556'),
    ('María Quispe Huamán',           '10712345678', '998877665'),
    ('Grupo Textil Arequipa SAC',     '20455667788', '976655443'),
    ('Consultora Vega y Asociados',   '20601122334', '965544332'),
    ('Agroexportadora Ica SAC',       '20534455667', '954433221'),
    ('Botica Salud Total',            '10334455667', '943322110');

SET @hoy = CURDATE();

-- La fecha de emisión es la de venta y el vencimiento es un año después.
-- El sistema recalcula el estado (Vigente, Por vencer, Vencida) al abrir.
INSERT INTO licencia (id_cliente, id_programa, tipo, fecha_venta, monto, descuento,
                      medio_pago, fecha_emision, fecha_vencimiento, estado, notas)
SELECT id_cliente, id_programa, tipo, f, monto, descuento,
       medio, f, DATE_ADD(f, INTERVAL 1 YEAR), 'Vigente', notas
FROM (
              SELECT 1 id_cliente, 1 id_programa, 'Nueva' tipo, DATE_SUB(@hoy, INTERVAL 2 DAY) f, 250.00 monto, 0.0000 descuento, 'Yape' medio, NULL notas
    UNION ALL SELECT 2,  2, 'Nueva',      DATE_SUB(@hoy, INTERVAL 4 DAY),  180.00, 0.0000, 'Transferencia', NULL
    UNION ALL SELECT 4,  1, 'Nueva',      DATE_SUB(@hoy, INTERVAL 6 DAY),  250.00, 0.1000, 'Plin', 'Descuento por pago adelantado'
    UNION ALL SELECT 5,  3, 'Nueva',      DATE_SUB(@hoy, INTERVAL 9 DAY),  150.00, 0.0000, 'Mercado Pago', NULL
    UNION ALL SELECT 6,  1, 'Nueva',      DATE_SUB(@hoy, INTERVAL 12 DAY), 250.00, 0.0000, 'Transferencia', NULL
    UNION ALL SELECT 7,  6, 'Nueva',      DATE_SUB(@hoy, INTERVAL 15 DAY),  90.00, 0.0000, 'Yape', NULL
    UNION ALL SELECT 9,  2, 'Nueva',      DATE_SUB(@hoy, INTERVAL 18 DAY), 180.00, 0.0500, 'Transferencia', NULL
    UNION ALL SELECT 10, 4, 'Nueva',      DATE_SUB(@hoy, INTERVAL 40 DAY), 120.00, 0.0000, 'Yape', NULL
    UNION ALL SELECT 11, 1, 'Nueva',      DATE_SUB(@hoy, INTERVAL 75 DAY), 250.00, 0.0000, 'Transferencia', NULL
    UNION ALL SELECT 12, 5, 'Nueva',      DATE_SUB(@hoy, INTERVAL 120 DAY), 100.00, 0.0000, 'Efectivo', NULL
    UNION ALL SELECT 8,  2, 'Nueva',      DATE_SUB(@hoy, INTERVAL 200 DAY), 180.00, 0.0000, 'Yape', NULL
    UNION ALL SELECT 1,  2, 'Nueva',      DATE_SUB(@hoy, INTERVAL 300 DAY), 180.00, 0.0000, 'Transferencia', NULL
    -- Por vencer (vence en 16 y 5 días)
    UNION ALL SELECT 3,  6, 'Nueva',      DATE_ADD(DATE_SUB(@hoy, INTERVAL 1 YEAR), INTERVAL 16 DAY), 90.00, 0.0000, 'Yape', NULL
    UNION ALL SELECT 2,  1, 'Nueva',      DATE_ADD(DATE_SUB(@hoy, INTERVAL 1 YEAR), INTERVAL 5 DAY), 250.00, 0.0000, 'Transferencia', NULL
    -- Vencida hace 30 días, sin renovar
    UNION ALL SELECT 8,  1, 'Nueva',      DATE_SUB(DATE_SUB(@hoy, INTERVAL 1 YEAR), INTERVAL 30 DAY), 250.00, 0.0000, 'Yape', NULL
    -- Licencia del año pasado que el cliente renovó ayer (la de abajo)
    UNION ALL SELECT 5,  1, 'Nueva',      DATE_SUB(DATE_SUB(@hoy, INTERVAL 1 YEAR), INTERVAL 1 DAY), 250.00, 0.0000, 'Transferencia', NULL
    UNION ALL SELECT 5,  1, 'Renovación', DATE_SUB(@hoy, INTERVAL 1 DAY),  250.00, 0.1000, 'Transferencia', 'Renovación anual'
) AS t;

UPDATE licencia SET estado = 'Renovada'
WHERE id_cliente = 5 AND id_programa = 1 AND tipo = 'Nueva';
