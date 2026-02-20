# EduConnect Freelance School

Full-stack application: **Angular 15** frontend and **Java 17** Spring Boot API.

## Default Admin Account (auto-creation on startup)

When the API starts, it **ensures a default admin account exists** (created on first run if missing). Credentials and all other config are **driven from configuration** (see **Configuration** below); defaults in `application.yml` can be overridden via environment variables.

## Names (full name only)

**Users** (`ApplicationUser`) and **students** use a **single FullName field** (no separate first/last name). This is used consistently across:

- **Backend:** Domain entities (`full_name` column), DTOs (`fullName`), and request validators (`@NotBlank` on `fullName` in CreateTeacherRequest, CreateParentRequest, CreateStudentRequest).
- **API:** All create/update endpoints accept and return `fullName`.
- **Frontend:** Forms and grids use `fullName`; labels are “Full name” or “Name” for person names (group class and holiday use “Name” for the entity title, not person).

Do not introduce first name / last name fields; keep a single full name everywhere.

## Class Types

There are **two class types**:

### One-To-One (contract-based)

- **Model:** One teacher, one student. Stored as a **Contract** (`ContractSession`) linked to an optional One-To-One subscription.
- **Flow:** Admin creates the contract (teacher, student, schedule). Teacher runs a 1:1 session from **Sessions**: **Check in**, **Check out**, and optional lesson notes. Duration (hours used) is recorded on the attendance log.
- **Admin UI:** Create form uses **days-of-week checkboxes** (Mon–Sun), like Group class, and **optional Start/End time**. The contracts table includes a **Schedule** column (e.g. *Mon, Wed · 09:00–10:00*).
- **In the system:** Contract + `AttendanceLog` per session (check-in, check-out, hours used, notes).

### Group

- **Model:** One teacher, many students. Admin creates a **Group class** (`GroupClass`); students are **enrolled** via a **One-To-One** or **Group** subscription (or legacy contract).
- **Flow:** Admin creates the group class and enrolls students (subscription or contract). Teacher starts a **Group session** for that class on a date, then **Check in** / **Check out** and lesson notes. **Duration is recorded per student** in `GroupSessionAttendance` (hours used per attendee).
- **Parent visibility:** Parents see their students’ learning overview (assigned teacher, sessions, homework, grades). Parent-facing notifications and alerts (e.g. contract/subscription ending soon) are driven by app config (e.g. `contract-ending-soon-notification-days`, `subscription-expiring-alert-days`).

In the UI, the admin sidebar uses **One-To-One** and **Group**. The technical term **Contract** means an One-To-One class.

**Schedule validation (Group and One-To-One):** Days of week must be 1–7 with no duplicates; end time must be after start time when both are set. The API enforces this on create/update. When admin changes a Group class schedule (days or times), the backend sets `scheduleUpdatedAt`; the teacher API exposes it so the UI can show “Schedule updated on …”. **Enrollment by contract:** When enrolling a student in a group class by contract, the API checks access: for subscription-backed contracts the subscription must be ACTIVE and today within its period; for legacy contracts the legacy end date must not be past.

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

## Zoom for teaching

Each teacher uses **their own Zoom account**. No shared Zoom; teachers set their own join URLs:

- **Profile (One-To-One default)** – In **Profile**, the teacher sets their default **Zoom join URL** used for One-To-One sessions when no session-specific link is set. This is the only profile field teachers can edit.
- **Per Group class** – In **Group classes**, the teacher can set a **Zoom join URL** per class (edit the class and save the Zoom link).

The app shows **“Join Zoom meeting”** in the **Sessions** grid when a session is **in progress** (checked in, not yet checked out) and a Zoom URL is available for that session (from profile for 1:1, or from the group class for group sessions). The link opens in a new tab.

## Check-in / Check-out

- **One-To-One** – Teacher selects a contract session from **Sessions**, then **Check in**, **Check out**, and **Lesson notes**; duration (hours used) is stored on the contract’s attendance log.
- **Group** – Teacher selects a group session (by class and date), then **Check in**, **Check out**, and **Lesson notes**; duration is recorded **per student** in the group session attendances.
- **Admin override** – In **Attendance**, filter by date and use **Override** on any row to set check-in time, check-out time, hours used, and lesson notes. Admin attendance lists and overrides **One-To-One** sessions only; group session attendance is managed from the teacher **Sessions** UI.

## Student Active / Freeze

Admin can **Freeze** or **Activate** a student from the **Students** page. Frozen students can be excluded from active class lists where applicable.

## Roles & Access

