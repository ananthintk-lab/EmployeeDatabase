# exception package

- `GlobalExceptionHandler` is the single `@RestControllerAdvice` for the API. Add new `@ExceptionHandler` methods to this existing class rather than creating additional advice classes.
- As of US-008, every handler (`EmployeeNotFoundException` → 404, `DuplicateEmailException` → 409, `MethodArgumentNotValidException` → 400, `MethodArgumentTypeMismatchException` → 400) returns the standard shape `{ timestamp, status, error, message, path }` via the shared private `buildResponse(HttpStatus, String, HttpServletRequest)` helper. Add new handlers by calling this helper, not by building the response map inline.
- `MethodArgumentNotValidException`'s `message` field joins all field errors as `"field: defaultMessage; field2: defaultMessage2"` (built from `exception.getBindingResult().getFieldErrors()`), not just the first error — client code parsing this message should split on `; ` if it needs individual field errors.
- `path` comes from `HttpServletRequest.getRequestURI()`, injected as a handler method parameter (Spring resolves it automatically in `@ExceptionHandler` methods) — no need for `HttpServletRequest` to be autowired into the class.
