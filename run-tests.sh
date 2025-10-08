#!/bin/bash

# Script para ejecutar todas las pruebas del proyecto API Example

echo "🚀 API Example - Test Runner"
echo "=========================="

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para mostrar ayuda
show_help() {
    echo "Uso: $0 [OPCIÓN]"
    echo ""
    echo "Opciones:"
    echo "  unit          Ejecutar solo pruebas unitarias"
    echo "  integration   Ejecutar solo pruebas de integración"
    echo "  load          Ejecutar solo pruebas de carga"
    echo "  performance   Ejecutar solo pruebas de performance"
    echo "  all           Ejecutar todas las pruebas (default)"
    echo "  clean         Limpiar y compilar proyecto"
    echo "  help          Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0 unit        # Solo pruebas unitarias"
    echo "  $0 load        # Solo pruebas de carga"
    echo "  $0 all         # Todas las pruebas"
}

# Función para ejecutar pruebas unitarias
run_unit_tests() {
    echo -e "${BLUE}📋 Ejecutando pruebas unitarias...${NC}"
    mvn test -Dtest="**/*Test" -DexcludedGroups="integration,load,performance"
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Pruebas unitarias completadas exitosamente${NC}"
    else
        echo -e "${RED}❌ Pruebas unitarias fallaron${NC}"
        return 1
    fi
}

# Función para ejecutar pruebas de integración
run_integration_tests() {
    echo -e "${BLUE}🔗 Ejecutando pruebas de integración...${NC}"
    mvn test -Dtest="**/*IntegrationTest"
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Pruebas de integración completadas exitosamente${NC}"
    else
        echo -e "${RED}❌ Pruebas de integración fallaron${NC}"
        return 1
    fi
}

# Función para ejecutar pruebas de carga
run_load_tests() {
    echo -e "${BLUE}⚡ Ejecutando pruebas de carga...${NC}"
    mvn test -Dtest="**/*LoadTest"
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Pruebas de carga completadas exitosamente${NC}"
    else
        echo -e "${RED}❌ Pruebas de carga fallaron${NC}"
        return 1
    fi
}

# Función para ejecutar pruebas de performance
run_performance_tests() {
    echo -e "${BLUE}📊 Ejecutando pruebas de performance...${NC}"
    mvn test -Dtest="**/*PerformanceTest"
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Pruebas de performance completadas exitosamente${NC}"
    else
        echo -e "${RED}❌ Pruebas de performance fallaron${NC}"
        return 1
    fi
}

# Función para ejecutar todas las pruebas
run_all_tests() {
    echo -e "${BLUE}🎯 Ejecutando todas las pruebas...${NC}"
    
    # Compilar proyecto
    echo -e "${YELLOW}🔨 Compilando proyecto...${NC}"
    mvn clean compile test-compile
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Compilación falló${NC}"
        return 1
    fi
    
    # Ejecutar pruebas en orden
    run_unit_tests || return 1
    run_integration_tests || return 1
    run_load_tests || return 1
    run_performance_tests || return 1
    
    echo -e "${GREEN}🎉 Todas las pruebas completadas exitosamente${NC}"
}

# Función para limpiar y compilar
clean_and_compile() {
    echo -e "${YELLOW}🧹 Limpiando y compilando proyecto...${NC}"
    mvn clean compile test-compile
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Compilación exitosa${NC}"
    else
        echo -e "${RED}❌ Compilación falló${NC}"
        return 1
    fi
}

# Función para generar reporte de cobertura
generate_coverage_report() {
    echo -e "${BLUE}📈 Generando reporte de cobertura...${NC}"
    mvn jacoco:report
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Reporte de cobertura generado en target/site/jacoco/index.html${NC}"
    else
        echo -e "${RED}❌ Error generando reporte de cobertura${NC}"
        return 1
    fi
}

# Procesar argumentos
case "${1:-all}" in
    "unit")
        run_unit_tests
        ;;
    "integration")
        run_integration_tests
        ;;
    "load")
        run_load_tests
        ;;
    "performance")
        run_performance_tests
        ;;
    "all")
        run_all_tests
        ;;
    "clean")
        clean_and_compile
        ;;
    "coverage")
        run_all_tests && generate_coverage_report
        ;;
    "help"|"-h"|"--help")
        show_help
        ;;
    *)
        echo -e "${RED}❌ Opción no válida: $1${NC}"
        echo ""
        show_help
        exit 1
        ;;
esac

exit $?
