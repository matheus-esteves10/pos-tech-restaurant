package br.com.fiap.restaurant.presentation.swagger;

public final class ApiErrorExamples {

    private ApiErrorExamples() {
    }

    public static final String VALIDATION_ERROR = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "Validation failed",
              "instance": "/api/v1/users",
              "validationErrors": {
                "email": "must be a well-formed email address",
                "name": "Name must be between 3 and 100 characters"
              }
            }
            """;

    public static final String UNAUTHENTICATED = """
            {
              "type": "about:blank",
              "title": "Unauthorized",
              "status": 401,
              "detail": "Authentication is required to access this resource",
              "instance": "/api/v1/restaurant"
            }
            """;

    public static final String INVALID_CREDENTIALS = """
            {
              "type": "about:blank",
              "title": "Unauthorized",
              "status": 401,
              "detail": "Invalid login or password",
              "instance": "/api/v1/auth/login"
            }
            """;

    public static final String FORBIDDEN = """
            {
              "type": "about:blank",
              "title": "Forbidden",
              "status": 403,
              "detail": "User john.doe is not allowed to perform this operation",
              "instance": "/api/v1/restaurant/7/employee/3"
            }
            """;

    public static final String ENTITY_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Entity not found",
              "instance": "/api/v1/restaurant/999"
            }
            """;

    public static final String DUPLICATE_RESOURCE = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "Email already in use",
              "instance": "/api/v1/users"
            }
            """;

    public static final String ORDER_ALREADY_DELIVERED = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "Order already delivered",
              "instance": "/api/v1/restaurant/7/order/12/cancel"
            }
            """;

    public static final String ORDER_ALREADY_CANCELED = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "Order already canceled",
              "instance": "/api/v1/restaurant/7/order/12/deliver"
            }
            """;
}
