# controller test package

- `@SpringBootTest` test classes here share one cached Spring context (and therefore one
  `InMemoryEmployeeRepository` instance) across the whole test run. That's fine for tests that
  only assert on data they themselves created (use unique emails), but it makes state-dependent
  assertions like "store is empty" or "store has exactly N employees" unreliable/order-dependent.
- For endpoints whose acceptance criteria require asserting exact list contents (empty/single/
  multiple), prefer `@WebMvcTest(EmployeeController.class)` with `@MockitoBean EmployeeService`
  instead — this fully controls what the service returns per test, independent of other test
  classes. See `EmployeeGetAllControllerTest` for the pattern.
