package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DepartmentEntity::class,
        DoctorEntity::class,
        PatientEntity::class,
        AppointmentEntity::class,
        MedicalRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HospitalDatabase : RoomDatabase() {

    abstract fun hospitalDao(): HospitalDao

    companion object {
        @Volatile
        private var INSTANCE: HospitalDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): HospitalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HospitalDatabase::class.java,
                    "hospital_management.db"
                )
                    .addCallback(HospitalDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class HospitalDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.hospitalDao())
                }
            }
        }

        suspend fun populateInitialData(dao: HospitalDao) {
            // Seed Departments
            val cardioId = dao.insertDepartment(
                DepartmentEntity(name = "Cardiology", description = "Heart and vascular medicine")
            )
            val pedsId = dao.insertDepartment(
                DepartmentEntity(name = "Pediatrics", description = "Infant and adolescent care")
            )
            val neuroId = dao.insertDepartment(
                DepartmentEntity(name = "Neurology", description = "Brain and nervous system disorders")
            )

            // Seed Doctors
            val doc1 = dao.insertDoctor(
                DoctorEntity(
                    departmentId = cardioId,
                    fullName = "Dr. Aris Thorne",
                    specialization = "Cardiology",
                    phone = "+1-555-0101",
                    email = "dr.thorne@hospital.org",
                    availability = "Mon-Fri 08:30 - 16:30"
                )
            )
            val doc2 = dao.insertDoctor(
                DoctorEntity(
                    departmentId = pedsId,
                    fullName = "Dr. Helen Mirren",
                    specialization = "Pediatrics",
                    phone = "+1-555-0102",
                    email = "dr.mirren@hospital.org",
                    availability = "Mon-Thu 09:00 - 15:00"
                )
            )
            val doc3 = dao.insertDoctor(
                DoctorEntity(
                    departmentId = neuroId,
                    fullName = "Dr. Marcus Chen",
                    specialization = "Neurology",
                    phone = "+1-555-0103",
                    email = "dr.chen@hospital.org",
                    availability = "Tue-Sat 10:00 - 18:00"
                )
            )

            // Seed Patients
            val pat1 = dao.insertPatient(
                PatientEntity(
                    fullName = "Eleanor Vance",
                    dateOfBirth = "1989-04-12",
                    gender = "Female",
                    phone = "+1-555-0201",
                    email = "eleanor.vance@example.com",
                    address = "42 Crestview Terrace, Boston, MA",
                    bloodGroup = "O+",
                    emergencyContact = "Marcus Vance (+1-555-0202)"
                )
            )
            val pat2 = dao.insertPatient(
                PatientEntity(
                    fullName = "David Miller",
                    dateOfBirth = "1975-11-23",
                    gender = "Male",
                    phone = "+1-555-0203",
                    email = "david.miller@example.com",
                    address = "108 Beacon Hill Lane, Boston, MA",
                    bloodGroup = "A-",
                    emergencyContact = "Sarah Miller (+1-555-0204)"
                )
            )

            // Seed Appointments
            dao.insertAppointment(
                AppointmentEntity(
                    patientId = pat1,
                    doctorId = doc1,
                    appointmentDate = "2026-09-25",
                    appointmentTime = "10:00",
                    status = "Scheduled",
                    reason = "Hypertension checkup and medication review"
                )
            )
            dao.insertAppointment(
                AppointmentEntity(
                    patientId = pat2,
                    doctorId = doc3,
                    appointmentDate = "2026-09-26",
                    appointmentTime = "14:30",
                    status = "Scheduled",
                    reason = "Recurrent migraine symptoms consultation"
                )
            )

            // Seed Medical Records
            dao.insertMedicalRecord(
                MedicalRecordEntity(
                    patientId = pat1,
                    doctorId = doc1,
                    diagnosis = "Stage 1 Essential Hypertension",
                    notes = "Patient advised low sodium diet and daily exercise. Prescribed Lisinopril 10mg once daily.",
                    recordDate = "2026-09-10"
                )
            )
        }
    }
}
