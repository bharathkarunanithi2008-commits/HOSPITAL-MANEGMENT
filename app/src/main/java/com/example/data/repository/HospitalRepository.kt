package com.example.data.repository

import com.example.data.local.AppointmentEntity
import com.example.data.local.AppointmentWithDetails
import com.example.data.local.DoctorEntity
import com.example.data.local.HospitalDao
import com.example.data.local.MedicalRecordEntity
import com.example.data.local.MedicalRecordWithDetails
import com.example.data.local.PatientEntity
import kotlinx.coroutines.flow.Flow

class HospitalRepository(private val dao: HospitalDao) {

    // Patients
    val allPatients: Flow<List<PatientEntity>> = dao.getAllPatients()
    val patientsCount: Flow<Int> = dao.getPatientsCount()

    suspend fun insertPatient(patient: PatientEntity): Result<Long> {
        return try {
            val existing = dao.getPatientByEmail(patient.email)
            if (existing != null) {
                return Result.failure(Exception("A patient with email '${patient.email}' already exists."))
            }
            val id = dao.insertPatient(patient)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePatient(patient: PatientEntity): Result<Unit> {
        return try {
            dao.updatePatient(patient)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePatient(patient: PatientEntity): Result<Unit> {
        return try {
            dao.deletePatient(patient)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Doctors
    val allDoctors: Flow<List<DoctorEntity>> = dao.getAllDoctors()
    val doctorsCount: Flow<Int> = dao.getDoctorsCount()

    suspend fun insertDoctor(doctor: DoctorEntity): Result<Long> {
        return try {
            val id = dao.insertDoctor(doctor)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Appointments
    val allAppointments: Flow<List<AppointmentWithDetails>> = dao.getAllAppointmentsWithDetails()
    val appointmentsCount: Flow<Int> = dao.getAppointmentsCount()
    val pendingAppointmentsCount: Flow<Int> = dao.getPendingAppointmentsCount()

    suspend fun bookAppointment(appointment: AppointmentEntity): Result<Long> {
        return try {
            val conflict = dao.checkAppointmentConflict(
                doctorId = appointment.doctorId,
                date = appointment.appointmentDate,
                time = appointment.appointmentTime
            )
            if (conflict > 0) {
                return Result.failure(
                    Exception("Doctor is already booked on ${appointment.appointmentDate} at ${appointment.appointmentTime}. Double booking prevented by SOP policy.")
                )
            }
            val id = dao.insertAppointment(appointment)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAppointmentStatus(appointmentId: Long, status: String): Result<Unit> {
        return try {
            dao.updateAppointmentStatus(appointmentId, status)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAppointment(appointmentId: Long): Result<Unit> {
        return try {
            dao.deleteAppointmentById(appointmentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Medical Records
    val allMedicalRecords: Flow<List<MedicalRecordWithDetails>> = dao.getAllMedicalRecordsWithDetails()
    val medicalRecordsCount: Flow<Int> = dao.getMedicalRecordsCount()

    suspend fun insertMedicalRecord(record: MedicalRecordEntity): Result<Long> {
        return try {
            val id = dao.insertMedicalRecord(record)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMedicalRecord(recordId: Long): Result<Unit> {
        return try {
            dao.deleteMedicalRecordById(recordId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
