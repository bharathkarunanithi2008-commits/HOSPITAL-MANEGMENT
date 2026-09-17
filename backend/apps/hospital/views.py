from rest_framework import viewsets, status
from rest_framework.views import APIView
from rest_framework.response import Response
from django.db.models import Q, Count
from django.utils import timezone
from .models import Department, Doctor, Patient, Appointment, MedicalRecord
from .serializers import (
    DepartmentSerializer,
    DoctorSerializer,
    PatientSerializer,
    AppointmentSerializer,
    MedicalRecordSerializer
)
from .permissions import HospitalRolePermission

class DepartmentViewSet(viewsets.ModelViewSet):
    queryset = Department.objects.all()
    serializer_class = DepartmentSerializer
    permission_classes = [HospitalRolePermission]
    search_fields = ['name', 'description']


class DoctorViewSet(viewsets.ModelViewSet):
    queryset = Doctor.objects.select_related('department').all()
    serializer_class = DoctorSerializer
    permission_classes = [HospitalRolePermission]

    def get_queryset(self):
        qs = super().get_queryset()
        search = self.request.query_params.get('search')
        spec = self.request.query_params.get('specialization')
        dept = self.request.query_params.get('department')

        if search:
            qs = qs.filter(
                Q(full_name__icontains=search) |
                Q(specialization__icontains=search) |
                Q(email__icontains=search)
            )
        if spec:
            qs = qs.filter(specialization__icontains=spec)
        if dept:
            qs = qs.filter(department_id=dept)
        return qs


class PatientViewSet(viewsets.ModelViewSet):
    queryset = Patient.objects.all()
    serializer_class = PatientSerializer
    permission_classes = [HospitalRolePermission]

    def get_queryset(self):
        qs = super().get_queryset()
        search = self.request.query_params.get('search')
        gender = self.request.query_params.get('gender')
        blood_group = self.request.query_params.get('blood_group')

        if search:
            qs = qs.filter(
                Q(full_name__icontains=search) |
                Q(phone__icontains=search) |
                Q(email__icontains=search) |
                Q(address__icontains=search)
            )
        if gender:
            qs = qs.filter(gender__iexact=gender)
        if blood_group:
            qs = qs.filter(blood_group__iexact=blood_group)
        return qs


class AppointmentViewSet(viewsets.ModelViewSet):
    queryset = Appointment.objects.select_related('patient', 'doctor').all()
    serializer_class = AppointmentSerializer
    permission_classes = [HospitalRolePermission]

    def get_queryset(self):
        qs = super().get_queryset()
        patient_id = self.request.query_params.get('patient')
        doctor_id = self.request.query_params.get('doctor')
        appt_status = self.request.query_params.get('status')
        appt_date = self.request.query_params.get('date')

        if patient_id:
            qs = qs.filter(patient_id=patient_id)
        if doctor_id:
            qs = qs.filter(doctor_id=doctor_id)
        if appt_status:
            qs = qs.filter(status__iexact=appt_status)
        if appt_date:
            qs = qs.filter(appointment_date=appt_date)
        return qs


class MedicalRecordViewSet(viewsets.ModelViewSet):
    queryset = MedicalRecord.objects.select_related('patient', 'doctor').all()
    serializer_class = MedicalRecordSerializer
    permission_classes = [HospitalRolePermission]

    def get_queryset(self):
        qs = super().get_queryset()
        patient_id = self.request.query_params.get('patient')
        doctor_id = self.request.query_params.get('doctor')

        if patient_id:
            qs = qs.filter(patient_id=patient_id)
        if doctor_id:
            qs = qs.filter(doctor_id=doctor_id)
        return qs


class DashboardStatsView(APIView):
    """
    Returns aggregated metrics for the hospital management dashboard.
    """
    permission_classes = [HospitalRolePermission]

    def get(self, request):
        today = timezone.now().date()
        total_patients = Patient.objects.count()
        total_doctors = Doctor.objects.count()
        total_appointments = Appointment.objects.count()
        today_appointments = Appointment.objects.filter(appointment_date=today).count()
        pending_appointments = Appointment.objects.filter(status='Scheduled').count()
        completed_appointments = Appointment.objects.filter(status='Completed').count()
        total_records = MedicalRecord.objects.count()

        return Response({
            "success": True,
            "data": {
                "total_patients": total_patients,
                "total_doctors": total_doctors,
                "total_appointments": total_appointments,
                "today_appointments": today_appointments,
                "pending_appointments": pending_appointments,
                "completed_appointments": completed_appointments,
                "total_records": total_records
            }
        })
