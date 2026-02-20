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

## Billing Model (monthly only)

Parent-paid subscriptions drive access. Billing is **monthly only**:

- **Create subscription** – Admin creates a subscription (student, type One-To-One or Group, start date). End date is set to start + 1 month.
- **Renew** – Admin renews via **Subscriptions/Payments**: use “Renew 1 month” or `POST /admin/subscriptions/{id}/renew?additionalMonths=1`.

Admin creates classes and assigns teachers/students; subscriptions define the billing period.

## Teacher Management

- **Onboard** – Admin creates teacher (email, full name, phone, education, bio, specializations). A temporary password is generated and shown once.
- **Edit** – Admin can edit full name, phone, education, bio, and specializations (comma-separated).
- **Reset password** – Admin can reset a teacher’s password from the Teachers list; a new temporary password is shown once and the teacher must change it on next login.
- **Verify / Reject** – Set verification status.
- **Activate / Suspend** – Enable or disable the teacher account.

## Check-in / Check-out

- **Teacher** – From **Sessions**: start a 1:1 session, then **Check in**, **Check out**, and add **Lesson notes** for One-To-One and Group sessions.
- **Admin override** – In **Attendance**, filter by date and use **Override** on any row to set check-in time, check-out time, hours used, and lesson notes.

## Student Active / Freeze

Admin can **Freeze** or **Activate** a student from the **Students** page. Frozen students can be excluded from active class lists where applicable.

## Roles & Access

- **Admin** – Dashboard, teachers (onboard, edit, verify, reject, activate/suspend), parents & students (create, list; student active/freeze), One-To-One, Group, attendance (with override), subscriptions (monthly create & renew), reports, Settings.
- **Teacher** – Dashboard, availability (weekly), assigned students, sessions (1:1 and Group check-in/out and notes), group classes (edit name/Zoom/active; enroll by contract), homework & grades, profile (read-only; Zoom URL for 1:1).
- **Parent** – My Students and student learning overview (assigned teacher, sessions, homework, grades). No self-registration; admin creates parent and shares credentials.

## Time zone (Myanmar, Asia/Yangon UTC+6:30)

- **Backend** – Default time zone is set to `Asia/Yangon` (`app.timezone` in `application.yml`). “Today” and report date ranges use Myanmar date. Date/time values in the API are serialized as **UTC with Z** (ISO-8601) so the client can display them correctly.
- **Frontend** – User-facing dates and times are shown in **+0630** using the shared `myanmarDate` pipe (e.g. `{{ value | myanmarDate:'short' }}`). Use this pipe for any API date/time (check-in, check-out, etc.).

## Admin reset teacher password

From **Teachers** list, use **Reset password** on a teacher. A new temporary password is generated and shown once; the teacher must change it on next login (`mustChangePassword` is set).

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

## Exception handling & API errors (backend)

All API errors return a consistent JSON body via **GlobalExceptionHandler** (`@RestControllerAdvice`):

- **Validation** (400) – `MethodArgumentNotValidException`: `error`, `message`, `details` (field errors), `path`.
- **Bad request** (400) – `IllegalArgumentException`.
- **Conflict** (409) – `IllegalStateException`.
- **Forbidden** (403) – `AccessDeniedException`.
- **Server error** (500) – any unhandled `Exception` (generic message, no stack in body).

Shape: `{ "error": "...", "message": "...", "status": 400, "details": [], "path": "/..." }` (see `ApiErrorResponse`).

## Guards & interceptors (Angular)

- **AuthGuard** – Protects routes; redirects to `/auth/login?returnUrl=...` when not authenticated.
- **RoleGuard** – Use after AuthGuard; allows access if user has one of the route’s `data.roles`. If authenticated but wrong role, redirects to the user’s role home (`/admin`, `/teacher`, `/parent`) with `?unauthorized=1`.
- **JwtInterceptor** – Adds `Authorization: Bearer <token>` to requests to `environment.apiUrl`.
- **ErrorInterceptor** – On 401: logout and redirect to login; on 403: redirect to role home or login. Rethrows so components can still read `err.error` (e.g. `message`, `details`). Typed as `ApiErrorBody` in `core/http/error.interceptor.ts`.

Routes use `canActivate: [AuthGuard, RoleGuard]` and `data: { roles: [Roles.ADMIN] }` (or TEACHER/PARENT).

## Build

- **Backend:** `mvn clean package` (or `./mvnw clean package`)
- **Frontend:** `npm run build` (output in `frontend/dist/frontend`)

## Development workflow

After each logical change: update this README if the change affects setup, behaviour, or architecture; then commit and push with a clear, conventional message (e.g. `feat: ...`, `fix: ...`, `docs: ...`).
