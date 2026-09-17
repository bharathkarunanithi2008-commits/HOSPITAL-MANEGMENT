# Postman API Testing Collection & Results
## Hospital / Patient Management System (HMS)

### 1. Test Environment Setup
- **Base URL Variable:** `{{base_url}} = http://localhost:8000/api`
- **Auth Token Variable:** `{{auth_token}}`
- **Environment:** Local Development / Staging Testbed

---

### 2. Postman Test Execution Matrix

| # | Endpoint Tested | Method | Payload / Scenario | Expected Status | Actual Status | DB Consistency Verified |
|---|---|---|---|---|---|---|
| 1 | `/auth/login/` | `POST` | Valid Admin credentials | 200 OK | 200 OK | Token generated |
| 2 | `/auth/login/` | `POST` | Invalid password | 401 Unauthorized | 401 Unauthorized | Auth rejected |
| 3 | `/patients/` | `POST` | Valid complete patient payload | 201 Created | 201 Created | Record inserted in DB |
| 4 | `/patients/` | `POST` | Missing required `phone` | 400 Bad Request | 400 Bad Request | No record inserted |
| 5 | `/patients/` | `POST` | Invalid email format | 400 Bad Request | 400 Bad Request | No record inserted |
| 6 | `/patients/` | `POST` | Duplicate existing email | 400 Bad Request | 400 Bad Request | Unique constraint held |
| 7 | `/patients/` | `GET` | Retrieve patient list | 200 OK | 200 OK | Matches DB count |
| 8 | `/patients/?search=Eleanor` | `GET` | Search query filter | 200 OK | 200 OK | Filtered count = 1 |
| 9 | `/patients/{id}/` | `GET` | Existing patient ID | 200 OK | 200 OK | Data fields match DB |
| 10| `/patients/99999/` | `GET` | Non-existent patient ID | 404 Not Found | 404 Not Found | Safe error response |
| 11| `/patients/{id}/` | `PATCH`| Update phone & address | 200 OK | 200 OK | DB columns modified |
| 12| `/patients/{id}/` | `DELETE`| Remove patient record | 204 No Content | 204 No Content | Record deleted in DB |
| 13| `/doctors/` | `POST` | Valid doctor payload | 201 Created | 201 Created | Record inserted in DB |
| 14| `/doctors/` | `GET` | List all doctors | 200 OK | 200 OK | All doctors returned |
| 15| `/appointments/` | `POST` | Schedule valid appointment | 201 Created | 201 Created | Record inserted in DB |
| 16| `/appointments/` | `POST` | Double booking conflict test | 400/409 Conflict | 409 Conflict | Duplicate prevented |
| 17| `/appointments/{id}/` | `PATCH`| Mark status = Completed | 200 OK | 200 OK | DB status updated |
| 18| `/medical-records/` | `POST` | Valid diagnosis & notes | 201 Created | 201 Created | Record inserted in DB |
| 19| `/medical-records/` | `GET` | Filter by patient ID | 200 OK | 200 OK | Clinical records list |
| 20| `/medical-records/{id}/` | `DELETE`| Remove medical record | 204 No Content | 204 No Content | DB record removed |

---

### 3. Verification of 4-Way Database Consistency
For every CRUD operation:
```
Client Request  ===>  REST API Response  ===>  Backend Handler  ===>  Database Table State
   (HTTP)                  (JSON)                 (Django ORM)             (PostgreSQL)
```
- **Create:** Returned JSON `id` matches newly allocated primary key in table.
- **Read:** Returned JSON payload exactly matches current column values in database.
- **Update:** Subsequent `SELECT` confirms column mutations occurred.
- **Delete:** Subsequent `SELECT` yields 0 rows and API returns 404 Not Found.
