# Infrastructure Foundation

Planned deployment and local development assets:

- Dockerfiles for backend and frontend
- Docker Compose for local development
- Kubernetes or OpenShift deployment manifests
- Secrets, resource limits, and runtime configuration

## Local Compose Setup

- Copy `infrastructure/compose/.env.example` to `infrastructure/compose/.env`
- Set `POSTGRES_PASSWORD` in the `.env` file before starting Compose
