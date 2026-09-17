import axios from 'axios';
import { Patient, Doctor, Appointment, MedicalRecord, Department, DashboardStats } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor for attaching JWT Token if available
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('hms_auth_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const api = {
  // Dashboard Stats
  getStats: () => apiClient.get<{ success: boolean; data: DashboardStats }>('/dashboard/stats/'),

  // Patients CRUD
  getPatients: (params?: { search?: string; gender?: string; blood_group?: string }) =>
    apiClient.get<Patient[]>('/patients/', { params }),
  getPatient: (id: number) => apiClient.get<Patient>(`/patients/${id}/`),
  createPatient: (data: Omit<Patient, 'patient_id'>) => apiClient.post<Patient>('/patients/', data),
  updatePatient: (id: number, data: Partial<Patient>) => apiClient.patch<Patient>(`/patients/${id}/`, data),
  deletePatient: (id: number) => apiClient.delete(`/patients/${id}/`),

  // Doctors CRUD
  getDoctors: (params?: { search?: string; specialization?: string }) =>
    apiClient.get<Doctor[]>('/doctors/', { params }),
  getDoctor: (id: number) => apiClient.get<Doctor>(`/doctors/${id}/`),
  createDoctor: (data: Omit<Doctor, 'doctor_id'>) => apiClient.post<Doctor>('/doctors/', data),
  updateDoctor: (id: number, data: Partial<Doctor>) => apiClient.patch<Doctor>(`/doctors/${id}/`, data),
  deleteDoctor: (id: number) => apiClient.delete(`/doctors/${id}/`),

  // Appointments CRUD
  getAppointments: (params?: { patient?: number; doctor?: number; status?: string; date?: string }) =>
    apiClient.get<Appointment[]>('/appointments/', { params }),
  createAppointment: (data: Omit<Appointment, 'appointment_id'>) => apiClient.post<Appointment>('/appointments/', data),
  updateAppointment: (id: number, data: Partial<Appointment>) => apiClient.patch<Appointment>(`/appointments/${id}/`, data),
  deleteAppointment: (id: number) => apiClient.delete(`/appointments/${id}/`),

  // Medical Records CRUD
  getMedicalRecords: (params?: { patient?: number; doctor?: number }) =>
    apiClient.get<MedicalRecord[]>('/medical-records/', { params }),
  createMedicalRecord: (data: Omit<MedicalRecord, 'record_id'>) => apiClient.post<MedicalRecord>('/medical-records/', data),
  deleteMedicalRecord: (id: number) => apiClient.delete(`/medical-records/${id}/`),

  // Departments
  getDepartments: () => apiClient.get<Department[]>('/departments/'),
};
