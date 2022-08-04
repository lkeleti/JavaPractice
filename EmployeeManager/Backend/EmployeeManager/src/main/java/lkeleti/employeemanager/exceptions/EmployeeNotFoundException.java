package lkeleti.employeemanager.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class EmployeeNotFoundException extends AbstractThrowableProblem {
    public EmployeeNotFoundException(long id) {
        super(
                URI.create("/employees/employee-not-found"),
                "Employee not found!",
                Status.NOT_FOUND,
                String.format("Employee not found by id: %d", id)
        );

    }
}
