package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppointmentEntity
import com.example.data.local.AppointmentWithDetails
import com.example.data.local.DoctorEntity
import com.example.data.local.MedicalRecordEntity
import com.example.data.local.MedicalRecordWithDetails
import com.example.data.local.PatientEntity
import com.example.data.repository.HospitalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UserRole(val label: String) {
    ADMIN("Administrator"),
    DOCTOR("Doctor"),
    RECEPTIONIST("Receptionist"),
    PATIENT("Patient")
}

enum class NavigationTab(val label: String) {
    DASHBOARD("Dashboard"),
    PATIENTS("Patients"),
    DOCTORS("Doctors"),
    APPOINTMENTS("Appointments"),
    RECORDS("Medical Records")
}

data class DashboardStatsUiState(
    val totalPatients: Int = 0,
    val totalDoctors: Int = 0,
    val totalAppointments: Int = 0,
    val pendingAppointments: Int = 0,
    val totalRecords: Int = 0
)

class HospitalViewModel(private val repository: HospitalRepository) : ViewModel() {

    // Navigation & Role State
    private val _currentTab = MutableStateFlow(NavigationTab.DASHBOARD)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Search and Filters
    private val _patientSearchQuery = MutableStateFlow("")
    val patientSearchQuery: StateFlow<String> = _patientSearchQuery.asStateFlow()

    private val _patientBloodFilter = MutableStateFlow<String?>(null)
    val patientBloodFilter: StateFlow<String?> = _patientBloodFilter.asStateFlow()

    private val _doctorSearchQuery = MutableStateFlow("")
    val doctorSearchQuery: StateFlow<String> = _doctorSearchQuery.asStateFlow()

    private val _appointmentStatusFilter = MutableStateFlow<String?>(null)
    val appointmentStatusFilter: StateFlow<String?> = _appointmentStatusFilter.asStateFlow()

