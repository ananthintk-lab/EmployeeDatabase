package com.employee.EmployeeDatabase.repository;

import com.employee.EmployeeDatabase.model.Employee;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Thread-safe in-memory {@link EmployeeRepository} backed by a {@link ConcurrentHashMap}. */
@Repository
public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<Long, Employee> employees = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @Override
    public Employee save(Employee employee) {
        if (employee.getId() == null) {
            employee.setId(idSequence.incrementAndGet());
        } else {
            idSequence.updateAndGet(current -> Math.max(current, employee.getId()));
        }
        employees.put(employee.getId(), employee);
        return employee;
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(employees.get(id));
    }

    @Override
    public List<Employee> findAll() {
        return employees.values().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        employees.remove(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return employees.values().stream()
                .anyMatch(employee -> employee.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return employees.values().stream()
                .anyMatch(employee -> employee.getEmail().equalsIgnoreCase(email) && !employee.getId().equals(id));
    }
}
