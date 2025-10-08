#!/bin/bash

# Script para ejecutar tests con diferentes configuraciones de repositorio
# Uso: ./run-tests-with-db.sh [mock|persistent|integration]

set -e

REPO_TYPE=${1:-mock}

echo "Running tests with repository type: $REPO_TYPE"

case $REPO_TYPE in
    "mock")
        echo "Running tests with MockTransactionRepository..."
        mvn test -Dspring.profiles.active=test -DTEST_REPOSITORY_TYPE=mock
        ;;
    "persistent")
        echo "Running tests with JpaTransactionRepository (H2)..."
        mvn test -Dspring.profiles.active=test -DTEST_REPOSITORY_TYPE=jpa
        ;;
    "integration")
        echo "Running integration tests with real database..."
        # Start MySQL container
        docker-compose up -d mysql
        sleep 30
        
        # Run tests
        mvn test -Dspring.profiles.active=test -DTEST_REPOSITORY_TYPE=jpa \
            -Dspring.datasource.url=jdbc:mysql://localhost:3306/api_example_test \
            -Dspring.datasource.username=api_user \
            -Dspring.datasource.password=api_password
        
        # Stop MySQL container
        docker-compose down
        ;;
    *)
        echo "Invalid repository type. Use: mock, persistent, or integration"
        exit 1
        ;;
esac

echo "Tests completed successfully!"
