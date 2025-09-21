#Dev Setup & Guide

- Free ports: `5432`, `8080`, `5173`

---

##  1. First-Time Setup

### Clone the repo
```bash
git clone git@github.com:starplatinummaster/FixedDepositSystem.git
cd FixedDepositSystem
```

### Build and start everything
```bash
docker compose up --build
```
This will:
- Start Postgres (db) - PORT: 5432
- Run Flyway migrations
- Start backend → http://localhost:8080
- Start frontend → http://localhost:5173
- 

## 2. Development Workflow

### Frontend Changes

- Files are mounted into the container (volumes), so you don't need to rebuild for .vue, .js, .ts, .css changes.
- Expected delay: 5-10 seconds for browser hot reload, depending on file size.

- #### If browser does not auto-refresh:
```shell
docker compose restart frontend
```

### Backend Changes
- Java code (src/main/java) is also mounted; Spring Boot DevTools will reload.
- Expected delay: 5--10 seconds for backend restart.

- #### If changes don't apply:
```shell
docker compose restart backend
```


## 3. Running Tests

- Frontend Tests
```shell
docker compose exec frontend npm test
```

- Run a single test file:
```shell
docker compose exec frontend npm test path/to/file.test.js
```

- Backend Tests
```shell
docker compose exec backend mvn test
```

- Run a specific test:
```shell
docker compose exec backend mvn -Dtest=MyTestClass test
```


## 4. Common Issues & Fixes

### Error: npm ci fails
- You need a package-lock.json in frontend/. Generate it locally:
```shell
cd frontend
npm install
```
- Commit the generated package-lock.json.

### Changes not appearing in browser
- Check if container sees your file changes:
- docker compose exec frontend ls -l /app/src
- If not updating → your editor may not be writing directly to disk (fix editor sync settings).

#### Last resort:
```shell
docker compose restart frontend
```

#### Backend changes not picked up
- If using Lombok → rebuild with:
```shell
docker compose exec backend mvn clean install
docker compose restart backend
```


## 5. Reset Everything

### Wipe containers, volumes, and networks:
```shell
docker compose down -v --remove-orphans
```

### Then rebuild:
```shell
docker compose up --build
```


## 6. Quick Commands Cheat Sheet

| Action                           | Command                                                                |
|----------------------------------|------------------------------------------------------------------------|
| Start app (first time)           | `docker compose up --build`                                            |
| Start app (after first time)     | `docker compose up`                                                    |
| Stop app                         | `docker compose down`                                                  |
| Restart frontend only            | `docker compose restart frontend`                                      |
| Restart backend only             | `docker compose restart backend`                                       |
| Run frontend tests               | `docker compose exec frontend npm test`                                |
| Run backend tests                | `docker compose exec backend mvn test`                                 |
| Run backend specific class tests | `docker compose exec backend mvn -Dtest=YourTestClassName test`        |
| Wipe everything & rebuild        | `docker compose down -v --remove-orphans && docker compose up --build` |
