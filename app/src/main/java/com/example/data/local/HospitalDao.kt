package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class AppointmentWithDetails(
    val appointmentId: Long,
    val patientId: Long,
    val patientName: String,
    val doctorId: Long,
    val doctorName: String,
    val doctorSpecialization: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val status: String,
    val reason: String
)

data class MedicalRecordWithDetails(
    val recordId: Long,
    val patientId: Long,
    val patientName: String,
    val doctorId: Long,
    val doctorName: String,
    val diagnosis: String,
    val notes: String,
    val recordDate: String
)

@Dao
interface HospitalDao {

    // --- DEPARTMENTS ---
    @Query("SELECT * FROM departments ORDER BY name ASC")
    fun getAllDepartments(): Flow<List<DepartmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartment(dept: DepartmentEntity): Long

    // --- DOCTORS ---
    @Query("SELECT * FROM doctors ORDER BY fullName ASC")
    fun getAllDoctors(): Flow<List<DoctorEntity>>

    @Query("SELECT * FROM doctors WHERE doctorId = :id LIMIT 1")
    suspend fun getDoctorById(id: Long): DoctorEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDoctor(doctor: DoctorEntity): Long

    @Update
    suspend fun updateDoctor(doctor: DoctorEntity)

    @Delete
    suspend fun deleteDoctor(doctor: DoctorEntity)

    // --- PATIENTS ---
    @Query("SELECT * FROM patients ORDER BY patientId DESC")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE patientId = :id LIMIT 1")
    suspend fun getPatientById(id: Long): PatientEntity?

    @Query("SELECT * FROM patients WHERE email = :email LIMIT 1")
    suspend fun getPatientByEmail(email: String): PatientEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPatient(patient: PatientEntity): Long

    @Update
    suspend fun updatePatient(patient: PatientEntity)

    @Delete
    suspend fun deletePatient(patient: PatientEntity)

    // --- APPOINTMENTS ---
    @Query("""
        SELECT a.appointmentId, a.patientId, p.fullName AS patientName,
               a.doctorId, d.fullName AS doctorName, d.specialization AS doctorSpecialization,
               a.appointmentDate, a.appointmentTime, a.status, a.reason
        FROM appointments a
        INNER JOIN patients p ON a.patientId = p.patientId
        INNER JOIN doctors d ON a.doctorId = d.doctorId
        ORDER BY a.appointmentDate DESC, a.appointmentTime ASC
    """)
    fun getAllAppointmentsWithDetails(): Flow<List<AppointmentWithDetails>>

    @Query("""
        SELECT COUNT(*) FROM appointments 
        WHERE doctorId = :doctorId 
          AND appointmentDate = :date 
          AND appointmentTime = :time 
          AND status != 'Cancelled'
          AND (:excludeId IS NULL OR appointmentId != :excludeId)
    """)
    suspend fun checkAppointmentConflict(doctorId: Long, date: String, time: String, excludeId: Long? = null): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Query("UPDATE appointments SET status = :newStatus WHERE appointmentId = :id")
    suspend fun updateAppointmentStatus(id: Long, newStatus: String)

    @Query("DELETE FROM appointments WHERE appointmentId = :id")
    suspend fun deleteAppointmentById(id: Long)

    // --- MEDICAL RECORDS ---
    @Query("""
        SELECT m.recordId, m.patientId, p.fullName AS patientName,
               m.doctorId, d.fullName AS doctorName,
               m.diagnosis, m.notes, m.recordDate
        FROM medical_records m
        INNER JOIN patients p ON m.patientId = p.patientId
        INNER JOIN doctors d ON m.doctorId = d.doctorId
        ORDER BY m.recordDate DESC, m.recordId DESC
    """)
    fun getAllMedicalRecordsWithDetails(): Flow<List<MedicalRecordWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalRecord(record: MedicalRecordEntity): Long

    @Query("DELETE FROM medical_records WHERE recordId = :id")
    suspend fun deleteMedicalRecordById(id: Long)

    // --- DASHBOARD COUNTS ---
    @Query("SELECT COUNT(*) FROM patients")
    fun getPatientsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM doctors")
    fun getDoctorsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM appointments")
    fun getAppointmentsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM appointments WHERE status = 'Scheduled'")
    fun getPendingAppointmentsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM medical_records")
    fun getMedicalRecordsCount(): Flow<Int>
}
