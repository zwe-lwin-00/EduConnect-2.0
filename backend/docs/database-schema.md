# EduConnect – Database schema and relationships

This document describes the main tables and entity relationships used by the EduConnect API (JPA/Hibernate with SQL Server or H2).

---

## Core identity and roles

### application_users

| Column          | Type         | Notes                          |
|-----------------|--------------|--------------------------------|
| id              | UUID (PK)    |                                |
| email           | VARCHAR      | NOT NULL, UNIQUE               |
| password_hash   | VARCHAR      | NOT NULL                       |
| full_name       | VARCHAR      | NOT NULL                       |
| phone           | VARCHAR      |                                |
| must_change_pwd | BOOLEAN      | NOT NULL, default true         |
| active          | BOOLEAN      | NOT NULL, default true         |
| created_at      | TIMESTAMP    | NOT NULL                       |
| updated_at      | TIMESTAMP    |                                |

- **user_roles** (element collection): `user_id` → `role` (e.g. ADMIN, TEACHER, PARENT). Table `user_roles(user_id, role)`.

**Relationships:**  
- Referenced by: `teacher_profiles.user_id`, `refresh_tokens.user_id`, `students.parent_id` (parent is an ApplicationUser).

---

### teacher_profiles

| Column               | Type         | Notes                          |
|----------------------|--------------|--------------------------------|
| id                   | UUID (PK)    |                                |
| user_id              | UUID (FK)    | NOT NULL, UNIQUE → application_users |
| nrc_encrypted        | VARCHAR      |                                |
| education            | VARCHAR      |                                |
| bio                  | VARCHAR(2000)|                                |
| verification_status  | VARCHAR      | NOT NULL (PENDING, VERIFIED, REJECTED) |
| hourly_rate          | DOUBLE       |                                |
| zoom_join_url        | VARCHAR      |                                |

- **teacher_specializations** (element collection): `teacher_id` → `specialization`. Table `teacher_specializations(teacher_id, specialization)`.

**Relationships:**  
- One-to-one with `application_users`.  
- Referenced by: `group_classes.teacher_id`, `contract_sessions.teacher_id`, `homework.teacher_id`, `student_grades.teacher_id`, `teacher_availabilities.teacher_id`.

---

## Students and parents

### students

| Column     | Type         | Notes                          |
|------------|--------------|--------------------------------|
| id         | UUID (PK)    |                                |
| full_name  | VARCHAR      | NOT NULL                       |
| grade      | VARCHAR      | NOT NULL (P1–P4)               |
| date_of_birth | DATE      |                                |
| parent_id  | UUID (FK)    | NOT NULL → application_users   |
| active     | BOOLEAN      | default true                   |
| created_at | TIMESTAMP    | NOT NULL                       |
| updated_at | TIMESTAMP    |                                |

**Relationships:**  
- Many-to-one **parent** → `application_users`.  
- Referenced by: `subscriptions.student_id`, `contract_sessions.student_id`, `group_class_enrollments.student_id`, `group_session_attendances.student_id`, `homework.student_id`, `student_grades.student_id`.

---

## Subscriptions and contracts

### subscriptions

| Column      | Type         | Notes                          |
|-------------|--------------|--------------------------------|
| id          | UUID (PK)    |                                |
| student_id  | UUID (FK)    | NOT NULL → students            |
| type        | VARCHAR      | NOT NULL (ONE_TO_ONE, GROUP)    |
| start_date  | DATE         | NOT NULL                       |
| end_date    | DATE         | NOT NULL                       |
| status      | VARCHAR      | NOT NULL (ACTIVE, EXPIRED, CANCELLED) |
| created_at  | TIMESTAMP    | NOT NULL                       |
| updated_at  | TIMESTAMP    |                                |

**Relationships:**  
- Many-to-one **student** → `students`.  
- Referenced by: `contract_sessions.subscription_id`, `group_class_enrollments.subscription_id`.

---

### contract_sessions

| Column              | Type         | Notes                          |
|---------------------|--------------|--------------------------------|
| id                  | UUID (PK)    |                                |
| teacher_id          | UUID (FK)    | NOT NULL → teacher_profiles     |
| student_id          | UUID (FK)    | NOT NULL → students            |
| subscription_id    | UUID (FK)    | optional → subscriptions       |
| legacy_period_end   | DATE         | when no subscription           |
| schedule_start_time | TIME         |                                |
| schedule_end_time   | TIME         |                                |
| status              | VARCHAR      | NOT NULL (ACTIVE, CANCELLED, ENDED) |
| created_at          | TIMESTAMP    | NOT NULL                       |
| updated_at          | TIMESTAMP    |                                |

