# MySQL Setup Guide for Subscription Billing System

## Prerequisites

- MySQL 8.0 or higher
- MySQL Command Line Client or MySQL Workbench
- Administrator access

## MySQL Installation

### Windows

1. **Download MySQL Community Server**
   - Visit: https://dev.mysql.com/downloads/mysql/
   - Download Windows MSI installer

2. **Run the installer**
   - Execute `mysql-installer-community-X.X.XX.msi`
   - Follow setup wizard
   - Choose "Developer Default" or "Server only"

3. **Configure MySQL**
   - Port: 3306 (default)
   - MySQL Server Type: Development Machine
   - Authentication Method: MySQL 8.0 compatible authentication

4. **Verify installation**
   ```bash
   mysql --version
   ```

### macOS

Using Homebrew (recommended):

```bash
# Install MySQL
brew install mysql

# Start MySQL service
brew services start mysql

# Verify installation
mysql --version

# Connect to MySQL
mysql -u root
```

Using DMG installer:
1. Download from https://dev.mysql.com/downloads/mysql/
2. Run the DMG file
3. Follow installation instructions
4. Add MySQL to PATH:
   ```bash
   export PATH="/usr/local/mysql/bin:$PATH"
   ```

### Linux (Ubuntu/Debian)

```bash
# Update package list
sudo apt-get update

# Install MySQL Server
sudo apt-get install mysql-server mysql-client

# Run installation script (optional)
sudo mysql_secure_installation

# Start MySQL service
sudo systemctl start mysql

# Verify installation
mysql --version
```

### Linux (CentOS/RHEL)

```bash
# Install MySQL Server
sudo yum install mysql-server mysql

# Start MySQL service
sudo systemctl start mysqld

# Verify installation
mysql --version
```

## MySQL Configuration

### 1. Create Database and User

```bash
# Connect to MySQL as root
mysql -u root -p

# When prompted, enter your MySQL root password
```

```sql
-- Create database
CREATE DATABASE subscription_billing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create application user
CREATE USER 'billing_user'@'localhost' IDENTIFIED BY 'SecurePassword123!';

-- Grant privileges
GRANT ALL PRIVILEGES ON subscription_billing.* TO 'billing_user'@'localhost';

-- Apply privileges
FLUSH PRIVILEGES;

-- Verify
SHOW GRANTS FOR 'billing_user'@'localhost';

-- Exit
EXIT;
```

### 2. Import Database Schema

```bash
# Option 1: Using MySQL command line
mysql -u root -p subscription_billing < schema_mysql.sql

# Option 2: Using specific user
mysql -u billing_user -p subscription_billing < schema_mysql.sql

# Option 3: From MySQL command line
mysql> USE subscription_billing;
mysql> SOURCE schema_mysql.sql;
```

### 3. Verify Database Setup

```bash
# Connect to database
mysql -u billing_user -p subscription_billing

# List tables
SHOW TABLES;

# Check table structure
DESCRIBE users;

# Exit
EXIT;
```

## Spring Boot Configuration

### 1. Update application.properties

For development with MySQL:

```properties
# Database Configuration - MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/subscription_billing?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=billing_user
spring.datasource.password=SecurePassword123!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

### 2. Alternative: Use Profile-Specific Configuration

Keep `application.properties` generic and use `application-mysql.properties`:

```bash
# In application.properties
spring.profiles.active=mysql

# Then run
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"
```

## Database Verification

### Check Tables

```sql
-- Connect to database
mysql -u billing_user -p subscription_billing

-- List all tables
SHOW TABLES;

-- Expected tables:
-- - users
-- - subscription_plans
-- - subscriptions
-- - invoices
-- - audit_logs

-- Check table structure
DESCRIBE users;
DESCRIBE subscription_plans;
DESCRIBE subscriptions;
DESCRIBE invoices;
DESCRIBE audit_logs;

-- Verify indexes
SHOW INDEX FROM users;
SHOW INDEX FROM subscriptions;
SHOW INDEX FROM invoices;

-- Check views
SHOW FULL TABLES WHERE TABLE_TYPE LIKE 'VIEW';

