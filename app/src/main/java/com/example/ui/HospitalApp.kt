package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.screens.AppointmentsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DoctorsScreen
import com.example.ui.screens.MedicalRecordsScreen
import com.example.ui.screens.PatientsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalApp(viewModel: HospitalViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var roleMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hospital Management",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    Box {
                        IconButton(
                            onClick = { roleMenuExpanded = true },
                            modifier = Modifier.testTag("role_switcher_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Switch Role",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        DropdownMenu(
                            expanded = roleMenuExpanded,
                            onDismissRequest = { roleMenuExpanded = false }
                        ) {
                            UserRole.values().forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = role.label + if (role == currentRole) " (Active)" else "",
                                            fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        viewModel.selectRole(role)
                                        roleMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == NavigationTab.DASHBOARD,
                    onClick = { viewModel.selectTab(NavigationTab.DASHBOARD) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    modifier = Modifier.testTag("tab_dashboard")
                )
                NavigationBarItem(
                    selected = currentTab == NavigationTab.PATIENTS,
                    onClick = { viewModel.selectTab(NavigationTab.PATIENTS) },
                    icon = { Icon(Icons.Default.People, contentDescription = "Patients") },
                    label = { Text("Patients") },
                    modifier = Modifier.testTag("tab_patients")
                )
                NavigationBarItem(
                    selected = currentTab == NavigationTab.DOCTORS,
                    onClick = { viewModel.selectTab(NavigationTab.DOCTORS) },
                    icon = { Icon(Icons.Default.LocalHospital, contentDescription = "Doctors") },
                    label = { Text("Doctors") },
                    modifier = Modifier.testTag("tab_doctors")
                )
                NavigationBarItem(
                    selected = currentTab == NavigationTab.APPOINTMENTS,
                    onClick = { viewModel.selectTab(NavigationTab.APPOINTMENTS) },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Appointments") },
                    label = { Text("Appointments") },
                    modifier = Modifier.testTag("tab_appointments")
                )
                NavigationBarItem(
                    selected = currentTab == NavigationTab.RECORDS,
                    onClick = { viewModel.selectTab(NavigationTab.RECORDS) },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Records") },
                    label = { Text("Records") },
                    modifier = Modifier.testTag("tab_records")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                NavigationTab.PATIENTS -> PatientsScreen(viewModel = viewModel)
                NavigationTab.DOCTORS -> DoctorsScreen(viewModel = viewModel)
                NavigationTab.APPOINTMENTS -> AppointmentsScreen(viewModel = viewModel)
                NavigationTab.RECORDS -> MedicalRecordsScreen(viewModel = viewModel)
            }
        }
    }
}
