# Docker Commands Documentation - ZetaHorizon FD System

## Docker Compose Overview
The project uses Docker Compose to orchestrate multiple containers:
- **db**: PostgreSQL database
- **flyway**: Database migration tool
- **backend**: Spring Boot application
- **frontend**: Vue.js application

## Basic Docker Compose Commands

### Start/Stop Application
```bash
# Start all services (first time - builds images)
docker compose up --build

# Start all services (subsequent runs)
docker compose up

# Start in background (detached mode)
docker compose up -d

# Stop all services
docker compose down

# Stop and remove volumes (clears database)
docker compose down -v

# Stop and remove everything including orphaned containers
docker compose down -v --remove-orphans
```

### Individual Service Management
```bash
# Start specific service
docker compose up backend
docker compose up frontend
docker compose up db

# Restart specific service
docker compose restart backend
docker compose restart frontend
docker compose restart db

# Stop specific service
docker compose stop backend
docker compose stop frontend
```

### View Service Status
```bash
# Check running containers
docker compose ps

# View service logs
docker compose logs
docker compose logs backend
docker compose logs frontend
docker compose logs db

# Follow logs in real-time
docker compose logs -f
docker compose logs -f backend
```

## Development Workflow Commands

### Backend Development
```bash
# Restart backend after code changes
docker compose restart backend

# View backend logs
docker compose logs -f backend

# Execute commands in backend container
docker compose exec backend bash
docker compose exec backend mvn clean compile
docker compose exec backend mvn test

# Run specific test class
docker compose exec backend mvn test -Dtest=SupportTicketServiceTest
docker compose exec backend mvn test -Dtest=SupportTicketControllerTest
```

### Frontend Development
```bash
# Restart frontend (if hot reload not working)
docker compose restart frontend

# View frontend logs
docker compose logs -f frontend

# Execute commands in frontend container
docker compose exec frontend bash
docker compose exec frontend npm install
docker compose exec frontend npm run build

# Check if files are mounted correctly
docker compose exec frontend ls -la /app/src
```

### Database Operations
```bash
# Connect to database
docker compose exec db psql -U fd_user -d fd_db

# Run SQL file
docker compose exec -T db psql -U fd_user -d fd_db < script.sql

# Backup database
docker compose exec db pg_dump -U fd_user fd_db > backup.sql

# Check database logs
docker compose logs db
```

## Testing Commands

### Backend Tests
```bash
# Run all tests
docker compose exec backend mvn test

# Run tests with verbose output
docker compose exec backend mvn test -X

# Run specific test class
docker compose exec backend mvn test -Dtest=SupportTicketServiceTest

# Run specific test method
docker compose exec backend mvn test -Dtest=SupportTicketServiceTest#testCreateTicket

# Run tests and generate reports
docker compose exec backend mvn test jacoco:report

# Skip tests during build
docker compose exec backend mvn clean install -DskipTests
```

### Frontend Tests (if implemented)
```bash
# Run frontend tests
docker compose exec frontend npm test

# Run tests in watch mode
docker compose exec frontend npm run test:watch

# Run tests with coverage
docker compose exec frontend npm run test:coverage
```

### Integration Testing
```bash
# Start services for testing
docker compose up -d db backend

# Wait for services to be ready
sleep 10

# Run integration tests
docker compose exec backend mvn test -Dtest=*IntegrationTest

# Test API endpoints
curl -X GET http://localhost:8080/api/support/user/123e4567-e89b-12d3-a456-426614174000
```

## Container Management

### Image Operations
```bash
# Build images without cache
docker compose build --no-cache

# Build specific service
docker compose build backend
docker compose build frontend

# Pull latest base images
docker compose pull

# List images
docker images | grep zetahorizon
```

### Container Inspection
```bash
# Inspect container details
docker compose exec backend env
docker compose exec frontend env

# Check container resource usage
docker stats

# View container processes
docker compose exec backend ps aux
docker compose exec frontend ps aux
```

### Volume Management
```bash
# List volumes
docker volume ls

# Inspect volume
docker volume inspect zetahorizon_postgres_data

# Remove volumes (WARNING: deletes data)
docker volume rm zetahorizon_postgres_data

# Backup volume
docker run --rm -v zetahorizon_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz -C /data .
```

## Debugging Commands

### Container Debugging
```bash
# Access container shell
docker compose exec backend bash
docker compose exec frontend sh
docker compose exec db bash

# Check container health
docker compose exec backend curl http://localhost:8080/actuator/health
docker compose exec frontend curl http://localhost:5173

# View container filesystem
docker compose exec backend ls -la /app
docker compose exec frontend ls -la /app
```

### Network Debugging
```bash
# List networks
docker network ls

# Inspect network
docker network inspect zetahorizon_default

# Test connectivity between containers
docker compose exec frontend ping backend
docker compose exec backend ping db

# Check port bindings
docker compose port backend 8080
docker compose port frontend 5173
docker compose port db 5432
```

