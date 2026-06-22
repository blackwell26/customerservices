# Backend Foundation

Planned baseline for the Spring Boot service:

- Java 21
- Spring Boot
- JWT authentication
- REST APIs under `/api/v1`
- WebSocket chat streaming
- PostgreSQL, MongoDB, Kafka, and Ollama integration

## Proposed Structure

- `src/main/java/`
- `src/main/resources/`
- `src/test/java/`
- `pom.xml`

## Runtime Credentials

Local and dev profiles read account names and passwords from environment variables.
Use [.env.example](./.env.example) as the starting point for local setup.
