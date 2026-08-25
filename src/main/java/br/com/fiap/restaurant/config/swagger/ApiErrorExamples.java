package br.com.fiap.restaurant.config.swagger;

public final class ApiErrorExamples {

    private ApiErrorExamples() {
    }

    public static final String VALIDATION_ERROR = """
            {
              "timestamp": "2026-08-24T10:15:30",
              "status": 400,
              "error": "Bad Request",
              "message": "Validation failed",
              "path": "/api/users",
              "validationErrors": {
                "email": "must be a well-formed email address",
                "name": "Name must be between 3 and 100 characters"
              }
            }
            """;

    public static final String UNAUTHENTICATED = """
            {
              "timestamp": "2026-08-24T10:15:30",
              "status": 401,
              "error": "Unauthorized",
              "message": "Authentication is required to access this resource",
              "path": "/api/restaurant",
              "validationErrors": null
            }
            """;

    public static final String INVALID_CREDENTIALS = """
            {
              "timestamp": "2026-08-24T10:15:30",
              "status": 401,
              "error": "Unauthorized",
              "message": "Invalid login or password",
              "path": "/api/auth/login",
              "validationErrors": null
            }
            """;

    public static final String FORBIDDEN = """
            {
              "timestamp": "2026-08-24T10:15:30",
              "status": 403,
              "error": "Forbidden",
              "message": "User john.doe is not allowed to perform this operation",
              "path": "/api/restaurant/7/employee/3",
              "validationErrors": null
            }
            """;

    public static final String ENTITY_NOT_FOUND = """
            {
              "timestamp": "2026-08-24T10:15:30",
              "status": 404,
              "error": "Not Found",
              "message": "Entity not found",
              "path": "/api/restaurant/999",
              "validationErrors": null
            }
            """;

    public static final String DUPLICATE_RESOURCE = """
            {
              "timestamp": "2026-08-24T10:15:30",
              "status": 409,
              "error": "Conflict",
              "message": "Email already in use",
              "path": "/api/users",
              "validationErrors": null
            }
            """;

    public static final String ORDER_ALREADY_DELIVERED = """
            {
              "timestamp": "2026-08-24T10:15:30",
              "status": 409,
              "error": "Conflict",
              "message": "Order already delivered",
              "path": "/api/restaurant/7/order/12/cancel",
              "validationErrors": null
            }
            """;

    public static final String ORDER_ALREADY_CANCELED = """
            {
              "timestamp": "2026-08-24T10:15:30",
              "status": 409,
              "error": "Conflict",
              "message": "Order already canceled",
              "path": "/api/restaurant/7/order/12/deliver",
              "validationErrors": null
            }
            """;
}
