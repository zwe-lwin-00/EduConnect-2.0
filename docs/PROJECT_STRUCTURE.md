# EduConnect – Clean Architecture & Feature-Based Structure

This document describes the **Clean Architecture** layout and **feature-based organization** for both backend and frontend so the codebase stays clean and maintainable.

---

## Backend: Clean Architecture

Dependencies point **inward**: domain has no dependencies; application depends only on domain; infrastructure implements application ports and depends on application + domain.

```
backend/src/main/java/com/educonnect/
├── domain/                    # Layer 1: Entities only (no framework deps)
│   ├── ApplicationUser.java
│   ├── TeacherProfile.java
│   ├── Student.java
│   ├── Subscription.java
│   ├── ContractSession.java
│   ├── ... (all JPA entities)
│   └── ...
│
├── application/               # Layer 2: Use cases & ports (feature-based)
│   ├── auth/
│   │   ├── dto/               # AuthDto (LoginRequest, LoginResponse, UserResponse)
│   │   ├── port/              # LoadUserPort, TokenPort
│   │   └── usecase/           # LoginUseCase, GetCurrentUserUseCase
│   └── admin/
│       ├── dto/               # DashboardDto
│       ├── port/              # DashboardQueryPort, SubscriptionCommandPort
│       └── usecase/           # GetDashboardUseCase, RenewSubscriptionUseCase
│
├── infrastructure/            # Layer 3: Adapters (implement ports)
│   ├── persistence/
│   │   └── adapter/           # UserPersistenceAdapter, DashboardPersistenceAdapter,
│   │                          # SubscriptionPersistenceAdapter (implement application ports)
│   └── (config, security, web live in sibling packages – see below)
│
├── repository/                # Spring Data JPA repositories (used by persistence adapters)
├── config/                    # Infrastructure: AppProperties, JwtProperties, CorsConfig, etc.
├── security/                  # Infrastructure: JwtService (implements TokenPort), filter, SecurityConfig
├── service/                   # Infrastructure: DefaultAdminSeeder (bootstrap)
└── web/                       # Infrastructure: HTTP adapters (controllers), feature-based
    ├── auth/                  # AuthController, AuthMeController
    ├── admin/                 # AdminDashboardController, AdminSubscriptionController
    └── common/                # GlobalExceptionHandler
```

### Flow

- **Controllers** (web) call **use cases** (application). They do not use repositories or domain directly for business logic.
- **Use cases** depend on **ports** (interfaces in application). They do not know about JPA or HTTP.
- **Adapters** (infrastructure) implement ports: persistence adapters use JPA repositories; `JwtService` implements `TokenPort`.

### Feature-based packages

- **auth** – Login, current user (me), change-password. Ports: `LoadUserPort`, `TokenPort`. Use cases: `LoginUseCase`, `GetCurrentUserUseCase`.
- **admin** – Dashboard, subscription renew. Ports: `DashboardQueryPort`, `SubscriptionCommandPort`. Use cases: `GetDashboardUseCase`, `RenewSubscriptionUseCase`.

Adding a new feature (e.g. **teacher**, **parent**): add `application/<feature>/port`, `usecase`, `dto`, then `web/<feature>` controllers and infrastructure adapters that implement the new ports.

---

## Frontend: Feature-based organization

```
frontend/src/app/
├── core/                      # Singleton services & guards (app-wide)
│   ├── auth/                  # AuthService, AuthGuard, RoleGuard
│   └── http/                  # JwtInterceptor
│
├── shared/                    # SharedModule – reuse everywhere
│   ├── constants/
│   │   └── auth.constants.ts  # AuthRoutes, Roles, ROLE_HOME (no magic strings)
│   ├── components/
│   │   ├── placeholder/
│   │   ├── form-field/        # Label + input + error (forms)
│   │   ├── primary-button/    # Submit button with loading state
│   │   ├── brand/             # EduConnect + optional subtitle
│   │   ├── logout-button/     # JWT logout + redirect to login
│   │   └── card/              # Content card wrapper
│   └── shared.module.ts
│
├── features/                  # Feature-based areas
│   ├── auth/
│   │   └── login/             # LoginComponent
│   ├── admin/
│   │   ├── admin-layout/
│   │   └── admin-dashboard/
│   ├── teacher/
│   │   ├── teacher-layout/
│   │   └── teacher-dashboard/
│   └── parent/
│       ├── parent-layout/
│       └── parent-dashboard/
│
├── app.module.ts
├── app-routing.module.ts
└── app.component.*
```

### Conventions

- **core/** – Auth, HTTP, guards. No feature-specific code.
- **shared/** – Components and modules used by multiple features (e.g. placeholder).
- **features/** – One folder per feature (auth, admin, teacher, parent). Each feature contains its layout, dashboard, and future sub-pages. Routes in `app-routing.module.ts` reference components under `features/*`.

### Shared components and auth

- **Always use shared components** for reusable UI: `app-form-field`, `app-primary-button`, `app-brand`, `app-logout-button`, `app-card`, `app-placeholder`. Add new reusable pieces to `shared/components/` and export from `SharedModule`.
- **JWT authentication**: Login and logout are in `AuthService`; `JwtInterceptor` adds the Bearer token to API requests; `AuthGuard` and `RoleGuard` protect routes. After login, **role-based redirect**: Admin → `/admin`, Teacher → `/teacher`, Parent → `/parent` (see `AuthRoutes` and `getRedirectByRole()`).
- **Constants**: Use `AuthRoutes` and `Roles` from `shared/constants/auth.constants.ts` instead of magic strings.

### Adding a new feature

1. Create `features/<name>/` with layout and main pages.
2. Use shared components where applicable (logout button, brand, card, form-field, primary-button).
3. Add route(s) in `app-routing.module.ts` with `canActivate: [AuthGuard, RoleGuard]` and `data: { roles: [Roles.XXX] }` if needed.
4. Declare new components in `AppModule` (or introduce a feature module and lazy-load later).

---

## Summary

| Layer / Area   | Backend                          | Frontend              |
|----------------|----------------------------------|------------------------|
| **Domain/Core**| `domain/` entities               | `core/` services, guards |
| **Application**| `application/<feature>/` use cases + ports + dto | (use cases are in core/feature services) |
| **Infrastructure** | `web/<feature>/`, `config/`, `security/`, `infrastructure.persistence.adapter/` | `features/<feature>/` UI |
| **Shared**     | (common in `web/common/`)        | `shared/`              |

Dependency rule (backend): **domain ← application ← infrastructure**.  
Frontend: **core** and **shared** used by **features**; features do not depend on each other.
