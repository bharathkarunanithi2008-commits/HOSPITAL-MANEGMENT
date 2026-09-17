from django.test import TestCase
from django.utils import timezone
from datetime import date, timedelta
from rest_framework.test import APIClient
from rest_framework import status
from .models import Department, Doctor, Patient, Appointment, MedicalRecord

class HospitalSystemTests(TestCase):
    def setUp(self):
        self.client = APIClient()

        # Create department
        self.department = Department.objects.create(
            name="Cardiology",
            description="Cardiovascular medicine and care"
        )

        # Create doctor
        self.doctor = Doctor.objects.create(
            full_name="Dr. Sarah Jenkins",
            specialization="Cardiologist",
            department=self.department,
            phone="+1-555-0101",
            email="sarah.jenkins@hospital.org",
            availability="Mon-Fri 09:00 - 17:00"
        )

        # Create patient
        self.patient = Patient.objects.create(
            full_name="John Doe",
            date_of_birth=date(1990, 5, 15),
            gender="Male",
            phone="+1-555-0202",
            email="john.doe@example.com",
            address="123 Main Street, Suite 4",
            blood_group="O+",
            emergency_contact="Jane Doe (+1-555-0203)"
        )

    def test_patient_crud_lifecycle(self):
        # 1. CREATE
        new_patient_data = {
            "full_name": "Alice Smith",
            "date_of_birth": "1995-08-20",
            "gender": "Female",
            "phone": "+1-555-0303",
            "email": "alice.smith@example.com",
            "address": "789 Pine Ave",
            "blood_group": "A+",
            "emergency_contact": "Bob Smith (+1-555-0304)"
        }
        res_create = self.client.post('/api/patients/', new_patient_data, format='json')
        self.assertEqual(res_create.status_code, status.HTTP_201_CREATED)
        new_id = res_create.data['patient_id']
        self.assertTrue(Patient.objects.filter(patient_id=new_id).exists())

        # 2. READ ALL
        res_list = self.client.get('/api/patients/')
        self.assertEqual(res_list.status_code, status.HTTP_200_OK)

        # 3. READ ONE
        res_read = self.client.get(f'/api/patients/{new_id}/')
        self.assertEqual(res_read.status_code, status.HTTP_200_OK)
        self.assertEqual(res_read.data['full_name'], "Alice Smith")

        # 4. UPDATE
        res_update = self.client.patch(f'/api/patients/{new_id}/', {'phone': '+1-555-9999'}, format='json')
        self.assertEqual(res_update.status_code, status.HTTP_200_OK)
        self.assertEqual(Patient.objects.get(patient_id=new_id).phone, '+1-555-9999')

        # 5. DELETE
        res_del = self.client.delete(f'/api/patients/{new_id}/')
        self.assertEqual(res_del.status_code, status.HTTP_204_NO_CONTENT)
        self.assertFalse(Patient.objects.filter(patient_id=new_id).exists())

    def test_patient_validation_duplicate_email(self):
        duplicate_data = {
            "full_name": "Copy Cat",
            "date_of_birth": "1992-01-01",
            "gender": "Male",
            "phone": "+1-555-1111",
            "email": "john.doe@example.com",  # Already taken
            "address": "456 Oak St",
            "blood_group": "B+",
            "emergency_contact": "Someone (+1-555-2222)"
        }
        res = self.client.post('/api/patients/', duplicate_data, format='json')
        self.assertEqual(res.status_code, status.HTTP_400_BAD_REQUEST)

    def test_appointment_double_booking_prevention(self):
        tomorrow = date.today() + timedelta(days=1)
        appt1 = Appointment.objects.create(
            patient=self.patient,
            doctor=self.doctor,
            appointment_date=tomorrow,
            appointment_time="10:00",
            status="Scheduled",
            reason="Checkup 1"
        )
        self.assertIsNotNone(appt1.appointment_id)

        # Attempt to book exact same slot
        res = self.client.post('/api/appointments/', {
            "patient": self.patient.patient_id,
            "doctor": self.doctor.doctor_id,
            "appointment_date": tomorrow.strftime("%Y-%m-%d"),
            "appointment_time": "10:00",
            "status": "Scheduled",
            "reason": "Checkup 2"
        }, format='json')
        self.assertEqual(res.status_code, status.HTTP_400_BAD_REQUEST)

    def test_medical_record_crud(self):
        record_data = {
            "patient": self.patient.patient_id,
            "doctor": self.doctor.doctor_id,
            "diagnosis": "Normal sinus rhythm, blood pressure optimal",
            "notes": "Patient reports regular exercise and low sodium diet.",
            "record_date": str(date.today())
        }
        res = self.client.post('/api/medical-records/', record_data, format='json')
        self.assertEqual(res.status_code, status.HTTP_201_CREATED)
        record_id = res.data['record_id']
        self.assertTrue(MedicalRecord.objects.filter(record_id=record_id).exists())