    // Alert / Feedback message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Data Streams
    val rawPatients = repository.allPatients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredPatients: StateFlow<List<PatientEntity>> = combine(
        rawPatients,
        _patientSearchQuery,
        _patientBloodFilter
    ) { patients, query, blood ->
        patients.filter { p ->
            val matchesQuery = query.isBlank() ||
                    p.fullName.contains(query, ignoreCase = true) ||
                    p.email.contains(query, ignoreCase = true) ||
                    p.phone.contains(query)
            val matchesBlood = blood == null || p.bloodGroup.equals(blood, ignoreCase = true)
            matchesQuery && matchesBlood
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawDoctors = repository.allDoctors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredDoctors: StateFlow<List<DoctorEntity>> = combine(
        rawDoctors,
        _doctorSearchQuery
    ) { doctors, query ->
        if (query.isBlank()) doctors else doctors.filter { d ->
            d.fullName.contains(query, ignoreCase = true) ||
            d.specialization.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawAppointments = repository.allAppointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredAppointments: StateFlow<List<AppointmentWithDetails>> = combine(
        rawAppointments,
        _appointmentStatusFilter
    ) { appts, status ->
        if (status == null) appts else appts.filter { it.status.equals(status, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val medicalRecords: StateFlow<List<MedicalRecordWithDetails>> = repository.allMedicalRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashboardStats: StateFlow<DashboardStatsUiState> = combine(
        repository.patientsCount,
        repository.doctorsCount,
        repository.appointmentsCount,
        repository.pendingAppointmentsCount,
        repository.medicalRecordsCount
    ) { patients, doctors, appointments, pending, records ->
        DashboardStatsUiState(
            totalPatients = patients,
            totalDoctors = doctors,
            totalAppointments = appointments,
            pendingAppointments = pending,
            totalRecords = records
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStatsUiState())

    // --- Actions ---
    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun selectRole(role: UserRole) {
        _currentRole.value = role
        showMessage("Switched role to: ${role.label}")
    }

    fun setPatientSearchQuery(query: String) {
        _patientSearchQuery.value = query
    }

    fun setPatientBloodFilter(blood: String?) {
        _patientBloodFilter.value = blood
    }

    fun setDoctorSearchQuery(query: String) {
        _doctorSearchQuery.value = query
    }

    fun setAppointmentStatusFilter(status: String?) {
        _appointmentStatusFilter.value = status
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    private fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    // CRUD: Patients
    fun createPatient(
        fullName: String,
        dob: String,
        gender: String,
        phone: String,
        email: String,
        address: String,
        bloodGroup: String,
        emergencyContact: String,
        onSuccess: () -> Unit
    ) {
        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || dob.isBlank()) {
            showMessage("Validation Error: Please fill in all required fields.")
            return
        }
        viewModelScope.launch {
            val patient = PatientEntity(
                fullName = fullName.trim(),
                dateOfBirth = dob.trim(),
                gender = gender,
                phone = phone.trim(),
                email = email.trim(),
                address = address.trim(),
                bloodGroup = bloodGroup,
                emergencyContact = emergencyContact.trim()
            )
            repository.insertPatient(patient)
                .onSuccess {
                    showMessage("Patient '${fullName.trim()}' registered successfully.")
                    onSuccess()
                }
                .onFailure {
                    showMessage("Error: ${it.localizedMessage}")
                }
        }
    }

    fun updatePatient(patient: PatientEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updatePatient(patient)
                .onSuccess {
                    showMessage("Patient #${patient.patientId} updated.")
                    onSuccess()
                }
                .onFailure {
                    showMessage("Error: ${it.localizedMessage}")
                }
        }
    }

    fun deletePatient(patient: PatientEntity) {
        viewModelScope.launch {
            repository.deletePatient(patient)
                .onSuccess {
                    showMessage("Patient record removed.")
                }
                .onFailure {
                    showMessage("Error deleting patient: ${it.localizedMessage}")
                }
        }
    }

    // CRUD: Doctors
    fun createDoctor(
        fullName: String,
        specialization: String,
        phone: String,
        email: String,
        availability: String,
        onSuccess: () -> Unit
    ) {
        if (fullName.isBlank() || specialization.isBlank() || email.isBlank()) {
            showMessage("Validation Error: Name, specialization, and email are required.")
            return
        }
        viewModelScope.launch {
            val doctor = DoctorEntity(
                fullName = fullName.trim(),
                specialization = specialization.trim(),
                phone = phone.trim(),
                email = email.trim(),
                availability = availability.trim()
            )
            repository.insertDoctor(doctor)
                .onSuccess {
                    showMessage("Doctor '${fullName.trim()}' added.")
                    onSuccess()
                }
                .onFailure {
                    showMessage("Error: ${it.localizedMessage}")
                }
        }
    }

    // CRUD: Appointments
    fun bookAppointment(
        patientId: Long,
        doctorId: Long,
        date: String,
        time: String,
        reason: String,
        onSuccess: () -> Unit
    ) {
        if (date.isBlank() || reason.isBlank()) {
            showMessage("Validation Error: Date and Reason are required.")
            return
        }
        viewModelScope.launch {
            val appointment = AppointmentEntity(
                patientId = patientId,
                doctorId = doctorId,
                appointmentDate = date.trim(),
                appointmentTime = time.trim(),
                reason = reason.trim()
            )
            repository.bookAppointment(appointment)
                .onSuccess {
                    showMessage("Appointment scheduled successfully.")
                    onSuccess()
                }
                .onFailure {
                    showMessage(it.localizedMessage ?: "Failed to book appointment")
                }
        }
    }

    fun updateAppointmentStatus(appointmentId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(appointmentId, newStatus)
                .onSuccess {
                    showMessage("Appointment #$appointmentId status changed to '$newStatus'.")
                }
        }
    }

    fun deleteAppointment(appointmentId: Long) {
        viewModelScope.launch {
            repository.deleteAppointment(appointmentId)
                .onSuccess {
                    showMessage("Appointment deleted.")
                }
        }
    }

    // CRUD: Medical Records
    fun createMedicalRecord(
        patientId: Long,
        doctorId: Long,
        diagnosis: String,
        notes: String,
        recordDate: String,
        onSuccess: () -> Unit
    ) {
        if (diagnosis.isBlank() || notes.isBlank()) {
            showMessage("Validation Error: Diagnosis and Notes are required.")
            return
        }
        viewModelScope.launch {
            val record = MedicalRecordEntity(
                patientId = patientId,
                doctorId = doctorId,
                diagnosis = diagnosis.trim(),
                notes = notes.trim(),
                recordDate = recordDate.trim()
            )
            repository.insertMedicalRecord(record)
                .onSuccess {
                    showMessage("Clinical encounter documented successfully.")
                    onSuccess()
                }
                .onFailure {
                    showMessage("Error: ${it.localizedMessage}")
                }
        }
    }

    fun deleteMedicalRecord(recordId: Long) {
        viewModelScope.launch {
            repository.deleteMedicalRecord(recordId)
                .onSuccess {
                    showMessage("Clinical record removed.")
                }
        }
    }
}

class HospitalViewModelFactory(private val repository: HospitalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HospitalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HospitalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
