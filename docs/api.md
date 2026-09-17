# REST API Specification & Contract
## Hospital / Patient Management System (HMS)

**Base URL:** `http://localhost:8000/api`  
**Content-Type:** `application/json`  
**Authentication Header:** `Authorization: Bearer <jwt-token>` (or `Token <token>`)

---

### Standard Envelope Formats

#### Standard Success Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... }
}
```

#### Standard Error Response
```json
{
  "success": false,
  "message": "Validation failed / Resource not found",
  "errors": {
    "field_name": ["Specific error description"]
  }
}
```

---

### Authentication Endpoints

#### 1. User Login
- **Method:** `POST`
- **URL:** `/api/auth/login/`
- **Authentication:** None (Public)
- **Request Body:**
  ```json
  {
    "username": "admin@hospital.org",
    "password": "SecurePassword123!"
  }
  ```
- **Validation Rules:**
  - `username`: Required, valid email or username string.
  - `password`: Required, minimum 8 characters.
- **Success Status:** `200 OK`
- **Response Format:**
  ```json
  {
    "success": true,
    "message": "Login successful",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "user": {
        "id": 1,
        "username": "admin@hospital.org",
        "role": "Admin",
        "full_name": "Hospital Administrator"
      }
    }
  }
  ```
- **Error Statuses:** `400 Bad Request` (missing credentials), `401 Unauthorized` (invalid password/account).

---

### Patients Endpoints (`/api/patients/`)

#### 1. Create Patient
- **Method:** `POST`
- **URL:** `/api/patients/`
- **Authentication:** Admin, Receptionist
- **Request Body:**
  ```json
  {
    "full_name": "Eleanor Vance",
    "date_of_birth": "1988-04-12",
    "gender": "Female",
    "phone": "+1-555-0192",
    "email": "eleanor.vance@example.com",
    "address": "42 Crestview Terrace, Boston, MA",
    "blood_group": "O+",
    "emergency_contact": "Marcus Vance (+1-555-0193)"
  }
  ```
- **Validation Rules:**
  - `full_name`: Required, max 120 chars.
  - `date_of_birth`: Required, YYYY-MM-DD, must be strictly in the past.
  - `gender`: Required, one of `["Male", "Female", "Other"]`.
  - `phone`: Required, valid phone pattern.
  - `email`: Required, RFC 5322 email regex, unique in system.
  - `blood_group`: Required, one of `["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"]`.
  - `emergency_contact`: Required, min 5 chars.
- **Success Status:** `201 Created`
- **Error Statuses:** `400 Bad Request` (validation error), `409 Conflict` (duplicate email), `401/403` (unauthorized).

#### 2. Read All Patients
- **Method:** `GET`
- **URL:** `/api/patients/`
- **Authentication:** Admin, Doctor, Receptionist
- **Query Parameters:**
  - `search`: Filter by name, email, phone (e.g. `?search=Eleanor`)
  - `blood_group`: Filter by blood group (e.g. `?blood_group=O+`)
  - `gender`: Filter by gender (e.g. `?gender=Female`)
- **Success Status:** `200 OK`
- **Response Format:**
  ```json
  {
    "success": true,
    "count": 1,
    "data": [
      {
        "patient_id": 1,
        "full_name": "Eleanor Vance",
        "date_of_birth": "1988-04-12",
        "gender": "Female",
        "phone": "+1-555-0192",
        "email": "eleanor.vance@example.com",
        "address": "42 Crestview Terrace, Boston, MA",
        "blood_group": "O+",
        "emergency_contact": "Marcus Vance (+1-555-0193)",
        "created_at": "2026-09-17T10:00:00Z",
        "updated_at": "2026-09-17T10:00:00Z"
      }
    ]
  }
  ```

#### 3. Read One Patient
- **Method:** `GET`
- **URL:** `/api/patients/{id}/`
- **Authentication:** Admin, Doctor, Receptionist, or Patient Owner
- **Success Status:** `200 OK`
- **Error Statuses:** `404 Not Found`

#### 4. Update Patient
- **Method:** `PUT` / `PATCH`
- **URL:** `/api/patients/{id}/`
- **Authentication:** Admin, Receptionist
- **Request Body:** Partial or complete patient attributes
- **Success Status:** `200 OK`
- **Error Statuses:** `400 Bad Request`, `404 Not Found`, `409 Conflict`

#### 5. Delete Patient
- **Method:** `DELETE`
- **URL:** `/api/patients/{id}/`
- **Authentication:** Admin
- **Success Status:** `204 No Content`
- **Error Statuses:** `403 Forbidden` (non-admin), `404 Not Found`

---

### Doctors Endpoints (`/api/doctors/`)

#### 1. Create Doctor
- **Method:** `POST`
- **URL:** `/api/doctors/`
- **Authentication:** Admin
- **Request Body:**
  ```json
  {
    "full_name": "Dr. Aris Thorne",
    "specialization": "Cardiology",
    "department": 1,
    "phone": "+1-555-0144",
    "email": "aris.thorne@hospital.org",
    "availability": "Mon-Fri 08:00 - 16:00"
  }
  ```
- **Validation Rules:**
  - `full_name`, `specialization`, `phone`, `email`, `availability`: Required.
  - `email`: Unique email check.
  - `department`: Must reference an existing valid department ID.
- **Success Status:** `201 Created`
- **Error Statuses:** `400 Bad Request`, `409 Conflict`

#### 2. Read All Doctors
- **Method:** `GET`
- **URL:** `/api/doctors/`
- **Query Parameters:** `search`, `specialization`, `department`
- **Success Status:** `200 OK`

#### 3. Read One Doctor
- **Method:** `GET`
- **URL:** `/api/doctors/{id}/`
- **Success Status:** `200 OK`
- **Error Statuses:** `404 Not Found`

#### 4. Update Doctor
- **Method:** `PUT` / `PATCH`
- **URL:** `/api/doctors/{id}/`
- **Authentication:** Admin, Doctor Owner
- **Success Status:** `200 OK`

#### 5. Delete Doctor
- **Method:** `DELETE`
- **URL:** `/api/doctors/{id}/`
- **Authentication:** Admin
- **Success Status:** `204 No Content`

---

### Appointments Endpoints (`/api/appointments/`)

#### 1. Create Appointment
- **Method:** `POST`
- **URL:** `/api/appointments/`
- **Authentication:** Admin, Receptionist, Patient
- **Request Body:**
  ```json
  {
    "patient": 1,
    "doctor": 2,
    "appointment_date": "2026-09-25",
    "appointment_time": "10:30",
    "status": "Scheduled",
    "reason": "Routine cardiac monitoring and BP check"
  }
  ```
- **Validation Rules:**
  - `patient`: Foreign key must exist.
  - `doctor`: Foreign key must exist.
  - `appointment_date`: Must not be in the past.
  - `appointment_time`: Valid format (HH:MM).
  - Double booking conflict check: Doctor must not already have an active appointment on the given date and time.
- **Success Status:** `201 Created`
- **Error Statuses:** `400 Bad Request` (validation/date error), `409 Conflict` (double booking)

#### 2. Read All Appointments
- **Method:** `GET`
- **URL:** `/api/appointments/`
- **Query Parameters:** `doctor`, `patient`, `date`, `status`
- **Success Status:** `200 OK`

#### 3. Read One Appointment
- **Method:** `GET`
- **URL:** `/api/appointments/{id}/`
- **Success Status:** `200 OK`

#### 4. Update Appointment Status / Details
- **Method:** `PUT` / `PATCH`
- **URL:** `/api/appointments/{id}/`
- **Request Body:** Status update (`"status": "Completed"`, `"Cancelled"`, etc.)
- **Success Status:** `200 OK`

#### 5. Delete / Cancel Appointment
- **Method:** `DELETE`
- **URL:** `/api/appointments/{id}/`
- **Success Status:** `204 No Content`

---

### Medical Records Endpoints (`/api/medical-records/`)

#### 1. Create Medical Record
- **Method:** `POST`
- **URL:** `/api/medical-records/`
- **Authentication:** Doctor, Admin
- **Request Body:**
  ```json
  {
    "patient": 1,
    "doctor": 2,
    "diagnosis": "Mild Stage 1 Hypertension",
    "notes": "Patient advised low sodium diet, 30 min daily walking. Prescribed Lisinopril 10mg daily. Review in 4 weeks.",
    "record_date": "2026-09-17"
  }
  ```
- **Validation Rules:**
  - `patient` and `doctor`: Foreign keys must exist.
  - `diagnosis`: Required, max 255 chars.
  - `notes`: Required text.
  - `record_date`: Valid date format.
- **Success Status:** `201 Created`

#### 2. Read All Medical Records
- **Method:** `GET`
- **URL:** `/api/medical-records/`
- **Query Parameters:** `patient`, `doctor`
- **Success Status:** `200 OK`

#### 3. Read One Medical Record
- **Method:** `GET`
- **URL:** `/api/medical-records/{id}/`
- **Success Status:** `200 OK`

#### 4. Update Medical Record
- **Method:** `PUT` / `PATCH`
- **URL:** `/api/medical-records/{id}/`
- **Authentication:** Doctor (author) or Admin
- **Success Status:** `200 OK`

#### 5. Delete Medical Record
- **Method:** `DELETE`
- **URL:** `/api/medical-records/{id}/`
- **Authentication:** Admin
- **Success Status:** `204 No Content`
