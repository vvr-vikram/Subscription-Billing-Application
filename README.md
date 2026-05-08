# Subscription Billing System

An enterprise-level subscription billing system built with Java Spring Boot, featuring comprehensive user management, subscription plans, invoice management, and audit logging.

## Features

- **User Management**: Complete user management with role-based access control (RBAC)
  - ADMIN, MANAGER, and USER roles
  - User status tracking (ACTIVE, INACTIVE, SUSPENDED, PENDING)
  - User search and filtering

- **Subscription Plans**: Create and manage subscription plans
  - Monthly and yearly pricing
  - Plan features and limits (users, projects, storage)
  - Featured plans support
  - Plan status management (ACTIVE, INACTIVE, ARCHIVED)

- **Subscriptions**: Manage customer subscriptions
  - Multiple billing cycles (MONTHLY, YEARLY, QUARTERLY)
  - Subscription status tracking
  - Auto-renewal configuration
  - Plan upgrade/downgrade
  - Subscription suspension and cancellation

- **Invoicing**: Complete billing management
  - Automatic invoice generation
  - Invoice status tracking (DRAFT, ISSUED, OVERDUE, PAID, CANCELLED, REFUNDED)
  - Tax calculation
  - Revenue reporting

- **Audit Logging**: Complete operation tracking
  - User action logging
  - Entity change tracking
  - IP address capture
  - Old and new value comparison

- **Dashboard & Reporting**: Key metrics and analytics
  - Active subscriptions count
  - Revenue statistics
  - Invoice metrics
  - Overdue invoices tracking

- **API Documentation**: Swagger/OpenAPI integration
  - Interactive API exploration
  - Request/response examples
  - Authentication documentation

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Java Version**: Java 17+
- **Database**: PostgreSQL/MySQL
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security with JWT
- **Documentation**: Swagger/OpenAPI 3.0
- **Logging**: SLF4J with Logback
- **Build Tool**: Maven
- **Mapping**: MapStruct

## Project Structure

```
subscription-billing-system/
├── src/main/java/com/billing/subscription/
│   ├── controller/          # REST API controllers
│   ├── service/             # Business logic services
│   ├── repository/          # Data access layer
│   ├── entity/              # JPA entities
│   ├── dto/                 # Data transfer objects
│   ├── exception/           # Custom exceptions
│   ├── config/              # Spring configuration
│   └── SubscriptionBillingApplication.java  # Main class
├── src/main/resources/
│   └── application.properties  # Application configuration
├── pom.xml                  # Maven dependencies
└── schema.sql              # Database schema
```

## Getting Started

### Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.8+

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd subscription-billing-system
   ```

2. **Configure the database (MySQL)**
   - Ensure MySQL 8 is running
   - Execute the schema file:
   ```bash
   mysql -u root -p < schema.sql
   ```
   - This will create the database `subscription_billing_db` and all tables

3. **Update application.properties (if needed)**
   The application is already configured for MySQL with these settings:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/subscription_billing_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=root
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
   ```
   Adjust the password if your MySQL root user has a different password.

4. **Build the application**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Documentation

### Swagger/OpenAPI UI

Access the interactive API documentation at:
```
http://localhost:8080/api/swagger-ui.html
```

### API Base URL

```
http://localhost:8080/api/v1
```

## API Endpoints

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/users` | Create a new user |
| GET | `/users/{id}` | Get user by ID |
| GET | `/users/username/{username}` | Get user by username |
| GET | `/users` | Get all users (paginated) |
| GET | `/users/search?search=keyword` | Search users |
| GET | `/users/role/{role}` | Get users by role |
| GET | `/users/status/{status}` | Get users by status |
| PUT | `/users/{id}` | Update user |
| DELETE | `/users/{id}` | Delete user |
| PATCH | `/users/{id}/status` | Change user status |

### Subscription Plans

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/plans` | Create a new plan |
| GET | `/plans/{id}` | Get plan by ID |
| GET | `/plans` | Get all plans (paginated) |
| GET | `/plans/search?search=keyword` | Search plans |
| GET | `/plans/active` | Get active plans |
| GET | `/plans/featured` | Get featured plans |
| PUT | `/plans/{id}` | Update plan |
| DELETE | `/plans/{id}` | Delete plan |
| PATCH | `/plans/{id}/status` | Change plan status |

