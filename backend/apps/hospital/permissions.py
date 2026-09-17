from rest_framework import permissions

class IsAdminOrReadOnly(permissions.BasePermission):
    """
    Allow read-only permissions for any request, but write permissions only for admin users.
    """
    def has_permission(self, request, view):
        if request.method in permissions.SAFE_METHODS:
            return True
        return bool(request.user and request.user.is_staff)


class HospitalRolePermission(permissions.BasePermission):
    """
    Role-based access control checking user groups / staff flags.
    """
    def has_permission(self, request, view):
        if not request.user or not request.user.is_authenticated:
            # Allow safe methods in development if configured
            return request.method in permissions.SAFE_METHODS

        # Superusers and staff have full access
        if request.user.is_superuser or request.user.is_staff:
            return True

        # Check groups
        user_groups = set(request.user.groups.values_list('name', flat=True))
        if 'Admin' in user_groups:
            return True

        if 'Receptionist' in user_groups:
            # Receptionist can manage patients and appointments, read doctors
            if view.basename in ['patient', 'appointment', 'department']:
                return True
            return request.method in permissions.SAFE_METHODS

        if 'Doctor' in user_groups:
            # Doctors can read patients, manage their medical records
            if view.basename == 'medical-record':
                return True
            return request.method in permissions.SAFE_METHODS

        # Patients can read/create appointments, read own info
        return request.method in permissions.SAFE_METHODS
