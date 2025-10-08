-- Script de inicialización de la base de datos MySQL
-- Este script se ejecuta automáticamente cuando se crea el contenedor MySQL

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS api_example CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS api_example_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS api_example_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos principal
USE api_example;

-- Crear usuario si no existe
CREATE USER IF NOT EXISTS 'api_user'@'%' IDENTIFIED BY 'api_password';

-- Otorgar permisos al usuario
GRANT ALL PRIVILEGES ON api_example.* TO 'api_user'@'%';
GRANT ALL PRIVILEGES ON api_example_dev.* TO 'api_user'@'%';
GRANT ALL PRIVILEGES ON api_example_test.* TO 'api_user'@'%';

-- Aplicar los cambios
FLUSH PRIVILEGES;

-- Mostrar información de la base de datos
SELECT 'Database initialization completed successfully' as status;
SHOW DATABASES;
