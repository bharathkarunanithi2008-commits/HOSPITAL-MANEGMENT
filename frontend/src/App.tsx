import React, { useState } from 'react';
import { Navbar } from './components/Navbar';
import { DashboardPage } from './pages/DashboardPage';
import { PatientsPage } from './pages/PatientsPage';
import { DoctorsPage } from './pages/DoctorsPage';
import { AppointmentsPage } from './pages/AppointmentsPage';
import { MedicalRecordsPage } from './pages/MedicalRecordsPage';

export function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [currentRole, setCurrentRole] = useState('Admin');

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col text-slate-900">
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        currentRole={currentRole}
        setCurrentRole={setCurrentRole}
      />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {activeTab === 'dashboard' && <DashboardPage />}
        {activeTab === 'patients' && <PatientsPage />}
        {activeTab === 'doctors' && <DoctorsPage />}
        {activeTab === 'appointments' && <AppointmentsPage />}
        {activeTab === 'records' && <MedicalRecordsPage />}
      </main>

      <footer className="bg-white border-t border-slate-200 py-4 text-center text-xs text-slate-500">
        Hospital / Patient Management System &copy; 2026 — Built strictly to Healthcare SOP Standards
      </footer>
    </div>
  );
}

export default App;
