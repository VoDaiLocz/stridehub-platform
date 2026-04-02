# StrideHub Backend

Spring Boot workspace for the StrideHub marketplace platform.

## Stack

- Java 21
- Spring Boot 4
- PostgreSQL
- Redis
- Flyway
- Spring Security

## Local Development

1. Start infrastructure from the repository root:

   ```powershell
   docker compose up -d
   ```

2. Run the API:

   ```powershell
   .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
   ```

## Verification

```powershell
.\mvnw.cmd test
.\mvnw.cmd -q -DskipTests compile
```
