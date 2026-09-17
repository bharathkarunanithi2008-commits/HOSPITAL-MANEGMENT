package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.data.local.HospitalDatabase
import com.example.data.repository.HospitalRepository
import com.example.ui.HospitalApp
import com.example.ui.HospitalViewModel
import com.example.ui.HospitalViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val database by lazy {
        HospitalDatabase.getDatabase(applicationContext, lifecycleScope)
    }

    private val repository by lazy {
        HospitalRepository(database.hospitalDao())
    }

    private val viewModel by viewModels<HospitalViewModel> {
        HospitalViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HospitalApp(viewModel = viewModel)
            }
        }
    }
}
