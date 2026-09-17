"""
Custom DRF exception handler enforcing standardized JSON error envelope.
Format:
{
    "success": false,
    "message": "Error description",
    "errors": { ... }
}
"""
from rest_framework.views import exception_handler
from rest_framework.response import Response
from rest_framework import status

def custom_exception_handler(exc, context):
    response = exception_handler(exc, context)

    if response is not None:
        custom_data = {
            "success": False,
            "message": "A validation or request error occurred.",
            "errors": response.data
        }
        if isinstance(response.data, dict) and "detail" in response.data:
            custom_data["message"] = str(response.data["detail"])
        elif isinstance(response.data, list) and len(response.data) > 0:
            custom_data["message"] = str(response.data[0])

        response.data = custom_data
    else:
        # Unhandled 500 server error
        return Response(
            {
                "success": False,
                "message": "Internal server error occurred. Please contact support.",
                "errors": {}
            },
            status=status.HTTP_500_INTERNAL_SERVER_ERROR
        )

    return response