### Subscriptions

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/subscriptions` | Create a new subscription |
| GET | `/subscriptions/{id}` | Get subscription by ID |
| GET | `/subscriptions` | Get all subscriptions (paginated) |
| GET | `/subscriptions/search?search=keyword` | Search subscriptions |
| GET | `/subscriptions/user/{userId}` | Get user subscriptions |
| GET | `/subscriptions/status/{status}` | Get subscriptions by status |
| PUT | `/subscriptions/{id}` | Update subscription |
| PATCH | `/subscriptions/{id}/cancel` | Cancel subscription |
| PATCH | `/subscriptions/{id}/suspend` | Suspend subscription |
| PATCH | `/subscriptions/{id}/change-plan?newPlanId=2` | Change plan |
| GET | `/subscriptions/stats/active-count` | Get active count |

### Invoices

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/invoices` | Create a new invoice |
| GET | `/invoices/{id}` | Get invoice by ID |
| GET | `/invoices` | Get all invoices (paginated) |
| GET | `/invoices/search?search=keyword` | Search invoices |
| GET | `/invoices/user/{userId}` | Get user invoices |
| GET | `/invoices/subscription/{subscriptionId}` | Get subscription invoices |
| GET | `/invoices/status/{status}` | Get invoices by status |
| GET | `/invoices/overdue` | Get overdue invoices |
| PUT | `/invoices/{id}` | Update invoice (draft only) |
| PATCH | `/invoices/{id}/mark-as-paid` | Mark as paid |
| PATCH | `/invoices/{id}/mark-as-overdue` | Mark as overdue |
| DELETE | `/invoices/{id}` | Delete invoice |
| GET | `/invoices/stats/revenue?startDate=2024-01-01&endDate=2024-12-31` | Get revenue |
| GET | `/invoices/stats/paid-count` | Get paid invoices count |
| GET | `/invoices/stats/overdue-count` | Get overdue count |

### Dashboard & Reporting

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/dashboard/summary` | Get dashboard summary |
| GET | `/dashboard/revenue` | Get revenue report |
| GET | `/dashboard/subscriptions` | Get subscription metrics |
| GET | `/dashboard/invoices` | Get invoice metrics |

## Request/Response Examples

### Create a User

**Request:**
```json
POST /api/v1/users
Content-Type: application/json