- **Admin** – Dashboard, teachers (onboard, edit, verify, reject, activate/suspend), parents & students (create, list; student active/freeze), One-To-One, Group, attendance (with override), subscriptions (monthly create & renew), reports, Settings.
- **Teacher** – Dashboard, availability (weekly), assigned students, sessions (One-To-One and Group check-in/out and notes; “Join Zoom meeting” when session in progress), **month calendar** (1:1 and group, upcoming and completed), group classes (edit name/Zoom/active; students enrolled by One-To-One or Group subscription), homework & grades (homework can only be assigned to students the teacher teaches via 1:1 contract or group enrollment), profile (set default Zoom join URL for 1:1).
- **Parent** – My Students and student learning overview (assigned teacher, sessions, homework, grades); **month calendar per student** (1:1 and group as “Group: &lt;class name&gt;”, DateYmd and holidays); parent notifications/alerts driven by config (e.g. contract or subscription ending soon). No self-registration; admin creates parent and shares credentials.

## Calendars (teacher & parent)

Month calendars use **DateYmd** (`yyyy-MM-dd`) for correct day matching and holiday handling.

- **Teacher calendar** (`/teacher/calendar`) – Shows one-to-one and group sessions for the month:
  - **Upcoming** from contract/group schedule (recurrence by days of week; days that fall on holidays are excluded).
  - **Completed** from attendance logs and group sessions.
- **Parent student calendar** (`/parent/student/:studentId/calendar`) – Same for a single student (1:1 + group). Group sessions are shown as **“Group: &lt;class name&gt;”**. Only sessions for that student are included (for group, only sessions where the student has an attendance record count as completed; upcoming group days come from the student’s enrolled classes). **Parent student overview** (recent sessions and total sessions count) uses the same rule: group sessions count only when the student has a `GroupSessionAttendance` record (i.e. actually attended).
- **Holidays** – Admin-configured holidays (Settings) are excluded from upcoming schedule generation and returned in the calendar API so the UI can highlight or grey out holiday days.

## Notifications (bell and Mark all as read)

Admin, Teacher, and Parent layouts include a **notification bell** in the header/sidebar. Notifications are stored per user. The dropdown lists notifications and includes **“Mark all as read”** for better UX when there are many. **API:** `GET /notifications` returns the current user’s notifications; `POST /notifications/mark-all-read` marks all stored notifications for the current user as read. The `/notifications/**` endpoints are available to any authenticated role.

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
- Login: `POST /auth/login` with `{"email":"admin@educonnect.com","password":"1qaz!QAZ"}`. Response includes **access token** and **refresh token** (refresh token is stored hashed in DB). Use **refresh**: `POST /auth/refresh` with `{"refreshToken":"..."}` to get new access + refresh tokens (rotation: old refresh token is revoked). **Logout**: `POST /auth/logout` with `Authorization: Bearer <access token>` revokes all refresh tokens for that user. The frontend on **401** tries refresh once and retries the request; on refresh failure it logs out and redirects to login.

Configuration is in `src/main/resources/application.yml`: app thresholds, seed-data (default admin), JWT, CORS, etc. **No hardcoded config in code** – all values come from `application.yml` (with env overrides). See **Configuration (dynamic config)** below.

## Frontend (Angular 15 + DevExtreme)

```bash
cd frontend
npm install
npm start
```

- App: **http://localhost:4200**
- **DevExtreme 22.2.4** is used for DataGrid and other widgets (see Admin Dashboard). Theme: `dx.light.css` in `angular.json`.
- Login at `/auth/login` with the default admin credentials above. After login, Admin is redirected to `/admin`.
- **API URL normalization:** All API URLs are built via `getApiUrl()` / `getApiBase()` (`core/services/api-url.ts`) so `environment.apiUrl` + path never produces a double slash.
- **Browser auto-launch:** `ng serve` does not open the browser by default (`angular.json` → serve `open: false`). Open http://localhost:4200 manually if needed.
- **Swagger:** Not included. If you add Swagger/OpenAPI (e.g. springdoc), keep it disabled by default and do not enable browser auto-launch.

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

## Change password

- **POST /auth/change-password** requires **authentication** (Bearer token). Body: `currentPassword`, `newPassword`. Updates the current user’s password and clears `mustChangePassword`. Returns 400 if current password is incorrect, 401 if not authenticated.

## Refresh tokens

