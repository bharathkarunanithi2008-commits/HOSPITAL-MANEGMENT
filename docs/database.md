# Database Schema & Relational Design Document
## Hospital / Patient Management System (HMS)

### 1. Database Overview
- **Database Engine:** PostgreSQL 14+ (production/server) & SQLite via Room (Android client)
- **ORM:** Django ORM (Python) & Room DAO (Android Kotlin)
- **Character Encoding:** UTF-8
- **Transaction Isolation:** Read Committed (ACID compliance)

---

### 2. Table Schemas & Relational Attributes

#### Table: `departments`
Stores clinical department data.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `department_id` | SERIAL / INT | PRIMARY KEY | Unique department identifier |
| `name` | VARCHAR(100) | NOT NULL, UNIQUE | Department title (e.g., Cardiology) |
| `description` | TEXT | NULLABLE | Department scope & location |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Timestamp of creation |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Timestamp of last modification |

#### Table: `doctors`
Stores doctor professional profiles.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `doctor_id` | SERIAL / INT | PRIMARY KEY | Unique doctor identifier |
| `full_name` | VARCHAR(120) | NOT NULL | Doctor's full name with title |
| `specialization` | VARCHAR(100) | NOT NULL | Medical specialty (e.g. Pediatrics) |
| `department_id` | INT | FOREIGN KEY -> departments(department_id), ON DELETE SET NULL | Associated medical department |
| `phone` | VARCHAR(20) | NOT NULL | Doctor contact phone |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE | Official communication email |
| `availability` | VARCHAR(100) | NOT NULL | Working shift hours |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Timestamp of creation |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Timestamp of last modification |

#### Table: `patients`
Stores registered patient demographic and health baseline data.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `patient_id` | SERIAL / INT | PRIMARY KEY | Unique patient registration ID |
| `full_name` | VARCHAR(120) | NOT NULL | Patient's legal full name |
| `date_of_birth` | DATE | NOT NULL | Birth date (validated in past) |
| `gender` | VARCHAR(20) | NOT NULL | Gender identity (Male, Female, Other) |
| `phone` | VARCHAR(20) | NOT NULL | Primary contact phone |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE | Patient email address |
| `address` | TEXT | NOT NULL | Residential address |
| `blood_group` | VARCHAR(5) | NOT NULL | Blood type (A+, A-, B+, B-, AB+, AB-, O+, O-) |
| `emergency_contact`| VARCHAR(100) | NOT NULL | Name & phone of emergency contact |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Timestamp of creation |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Timestamp of last modification |

#### Table: `appointments`
Manages scheduled clinical consultations.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `appointment_id` | SERIAL / INT | PRIMARY KEY | Unique appointment number |
| `patient_id` | INT | NOT NULL, FOREIGN KEY -> patients(patient_id), ON DELETE CASCADE | Patient attending consultation |
| `doctor_id` | INT | NOT NULL, FOREIGN KEY -> doctors(doctor_id), ON DELETE CASCADE | Doctor conducting consultation |
| `appointment_date` | DATE | NOT NULL | Scheduled calendar date |
| `appointment_time` | VARCHAR(10) | NOT NULL | Scheduled consultation time (HH:MM) |
| `status` | VARCHAR(20) | NOT NULL, DEFAULT 'Scheduled' | 'Scheduled', 'Completed', 'Cancelled', 'In-Progress' |
| `reason` | TEXT | NOT NULL | Reason for visit / chief complaint |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Timestamp of creation |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Timestamp of last modification |

*Constraints:* `UNIQUE(doctor_id, appointment_date, appointment_time)` prevents double-booking.

#### Table: `medical_records`
Clinical documentation and treatment history.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `record_id` | SERIAL / INT | PRIMARY KEY | Unique medical record record ID |
| `patient_id` | INT | NOT NULL, FOREIGN KEY -> patients(patient_id), ON DELETE CASCADE | Patient associated with record |
| `doctor_id` | INT | NOT NULL, FOREIGN KEY -> doctors(doctor_id), ON DELETE CASCADE | Attending physician |
| `diagnosis` | VARCHAR(255) | NOT NULL | Clinical diagnostic assessment |
| `notes` | TEXT | NOT NULL | Treatment plan, symptoms, medication |
| `record_date` | DATE | NOT NULL | Date of clinical encounter |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last modification timestamp |

---

### 3. Referential Integrity Rules
1. **Patient Deletion Policy:** When a patient is removed, dependent appointments and medical records are either cascaded or anonymized depending on hospital compliance policy (SOP supports cascading during operational cleanup).
2. **Doctor Deletion Policy:** Appointments and records maintain historical physician references.
3. **Index Optimization:**
   - B-Tree index on `patients(email)` and `patients(phone)` for rapid lookup.
   - B-Tree index on `appointments(appointment_date, status)`.
   - B-Tree index on `medical_records(patient_id)`.
