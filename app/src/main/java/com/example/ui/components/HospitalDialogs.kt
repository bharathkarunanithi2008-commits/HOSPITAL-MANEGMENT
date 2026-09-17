package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.DoctorEntity
import com.example.data.local.PatientEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPatientDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        fullName: String,
        dob: String,
        gender: String,
        phone: String,
        email: String,
        address: String,
        bloodGroup: String,
        emergencyContact: String
    ) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("1990-01-01") }
    var gender by remember { mutableStateOf("Male") }
    var phone by remember { mutableStateOf("+1-555-") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var emergencyContact by remember { mutableStateOf("") }

    val bloodOptions = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    val genderOptions = listOf("Male", "Female", "Other")

    var bloodExpanded by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Patient", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("patient_name_input")
                )
                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("Date of Birth (YYYY-MM-DD) *") },
                    modifier = Modifier.fillMaxWidth().testTag("patient_dob_input")
                )

                // Gender Dropdown
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = it }
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genderOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    gender = opt
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                // Blood Group Dropdown
                ExposedDropdownMenuBox(
                    expanded = bloodExpanded,
                    onExpandedChange = { bloodExpanded = it }
                ) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Blood Group") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = bloodExpanded,
                        onDismissRequest = { bloodExpanded = false }
                    ) {
                        bloodOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    bloodGroup = opt
                                    bloodExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number *") },
                    modifier = Modifier.fillMaxWidth().testTag("patient_phone_input")
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address *") },
                    modifier = Modifier.fillMaxWidth().testTag("patient_email_input")
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Home Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    label = { Text("Emergency Contact (Name & Phone)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(fullName, dob, gender, phone, email, address, bloodGroup, emergencyContact)
                },
                modifier = Modifier.testTag("submit_patient_button")
            ) {
                Text("Register")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditPatientDialog(
    patient: PatientEntity,
    onDismiss: () -> Unit,
    onConfirm: (PatientEntity) -> Unit
) {
    var fullName by remember { mutableStateOf(patient.fullName) }
    var phone by remember { mutableStateOf(patient.phone) }
    var email by remember { mutableStateOf(patient.email) }
    var address by remember { mutableStateOf(patient.address) }
    var emergencyContact by remember { mutableStateOf(patient.emergencyContact) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Patient #${patient.patientId}") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    label = { Text("Emergency Contact") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        patient.copy(
                            fullName = fullName,
                            phone = phone,
                            email = email,
                            address = address,
                            emergencyContact = emergencyContact
                        )
                    )
                }
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddDoctorDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, spec: String, phone: String, email: String, availability: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("Cardiology") }
    var phone by remember { mutableStateOf("+1-555-0100") }
    var email by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("Mon-Fri 09:00 - 17:00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medical Specialist") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Doctor Name *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = specialization,
                    onValueChange = { specialization = it },
                    label = { Text("Specialization *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = availability,
                    onValueChange = { availability = it },
                    label = { Text("Consultation Hours") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(name, specialization, phone, email, availability)
                }
            ) {
                Text("Add Doctor")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentDialog(
    patients: List<PatientEntity>,
    doctors: List<DoctorEntity>,
    onDismiss: () -> Unit,
    onConfirm: (patientId: Long, doctorId: Long, date: String, time: String, reason: String) -> Unit
) {
    var selectedPatientIndex by remember { mutableStateOf(0) }
    var selectedDoctorIndex by remember { mutableStateOf(0) }
    var date by remember { mutableStateOf("2026-09-28") }
    var time by remember { mutableStateOf("10:00") }
    var reason by remember { mutableStateOf("") }

    var patientDropdownExpanded by remember { mutableStateOf(false) }
    var doctorDropdownExpanded by remember { mutableStateOf(false) }
    var timeDropdownExpanded by remember { mutableStateOf(false) }

    val timeSlots = listOf("09:00", "10:00", "11:30", "14:00", "15:30")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Book Consultation Appointment") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (patients.isEmpty() || doctors.isEmpty()) {
                    Text(
                        "Please register at least one patient and one doctor before booking appointments.",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    // Patient picker
                    ExposedDropdownMenuBox(
                        expanded = patientDropdownExpanded,
                        onExpandedChange = { patientDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = patients[selectedPatientIndex].fullName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Patient") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = patientDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = patientDropdownExpanded,
                            onDismissRequest = { patientDropdownExpanded = false }
                        ) {
                            patients.forEachIndexed { idx, p ->
                                DropdownMenuItem(
                                    text = { Text("${p.fullName} (#${p.patientId})") },
                                    onClick = {
                                        selectedPatientIndex = idx
                                        patientDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Doctor picker
                    ExposedDropdownMenuBox(
                        expanded = doctorDropdownExpanded,
                        onExpandedChange = { doctorDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${doctors[selectedDoctorIndex].fullName} (${doctors[selectedDoctorIndex].specialization})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Specialist") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = doctorDropdownExpanded,
                            onDismissRequest = { doctorDropdownExpanded = false }
                        ) {
                            doctors.forEachIndexed { idx, d ->
                                DropdownMenuItem(
                                    text = { Text("${d.fullName} (${d.specialization})") },
                                    onClick = {
                                        selectedDoctorIndex = idx
                                        doctorDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date (YYYY-MM-DD) *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Time Slot Picker
                    ExposedDropdownMenuBox(
                        expanded = timeDropdownExpanded,
                        onExpandedChange = { timeDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = time,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Consultation Time Slot") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = timeDropdownExpanded,
                            onDismissRequest = { timeDropdownExpanded = false }
                        ) {
                            timeSlots.forEach { slot ->
                                DropdownMenuItem(
                                    text = { Text(slot) },
                                    onClick = {
                                        time = slot
                                        timeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Consultation Reason / Symptoms *") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = patients.isNotEmpty() && doctors.isNotEmpty(),
                onClick = {
                    onConfirm(
                        patients[selectedPatientIndex].patientId,
                        doctors[selectedDoctorIndex].doctorId,
                        date,
                        time,
                        reason
                    )
                }
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicalRecordDialog(
    patients: List<PatientEntity>,
    doctors: List<DoctorEntity>,
    onDismiss: () -> Unit,
    onConfirm: (patientId: Long, doctorId: Long, diagnosis: String, notes: String, date: String) -> Unit
) {
    var selectedPatientIndex by remember { mutableStateOf(0) }
    var selectedDoctorIndex by remember { mutableStateOf(0) }
    var diagnosis by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-09-17") }

    var patientDropdownExpanded by remember { mutableStateOf(false) }
    var doctorDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Author Clinical Record") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (patients.isEmpty() || doctors.isEmpty()) {
                    Text("Patients and Doctors must be registered first.", color = MaterialTheme.colorScheme.error)
                } else {
                    ExposedDropdownMenuBox(
                        expanded = patientDropdownExpanded,
                        onExpandedChange = { patientDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = patients[selectedPatientIndex].fullName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Patient") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = patientDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = patientDropdownExpanded,
                            onDismissRequest = { patientDropdownExpanded = false }
                        ) {
                            patients.forEachIndexed { idx, p ->
                                DropdownMenuItem(
                                    text = { Text(p.fullName) },
                                    onClick = {
                                        selectedPatientIndex = idx
                                        patientDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = doctorDropdownExpanded,
                        onExpandedChange = { doctorDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = doctors[selectedDoctorIndex].fullName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Attending Physician") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = doctorDropdownExpanded,
                            onDismissRequest = { doctorDropdownExpanded = false }
                        ) {
                            doctors.forEachIndexed { idx, d ->
                                DropdownMenuItem(
                                    text = { Text(d.fullName) },
                                    onClick = {
                                        selectedDoctorIndex = idx
                                        doctorDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = diagnosis,
                        onValueChange = { diagnosis = it },
                        label = { Text("Clinical Diagnosis *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Treatment Plan & Notes *") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = patients.isNotEmpty() && doctors.isNotEmpty(),
                onClick = {
                    onConfirm(
                        patients[selectedPatientIndex].patientId,
                        doctors[selectedDoctorIndex].doctorId,
                        diagnosis,
                        notes,
                        date
                    )
                }
            ) {
                Text("Save Record")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
