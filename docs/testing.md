# Automated Testing & Verification Plan
## Hospital / Patient Management System (HMS)

### 1. Test Strategy
Testing follows the classic test pyramid covering:
1. **Unit Tests:** Model validation, constraints, serialization logic, business rule calculations.
2. **Integration Tests:** REST API endpoints, HTTP status codes, error payload schemas, database mutation verification.
3. **End-to-End & CRUD Verification:** Full lifecycle (Create -> Read -> Update -> Delete) across all 4 core entities.
4. **Android Client Unit & Robolectric Tests:** ViewModels, Room DAOs, state flows, and UI rendering.

---

### 2. Backend Automated Test Suite (`backend/apps/hospital/tests.py`)

#### Test Case 1: Patient Model & API CRUD
- `test_create_patient_valid_data`: Creates patient, asserts 201 Created, verifies database record exists.
- `test_create_patient_invalid_email`: Submits `invalid-email`, asserts 400 Bad Request.
- `test_create_patient_duplicate_email`: Submits duplicate email, asserts 400/409 Conflict.
- `test_read_patient_by_id`: Fetches created patient, asserts 200 OK and matching attributes.
- `test_update_patient`: Updates phone and address, asserts 200 OK and database mutation.
- `test_delete_patient`: Deletes patient, asserts 204 No Content, verifies record is removed.

#### Test Case 2: Doctor Model & Department Relationships
- `test_doctor_creation_with_department`: Verifies foreign key binding to department.
- `test_read_all_doctors_with_search_filter`: Queries `?search=Cardiology`, asserts filtered result set.

#### Test Case 3: Appointment Scheduling & Conflict Prevention
- `test_create_valid_appointment`: Books consultation for future date, asserts 201 Created.
- `test_prevent_double_booking_conflict`: Attempts to schedule second appointment for same doctor at same date & time; asserts 400/409 Conflict with clear error message.
- `test_update_appointment_status`: Updates status from `Scheduled` to `Completed`, asserts database status change.

#### Test Case 4: Medical Record Clinical Documentation
- `test_create_medical_record`: Creates diagnosis record, asserts foreign key relationships to patient and doctor.
- `test_medical_record_requires_diagnosis_and_notes`: Validates required field enforcement.

---

### 3. Android Client Test Suite (`app/src/test/`)
- Room DAO testing verifying SQL queries, Flow emission, cascade deletions.
- ViewModel unit tests verifying StateFlow updates, input validation regex, and error state handling.
