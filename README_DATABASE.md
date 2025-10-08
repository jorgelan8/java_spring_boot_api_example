# Configuración de Base de Datos

## Resumen

Este proyecto ahora soporta tanto repositorio mock como base de datos MySQL real, configurable en tiempo de ejecución.

## Configuración Rápida

### 1. Variables de Entorno

Copia `env.example` a `.env` y configura:

```bash
cp env.example .env
```

Edita `.env` con tus valores:

```bash
# Base de datos
DB_TYPE=mysql
DB_HOST=localhost
DB_PORT=3306
DB_NAME=api_example
DB_USERNAME=api_user
DB_PASSWORD=api_password

# Repositorio
APP_REPOSITORY_TYPE=mock  # o 'jpa' para usar MySQL
```

### 2. Desarrollo Local

#### Opción A: Solo Mock (sin base de datos)
```bash
export APP_REPOSITORY_TYPE=mock
./mvnw spring-boot:run
```

#### Opción B: Con MySQL local
```bash
# Iniciar MySQL
docker-compose --profile dev up -d mysql

# Configurar para usar JPA
export APP_REPOSITORY_TYPE=jpa
export DB_HOST=localhost

# Ejecutar aplicación
./mvnw spring-boot:run
```

### 3. Producción

```bash
# Configurar variables de entorno
export APP_REPOSITORY_TYPE=jpa
export DB_HOST=your-mysql-server
export DB_USERNAME=your-username
export DB_PASSWORD=your-password

# Ejecutar
./mvnw spring-boot:run
```

## Testing

### Tests Rápidos (Mock)
```bash
./run-tests-with-db.sh mock
```

### Tests con Base de Datos (H2)
```bash
./run-tests-with-db.sh persistent
```

### Tests de Integración (MySQL)
```bash
./run-tests-with-db.sh integration
```

## Docker Compose

### Desarrollo
```bash
# MySQL + Aplicación
docker-compose --profile dev up -d

# Solo MySQL
docker-compose up -d mysql
```

### Producción
```bash
# Solo aplicación (conecta a MySQL externo)
docker-compose up spring_boot_api_example
```

## Comandos Útiles

### Verificar Conexión a MySQL
```bash
mysql -h localhost -u api_user -p api_example
```

### Ver Logs
```bash
# Aplicación
docker-compose logs -f spring_boot_api_example

# MySQL
docker-compose logs -f mysql
```

### Resetear Base de Datos
```bash
docker-compose down -v
docker-compose --profile dev up -d
```

## Troubleshooting

### Error: "Repository not found"
- Verificar que `APP_REPOSITORY_TYPE` esté configurado
- Verificar que `RepositoryConfig` esté en el classpath

### Error: "Cannot connect to MySQL"
- Verificar que MySQL esté ejecutándose
- Verificar variables DB_HOST, DB_PORT, DB_USERNAME, DB_PASSWORD
- Verificar que el usuario tenga permisos

### Error: "Table doesn't exist"
- Verificar que Flyway esté habilitado
- Verificar que las migraciones se ejecutaron
- Revisar logs de Flyway

## Migración de Datos

Si tienes datos en el repositorio mock y quieres migrarlos a MySQL:

1. **Exportar datos**:
   ```bash
   # Con repositorio mock activo
   curl -X GET "http://localhost:8080/api/v1/transactions" > transactions.json
   ```

2. **Cambiar a JPA**:
   ```bash
   export APP_REPOSITORY_TYPE=jpa
   ```

3. **Importar datos**:
   ```bash
   # Usar el endpoint de migración con el archivo CSV
   curl -X POST "http://localhost:8080/api/v1/migrate" \
        -F "file=@transactions.csv"
   ```

## Monitoreo

### Métricas de Base de Datos
- Pool de conexiones: `/actuator/metrics/hikaricp.connections`
- Consultas lentas: Habilitar `spring.jpa.show-sql=true` en desarrollo

### Logs Importantes
- Flyway: `org.flywaydb`
- Hibernate: `org.hibernate.SQL`
- Pool de conexiones: `com.zaxxer.hikari`

## Próximos Pasos

1. **Configurar monitoreo** con Micrometer
2. **Implementar backup automático** de la base de datos
3. **Configurar replicación** para alta disponibilidad
4. **Optimizar consultas** basado en uso real
5. **Implementar cache** para consultas frecuentes