-- View sample data
SELECT * FROM users;
SELECT * FROM subscription_plans;
```

### Connection Test

```bash
# From command line
mysql -u billing_user -p -h localhost subscription_billing -e "SELECT 1 AS connection_test;"

# Expected output:
# connection_test
# 1
```

## Troubleshooting

### Issue 1: "Access denied for user 'root'@'localhost'"

**Cause**: Wrong password or MySQL service not running

**Solution**:
```bash
# Check if MySQL is running
# Windows
net start MySQL80

# macOS
brew services start mysql

# Linux
sudo systemctl start mysql

# Reset root password
mysql -u root
# Then run:
SET PASSWORD FOR 'root'@'localhost' = PASSWORD('new_password');
```

### Issue 2: "Can't connect to MySQL server on 'localhost' (111)"

**Cause**: MySQL service not running

**Solution**:
```bash
# Windows
net start MySQL80

# macOS
brew services start mysql
sudo /usr/local/mysql/support-files/mysql.server start

# Linux
sudo systemctl start mysql
```

### Issue 3: "Error 1045 - Access denied for user 'billing_user'@'localhost'"

**Cause**: Wrong credentials or user doesn't exist

**Solution**:
```bash
# Connect as root
mysql -u root -p

# Verify user exists
SELECT User, Host FROM mysql.user WHERE User='billing_user';

# Recreate user
DROP USER IF EXISTS 'billing_user'@'localhost';
CREATE USER 'billing_user'@'localhost' IDENTIFIED BY 'SecurePassword123!';
GRANT ALL PRIVILEGES ON subscription_billing.* TO 'billing_user'@'localhost';
FLUSH PRIVILEGES;
```

### Issue 4: "java.sql.SQLException: The server time zone value is not recognized"

**Cause**: Timezone configuration issue

**Solution**: Update JDBC URL in application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/subscription_billing?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

### Issue 5: "Table already exists" during schema import

**Cause**: Database already contains tables

**Solution**:
```bash
# Drop and recreate database
mysql -u root -p -e "DROP DATABASE subscription_billing; CREATE DATABASE subscription_billing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Then import schema again
mysql -u root -p subscription_billing < schema_mysql.sql
```

### Issue 6: "Character set 'utf8mb4' is not a valid MySQL character set"

**Cause**: Old MySQL version

**Solution**: Update MySQL to version 5.5.3+
```bash
# Check version
mysql --version

# Upgrade MySQL (varies by OS)
```

## Performance Optimization

### 1. Configure MySQL Variables

```sql
-- Connect to MySQL
mysql -u root -p

-- Check current variables
SHOW VARIABLES LIKE 'max_connections';
SHOW VARIABLES LIKE 'innodb_buffer_pool_size';
SHOW VARIABLES LIKE 'query_cache_size';

-- Update configuration (in my.cnf or my.ini)
[mysqld]
max_connections=1000
innodb_buffer_pool_size=1G
query_cache_size=64M
innodb_log_file_size=512M
```

### 2. Enable Slow Query Log

```sql
-- In my.cnf or my.ini
[mysqld]
slow_query_log=1
slow_query_log_file=/var/log/mysql/slow-query.log
long_query_time=2

-- Or set dynamically
SET GLOBAL slow_query_log='ON';
SET GLOBAL long_query_time=2;
```

### 3. Monitor Performance

```sql
-- Check current connections
SHOW PROCESSLIST;

-- Check table statistics
SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='subscription_billing';

-- Check index usage
SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='subscription_billing';
```

## Backup and Recovery

### Regular Backups

```bash
# Full database backup
mysqldump -u root -p subscription_billing > backup_$(date +%Y%m%d_%H%M%S).sql

# Backup all databases
mysqldump -u root -p --all-databases > all_databases_$(date +%Y%m%d_%H%M%S).sql

# Backup with compression
mysqldump -u root -p subscription_billing | gzip > backup_$(date +%Y%m%d_%H%M%S).sql.gz
```

### Restore from Backup

```bash
# Restore database
mysql -u root -p subscription_billing < backup_20240115_103000.sql

# Restore from compressed backup
gunzip < backup_20240115_103000.sql.gz | mysql -u root -p subscription_billing
```

## Scheduled Backups (Linux/macOS)

Create backup script (`backup.sh`):

```bash
#!/bin/bash
BACKUP_DIR="/home/user/mysql_backups"
DB_NAME="subscription_billing"
DB_USER="root"
DB_PASSWORD="your_password"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

