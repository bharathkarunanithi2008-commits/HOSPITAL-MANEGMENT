# Security Architecture & Risk Mitigation Document
## Hospital / Patient Management System (HMS)

### 1. Threat Model & Security Posture
In compliance with the Healthcare Standard Operating Procedure and HIPAA/GDPR best practices, patient demographic information, consultation appointments, and diagnostic medical histories are classified as **Protected Health Information (PHI)** requiring confidentiality, integrity, and availability guarantees.

---

### 2. Core Security Controls Implemented

| Security Domain | Threat / Vulnerability | Mitigation Mechanism | Verification Evidence |
|---|---|---|---|
| **Secret Management** | Exposure of secrets in VCS | `.env` excluded in `.gitignore`; `.env.example` provided | Inspected git history & `.gitignore` |
| **Authentication** | Brute force, unauthorized access | Cryptographic password hashing (PBKDF2/SHA-256), JWT token authentication | Automated login tests, expired token rejection |
| **Authorization (RBAC)** | Privilege escalation, IDOR | Role-Based Access Control enforced at API view level and database layer | Role permission test matrix |
| **SQL Injection** | Arbitrary database execution | Django ORM & Room parameter binding (zero raw string concatenation) | Static code analysis, ORM query logging |
| **Cross-Site Scripting (XSS)**| Injected script execution | React automatic HTML escaping, strict Content-Type headers | Input sanitization tests |
| **Cross-Origin Requests (CORS)**| Unauthorized external web origin calls | Django `django-cors-headers` restricted to explicit domain origins | Origin header inspection |
| **Data Validation** | Malformed, duplicate, or conflicting data | Strict serializer validation (server) + UI form validation (client) | Automated boundary & invalid input tests |
| **Information Disclosure** | Stack traces, server details leaking | Custom DRF exception handler returning structured `{ success: false, message, errors }` | 400/404/500 mock error testing |

---

### 3. Role-Based Access Matrix (RBAC)

| Resource / Endpoint | Administrator | Doctor | Receptionist | Patient |
|---|---|---|---|---|
| `POST /api/auth/login/` | Yes | Yes | Yes | Yes |
| `GET /api/patients/` | All | Assigned/All | All | Self Only |
| `POST /api/patients/` | Yes | No | Yes | No |
| `PUT/PATCH /api/patients/{id}/` | Yes | No | Yes | Self (profile only) |
| `DELETE /api/patients/{id}/` | Yes | No | No | No |
| `GET /api/doctors/` | All | All | All | All (Public) |
| `POST/PUT/DELETE /api/doctors/` | Yes | Self (PUT) | No | No |
| `GET /api/appointments/` | All | Assigned | All | Self Only |
| `POST /api/appointments/` | Yes | Yes | Yes | Yes |
| `PATCH /api/appointments/{id}/`| Yes | Yes (Status) | Yes | Cancel Only |
| `DELETE /api/appointments/{id}/`| Yes | No | Yes (Cancel) | No |
| `GET /api/medical-records/` | All | Assigned | Read Summary | Self Only |
| `POST /api/medical-records/` | Yes | Yes (Author) | No | No |
| `PUT /api/medical-records/{id}/`| Yes | Yes (Author) | No | No |
| `DELETE /api/medical-records/` | Yes | No | No | No |

---

### 4. Exception & Error Sanitization
All exceptions in production are caught by custom middleware:
- Never displays `Traceback (most recent call last):` to clients.
- Database connection strings, passwords, and internal file paths are masked.
- Standardized error schema:
  ```json
  {
    "success": false,
    "message": "A validation error occurred.",
    "errors": {
      "email": ["A patient with this email already exists."]
    }
  }
  ```
