-- Script para migrar datos desde el repositorio mock a MySQL
-- Este script puede ser ejecutado manualmente para migrar datos existentes

-- Verificar que la tabla existe
SELECT 'Checking if user_transactions table exists...' as status;
SHOW TABLES LIKE 'user_transactions';

-- Mostrar datos actuales
SELECT 'Current data in user_transactions table:' as status;
SELECT COUNT(*) as total_records FROM user_transactions;

-- Ejemplo de consultas útiles para verificar la migración
SELECT 'Sample data verification:' as status;
SELECT 
    user_id,
    COUNT(*) as transaction_count,
    SUM(amount) as total_amount,
    MIN(date_time) as first_transaction,
    MAX(date_time) as last_transaction
FROM user_transactions 
GROUP BY user_id 
ORDER BY user_id;

-- Verificar índices
SELECT 'Checking indexes:' as status;
SHOW INDEX FROM user_transactions;
