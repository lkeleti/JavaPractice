package lkeleti.employeemanager.controller;

import lkeleti.employeemanager.dtos.CreateNewEmployeeCommand;
import lkeleti.employeemanager.dtos.EmployeeDto;
import lkeleti.employeemanager.dtos.ModifyEmployeeCommand;
import lkeleti.employeemanager.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@AllArgsConstructor
public class EmployeeController {
    private EmployeeService service;

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<EmployeeDto> listAllEmployees() {
        return service.listAllEmployees();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public EmployeeDto listEmployeeById(@PathVariable long id) {
        return service.listEmployeeById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto createNewEmployee(@Valid @RequestBody CreateNewEmployeeCommand createNewEmployeeCommand) {
        return service.createNewEmployee(createNewEmployeeCommand);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public EmployeeDto modifyEmployee(@PathVariable long id,  @Valid @RequestBody ModifyEmployeeCommand modifyEmployeeCommand) {
        return service.modifyEmployee(id, modifyEmployeeCommand);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployeeById(@PathVariable long id) {
        service.deleteEmployeeById(id);
    }
}
