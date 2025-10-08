# API Example - Migration & Balance Service (Java Spring Boot)

API RESTful en Java Spring Boot para migración de transacciones y consulta de balance de usuarios.

## 🚀 Características

- **Migración de transacciones** desde archivos CSV
- **Consulta de balance** de usuarios con filtros de fecha
- **Reportes automáticos** por email después de la migración
- **Documentación OpenAPI** completa (Swagger UI)
- **Suite de pruebas** completa (JUnit 5, Mockito)
- **Configuración flexible** mediante variables de entorno
- **Docker** para despliegue y desarrollo
- **Arquitectura limpia** con separación de responsabilidades

## 📋 Endpoints de la API

### Health Check
- `GET /api/v1/health` - Estado de salud de la API

### Migración
- `POST /api/v1/migrate` - Subir y procesar archivo CSV de transacciones

### Balance
- `GET /api/v1/users/{user_id}/balance` - Obtener balance de usuario
  - Query params: `from`, `to` (opcionales, formato: yyyy-MM-ddTHH:mm:ss)

### Documentación
- `GET /api/v1/docs` - Swagger UI interactivo
- `GET /api/v1/swagger.yaml` - Especificación OpenAPI en YAML
- `GET /api/v1/swagger.json` - Especificación OpenAPI en JSON

## 🛠️ Instalación y Uso

### Prerrequisitos
- Java 17+ (Descargalo [aquí](https://adoptium.net/))
- Maven 3.6+ (Descargalo [aquí](https://maven.apache.org/download.cgi))
- Docker (opcional) (Descargalo [aquí](https://www.docker.com/products/docker-desktop/))

### Instalación local

#### 1. Clonar el repositorio
```bash
git clone <repository-url>
cd java/spring_boot_api_example
```

#### 2. Compilar la aplicación
```bash
mvn clean compile
```

#### 3. Configurar variables de entorno
```bash
# Copiar el archivo de ejemplo
cp env.example .env

# Editar .env con tus configuraciones
nano .env
```

#### 4. Ejecutar la aplicación
```bash
# Desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Producción
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

*** Ahora ya puedes hacer request a la API ***

### 🧪 Testing API endpoints local

#### El server local está configurado para usar el puerto 8080

##### Probar health endpoint
```bash
curl -s http://localhost:8080/api/v1/health
```

##### Probar root endpoint
```bash
curl -s http://localhost:8080/
```

##### Probar migrate endpoint con archivo CSV
```bash
# Asegúrate de colocar la ruta correcta del archivo a cargar
curl -X POST http://localhost:8080/api/v1/migrate -F "csv_file=@examples/sample_transactions.csv"
```

##### Probar balance endpoint
```bash
curl -s "http://localhost:8080/api/v1/users/1001/balance"
```

### Usar el API con Docker

#### 1. Construir y ejecutar contenedor
```bash
docker-compose up --build
```

#### 2. Testing API endpoints en Docker
El contenedor está configurado para usar el puerto 8081

```bash
# Health check
curl -s http://localhost:8081/api/v1/health

# Root endpoint
curl -s http://localhost:8081/

# Migrate endpoint
curl -X POST http://localhost:8081/api/v1/migrate -F "csv_file=@examples/sample_transactions.csv"

# Balance endpoint
curl -s "http://localhost:8081/api/v1/users/1001/balance"
```

## 🧪 Testing

### Ejecutar pruebas unitarias
```bash
mvn test
```

### Ejecutar pruebas con cobertura
```bash
mvn test jacoco:report
```

### Ejecutar pruebas de integración
```bash
mvn verify
```

## 🔧 Variables de Entorno

Ver `env.example` para todas las variables disponibles.

### Principales:
- `APP_ENV` - Entorno (development/production)
- `PORT` - Puerto del servidor (default: 8080)
- `SMTP_HOST` - Servidor SMTP para reportes
- `SMTP_USER` - Usuario SMTP
- `SMTP_PASS` - Contraseña SMTP
- `TO_EMAILS` - Emails destino para reportes

## 📚 Arquitectura

### Estructura del Proyecto
```
src/main/java/com/jps/apiexample/
├── ApiExampleApplication.java          # Aplicación principal
├── config/                           # Configuraciones
│   ├── AppConfig.java
│   ├── EmailConfig.java
│   ├── ReportConfig.java
│   └── MoneySerializer.java
├── model/                            # Modelos de datos
│   ├── UserTransaction.java
│   ├── BalanceInfo.java
│   ├── MigrationReport.java
│   └── ReportChannel.java
├── repository/                       # Capa de datos
│   ├── TransactionRepository.java
│   └── MockTransactionRepository.java
├── service/                         # Lógica de negocio
│   ├── MigrationService.java
│   ├── UsersService.java
│   └── ReportService.java
└── controller/                      # Controladores REST
    ├── MigrationController.java
    ├── BalanceController.java
    ├── HealthController.java
    └── RootController.java
```

### Tecnologías Utilizadas
- **Spring Boot 3.2.0** - Framework principal
- **Spring Web MVC** - Para REST APIs
- **Spring Mail** - Para envío de emails
- **OpenCSV** - Para procesamiento de archivos CSV
- **Jackson** - Para serialización JSON
- **JUnit 5** - Para pruebas unitarias
- **Mockito** - Para mocking en pruebas
- **OpenAPI/Swagger** - Para documentación de API

## 🎯 Próximos Pasos

- [ ] **Base de datos persistente**: Integrar con PostgreSQL/MySQL
- [ ] **Autenticación/Autorización**: Agregar Spring Security
- [ ] **CI/CD**: Integración continua con GitHub Actions
- [ ] **Monitoreo**: Integrar con Micrometer y Prometheus
- [ ] **Caché**: Implementar Redis para mejorar rendimiento
- [ ] **Validación avanzada**: Agregar más validaciones de negocio
- [ ] **Rate Limiting**: Implementar límites de velocidad
- [ ] **CORS**: Configurar para conexiones cross-origin

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.
