from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import (
    DepartmentViewSet,
    DoctorViewSet,
    PatientViewSet,
    AppointmentViewSet,
    MedicalRecordViewSet,
    DashboardStatsView
)

router = DefaultRouter()
router.register(r'departments', DepartmentViewSet, basename='department')
router.register(r'doctors', DoctorViewSet, basename='doctor')
router.register(r'patients', PatientViewSet, basename='patient')
router.register(r'appointments', AppointmentViewSet, basename='appointment')
router.register(r'medical-records', MedicalRecordViewSet, basename='medical-record')

urlpatterns = [
    path('dashboard/stats/', DashboardStatsView.as_view(), name='dashboard-stats'),
    path('', include(router.urls)),
]
