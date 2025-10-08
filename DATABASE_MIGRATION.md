# Migración a Base de Datos MySQL

Este documento describe la migración del proyecto de un repositorio mock a una base de datos MySQL real.

## Resumen de Cambios

### 1. Dependencias Agregadas
- **Spring Data JPA**: Para mapeo objeto-relacional
- **MySQL Connector**: Driver para conectar con MySQL
- **H2 Database**: Para testing (scope: test)
- **Flyway**: Para migraciones de base de datos

### 2. Nuevos Archivos Creados

#### Repositorios
- `JpaTransactionRepository.java`: Repositorio JPA que extiende JpaRepository
- `JpaTransactionRepositoryImpl.java`: Implementación que adapta JPA a la interfaz existente

#### Configuración
- `RepositoryConfig.java`: Configuración condicional para seleccionar repositorio en runtime
- `TestRepositoryConfig.java`: Configuración específica para testing

#### Scripts de Base de Datos
- `scripts/init-db.sql`: Script de inicialización de MySQL
- `scripts/migrate-data.sql`: Script para migrar datos existentes
- `src/main/resources/db/migration/V1__Create_user_transactions_table.sql`: Migración Flyway

#### Testing
- `RepositoryIntegrationTest.java`: Tests de integración configurables
- `run-tests-with-db.sh`: Script para ejecutar tests con diferentes repositorios

### 3. Archivos Modificados

#### Modelo de Datos
- `UserTransaction.java`: Convertido a entidad JPA con anotaciones

#### Configuración
- `pom.xml`: Agregadas dependencias de JPA y MySQL
- `application.yml`: Configuración de base de datos con variables DB_*
- `application-dev.yml`: Configuración para desarrollo
- `application-prod.yml`: Configuración para producción
- `application-test.yml`: Configuración para testing
- `env.example`: Variables de entorno para base de datos

#### Docker
- `docker-compose.yml`: Servicio MySQL agregado

## Variables de Entorno

### Variables de Base de Datos (DB_*)
```bash
# Configuración de Base de Datos (agnostic to specific DB)
DB_TYPE=mysql
DB_HOST=localhost
DB_PORT=3306
DB_NAME=api_example
DB_USERNAME=api_user
DB_PASSWORD=api_password
DB_ROOT_PASSWORD=root_password
```

### Variables de Repositorio
```bash
# Configuración de Repositorio
APP_REPOSITORY_TYPE=mock  # mock, jpa
TEST_REPOSITORY_TYPE=mock  # mock, jpa
```

## Configuración por Perfiles

### Desarrollo (dev)
- **Repositorio**: Configurable (mock por defecto)
- **Base de Datos**: MySQL local o externa
- **JPA**: `ddl-auto: update` (permite cambios de esquema)
- **Logging**: SQL habilitado para debugging

### Producción (prod)
- **Repositorio**: JPA (configurable)
- **Base de Datos**: MySQL externa
- **JPA**: `ddl-auto: validate` (solo validación)
- **Pool de Conexiones**: Configurado para producción

### Testing (test)
- **Repositorio**: Configurable (mock por defecto)
- **Base de Datos**: H2 en memoria
- **JPA**: `ddl-auto: create-drop`
- **Flyway**: Deshabilitado

## Uso

### 1. Desarrollo Local con MySQL

```bash
# Usar repositorio mock (por defecto)
export APP_REPOSITORY_TYPE=mock
./mvnw spring-boot:run

# Usar repositorio JPA con MySQL local
export APP_REPOSITORY_TYPE=jpa
export DB_HOST=localhost
docker-compose up -d mysql
./mvnw spring-boot:run
```

### 2. Producción

```bash
# Configurar variables de entorno
export APP_REPOSITORY_TYPE=jpa
export DB_HOST=your-mysql-server
export DB_USERNAME=your-username
export DB_PASSWORD=your-password

# Ejecutar aplicación
./mvnw spring-boot:run
```

### 3. Testing

```bash
# Tests con repositorio mock (rápido)
./run-tests-with-db.sh mock

# Tests con repositorio JPA (H2)
./run-tests-with-db.sh persistent

# Tests de integración con MySQL real
./run-tests-with-db.sh integration
```

## Migración de Datos

### 1. Migración Automática
Flyway se encarga automáticamente de crear las tablas y aplicar migraciones.

### 2. Migración Manual
Si tienes datos existentes en el repositorio mock:

1. Ejecuta la aplicación con `APP_REPOSITORY_TYPE=mock`
2. Exporta los datos a CSV
3. Cambia a `APP_REPOSITORY_TYPE=jpa`
4. Importa los datos usando el endpoint de migración

### 3. Scripts de Verificación
```bash
# Conectar a MySQL y verificar datos
mysql -h localhost -u api_user -p api_example
source scripts/migrate-data.sql
```

## Docker Compose

### Servicios Disponibles

1. **mysql**: Base de datos MySQL 8.0
   - Puerto: 3306
   - Perfiles: dev, local

2. **spring_boot_api_example**: Aplicación Spring Boot
   - Puerto: 8082
   - Depende de MySQL

3. **h2-console**: Consola H2 (solo desarrollo)
   - Puerto: 8083
   - Perfil: dev

### Comandos Docker

```bash
# Desarrollo con MySQL local
docker-compose --profile dev up -d

# Solo la aplicación (conecta a MySQL externo)
docker-compose up spring_boot_api_example

# Ver logs
docker-compose logs -f spring_boot_api_example

# Parar servicios
docker-compose down
```

## Troubleshooting

### 1. Error de Conexión a MySQL
- Verificar que MySQL esté ejecutándose
- Verificar variables de entorno DB_*
- Verificar credenciales y permisos

### 2. Error de Migración Flyway
- Verificar que la base de datos existe
- Verificar permisos de usuario
- Revisar logs de Flyway

### 3. Tests Fallando
- Verificar configuración de perfil de test
- Verificar que H2 esté en el classpath
- Verificar variables TEST_REPOSITORY_TYPE

### 4. Repositorio No Encontrado
- Verificar que `RepositoryConfig` esté en el classpath
- Verificar variable `APP_REPOSITORY_TYPE`
- Verificar que solo un repositorio esté marcado como `@Primary`

## Próximos Pasos

1. **Monitoreo**: Agregar métricas de base de datos
2. **Backup**: Configurar respaldos automáticos
3. **Escalabilidad**: Configurar replicación de lectura
4. **Seguridad**: Implementar encriptación de datos sensibles
5. **Performance**: Optimizar consultas y agregar índices según uso

## Soporte

Para problemas o preguntas sobre la migración, consultar:
- Logs de la aplicación en `logs/`
- Logs de MySQL en Docker: `docker-compose logs mysql`
- Documentación de Spring Data JPA
- Documentación de Flyway
