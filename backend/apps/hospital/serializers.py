from rest_framework import serializers
from django.utils import timezone
from datetime import date
from .models import Department, Doctor, Patient, Appointment, MedicalRecord

class DepartmentSerializer(serializers.ModelSerializer):
    doctors_count = serializers.IntegerField(source='doctors.count', read_only=True)

    class Meta:
        model = Department
        fields = ['department_id', 'name', 'description', 'doctors_count', 'created_at', 'updated_at']
        read_only_fields = ['department_id', 'created_at', 'updated_at']


class DoctorSerializer(serializers.ModelSerializer):
    department_name = serializers.CharField(source='department.name', read_only=True)

    class Meta:
        model = Doctor
        fields = [
            'doctor_id', 'full_name', 'specialization', 'department',
            'department_name', 'phone', 'email', 'availability',
            'created_at', 'updated_at'
        ]
        read_only_fields = ['doctor_id', 'created_at', 'updated_at']

    def validate_email(self, value):
        doctor_id = self.instance.doctor_id if self.instance else None
        if Doctor.objects.filter(email__iexact=value).exclude(doctor_id=doctor_id).exists():
            raise serializers.ValidationError("A doctor with this email address already exists.")
        return value.lower()


class PatientSerializer(serializers.ModelSerializer):
    appointments_count = serializers.IntegerField(source='appointments.count', read_only=True)
    records_count = serializers.IntegerField(source='medical_records.count', read_only=True)

    class Meta:
        model = Patient
        fields = [
            'patient_id', 'full_name', 'date_of_birth', 'gender',
            'phone', 'email', 'address', 'blood_group',
            'emergency_contact', 'appointments_count', 'records_count',
            'created_at', 'updated_at'
        ]
        read_only_fields = ['patient_id', 'created_at', 'updated_at']

    def validate_date_of_birth(self, value):
        if value >= date.today():
            raise serializers.ValidationError("Date of birth must be strictly in the past.")
        return value

    def validate_email(self, value):
        patient_id = self.instance.patient_id if self.instance else None
        if Patient.objects.filter(email__iexact=value).exclude(patient_id=patient_id).exists():
            raise serializers.ValidationError("A patient with this email address already exists.")
        return value.lower()


class AppointmentSerializer(serializers.ModelSerializer):
    patient_name = serializers.CharField(source='patient.full_name', read_only=True)
    doctor_name = serializers.CharField(source='doctor.full_name', read_only=True)
    doctor_specialization = serializers.CharField(source='doctor.specialization', read_only=True)

    class Meta:
        model = Appointment
        fields = [
            'appointment_id', 'patient', 'patient_name',
            'doctor', 'doctor_name', 'doctor_specialization',
            'appointment_date', 'appointment_time', 'status',
            'reason', 'created_at', 'updated_at'
        ]
        read_only_fields = ['appointment_id', 'created_at', 'updated_at']

    def validate(self, attrs):
        doctor = attrs.get('doctor') or (self.instance.doctor if self.instance else None)
        appointment_date = attrs.get('appointment_date') or (self.instance.appointment_date if self.instance else None)
        appointment_time = attrs.get('appointment_time') or (self.instance.appointment_time if self.instance else None)
        status = attrs.get('status', 'Scheduled')

        # Check double booking if status is not Cancelled
        if status != 'Cancelled' and doctor and appointment_date and appointment_time:
            appt_id = self.instance.appointment_id if self.instance else None
            clash = Appointment.objects.filter(
                doctor=doctor,
                appointment_date=appointment_date,
                appointment_time=appointment_time
            ).exclude(status='Cancelled').exclude(appointment_id=appt_id).exists()

            if clash:
                raise serializers.ValidationError({
                    "appointment_time": f"Dr. {doctor.full_name} is already booked on {appointment_date} at {appointment_time}."
                })

        return attrs


class MedicalRecordSerializer(serializers.ModelSerializer):
    patient_name = serializers.CharField(source='patient.full_name', read_only=True)
    doctor_name = serializers.CharField(source='doctor.full_name', read_only=True)

    class Meta:
        model = MedicalRecord
        fields = [
            'record_id', 'patient', 'patient_name',
            'doctor', 'doctor_name', 'diagnosis',
            'notes', 'record_date', 'created_at', 'updated_at'
        ]
        read_only_fields = ['record_id', 'created_at', 'updated_at']
