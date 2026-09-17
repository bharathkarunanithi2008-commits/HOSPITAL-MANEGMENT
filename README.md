# Hospital / Patient Management System (HMS)

> Production-Quality, Multi-Tier Clinical Operations & Patient Records Management Platform built strictly according to Healthcare Standard Operating Procedure (SOP) Specifications.

---

## 1. Project Title
**Hospital / Patient Management System (HMS Enterprise)**  
*Full-Stack Healthcare Administration & Clinical Encounter Management Platform*

---

## 2. Project Overview
The Hospital Management System is a complete enterprise-grade application developed to streamline hospital operations, outpatient appointments, clinical diagnostics, physician assignments, and patient demographic registration. The system enforces strict separation of concerns across a presentation layer, a secure REST API routing layer, a business logic & validation layer, and an ACID-compliant relational persistence layer.

---

## 3. Problem Statement
Traditional paper-based or uncoordinated hospital tracking systems introduce severe clinical vulnerabilities:
- Illegible or fragmented physical charts risking medical history misinterpretations.
- Double-booking of doctors causing consultation delays and patient dissatisfaction.
- Unaudited data mutations creating regulatory and compliance violations.
- Security vulnerabilities and data leaks of Protected Health Information (PHI).

---

## 4. Objectives
- Centralize patient registration and health records into a validated database.
- Enforce strict role-based access control (RBAC) ensuring appropriate separation of duties.
- Prevent scheduling conflicts with automatic doctor appointment slot collision checks.
- Implement dual-layer (client & server) data validation to preserve data integrity.
- Deliver an intuitive, responsive user experience for clinical and administrative staff.

---

## 5. Features
- **Comprehensive Patient Roster:** Full CRUD operations on patient records, emergency contacts, and blood group classifications.
- **Doctor & Specialist Registry:** Directory of physicians, clinical departments, and consulting hours.
- **Smart Appointment Scheduler:** Real-time scheduling with conflict detection, preventing double-bookings.
- **Clinical Encounter Documentation:** Medical record authoring with diagnosis, clinical notes, and physician attribution.
- **Operational Metrics Dashboard:** Aggregated census metrics, today's consultations, and department statistics.
- **Role Switcher & RBAC:** Enforced access control for Administrator, Doctor, Receptionist, and Patient.
- **Search & Multi-Attribute Filtering:** Fast filtering across patients, doctors, and appointment statuses.

---

## 6. Technology Stack
- **Frontend Web:** React 18, TypeScript, Tailwind CSS, Vite, Axios
- **Mobile Client:** Android Native, Kotlin, Jetpack Compose (Material Design 3)
- **Backend Service:** Python 3.11, Django 5.0, Django REST Framework (DRF)
- **Database:** PostgreSQL 14+ (Backend Server) & Room SQLite (Android Native Client)
- **Authentication:** JWT (JSON Web Tokens) with PBKDF2 SHA-256 cryptographic password hashing
- **Testing:** Python `django.test.TestCase`, Postman Test Suite, Android JUnit & Robolectric

---

## 7. Architecture
Follows a strict decoupled multi-tier architecture:
```
User -> React Frontend / Android Native Client -> RESTful JSON API -> Django REST Framework -> Django ORM -> PostgreSQL
```
1. **Presentation Tier:** Responsive React SPA and native Android Compose client.
2. **API Gateway:** DRF views, serializers, authentication filters, and error handlers.
3. **Application Logic:** Role-based access control, appointment conflict engine.
4. **Data Access Layer:** Parameterized ORM queries preventing SQL injection.
5. **Database Tier:** Relational PostgreSQL / SQLite maintaining referential integrity.

See `docs/architecture.md` for full architectural diagrams.

---

## 8. Database Design
Relational entities with foreign keys and strict constraints:
- `departments`: `department_id` (PK), `name` (UNIQUE), `description`, timestamps
- `doctors`: `doctor_id` (PK), `department_id` (FK), `full_name`, `specialization`, `phone`, `email` (UNIQUE), `availability`, timestamps
- `patients`: `patient_id` (PK), `full_name`, `date_of_birth`, `gender`, `phone`, `email` (UNIQUE), `address`, `blood_group`, `emergency_contact`, timestamps
- `appointments`: `appointment_id` (PK), `patient_id` (FK), `doctor_id` (FK), `appointment_date`, `appointment_time`, `status`, `reason`, timestamps
- `medical_records`: `record_id` (PK), `patient_id` (FK), `doctor_id` (FK), `diagnosis`, `notes`, `record_date`, timestamps

