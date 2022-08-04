package lkeleti.employeemanager.service;

import lkeleti.employeemanager.dtos.CreateNewEmployeeCommand;
import lkeleti.employeemanager.dtos.EmployeeDto;
import lkeleti.employeemanager.dtos.ModifyEmployeeCommand;
import lkeleti.employeemanager.exceptions.EmployeeNotFoundException;
import lkeleti.employeemanager.model.Employee;
import lkeleti.employeemanager.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {

    private EmployeeRepository repository;
    private ModelMapper modelMapper;
    public List<EmployeeDto> listAllEmployees() {
        Type targetListType = new TypeToken<List<EmployeeDto>>(){}.getType();
        return modelMapper.map(repository.findAll(), targetListType);
    }

    public EmployeeDto listEmployeeById(long id) {
        Employee employee = repository.findById(id).orElseThrow(
                () -> new  EmployeeNotFoundException(id)
        );

        return modelMapper.map(employee, EmployeeDto.class);
    }

    public EmployeeDto createNewEmployee(CreateNewEmployeeCommand createNewEmployeeCommand) {
        Employee employee = new Employee(
                createNewEmployeeCommand.getName(),
                createNewEmployeeCommand.getEmail(),
                createNewEmployeeCommand.getJobTitle(),
                createNewEmployeeCommand.getPhone(),
                createNewEmployeeCommand.getImageUrl()
        );
        return modelMapper.map(repository.save(employee), EmployeeDto.class);
    }

    @Transactional
    public EmployeeDto modifyEmployee(long id, ModifyEmployeeCommand modifyEmployeeCommand) {
        Employee employee = repository.findById(id).orElseThrow(
                () -> new EmployeeNotFoundException(id)
        );

        employee.setName(modifyEmployeeCommand.getName());
        employee.setEmail(modifyEmployeeCommand.getEmail());
        employee.setJobTitle(modifyEmployeeCommand.getJobTitle());
        employee.setPhone(modifyEmployeeCommand.getPhone());
        employee.setImageUrl(modifyEmployeeCommand.getImageUrl());

        return modelMapper.map(employee, EmployeeDto.class);
    }

    public void deleteEmployeeById(long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException erdae) {
            throw new EmployeeNotFoundException(id);
        }
    }
}
