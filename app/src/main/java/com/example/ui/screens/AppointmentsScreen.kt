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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.data.local.AppointmentWithDetails
import com.example.ui.HospitalViewModel
import com.example.ui.components.BookAppointmentDialog

@Composable
fun AppointmentsScreen(viewModel: HospitalViewModel) {
    val appointments by viewModel.filteredAppointments.collectAsState()
    val patients by viewModel.rawPatients.collectAsState()
    val doctors by viewModel.rawDoctors.collectAsState()
    val statusFilter by viewModel.appointmentStatusFilter.collectAsState()

    var showBookDialog by remember { mutableStateOf(false) }

    val statusOptions = listOf("Scheduled", "In-Progress", "Completed", "Cancelled")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Appointment Roster",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = statusFilter == null,
                    onClick = { viewModel.setAppointmentStatusFilter(null) },
                    label = { Text("All Statuses") }
                )
                statusOptions.forEach { st ->
                    FilterChip(
                        selected = statusFilter == st,
                        onClick = {
                            if (statusFilter == st) viewModel.setAppointmentStatusFilter(null)
                            else viewModel.setAppointmentStatusFilter(st)
                        },
                        label = { Text(st) }
                    )
                }
            }

            if (appointments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No scheduled appointments found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(appointments, key = { it.appointmentId }) { appt ->
                        AppointmentCard(
                            appointment = appt,
                            onComplete = { viewModel.updateAppointmentStatus(appt.appointmentId, "Completed") },
                            onCancel = { viewModel.updateAppointmentStatus(appt.appointmentId, "Cancelled") },
                            onDelete = { viewModel.deleteAppointment(appt.appointmentId) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showBookDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("book_appointment_fab"),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Book Appointment")
        }
    }

    if (showBookDialog) {
        BookAppointmentDialog(
            patients = patients,
            doctors = doctors,
            onDismiss = { showBookDialog = false },
            onConfirm = { patientId, doctorId, date, time, reason ->
                viewModel.bookAppointment(patientId, doctorId, date, time, reason) {
                    showBookDialog = false
                }
            }
        )
    }
}

@Composable
private fun AppointmentCard(
    appointment: AppointmentWithDetails,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (appointment.status) {
        "Completed" -> MaterialTheme.colorScheme.primary
        "Cancelled" -> MaterialTheme.colorScheme.error
        "In-Progress" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                        text = appointment.patientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "With ${appointment.doctorName} (${appointment.doctorSpecialization})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Card(colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.15f))) {
                    Text(
                        text = appointment.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Slot: ${appointment.appointmentDate} at ${appointment.appointmentTime}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Reason: ${appointment.reason}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (appointment.status != "Completed" && appointment.status != "Cancelled") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onComplete) {
                        Icon(Icons.Default.Check, contentDescription = "Mark Complete", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.error)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}