See `docs/database.md` for complete schema descriptions.

---

## 9. ER Diagram
The visual Mermaid ER diagram illustrates all primary keys, foreign keys, and 1-to-many relationships:
- Department (1) <---> (M) Doctor
- Doctor (1) <---> (M) Appointment
- Patient (1) <---> (M) Appointment
- Doctor (1) <---> (M) MedicalRecord
- Patient (1) <---> (M) MedicalRecord

Detailed in `docs/ER-diagram.md`.

---

## 10. API Documentation
Standard RESTful endpoints with consistent JSON envelope:
- `POST /api/auth/login/`: Obtain JWT authentication tokens
- `GET/POST /api/patients/`: List all patients or register a new patient
- `GET/PATCH/DELETE /api/patients/{id}/`: Retrieve, update, or remove a patient
- `GET/POST /api/doctors/`: List doctors or create a new specialist
- `GET/POST /api/appointments/`: List or schedule consultations (with conflict checking)
- `PATCH /api/appointments/{id}/`: Update consultation status (Completed / Cancelled)
- `GET/POST /api/medical-records/`: Retrieve and author clinical diagnoses
- `GET /api/dashboard/stats/`: Aggregated operational metrics

Full contract specifications available in `docs/api.md`.

---

## 11. CRUD Implementation
Every core entity implements complete CRUD functionality without fake endpoints:
- **Create:** Validated insertion with 201 Created.
- **Read All:** Filtered, searchable lists with 200 OK.
- **Read One:** Specific record lookup by ID with 200 OK / 404 Not Found.
- **Update:** Complete (PUT) or partial (PATCH) field mutation with 200 OK.
- **Delete:** Referential-safe removal with 204 No Content.

---

## 12. Validation
- **Frontend Validation:** Real-time form feedback on mandatory fields, regex email formats, valid dates.
- **Backend Validation:** Serializer-level checks for date of birth in past, unique emails, and doctor time slot availability.

---

## 13. Security
- Sensitive Protected Health Information (PHI) access restricted by role.
- Cryptographic password hashing via PBKDF2 SHA-256.
- Secrets managed exclusively via `.env` (never committed; `.env.example` provided).
- SQL Injection prevented via Django ORM parameterized queries.
- CORS restricted to whitelisted frontend origins.

Detailed in `docs/security.md`.

---

## 14. Testing
- Automated test suite in `backend/apps/hospital/tests.py` covering model constraints, duplicate rejections, and appointment collisions.
- Mobile unit tests covering ViewModel state flows.
- Detailed testing strategy in `docs/testing.md`.

---

## 15. Installation
Clone the repository:
```bash
git clone https://github.com/hospital-management/hms-system.git
cd hms-system
```

---

## 16. Environment Variables
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```
Configure your database credentials and secret keys.

---

## 17. Database Setup
Ensure PostgreSQL is running, then create the database:
```sql
CREATE DATABASE hospital_db;
CREATE USER hospital_user WITH PASSWORD 'hospital_secure_password';
GRANT ALL PRIVILEGES ON DATABASE hospital_db TO hospital_user;
```

---

## 18. Frontend Setup
```bash
cd frontend
npm install
```

---

## 19. Backend Setup
```bash
cd backend
python3 -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
python manage.py makemigrations
python manage.py migrate
python seed_data.py  # Populate demo data
```

---

## 20. Running the Application
Start the backend server:
```bash
cd backend
python manage.py runserver 0.0.0.0:8000
```
Start the frontend development server:
```bash
cd frontend
npm run dev
```
Open `http://localhost:5173` in your browser.

---

## 21. Postman Testing
Import the collection described in `docs/postman-testing.md`. Run the automated runner against `http://localhost:8000/api` to verify all 20 test cases.

---

## 22. Git / GitHub Instructions
- Commit changes using standard conventional commit messages (`feat:`, `fix:`, `test:`, `docs:`).
- Keep secrets and build artifacts excluded in `.gitignore`.

---

## 23. Challenges and Solutions
- **Double-Booking Race Conditions:** Solved using composite database constraints `unique_together = ('doctor', 'appointment_date', 'appointment_time')` alongside serializer-level validation.
- **Cross-Platform Verification:** Built both a responsive React web frontend and a native Android Jetpack Compose client with Room database for offline accessibility.

---

## 24. Future Enhancements
- Biometric authentication integration.
- Laboratory report PDF generation and automated prescription dispensing integration.
- Telemedicine video consultation endpoints via WebRTC.
