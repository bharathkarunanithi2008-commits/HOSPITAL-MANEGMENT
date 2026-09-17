# Architecture Design Document
## Hospital / Patient Management System (HMS)

### 1. System Architecture Overview
The Hospital Management System follows a strict multi-tier, decoupled, service-oriented architecture ensuring separation of concerns, high maintainability, defense-in-depth security, and robust data persistence.

```
┌────────────────────────────────────────────────────────┐
│                      Client Tier                       │
│  ┌─────────────────────────┐  ┌─────────────────────┐  │
│  │   React / TypeScript    │  │  Android Native App │  │
│  │     Web Client (SPA)    │  │ (Compose + Room DB) │  │
│  └────────────┬────────────┘  └──────────┬──────────┘  │
└───────────────┼──────────────────────────┼─────────────┘
                │ HTTPS / JSON REST        │ Room Local /
                ▼                          │ REST API
┌──────────────────────────────────────────┼─────────────┐
│                      API Gateway & Web Tier            │
│  ┌───────────────────────────────────────▼──────────┐  │
│  │            Django REST Framework (DRF)           │  │
│  │      - CORS Headers & Authentication Filter      │  │
│  │      - URL Dispatcher & ViewSet Routers          │  │
│  │      - Serializer Validation Engine              │  │
│  └───────────────────────┬──────────────────────────┘  │
└──────────────────────────┼─────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────┐
│                   Application Business Logic           │
│  ┌──────────────────────────────────────────────────┐  │
│  │  - Role-Based Access Control (RBAC) Perms        │  │
│  │  - Appointment Scheduling & Conflict Engine      │  │
│  │  - Audit Logging & Status Transition Workflow    │  │
│  └───────────────────────┬──────────────────────────┘  │
└──────────────────────────┼─────────────────────────────┘
                           │ Parameterized ORM Queries
┌──────────────────────────▼─────────────────────────────┐
│                   Data Persistence Tier                │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Django ORM (Object-Relational Mapping) Engine   │  │
│  └───────────────────────┬──────────────────────────┘  │
│                          │ TCP / SSL Connection Pool   │
│  ┌───────────────────────▼──────────────────────────┐  │
│  │          PostgreSQL Relational Database          │  │
│  │    - Tables: Patients, Doctors, Appointments,    │  │
│  │              MedicalRecords, Departments         │  │
│  │    - Foreign Keys & Referential Integrity        │  │
│  │    - Constraints, Unique Indexes, ACID Txns      │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
```

---

### 2. Tier-by-Tier Breakdown

#### Layer 1: Client Tier (Presentation Layer)
- **React Frontend (Web SPA):**
  - Developed with modern React 18, TypeScript, and responsive Tailwind CSS / modern CSS grid.
  - State management handles asynchronous REST requests, loading states, error boundaries, and notifications.
  - Validates user input synchronously before dispatching network requests to provide instant visual feedback.
  - Communicates with the backend exclusively via standard HTTP fetch/Axios requests; direct database access is completely prohibited.
- **Android Native Client:**
  - Modern Jetpack Compose UI following Material Design 3 guidelines.
  - State management powered by ViewModels and Kotlin `StateFlow`.
  - Offline-first Room SQLite persistence with reactive repository pattern.

#### Layer 2: REST API & Routing Tier
- **Django REST Framework (DRF):**
  - Explicit URL routers map endpoints (`/api/patients/`, `/api/doctors/`, etc.).
  - Serializers define request and response schemas, enforce field-level and cross-field validation, and serialize model instances into standardized JSON format.
  - Consistent error handling returns standard HTTP status codes (200, 201, 204, 400, 401, 403, 404, 409).

#### Layer 3: Security & Business Logic Tier
- **Authentication:** Token-based authentication verifying user identity on protected routes.
- **Role-Based Access Control (RBAC):**
  - Permissions class (`IsAdminUser`, `IsDoctorUser`, `IsReceptionistUser`, `IsPatientOwner`) validates request tokens before passing execution to the views.
- **Conflict Prevention Engine:**
  - Prevents doctor appointment double-booking via transaction-safe database checks.
  - Verifies date constraints (e.g., patient birth date must be in past; appointment time slot must be valid).

#### Layer 4: Data Access & ORM Tier
- **Django ORM:**
  - Converts high-level Python class operations into secure, parameterized SQL statements, eliminating SQL injection vectors.
  - Manages relational joins, foreign key integrity, and cascade rules.
  - Provides automated migration management (`makemigrations` and `migrate`) for schema versioning.

#### Layer 5: Database Tier
- **PostgreSQL 14+:**
  - ACID-compliant storage guaranteeing data consistency.
  - Enforces primary keys, foreign keys, unique constraints (e.g., patient email, doctor email), check constraints, and indexed timestamps.
  - Strict referential integrity prevents orphaned medical records or appointments.
