export interface Department {
  department_id: number;
  name: string;
  description?: string;
  doctors_count?: number;
  created_at?: string;
  updated_at?: string;
}

export interface Doctor {
  doctor_id: number;
  full_name: string;
  specialization: string;
  department?: number;
  department_name?: string;
  phone: string;
  email: string;
  availability: string;
  created_at?: string;
  updated_at?: string;
}

export interface Patient {
  patient_id: number;
  full_name: string;
  date_of_birth: string;
  gender: 'Male' | 'Female' | 'Other';
  phone: string;
  email: string;
  address: string;
  blood_group: 'A+' | 'A-' | 'B+' | 'B-' | 'AB+' | 'AB-' | 'O+' | 'O-';
  emergency_contact: string;
  appointments_count?: number;
  records_count?: number;
  created_at?: string;
  updated_at?: string;
}

export interface Appointment {
  appointment_id: number;
  patient: number;
  patient_name?: string;
  doctor: number;
  doctor_name?: string;
  doctor_specialization?: string;
  appointment_date: string;
  appointment_time: string;
  status: 'Scheduled' | 'In-Progress' | 'Completed' | 'Cancelled';
  reason: string;
  created_at?: string;
  updated_at?: string;
}

export interface MedicalRecord {
  record_id: number;
  patient: number;
  patient_name?: string;
  doctor: number;
  doctor_name?: string;
  diagnosis: string;
  notes: string;
  record_date: string;
  created_at?: string;
  updated_at?: string;
}

export interface DashboardStats {
  total_patients: number;
  total_doctors: number;
  total_appointments: number;
  today_appointments: number;
  pending_appointments: number;
  completed_appointments: number;
  total_records: number;
}