- **contract_schedule_days** (element collection): `contract_id` → `day_of_week` (1–7). Table `contract_schedule_days(contract_id, day_of_week)`.

**Relationships:**  
- Many-to-one **teacher** → `teacher_profiles`, **student** → `students`, **subscription** → `subscriptions` (optional).  
- Referenced by: `attendance_logs.contract_id`, `group_class_enrollments.contract_id` (optional).

---

## Group classes and sessions

### group_classes

| Column               | Type         | Notes                          |
|----------------------|--------------|--------------------------------|
| id                   | UUID (PK)    |                                |
| name                 | VARCHAR      | NOT NULL                       |
| teacher_id           | UUID (FK)    | NOT NULL → teacher_profiles    |
| zoom_join_url        | VARCHAR      |                                |
| active               | BOOLEAN      | NOT NULL, default true         |
| schedule_start_time  | TIME         |                                |
| schedule_end_time    | TIME         |                                |
| schedule_updated_at  | TIMESTAMP    | set when admin changes schedule; teacher can show “Schedule updated on …” |
| created_at           | TIMESTAMP    | NOT NULL                       |
| updated_at           | TIMESTAMP    |                                |

- **group_class_schedule_days** (element collection): `group_class_id` → `day_of_week`. Table `group_class_schedule_days(group_class_id, day_of_week)`.

**Relationships:**  
- Many-to-one **teacher** → `teacher_profiles`.  
- Referenced by: `group_sessions.group_class_id`, `group_class_enrollments.group_class_id`.

---

### group_class_enrollments

| Column          | Type         | Notes                          |
|-----------------|--------------|--------------------------------|
| id              | UUID (PK)    |                                |
| group_class_id  | UUID (FK)    | NOT NULL → group_classes       |
| student_id      | UUID (FK)    | NOT NULL → students            |
| subscription_id | UUID (FK)    | optional → subscriptions       |
| contract_id     | UUID (FK)    | optional → contract_sessions   |
| created_at      | TIMESTAMP    | NOT NULL                       |

- Unique constraint: `(group_class_id, student_id)`.

**Relationships:**  
- Many-to-one **groupClass** → `group_classes`, **student** → `students`; optional **subscription** → `subscriptions`, **contract** → `contract_sessions`.

---

### group_sessions

| Column        | Type         | Notes                          |
|---------------|--------------|--------------------------------|
| id            | UUID (PK)    |                                |
| group_class_id| UUID (FK)    | NOT NULL → group_classes       |
| session_date  | DATE         | NOT NULL                       |
| check_in_at   | TIMESTAMP    |                                |
| check_out_at  | TIMESTAMP    |                                |
| lesson_notes  | VARCHAR(2000)|                                |
| zoom_join_url | VARCHAR      |                                |
| created_at    | TIMESTAMP    | NOT NULL                       |
| updated_at    | TIMESTAMP    |                                |

**Relationships:**  
- Many-to-one **groupClass** → `group_classes`.  
- One-to-many **attendances** → `group_session_attendances`.

---

### group_session_attendances

| Column           | Type         | Notes                          |
|------------------|--------------|--------------------------------|
| id               | UUID (PK)    |                                |
| group_session_id | UUID (FK)    | NOT NULL → group_sessions      |
| student_id       | UUID (FK)    | NOT NULL → students            |
| hours_used       | DOUBLE       |                                |

**Relationships:**  
- Many-to-one **groupSession** → `group_sessions`, **student** → `students`.

---

## 1:1 attendance and homework / grades

### attendance_logs

| Column        | Type         | Notes                          |
|---------------|--------------|--------------------------------|
| id            | UUID (PK)    |                                |
| contract_id   | UUID (FK)    | NOT NULL → contract_sessions   |
| session_date  | DATE         | NOT NULL                       |
| check_in_at   | TIMESTAMP    |                                |
| check_out_at  | TIMESTAMP    |                                |
| lesson_notes  | VARCHAR(2000)|                                |
| hours_used    | DOUBLE       |                                |
| zoom_join_url | VARCHAR      |                                |
| created_at    | TIMESTAMP    | NOT NULL                       |
| updated_at    | TIMESTAMP    |                                |

**Relationships:**  
- Many-to-one **contract** → `contract_sessions`.

