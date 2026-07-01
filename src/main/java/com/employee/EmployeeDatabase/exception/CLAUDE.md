# exception package

- `GlobalExceptionHandler` is the single `@RestControllerAdvice` for the API. Add new `@ExceptionHandler` methods to this existing class rather than creating additional advice classes.
- Bean Validation failures (`@Valid` on `@RequestBody`) are handled by Spring's default `MethodArgumentNotValidException` behavior (400 Bad Request) and do not need a handler here unless the response body format needs to change.
- The standard error response shape across all endpoints should be `{ timestamp, status, error, message, path }` (see US-008 in prd.json). Handlers added before US-008 may use a smaller ad-hoc shape and should be reconciled to this format when US-008 is implemented.