mysqldump -u $DB_USER -p$DB_PASSWORD $DB_NAME | gzip > $BACKUP_DIR/backup_${DATE}.sql.gz

# Keep only last 7 days of backups
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete
```

Make executable and add to crontab:

```bash
chmod +x backup.sh

# Add to crontab (daily at 2 AM)
crontab -e
# Add: 0 2 * * * /path/to/backup.sh
```

## Database Maintenance

### Regular Maintenance Tasks

```sql
-- Optimize all tables
OPTIMIZE TABLE users, subscription_plans, subscriptions, invoices, audit_logs;

-- Repair tables (if needed)
REPAIR TABLE users;

-- Check table integrity
CHECK TABLE users, subscription_plans, subscriptions, invoices, audit_logs;

-- Analyze table statistics
ANALYZE TABLE users, subscription_plans, subscriptions, invoices, audit_logs;
```

## Monitoring Tools

### MySQL Workbench

1. Download: https://dev.mysql.com/downloads/workbench/
2. Install and launch
3. Create new connection:
   - Hostname: localhost
   - Port: 3306
   - Username: billing_user
   - Password: your_password
4. Test connection
5. Browse and manage database visually

### Command Line Tools

```bash
# Interactive monitoring
watch -n 5 'mysql -u root -p -e "SHOW PROCESSLIST;"'

# Database status
mysql -u root -p -e "SHOW STATUS LIKE '%';"

# Variable values
mysql -u root -p -e "SHOW VARIABLES LIKE '%';"
```

## Security Best Practices

### 1. User Privileges

```sql
-- Create limited user for application
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON subscription_billing.* TO 'app_user'@'localhost';
FLUSH PRIVILEGES;

-- Create read-only user for reporting
CREATE USER 'report_user'@'localhost' IDENTIFIED BY 'report_password';
GRANT SELECT ON subscription_billing.* TO 'report_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Password Security

```sql
-- Set password expiration
SET GLOBAL default_password_lifetime=90;

-- Require password change
ALTER USER 'billing_user'@'localhost' PASSWORD EXPIRE;

-- Disable remote root access
DELETE FROM mysql.user WHERE User='root' AND Host != 'localhost';
FLUSH PRIVILEGES;
```

### 3. SSL/TLS Configuration

```bash
# Enable SSL in my.cnf
[mysqld]
ssl-ca=/path/to/ca.pem
ssl-cert=/path/to/server-cert.pem
ssl-key=/path/to/server-key.pem

# Require SSL for users
ALTER USER 'billing_user'@'localhost' REQUIRE SSL;
```

## Connection Pooling with HikariCP

The application uses HikariCP for connection pooling. Configuration in `application.properties`:

```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.auto-commit=true
```

## Monitoring Metrics

### Check Active Connections

```sql
SHOW PROCESSLIST;
```

### View Database Sizes

```sql
SELECT 
    table_schema,
    ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS size_mb
FROM information_schema.tables
WHERE table_schema = 'subscription_billing'
GROUP BY table_schema;
```

### View Table Sizes

```sql
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
FROM information_schema.tables
WHERE table_schema = 'subscription_billing'
ORDER BY (data_length + index_length) DESC;
```

## Production Recommendations

1. **Use separate database server** (not same as application)
2. **Enable binary logging** for replication and recovery
3. **Set up automated backups** (daily or more frequent)
4. **Monitor with tools** (MySQL Enterprise Monitor, Percona)
5. **Use SSL/TLS** for all connections
6. **Implement read replicas** for high availability
7. **Use managed databases** (AWS RDS, Google Cloud SQL)
8. **Regular security updates** and patching
9. **Database activity logging** for audit trails
10. **Performance monitoring** and optimization

## Next Steps

1. Set up MySQL database using the schema
2. Configure Spring Boot connection properties
3. Build and run the application
4. Import Postman collection
5. Test API endpoints
6. Monitor performance and optimize as needed

---

For more information about MySQL, visit: https://dev.mysql.com/doc/
For Spring Boot MySQL integration: https://spring.io/guides/gs/mysql/