# Smart Clinic System

> Spring Boot backend for Patient Continuity, Prescription Tracking & Reminder Engine

---

## Quick Start

```bash
# 1. Clone / unzip the project
cd smart-clinic

# 2. Build
./mvnw clean package -DskipTests

# 3. Run
./mvnw spring-boot:run
```

Server starts on **http://localhost:8080**

---

## Project Structure

```
src/main/java/com/clinic/
├── SmartClinicApplication.java
├── config/
│   ├── GlobalExceptionHandler.java   ← validation & error responses
│   ├── JwtUtil.java                  ← JWT generate / validate
│   └── ResourceNotFoundException.java
├── controller/
│   ├── AuthController.java           ← /api/auth
│   ├── PatientController.java        ← /api/patients
│   ├── DoctorController.java         ← /api/doctors
│   └── PrescriptionController.java   ← /api/prescriptions
├── service/
│   ├── AuthService.java              ← OTP simulation + JWT
│   ├── PatientService.java
│   ├── DoctorService.java
│   ├── PrescriptionService.java
│   └── ReminderService.java          ← logs reminders on prescription create
├── repository/
│   ├── PatientRepository.java
│   ├── DoctorRepository.java
│   ├── PrescriptionRepository.java
│   └── ReminderLogRepository.java
├── model/
│   ├── Patient.java
│   ├── Doctor.java
│   ├── Prescription.java
│   ├── Medication.java
│   └── ReminderLog.java
└── dto/
    ├── PatientRequest / PatientResponse
    ├── DoctorRequest  / DoctorResponse
    ├── MedicationRequest / MedicationResponse
    ├── PrescriptionRequest / PrescriptionResponse
    ├── OtpRequest / OtpVerifyRequest / AuthResponse
```

---

## Endpoints

| Method | Path                                  | Description                        |
|--------|---------------------------------------|------------------------------------|
| POST   | /api/auth/send-otp                    | Request OTP (mock — returns OTP)   |
| POST   | /api/auth/verify-otp                  | Verify OTP → receive JWT           |
| POST   | /api/patients                         | Create patient                     |
| GET    | /api/patients/{id}                    | Get patient by ID                  |
| POST   | /api/doctors                          | Create doctor                      |
| GET    | /api/doctors/{id}                     | Get doctor by ID                   |
| POST   | /api/prescriptions                    | Create prescription + medications  |
| GET    | /api/prescriptions/patient/{id}       | Get patient's prescription history |

H2 Console: http://localhost:8080/h2-console  
JDBC URL: `jdbc:h2:mem:clinicdb` | User: `sa` | Password: *(blank)*

---

## Switch to MySQL

1. Uncomment the MySQL dependency in `pom.xml`
2. Comment out the H2 dependency
3. In `application.yml`, comment the H2 datasource block and uncomment the MySQL block
4. Set your database credentials

---

## Auth Flow (Mock)

1. `POST /api/auth/send-otp` → OTP `123456` is stored in memory and returned in response
2. `POST /api/auth/verify-otp` → on match, returns a signed JWT
3. *(No filter wired yet — JWT validation is available via `JwtUtil` for future middleware)*

---

## Reminder Engine (Stub)

When a prescription is saved, `ReminderService.scheduleReminders()` creates one
`PENDING` `ReminderLog` entry per medication.  
Hook a `@Scheduled` task or messaging queue here when ready to fire real notifications.
