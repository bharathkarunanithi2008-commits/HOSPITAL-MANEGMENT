package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.PatientEntity
import com.example.ui.HospitalViewModel
import com.example.ui.UserRole
import com.example.ui.components.AddPatientDialog
import com.example.ui.components.EditPatientDialog

@Composable
fun PatientsScreen(viewModel: HospitalViewModel) {
    val patients by viewModel.filteredPatients.collectAsState()
    val searchQuery by viewModel.patientSearchQuery.collectAsState()
    val bloodFilter by viewModel.patientBloodFilter.collectAsState()
    val role by viewModel.currentRole.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingPatient by remember { mutableStateOf<PatientEntity?>(null) }
    var deletingPatient by remember { mutableStateOf<PatientEntity?>(null) }

    val bloodOptions = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Patient Registry",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${patients.size} registered patients",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setPatientSearchQuery(it) },
                placeholder = { Text("Search by name, email, or phone...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setPatientSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("patient_search_input")
            )

            // Blood group filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = bloodFilter == null,
                    onClick = { viewModel.setPatientBloodFilter(null) },
                    label = { Text("All") }
                )
                bloodOptions.forEach { bg ->
                    FilterChip(
                        selected = bloodFilter == bg,
                        onClick = {
                            if (bloodFilter == bg) viewModel.setPatientBloodFilter(null)
                            else viewModel.setPatientBloodFilter(bg)
                        },
                        label = { Text(bg) }
                    )
                }
            }

            // Patients list
            if (patients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No patient records match the filter criteria.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(patients, key = { it.patientId }) { patient ->
                        PatientCard(
                            patient = patient,
                            canEdit = role == UserRole.ADMIN || role == UserRole.RECEPTIONIST,
                            canDelete = role == UserRole.ADMIN,
                            onEdit = { editingPatient = patient },
                            onDelete = { deletingPatient = patient }
                        )
                    }
                }
            }
        }

        // Add Patient FAB (accessible to Admin & Receptionist)
        if (role == UserRole.ADMIN || role == UserRole.RECEPTIONIST) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .testTag("add_patient_fab"),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Register Patient")
            }
        }
    }

    // Dialogs
    if (showAddDialog) {
        AddPatientDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, dob, gender, phone, email, address, blood, emergency ->
                viewModel.createPatient(name, dob, gender, phone, email, address, blood, emergency) {
                    showAddDialog = false
                }
            }
        )
    }

    editingPatient?.let { patient ->
        EditPatientDialog(
            patient = patient,
            onDismiss = { editingPatient = null },
            onConfirm = { updated ->
                viewModel.updatePatient(updated) {
                    editingPatient = null
                }
            }
        )
    }

    deletingPatient?.let { patient ->
        AlertDialog(
            onDismissRequest = { deletingPatient = null },
            title = { Text("Confirm Record Deletion") },
            text = { Text("Are you sure you want to delete patient '${patient.fullName}' (#${patient.patientId})? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePatient(patient)
                        deletingPatient = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingPatient = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PatientCard(
    patient: PatientEntity,
    canEdit: Boolean,
    canDelete: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("patient_card_${patient.patientId}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = patient.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: #${patient.patientId} • DOB: ${patient.dateOfBirth} • ${patient.gender}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = patient.bloodGroup,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }

                    if (canEdit) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Patient", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    if (canDelete) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Patient", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Phone: ${patient.phone} • Email: ${patient.email}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (patient.address.isNotBlank()) {
                Text(
                    text = "Address: ${patient.address}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (patient.emergencyContact.isNotBlank()) {
                Text(
                    text = "Emergency: ${patient.emergencyContact}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
