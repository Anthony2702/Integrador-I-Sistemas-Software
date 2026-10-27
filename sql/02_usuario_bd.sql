-- =============================================================
-- Usuario de la aplicación con privilegios mínimos.
-- Solo puede leer, insertar y actualizar en autodev_licencias:
-- no puede borrar datos, cambiar tablas ni dar permisos.
-- Reemplaza CAMBIAR por una clave segura y ponla en config.properties.
-- =============================================================

CREATE USER IF NOT EXISTS 'autodev_app'@'localhost' IDENTIFIED BY 'CAMBIAR';
GRANT SELECT, INSERT, UPDATE ON autodev_licencias.* TO 'autodev_app'@'localhost';
FLUSH PRIVILEGES;
