# SOP Traceability Matrix
## Hospital / Patient Management System (HMS)

| SOP Requirement | Implementation Detail | File / Location | Test Evidence | Status |
|---|---|---|---|---|
| **Frontend** | React 18 + TypeScript SPA with responsive modern layout, modals, tabs, forms | `frontend/src/` | Interactive UI rendering, clean build | **PASS** |
| **Mobile Client** | Android Native Jetpack Compose app with Material 3 | `app/src/main/` | `compile_applet` clean build, responsive emulator UI | **PASS** |
| **Backend** | Python Django 5 + Django REST Framework with modular architecture | `backend/config/`, `backend/apps/hospital/` | Unit & API tests in `tests.py` | **PASS** |
| **REST API** | Standardized JSON REST API with proper HTTP verbs (GET, POST, PUT, PATCH, DELETE) | `backend/apps/hospital/views.py`, `urls.py` | Verified against `docs/api.md` | **PASS** |
| **Database** | PostgreSQL relational schema with foreign keys, unique constraints, timestamps | `backend/apps/hospital/models.py`, `docs/database.md` | Migrations executed, ACID checked | **PASS** |
| **Mobile Database** | Room SQLite database with DAO repository pattern & Flow | `app/src/main/java/com/example/data/` | Type-safe queries, reactive updates | **PASS** |
| **CRUD: Patients** | Create, Read All, Read One, Update, Delete for patient demographics | Models, Views, Serializers, Compose UI | Postman Test 3-12, Room tests | **PASS** |
| **CRUD: Doctors** | Create, Read All, Read One, Update, Delete for doctors & departments | Models, Views, Serializers, Compose UI | Postman Test 13-14, Room tests | **PASS** |
| **CRUD: Appointments** | Create, Read, Status Update, Cancellation with double-booking prevention | Models, Views, Serializers, Compose UI | Postman Test 15-17, conflict check | **PASS** |
| **CRUD: Medical Records** | Create clinical encounter, diagnosis, treatment notes, read, delete | Models, Views, Serializers, Compose UI | Postman Test 18-20 | **PASS** |
| **Input Validation** | Dual-layer validation (client-side regex + server-side serializer validators) | `validators.py`, Serializers, Compose ViewModel | Invalid email, past DOB tests pass | **PASS** |
| **Exception Handling** | Custom error envelope `{ success: false, message, errors }` with safe codes | `backend/config/exceptions.py` | 400, 401, 403, 404, 409 responses | **PASS** |
| **API Testing** | Postman test scenarios with automated verification | `docs/postman-testing.md` | All 20 API test scenarios passed | **PASS** |
| **Frontend/Backend Integration** | REST API service layer with Axios/Fetch, zero fake/hardcoded mocks | `frontend/src/services/api.ts` | Endpoints match Django REST routes | **PASS** |
| **Git / Version Control** | Initialized repository with atomic, meaningful commit messages | Git VCS | `.gitignore` protecting secrets | **PASS** |
| **README Documentation** | Comprehensive 24-section production README | `README.md` | Complete coverage of all 24 items | **PASS** |
| **Database / ER Diagram** | Entity-Relationship diagram showing cardinality & foreign keys | `docs/ER-diagram.md`, `docs/database.md` | Matches Django and Room models | **PASS** |
| **API Documentation** | Exhaustive API contract with request/response samples | `docs/api.md` | 100% endpoint coverage | **PASS** |
| **Security Controls** | Secrets management, RBAC, password hashing, SQL injection protection | `docs/security.md`, `.env.example` | Zero hardcoded secrets, parameterized queries | **PASS** |
| **No False API Rule** | Zero fictitious external APIs used; standard local REST API used | System architecture | Complies with section 21 rule | **PASS** |
| **No Fake Data Rule** | Clear distinction between sample seed script and operational data | `backend/seed_data.py`, UI seed toggle | Labeled demo data | **PASS** |
