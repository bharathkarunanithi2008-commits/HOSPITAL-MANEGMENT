"""
HMS Seed Script for Demonstration & Test Environments.
Clearly labeled DEMO DATA for hospital simulation.
Run with: python seed_data.py
"""
import os
import django
from datetime import date, timedelta

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'config.settings')
django.setup()

from apps.hospital.models import Department, Doctor, Patient, Appointment, MedicalRecord

def seed():
    print("Seeding DEMO data for Hospital Management System...")

    # Departments
    cardio, _ = Department.objects.get_or_create(
        name="Cardiology",
        defaults={"description": "Heart and vascular health care and diagnostic services"}
    )
    neuro, _ = Department.objects.get_or_create(
        name="Neurology",
        defaults={"description": "Brain, spinal cord, and nervous system disorders"}
    )
    pediatrics, _ = Department.objects.get_or_create(
        name="Pediatrics",
        defaults={"description": "Childhood and infant medical treatment"}
    )
    gen_med, _ = Department.objects.get_or_create(
        name="General Medicine",
        defaults={"description": "Comprehensive primary medical care and routine physicals"}
    )

    # Doctors
    doc1, _ = Doctor.objects.get_or_create(
        email="dr.thorne@hospital.org",
        defaults={
            "full_name": "Dr. Aris Thorne",
            "specialization": "Interventional Cardiology",
            "department": cardio,
            "phone": "+1-555-0101",
            "availability": "Mon-Fri 08:30 - 16:30"
        }
    )
    doc2, _ = Doctor.objects.get_or_create(
        email="dr.mirren@hospital.org",
        defaults={
            "full_name": "Dr. Helen Mirren",
            "specialization": "Pediatric Medicine",
            "department": pediatrics,
            "phone": "+1-555-0102",
            "availability": "Mon-Thu 09:00 - 15:00"
        }
    )
    doc3, _ = Doctor.objects.get_or_create(
        email="dr.chen@hospital.org",
        defaults={
            "full_name": "Dr. Marcus Chen",
            "specialization": "Clinical Neurology",
            "department": neuro,
            "phone": "+1-555-0103",
            "availability": "Tue-Sat 10:00 - 18:00"
        }
    )

    # Patients
    pat1, _ = Patient.objects.get_or_create(
        email="eleanor.vance@example.com",
        defaults={
            "full_name": "Eleanor Vance",
            "date_of_birth": date(1989, 4, 12),
            "gender": "Female",
            "phone": "+1-555-0201",
            "address": "42 Crestview Terrace, Boston, MA",
            "blood_group": "O+",
            "emergency_contact": "Marcus Vance (+1-555-0202)"
        }
    )
    pat2, _ = Patient.objects.get_or_create(
        email="david.miller@example.com",
        defaults={
            "full_name": "David Miller",
            "date_of_birth": date(1975, 11, 23),
            "gender": "Male",
            "phone": "+1-555-0203",
            "address": "108 Beacon Hill Lane, Boston, MA",
            "blood_group": "A-",
            "emergency_contact": "Sarah Miller (+1-555-0204)"
        }
    )

    # Appointments
    today = date.today()
    Appointment.objects.get_or_create(
        patient=pat1,
        doctor=doc1,
        appointment_date=today + timedelta(days=2),
        appointment_time="10:00",
        defaults={
            "status": "Scheduled",
            "reason": "Routine hypertension check and lipid panel evaluation"
        }
    )
    Appointment.objects.get_or_create(
        patient=pat2,
        doctor=doc3,
        appointment_date=today + timedelta(days=3),
        appointment_time="14:30",
        defaults={
            "status": "Scheduled",
            "reason": "Recurrent migraine symptoms consultation"
        }
    )

    # Medical Records
    MedicalRecord.objects.get_or_create(
        patient=pat1,
        doctor=doc1,
        diagnosis="Stage 1 Essential Hypertension",
        defaults={
            "notes": "Patient started on 5mg Amlodipine daily. Diet log recommended with reduced sodium intake. 60-day follow-up requested.",
            "record_date": today - timedelta(days=14)
        }
    )

    print("Demo data seeded successfully!")

if __name__ == '__main__':
    seed()
