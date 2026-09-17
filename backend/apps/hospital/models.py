from django.db import models
from django.core.validators import RegexValidator
from django.utils import timezone

phone_validator = RegexValidator(
    regex=r'^\+?[0-9\s\-\(\)]{7,20}$',
    message="Phone number must be between 7 and 20 digits, optionally starting with '+'."
)

class Department(models.Model):
    department_id = models.AutoField(primary_key=True)
    name = models.CharField(max_length=100, unique=True)
    description = models.TextField(blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'departments'
        ordering = ['name']

    def __str__(self):
        return self.name


class Doctor(models.Model):
    doctor_id = models.AutoField(primary_key=True)
    full_name = models.CharField(max_length=120)
    specialization = models.CharField(max_length=100)
    department = models.ForeignKey(
        Department,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='doctors'
    )
    phone = models.CharField(max_length=20, validators=[phone_validator])
    email = models.EmailField(max_length=100, unique=True)
    availability = models.CharField(max_length=100, default="Mon-Fri 09:00 - 17:00")
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'doctors'
        ordering = ['full_name']

    def __str__(self):
        return f"{self.full_name} ({self.specialization})"


class Patient(models.Model):
    GENDER_CHOICES = [
        ('Male', 'Male'),
        ('Female', 'Female'),
        ('Other', 'Other'),
    ]

    BLOOD_GROUP_CHOICES = [
        ('A+', 'A+'),
        ('A-', 'A-'),
        ('B+', 'B+'),
        ('B-', 'B-'),
        ('AB+', 'AB+'),
        ('AB-', 'AB-'),
        ('O+', 'O+'),
        ('O-', 'O-'),
    ]

    patient_id = models.AutoField(primary_key=True)
    full_name = models.CharField(max_length=120)
    date_of_birth = models.DateField()
    gender = models.CharField(max_length=10, choices=GENDER_CHOICES)
    phone = models.CharField(max_length=20, validators=[phone_validator])
    email = models.EmailField(max_length=100, unique=True)
    address = models.TextField()
    blood_group = models.CharField(max_length=5, choices=BLOOD_GROUP_CHOICES)
    emergency_contact = models.CharField(max_length=100)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'patients'
        ordering = ['-created_at']

    def __str__(self):
        return f"{self.full_name} (#{self.patient_id})"


class Appointment(models.Model):
    STATUS_CHOICES = [
        ('Scheduled', 'Scheduled'),
        ('In-Progress', 'In-Progress'),
        ('Completed', 'Completed'),
        ('Cancelled', 'Cancelled'),
    ]

    appointment_id = models.AutoField(primary_key=True)
    patient = models.ForeignKey(
        Patient,
        on_delete=models.CASCADE,
        related_name='appointments'
    )
    doctor = models.ForeignKey(
        Doctor,
        on_delete=models.CASCADE,
        related_name='appointments'
    )
    appointment_date = models.DateField()
    appointment_time = models.CharField(max_length=10)  # HH:MM format
    status = models.CharField(max_length=20, choices=STATUS_CHOICES, default='Scheduled')
    reason = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'appointments'
        ordering = ['-appointment_date', 'appointment_time']
        constraints = [
            models.UniqueConstraint(
                fields=['doctor', 'appointment_date', 'appointment_time'],
                name='unique_doctor_appointment_slot'
            )
        ]

    def __str__(self):
        return f"Appt #{self.appointment_id}: {self.patient.full_name} with {self.doctor.full_name} on {self.appointment_date} at {self.appointment_time}"


class MedicalRecord(models.Model):
    record_id = models.AutoField(primary_key=True)
    patient = models.ForeignKey(
        Patient,
        on_delete=models.CASCADE,
        related_name='medical_records'
    )
    doctor = models.ForeignKey(
        Doctor,
        on_delete=models.CASCADE,
        related_name='authored_records'
    )
    diagnosis = models.CharField(max_length=255)
    notes = models.TextField()
    record_date = models.DateField(default=timezone.now)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'medical_records'
        ordering = ['-record_date', '-created_at']

    def __str__(self):
        return f"Record #{self.record_id}: {self.patient.full_name} - {self.diagnosis}"
