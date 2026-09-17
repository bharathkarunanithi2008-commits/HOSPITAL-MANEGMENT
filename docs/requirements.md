# Requirement Analysis & Specification Document
## Hospital / Patient Management System (HMS)

**Project Identifier:** HMS-SOP-2026  
**Standard Operating Procedure (SOP) Reference:** Standard Operating Procedure for Enterprise Hospital & Patient Management Systems  
**Date:** September 2026  
**Status:** Approved & Implemented  

---

### 1. Problem Statement
Hospitals and healthcare clinics often suffer from fragmented record-keeping, reliance on paper charts or disjointed spreadsheets, error-prone manual appointment scheduling, delayed access to historical patient diagnoses, and security risks surrounding unauthorized access to sensitive Protected Health Information (PHI). 

This Hospital Management System (HMS) provides a centralized, secure, multi-tier digital platform enabling hospital administrators, doctors, receptionists, and patients to streamline patient registration, medical appointment scheduling, clinical diagnostics, and comprehensive medical record keeping with strict relational integrity, role-based access control (RBAC), and validated auditing.

---

### 2. Target Users & User Personas
1. **Hospital Administrator (Admin):**
   - Full governance over users, staff rosters, clinical departments, system configurations, and operational statistics.
   - Audits CRUD actions across all hospital records.
2. **Doctor / Medical Practitioner:**
   - Accesses assigned patient demographics, reviews medical histories, updates clinical diagnosis and treatment notes, views upcoming consultations.
3. **Receptionist / Front Desk Staff:**
   - Registers new patients, updates patient contact details, books, reschedules, or cancels appointments with doctors.
4. **Patient:**
   - Views personal registration details, scheduled appointments, and verified medical summary records.

---

### 3. Main Entities & Relationships
The core entities strictly reflect hospital operations with referential integrity:
1. **Department:** Clinical unit (e.g., Cardiology, Neurology, Pediatrics, Orthopedics, General Medicine).
2. **Doctor:** Medical practitioner affiliated with a department.
3. **Patient:** Individual receiving care, with unique identifier and emergency information.
4. **Appointment:** Scheduled consultation connecting Patient, Doctor, and Department with lifecycle status.
5. **Medical Record:** Clinical encounter documentation detailing diagnosis, treatment plans, and notes.

---

### 4. Fields for Each Entity

#### Patient
- `patient_id` (Primary Key, Auto-increment Integer / UUID)
- `full_name` (String, max 120, NOT NULL)
- `date_of_birth` (Date, NOT NULL, Must be in past)
- `gender` (Enum: Male, Female, Other, NOT NULL)
- `phone` (String, max 20, NOT NULL, E.164 compliant)
- `email` (Email string, max 100, UNIQUE, NOT NULL)
- `address` (Text, NOT NULL)
- `blood_group` (Enum: A+, A-, B+, B-, AB+, AB-, O+, O-, NOT NULL)
- `emergency_contact` (String, max 100, NOT NULL)
- `created_at` (Timestamp, auto-now-add)
- `updated_at` (Timestamp, auto-now)

#### Doctor
- `doctor_id` (Primary Key, Auto-increment Integer / UUID)
- `full_name` (String, max 120, NOT NULL)
- `specialization` (String, max 100, NOT NULL)
- `department` (Foreign Key -> Department, CASCADE / RESTRICT)
- `phone` (String, max 20, NOT NULL)
- `email` (Email string, max 100, UNIQUE, NOT NULL)
- `availability` (String, max 100, e.g., "Mon-Fri 09:00 - 17:00", NOT NULL)
- `created_at` (Timestamp, auto-now-add)
- `updated_at` (Timestamp, auto-now)

#### Appointment
- `appointment_id` (Primary Key, Auto-increment Integer / UUID)
- `patient` (Foreign Key -> Patient, ON DELETE CASCADE)
- `doctor` (Foreign Key -> Doctor, ON DELETE CASCADE)
- `appointment_date` (Date, NOT NULL, Future or current date)
- `appointment_time` (Time, String format "HH:MM", NOT NULL)
- `status` (Enum: Scheduled, Completed, Cancelled, In-Progress, NOT NULL)
- `reason` (Text, max 500, NOT NULL)
- `created_at` (Timestamp, auto-now-add)
- `updated_at` (Timestamp, auto-now)

#### Medical Record
- `record_id` (Primary Key, Auto-increment Integer / UUID)
- `patient` (Foreign Key -> Patient, ON DELETE CASCADE)
- `doctor` (Foreign Key -> Doctor, ON DELETE CASCADE)
- `diagnosis` (String, max 255, NOT NULL)
- `notes` (Text, NOT NULL)
- `record_date` (Date, NOT NULL)
- `created_at` (Timestamp, auto-now-add)
- `updated_at` (Timestamp, auto-now)

