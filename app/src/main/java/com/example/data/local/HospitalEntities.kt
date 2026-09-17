package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "departments")
data class DepartmentEntity(
    @PrimaryKey(autoGenerate = true)
    val departmentId: Long = 0,
    val name: String,
    val description: String
)

@Entity(
    tableName = "doctors",
    foreignKeys = [
        ForeignKey(
            entity = DepartmentEntity::class,
            parentColumns = ["departmentId"],
            childColumns = ["departmentId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("departmentId"), Index(value = ["email"], unique = true)]
)
data class DoctorEntity(
    @PrimaryKey(autoGenerate = true)
    val doctorId: Long = 0,
    val departmentId: Long? = null,
    val fullName: String,
    val specialization: String,
    val phone: String,
    val email: String,
    val availability: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "patients",
    indices = [Index(value = ["email"], unique = true)]
)
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)
    val patientId: Long = 0,
    val fullName: String,
    val dateOfBirth: String, // YYYY-MM-DD
    val gender: String, // Male, Female, Other
    val phone: String,
    val email: String,
    val address: String,
    val bloodGroup: String, // A+, B+, O+, etc.
    val emergencyContact: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DoctorEntity::class,
            parentColumns = ["doctorId"],
            childColumns = ["doctorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("patientId"),
        Index("doctorId"),
        Index(value = ["doctorId", "appointmentDate", "appointmentTime"], unique = true)
    ]
)
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val appointmentId: Long = 0,
    val patientId: Long,
    val doctorId: Long,
    val appointmentDate: String, // YYYY-MM-DD
    val appointmentTime: String, // HH:MM
    val status: String = "Scheduled", // Scheduled, In-Progress, Completed, Cancelled
    val reason: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "medical_records",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DoctorEntity::class,
            parentColumns = ["doctorId"],
            childColumns = ["doctorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId"), Index("doctorId")]
)
data class MedicalRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val recordId: Long = 0,
    val patientId: Long,
    val doctorId: Long,
    val diagnosis: String,
    val notes: String,
    val recordDate: String,
    val createdAt: Long = System.currentTimeMillis()
)
