# Banking Management System

Console-based Banking Management System built with Java, MySQL, JDBC, and OOP principles.

## Features

- Secure user registration and login with hashed passwords
- PIN-based verification for sensitive banking operations
- Account creation with unique account numbers
- Deposit, withdrawal, transfer, and balance inquiry
- SQL transaction handling with commit/rollback for reliable data integrity
- Modular layered design using models, DAOs, services, and utilities

## Tech Stack

- Java 17+
- Maven
- MySQL 8+
- JDBC

## Project Structure

```text
src/main/java/com/bms
  |- config
  |- dao
  |- exception
  |- model
  |- service
  |- util
  |- Main.java
database/schema.sql
```

## Database Setup

The application now creates the database and required tables automatically on startup.

If you want to override the defaults, set these environment variables:

```powershell
$env:BMS_DB_SERVER_URL="jdbc:mysql://localhost:3306"
$env:BMS_DB_NAME="banking_management_system"
$env:BMS_DB_USERNAME="root"
$env:BMS_DB_PASSWORD="your_password"
```

Defaults are:

- Server URL: `jdbc:mysql://localhost:3306`
- Database: `banking_management_system`
- Username: `root`
- Password: `Rudresh@2005`

## Run

```powershell
mvn "-Dmaven.repo.local=.m2repo" compile
mvn "-Dmaven.repo.local=.m2repo" exec:java
```

## Notes

- Passwords and PINs are stored as SHA-256 hashes.
- Monetary values use `BigDecimal`.
- Transfers, withdrawals, and deposits are recorded in the `transactions` table.
- Banking operations use database transactions to prevent partial updates.
- The app must connect to a MySQL server where the supplied user can create databases and tables.