---

### 5. CRUD Requirements
Every core entity supports complete lifecycle operations:
- **CREATE:** Validation on all mandatory fields; returns HTTP 201 Created with created object.
- **READ ALL:** Returns list of objects with optional search/filtering parameters and pagination; returns HTTP 200 OK.
- **READ ONE:** Returns specific resource by ID; returns HTTP 200 OK or HTTP 404 Not Found.
- **UPDATE:** Full update (PUT) and partial update (PATCH); validates data against integrity constraints; returns HTTP 200 OK.
- **DELETE:** Deletes or archives records according to referential integrity rules; returns HTTP 204 No Content.

---

### 6. Validation Requirements
Dual-layer validation (Client-side and Server-side):
- **Mandatory Fields:** Required fields cannot be empty or whitespace only.
- **Email Validation:** Standard RFC 5322 regex validation and unique constraint enforcement.
- **Phone Validation:** Numerical/E.164 pattern validation (min 10 digits).
- **Date Values:** Date of birth must be strictly prior to current date; appointment dates must not be in the past for new bookings.
- **Foreign Key Integrity:** Foreign keys must reference existing, active records.
- **Duplicate Prevention:** Doctors cannot have double-booked appointments at the exact same date and time.

---

### 7. Authentication Requirements
- Secure token-based authentication (JWT or Token Auth).
- Encrypted password transmission over TLS.
- Passwords hashed using PBKDF2 with SHA-256 (Django default) or Argon2.
- Session expiration and token refresh mechanism.

---

### 8. Authorization Requirements (RBAC)
Enforced at the API and database query level:
- **Admin:** Complete read/write permissions on all resources.
- **Doctor:** Read/write permissions on assigned appointments and clinical medical records; read permissions on patient roster.
- **Receptionist:** Read/write permissions on patient registration and appointments; read-only on medical records.
- **Patient:** Read-only access to their own demographic details, their own appointments, and their own medical summaries.

---

### 9. Search & Filter Requirements
- **Patients:** Search by `full_name`, `phone`, `email`, or `patient_id`; filter by `blood_group` and `gender`.
- **Doctors:** Search by `full_name`, `specialization`, or `department`; filter by `availability`.
- **Appointments:** Filter by `status`, `doctor`, `patient`, or `appointment_date`.
- **Medical Records:** Filter by `patient_id` or `doctor_id`.

---

### 10. API Requirements
- RESTful JSON APIs adhering to HTTP standards.
- Consistent envelope response for success and error handling:
  ```json
  {
    "success": true,
    "data": { ... },
    "message": "Resource retrieved successfully"
  }
  ```
- Error format:
  ```json
  {
    "success": false,
    "message": "Validation failed",
    "errors": { "field": ["error message"] }
  }
  ```
- HTTP status codes: 200 OK, 201 Created, 204 No Content, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict, 500 Internal Error.

---

### 11. Database Requirements
- PostgreSQL 14+ for relational backend persistence.
- Django ORM models with strict constraints (`unique=True`, `null=False`, `on_delete=models.CASCADE`).
- Room SQLite for native offline-first mobile client deployment.
- Strict migration history management (`makemigrations`, `migrate`).
- Seed script for deterministic testing without fabrication of sensitive clinical data.

---

### 12. Testing Requirements
- Unit and integration tests for all models, serializers, and views.
- CRUD testing on every endpoint with valid, missing, invalid, and duplicate payloads.
- Automated tests covering permission matrices.
- Postman test collection documentation verifying status codes and database consistency.

---

### 13. Security Requirements
- Zero hardcoded secrets in source files (`.env.example` provided).
- `.gitignore` configured to prevent committing `.env`, keyfiles, databases, or build artifacts.
- CORS restricted to explicit frontend origins.
- Protection against SQL injection via parameterized ORM queries.
- Input sanitization preventing XSS attacks.
- No sensitive stack traces or internal secrets leaked in error responses.

---

### 14. Documentation Requirements
- Full Architecture diagram and specifications (`docs/architecture.md`).
- ER Diagram with schema definition (`docs/ER-diagram.md`).
- Exhaustive API Contract First documentation (`docs/api.md`).
- Security architecture and threat model (`docs/security.md`).
- Testing documentation with Postman collection (`docs/testing.md` and `docs/postman-testing.md`).
- SOP Traceability Matrix (`docs/sop-compliance.md`).
- Complete `README.md` with all 24 required sections.
