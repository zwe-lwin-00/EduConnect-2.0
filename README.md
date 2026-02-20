# EduConnect Freelance School

Full-stack application: **Angular 15** frontend and **Java 17** Spring Boot API.

## Default Admin Account (auto-creation on startup)

When the API starts, it **ensures a default admin account exists** (created on first run if missing):

- **Email:** `admin@educonnect.com`
- **Password:** `1qaz!QAZ`

Check the API console on every startup:  
`EduConnect startup: default admin check (admin@educonnect.com). Created on first run if missing.`  
On first run you’ll see: `Default admin account CREATED. Login at /auth/login with: admin@educonnect.com ...`

## Class Types

| Type       | Description              | In the system                    |
|-----------|---------------------------|----------------------------------|
| One-To-One| One teacher, one student  | Stored as Contract (ContractSession) |
| Group     | One teacher, many students| GroupClass + enrollments         |

In the UI, the admin sidebar uses **One-To-One** and **Group**. The technical term "Contract" means an One-To-One class.

## Billing Model

Parent-paid subscriptions drive access. Admin creates subscriptions (student, type, duration), then creates classes and assigns teachers/students. Subscriptions have a period (start–end). Renew via Subscriptions/Payments or `POST /admin/subscriptions/{id}/renew?additionalMonths=1`.

## Roles & Access

- **Admin** – Full control: dashboard, teachers, parents, students, One-To-One, Group, attendance, payments, reports, Settings.
- **Teacher** – Dashboard, availability, students, sessions (1:1 and Group), group classes (Zoom, enrollments), homework & grades, profile.
- **Parent** – My Students and student learning overview (read-only). No self-registration; admin creates parent and shares credentials.

## Tech stack

- **Frontend:** Angular 15, **DevExtreme 22.2.4** (`devextreme`, `devextreme-angular`) for grids, forms, and UI components.
- **Backend:** Java 17, Spring Boot 3.2.
- **Database:** **SQL Server** (default). Optional H2 in-memory for local dev (profile `dev`).

## Prerequisites

- **Java 17**
- **Node.js 18+** and npm (for Angular 15)
- **Maven 3.8+** (optional if you use the Maven Wrapper)
- **SQL Server** (for default run), or use profile `dev` for H2

## Backend (Java 17)

**SQL Server (default)**  
Create a database named `educonnect`. Configure URL, username, and password via environment or `application.yml`:

- `SPRING_DATASOURCE_URL` (default: `jdbc:sqlserver://localhost:1433;databaseName=educonnect;encrypt=true;trustServerCertificate=true`)
- `SPRING_DATASOURCE_USERNAME` (default: `sa`)
- `SPRING_DATASOURCE_PASSWORD`

**H2 (development only)**  
Run with profile `dev` so the app uses in-memory H2 and no SQL Server:

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Otherwise (SQL Server):

```bash
cd backend
mvn spring-boot:run
```

- API: **http://localhost:8080**
- H2 console (when using `dev` profile): http://localhost:8080/h2-console
- Login: `POST /auth/login` with `{"email":"admin@educonnect.com","password":"1qaz!QAZ"}`

Configuration is in `src/main/resources/application.yml`: app thresholds, seed-data (default admin), JWT, CORS, etc.

## Frontend (Angular 15 + DevExtreme)

```bash
cd frontend
npm install
npm start
```

- App: **http://localhost:4200**
- **DevExtreme 22.2.4** is used for DataGrid and other widgets (see Admin Dashboard). Theme: `dx.light.css` in `angular.json`.
- Login at `/auth/login` with the default admin credentials above. After login, Admin is redirected to `/admin`.

## Project Structure

The project follows **Clean Architecture** (backend) and **feature-based organization** (backend & frontend).

- **backend** – **Domain** (`domain/`), **application** (`application/<feature>/` with ports, use cases, DTOs), **infrastructure** (`web/<feature>/`, `config/`, `security/`, `infrastructure.persistence.adapter/`). SQL Server (default) or H2 (profile `dev`).
- **frontend** – **core/** (auth, HTTP), **shared/** (SharedModule), **features/** (`auth`, `admin`, `teacher`, `parent`). Routes: `/auth/login`, `/admin/*`, `/teacher/*`, `/parent/*`.

For the **full layout and dependency rules**, see **[docs/PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md)**.

## Build

- **Backend:** `mvn clean package` (or `./mvnw clean package`)
- **Frontend:** `npm run build` (output in `frontend/dist/frontend`)