### Log Analysis
```bash
# View logs with timestamps
docker compose logs -t backend

# View last N lines of logs
docker compose logs --tail=50 backend

# Search logs for errors
docker compose logs backend | grep ERROR
docker compose logs backend | grep Exception

# Export logs to file
docker compose logs backend > backend.log
```

## Performance Monitoring

### Resource Usage
```bash
# Monitor resource usage
docker stats

# Monitor specific container
docker stats zetahorizon-backend-1

# View container processes
docker compose exec backend top
docker compose exec backend htop
```

### Database Performance
```bash
# Check database connections
docker compose exec db psql -U fd_user -d fd_db -c "SELECT * FROM pg_stat_activity;"

# Check table sizes
docker compose exec db psql -U fd_user -d fd_db -c "SELECT schemaname,tablename,attname,n_distinct,correlation FROM pg_stats WHERE tablename = 'support_tickets';"

# Monitor database queries
docker compose logs db | grep "LOG:"
```

## Production Deployment Commands

### Build for Production
```bash
# Build production images
docker compose -f docker-compose.prod.yml build

# Start production services
docker compose -f docker-compose.prod.yml up -d

# Scale services
docker compose -f docker-compose.prod.yml up -d --scale backend=3
```

### Health Checks
```bash
# Check service health
curl -f http://localhost:8080/actuator/health || exit 1
curl -f http://localhost:5173 || exit 1

# Automated health check script
#!/bin/bash
services=("backend:8080" "frontend:5173")
for service in "${services[@]}"; do
    IFS=':' read -r name port <<< "$service"
    if curl -f "http://localhost:$port" > /dev/null 2>&1; then
        echo "$name is healthy"
    else
        echo "$name is unhealthy"
        exit 1
    fi
done
```

## Cleanup Commands

### Remove Everything
```bash
# Stop and remove containers, networks, volumes
docker compose down -v --remove-orphans

# Remove all unused Docker resources
docker system prune -a --volumes

# Remove specific images
docker rmi zetahorizon-backend zetahorizon-frontend
```

### Selective Cleanup
```bash
# Remove only containers
docker compose down

# Remove unused images
docker image prune

# Remove unused volumes
docker volume prune

# Remove unused networks
docker network prune
```

## Troubleshooting Commands

### Common Issues

#### Port Already in Use
```bash
# Find process using port
lsof -i :8080
lsof -i :5173
lsof -i :5432

# Kill process
kill -9 <PID>

# Use different ports
docker compose up --build -p 8081:8080
```

#### Container Won't Start
```bash
# Check container logs
docker compose logs backend

# Check container exit code
docker compose ps

# Start container in interactive mode
docker compose run --rm backend bash
```

#### Database Connection Issues
```bash
# Check if database is ready
docker compose exec db pg_isready -U fd_user -d fd_db

# Test connection
docker compose exec backend nc -zv db 5432

# Check database logs
docker compose logs db | tail -20
```

#### File Permission Issues
```bash
# Fix file permissions (macOS/Linux)
sudo chown -R $USER:$USER .

# Check mounted volumes
docker compose exec backend ls -la /app
docker compose exec frontend ls -la /app
```

### Emergency Recovery
```bash
# Complete reset (WARNING: loses all data)
docker compose down -v --remove-orphans
docker system prune -a --volumes
docker compose up --build

# Backup before reset
docker compose exec db pg_dump -U fd_user fd_db > emergency_backup.sql
```

## Automation Scripts

### Development Script
```bash
#!/bin/bash
# dev.sh - Development startup script

echo "Starting ZetaHorizon development environment..."

# Stop any running containers
docker compose down

# Start services
docker compose up --build -d

# Wait for services
echo "Waiting for services to start..."
sleep 30

# Check health
curl -f http://localhost:8080/actuator/health && echo "Backend: OK"
curl -f http://localhost:5173 && echo "Frontend: OK"

echo "Development environment ready!"
echo "Frontend: http://localhost:5173"
echo "Backend: http://localhost:8080"
```

### Test Script
```bash
#!/bin/bash
# test.sh - Run all tests

echo "Running backend tests..."
docker compose exec backend mvn test

echo "Testing API endpoints..."
curl -X POST "http://localhost:8080/api/support?userId=123e4567-e89b-12d3-a456-426614174000" \
  -H "Content-Type: application/json" \
  -d '{"subject": "Test", "description": "Test description"}'

echo "All tests completed!"
```

## Docker Compose File Structure

### Services Configuration
```yaml
version: '3.8'
services:
  db:
    image: postgres:16
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: fd_db
      POSTGRES_USER: fd_user
      POSTGRES_PASSWORD: fd_password
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - db
    volumes:
      - ./backend:/app
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/fd_db

  frontend:
    build: ./frontend
    ports:
      - "5173:5173"
    volumes:
      - ./frontend:/app
      - /app/node_modules
    environment:
      VITE_API_URL: http://localhost:8080

volumes:
  postgres_data:
```

This comprehensive Docker command reference covers all aspects of container management, testing, debugging, and deployment for the ZetaHorizon FD System.