- **Login** returns an **access token** and a **refresh token**. The refresh token is stored **hashed** (SHA-256) in the DB (`refresh_tokens` table).
- **401** on an API request: the frontend calls **POST /auth/refresh** with the stored refresh token; on success it stores the new tokens and **retries** the original request. On refresh failure it logs out and redirects to login.
- **Logout**: frontend calls **POST /auth/logout** with the current access token; the backend **revokes all refresh tokens** for that user. Then the frontend clears local storage.
- **Rotation**: each **POST /auth/refresh** returns a new access and a new refresh token; the previous refresh token is **revoked** (one-time use).

## Guards & interceptors (Angular)

- **AuthGuard** – Protects routes; redirects to `/auth/login?returnUrl=...` when not authenticated.
- **RoleGuard** – Use after AuthGuard; allows access if user has one of the route’s `data.roles`. If authenticated but wrong role, redirects to the user’s role home (`/admin`, `/teacher`, `/parent`) with `?unauthorized=1`.
- **JwtInterceptor** – Adds `Authorization: Bearer <token>` to requests to `environment.apiUrl`.
- **ErrorInterceptor** – On 401: logout and redirect to login; on 403: redirect to role home or login. Rethrows so components can still read `err.error` (e.g. `message`, `details`). Typed as `ApiErrorBody` in `core/http/error.interceptor.ts`.

Routes use `canActivate: [AuthGuard, RoleGuard]` and `data: { roles: [Roles.ADMIN] }` (or TEACHER/PARENT).

## Project flow validation

The following flows are implemented and verified:

- **Auth** – Login (`/auth/login`), change-password (requires auth; updates password and clears mustChangePassword); **returnUrl** is set when guards redirect to login and used after successful login to send the user back. **Guards**: AuthGuard (require token, redirect with `returnUrl`), RoleGuard (require one of route’s roles, else redirect to role home or login).
- **401 refresh** – ErrorInterceptor tries **POST /auth/refresh** on 401, then retries the failed request with the new token; on refresh failure, logout and redirect to login.
- **Role-based routing** – `/admin/*`, `/teacher/*`, `/parent/*` protected by AuthGuard + RoleGuard with the corresponding roles.
- **API ↔ frontend IDs** – Contract, student, teacher, parent IDs are passed consistently (e.g. route params, request bodies, DTOs). Payloads match backend DTOs (e.g. CreateContractRequest, StudentDto, TeacherDto, CreateParentRequest).
- **Invalid Parent student route** – For `/parent/student/:studentId`, the frontend **validates** that `studentId` is one of the current parent’s students (from `GET /parent/students`) **before** calling `GET /parent/students/:studentId/overview`. If the ID is not in the list (e.g. `/parent/student/xyz` or another parent’s student), the app shows **“Invalid student”** and does **not** call the overview API.

## Configuration (dynamic config)

All config is externalized; there are **no hardcoded values** in code for URLs, limits, or secrets.

- **Backend** – `application.yml` defines defaults; **environment variables** override them. Examples:
  - **App:** `APP_TIMEZONE`, `APP_PARENT_OVERVIEW_RECENT_MONTHS`, `APP_PARENT_OVERVIEW_RECENT_SESSIONS_MAX`, `APP_PARENT_OVERVIEW_TOTAL_SESSIONS_YEARS`, `APP_REPORT_DEFAULT_MONTHS_BACK`, `API_PUBLIC_URL`, etc.
  - **Seed admin:** `SEED_DATA_ADMIN_EMAIL`, `SEED_DATA_ADMIN_PASSWORD`, `SEED_DATA_ADMIN_FULL_NAME`
  - **JWT:** `JWT_SECRET` (set in production), `JWT_EXPIRATION_MS`, `JWT_REFRESH_EXPIRATION_MS`
  - **CORS:** `CORS_ALLOWED_ORIGINS`
- **Public config endpoint** – `GET /config` (no auth) returns `{ "apiUrl": "<app.api-public-url>" }` so the frontend can use the correct API base URL at runtime.
- **Frontend** – At bootstrap the app calls `GET {environment.apiUrl}/config` and, if present, uses the returned `apiUrl` for all API requests. Build-time `environment.apiUrl` (e.g. in `environment.ts` / `environment.prod.ts`) is only the **initial** URL used to fetch config; after that, the backend-driven URL is used. Same build can therefore target different backends by configuring the initial URL (or by deploying with the backend and using a relative path).

## Build

- **Backend:** `mvn clean package` (or `./mvnw clean package`)
- **Frontend:** `npm run build` (output in `frontend/dist/frontend`)

## Development workflow

After each logical change: update this README if the change affects setup, behaviour, or architecture; then commit and push with a clear, conventional message (e.g. `feat: ...`, `fix: ...`, `docs: ...`).