{
  "username": "john.doe",
  "email": "john@example.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "role": "USER"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": 1,
    "username": "john.doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890",
    "role": "USER",
    "status": "ACTIVE",
    "active": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### Create a Subscription Plan

**Request:**
```json
POST /api/v1/plans
Content-Type: application/json

{
  "name": "Professional Plan",
  "description": "For growing businesses",
  "monthlyPrice": 99.99,
  "yearlyPrice": 999.99,
  "maxUsers": 50,
  "maxProjects": 100,
  "storageGB": 1000,
  "featured": true,
  "features": "Advanced features, Priority support, 1TB storage"
}
```

### Create a Subscription

**Request:**
```json
POST /api/v1/subscriptions
Content-Type: application/json

{
  "userId": 1,
  "planId": 2,
  "billingCycle": "MONTHLY",
  "startDate": "2024-01-15",
  "endDate": "2025-01-15",
  "nextBillingDate": "2024-02-15",
  "autoRenew": true
}
```

### Create an Invoice

**Request:**
```json
POST /api/v1/invoices
Content-Type: application/json

{
  "subscriptionId": 1,
  "userId": 1,
  "amount": 99.99,
  "invoiceDate": "2024-01-15",
  "dueDate": "2024-02-15",
  "description": "Monthly subscription for Professional Plan"
}
```

## Pagination

All list endpoints support pagination with the following parameters:

- `page`: Page number (0-based, default: 0)
- `size`: Number of records per page (default: 20)
- `sortBy`: Field to sort by (default: id)
- `direction`: Sort direction - ASC or DESC (default: DESC)

**Example:**
```
GET /api/v1/users?page=0&size=10&sortBy=createdAt&direction=DESC
```

## Error Handling

The API returns standardized error responses:

```json
{
  "success": false,
  "message": "Resource not found",
  "error": {
    "errorCode": "NOT_FOUND",
    "errorType": "ResourceNotFoundException",
    "details": "User not found with ID: 999"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### Error Codes

- `NOT_FOUND` (404): Resource not found
- `CONFLICT` (409): Duplicate resource
- `BAD_REQUEST` (400): Invalid request or validation error
- `INTERNAL_SERVER_ERROR` (500): Server error

## Entity Relationships

### User
- One User can have multiple Subscriptions
- One User can have multiple Invoices
- User has a role (ADMIN, MANAGER, USER)
- User has a status (ACTIVE, INACTIVE, SUSPENDED, PENDING)

### Subscription Plan
- One Plan can be subscribed by multiple Users
- Plan has pricing for monthly and yearly billing

### Subscription
- Belongs to one User
- Belongs to one SubscriptionPlan
- Can have multiple Invoices
- Has status (ACTIVE, INACTIVE, SUSPENDED, CANCELLED, EXPIRED, PENDING)

### Invoice
- Belongs to one Subscription
- Belongs to one User
- Has status (DRAFT, ISSUED, OVERDUE, PAID, CANCELLED, REFUNDED)

## Audit Logging

All CRUD operations are automatically logged in the `audit_logs` table with:
- Entity type and ID
- Action performed (CREATE, UPDATE, DELETE)
- User who performed the action
- Old and new values
- IP address
- Timestamp

## Database Connection Pool

The application uses HikariCP with the following configuration:
- Maximum pool size: 20
- Minimum idle: 5
- Idle timeout: 600000ms (10 minutes)

## Logging Configuration

Logs are configured with:
- Console output for development
- File output to `logs/application.log`
- Rotating file appender (max size: 10MB, max history: 10 files)

### Log Levels

- `com.billing.subscription`: DEBUG
- `org.springframework.web`: INFO
- `org.hibernate.SQL`: DEBUG
- Root level: INFO

## Configuration Files

### application.properties

Key configurations:
```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/subscription_billing
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
```

## Building and Deployment

### Build

```bash
mvn clean install
```

### Run Tests

```bash
mvn test
```

### Package for Production

```bash
mvn clean package -DskipTests
```

### Docker Deployment

Create a `Dockerfile`:

```dockerfile
FROM openjdk:17-slim
COPY target/subscription-billing-system-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build and run:

```bash
docker build -t subscription-billing:1.0.0 .
docker run -p 8080:8080 subscription-billing:1.0.0
```

## Performance Considerations

1. **Database Indexes**: Created on frequently queried columns
2. **Pagination**: All list endpoints support pagination for large datasets
3. **Connection Pooling**: HikariCP with optimized settings
4. **Lazy Loading**: JPA relationships configured with LAZY loading
5. **Caching**: Can be added using Spring Cache abstraction

## Security Considerations

1. **Password Encoding**: Passwords are encoded using Spring Security
2. **Input Validation**: All inputs are validated using Jakarta validation
3. **SQL Injection**: Protected by parameterized queries (JPA)
4. **CORS**: Can be configured as needed
5. **JWT Token**: Implement JWT for API authentication

## Future Enhancements

1. Email notifications for invoice reminders
2. Payment gateway integration
3. Subscription renewal automation
4. Advanced reporting and analytics
5. Multi-tenancy support
6. API rate limiting
7. Webhook support for external integrations
8. CSV/PDF invoice export

## Troubleshooting

### Database Connection Issues

```properties
# Verify connection settings in application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/subscription_billing
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### Port Already in Use

```bash
# Change port in application.properties
server.port=8081
```

### Hibernate Mapping Issues

```properties
# Check mapping configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Contributing

1. Follow the existing code structure and naming conventions
2. Write tests for new features
3. Update documentation for API changes
4. Ensure all tests pass before submitting

## License

This project is licensed under the Apache License 2.0.

## Support

For issues, questions, or suggestions, please contact support@billing.com

## Version

Current Version: 1.0.0
Last Updated: January 2024