---

### homework

| Column          | Type         | Notes                          |
|-----------------|--------------|--------------------------------|
| id              | UUID (PK)    |                                |
| teacher_id      | UUID (FK)    | NOT NULL → teacher_profiles     |
| student_id      | UUID (FK)    | NOT NULL → students            |
| title           | VARCHAR      | NOT NULL                       |
| description     | VARCHAR(2000)|                                |
| due_date        | DATE         | NOT NULL                       |
| status          | VARCHAR      | NOT NULL (ASSIGNED, SUBMITTED, GRADED, OVERDUE) |
| teacher_feedback| VARCHAR(2000)|                                |
| created_at      | TIMESTAMP    | NOT NULL                       |
| updated_at      | TIMESTAMP    |                                |

**Relationships:**  
- Many-to-one **teacher** → `teacher_profiles`, **student** → `students`.

---

### student_grades

| Column      | Type         | Notes                          |
|-------------|--------------|--------------------------------|
| id          | UUID (PK)    |                                |
| teacher_id  | UUID (FK)    | NOT NULL → teacher_profiles     |
| student_id  | UUID (FK)    | NOT NULL → students            |
| title       | VARCHAR      | NOT NULL                       |
| grade_value | DOUBLE       | NOT NULL                       |
| max_value   | DOUBLE       |                                |
| grade_date  | DATE         | NOT NULL                       |
| notes       | VARCHAR(1000)|                                |
| created_at  | TIMESTAMP    | NOT NULL                       |

**Relationships:**  
- Many-to-one **teacher** → `teacher_profiles`, **student** → `students`.

---

## Teacher availability and system data

### teacher_availabilities

| Column     | Type         | Notes                          |
|------------|--------------|--------------------------------|
| id         | UUID (PK)    |                                |
| teacher_id | UUID (FK)    | NOT NULL → teacher_profiles    |
| day_of_week| INT          | NOT NULL (1–7)                 |
| start_time | TIME         | NOT NULL                       |
| end_time   | TIME         | NOT NULL                       |

**Relationships:**  
- Many-to-one **teacher** → `teacher_profiles`.

---

### refresh_tokens

| Column     | Type         | Notes                          |
|------------|--------------|--------------------------------|
| id         | UUID (PK)    |                                |
| user_id    | UUID (FK)    | NOT NULL → application_users   |
| token_hash | VARCHAR      | NOT NULL, UNIQUE               |
| expires_at | TIMESTAMP    | NOT NULL                       |
| revoked    | BOOLEAN      |                                |
| created_at | TIMESTAMP    | NOT NULL                       |

**Relationships:**  
- Many-to-one **user** → `application_users`.

---

### holidays

| Column       | Type         | Notes                          |
|--------------|--------------|--------------------------------|
| id           | UUID (PK)    |                                |
| holiday_date | DATE         | NOT NULL, UNIQUE               |
| name         | VARCHAR      | NOT NULL                       |
| description  | VARCHAR(500) |                                |

No FKs.

---

### system_settings

| Column     | Type         | Notes                          |
|------------|--------------|--------------------------------|
| id         | UUID (PK)    |                                |
| key_name   | VARCHAR      | NOT NULL, UNIQUE               |
| value      | VARCHAR(2000)|                                |
| description| VARCHAR(500) |                                |

No FKs.

---

## Entity relationship summary

- **application_users**: central identity; has **teacher_profiles** (1:1), **students** as parent (1:n), **refresh_tokens** (1:n).
- **teacher_profiles**: **group_classes**, **contract_sessions**, **attendance_logs** (via contract), **homework**, **student_grades**, **teacher_availabilities**.
- **students**: **subscriptions**, **contract_sessions**, **group_class_enrollments**, **group_session_attendances**, **homework**, **student_grades**; parent → **application_users**.
- **subscriptions** → **students**; optional link from **contract_sessions** and **group_class_enrollments**.
- **contract_sessions** link **teacher_profiles**, **students**, optional **subscriptions**; **attendance_logs** and optional **group_class_enrollments** reference them.
- **group_classes** → **teacher_profiles**; **group_sessions** → **group_classes**; **group_session_attendances** → **group_sessions** and **students**.
- **group_class_enrollments** tie **group_classes**, **students**, and optionally **subscriptions** / **contract_sessions**.

Schema is created/updated by Hibernate with `ddl-auto` (e.g. `update`); see `application.yml` and `application-dev.yml` for datasource and JPA settings.
