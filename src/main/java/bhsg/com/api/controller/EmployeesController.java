package bhsg.com.api.controller;

import bhsg.com.api.dtos.Employee;
import bhsg.com.api.service.RDBMSService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static bhsg.com.api.logging.LogContextUtils.trace;
import static bhsg.com.api.logging.LoggingConstants.Symbols.IN;
import static bhsg.com.api.logging.LoggingConstants.Symbols.OUT;
import static bhsg.com.api.logging.LoggingConstants.Tags.CONTROLLER;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/v1/employees", produces = "application/JSON")
@RequiredArgsConstructor
public class EmployeesController {

    private final RDBMSService RDBMSService;

    @GetMapping
    public List<Employee> getEmployees(final Pageable pageable) {
        log.info("{} {} [GET] /v1/employees - retrieving employees, params {} - {}", IN, CONTROLLER, pageable, trace());

        final var employees = RDBMSService.getAllEmployees();

        log.info("{} {} [GET] /v1/employees - returned {} employees, status=200 - {}", OUT, CONTROLLER, employees.size(), trace());
        return employees;
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable("id") @NotNull final UUID id) {
        log.info("{} {} [GET] /v1/employees - retrieving employeeId={} - {}", IN, CONTROLLER, id, trace());

        final var employee = RDBMSService.get(id);

        log.info("{} {} [GET] /v1/employees/{} - retrieving employeeId={}, status=200 - {}", OUT, CONTROLLER, id, employee.id(), trace());
        return employee;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee postEmployee(@RequestBody @Validated final Employee employee){
        log.info("{} {} [POST] /v1/employees - creating employee={} - {}", IN, CONTROLLER, employee, trace());

        final var employeePosted = RDBMSService.createEmployee(employee);

        log.info("{} {} [POST] /v1/employees - created employeeId={}, status=201 - {}", OUT, CONTROLLER, employeePosted.id(), trace());
        return employeePosted;
    }

    @PutMapping("/{id}")
    public Employee putEmployee(@PathVariable("id") @NotNull final UUID id, @Validated @RequestBody final Employee employee){
        log.info("{} {} [PUT] /v1/employees/{} - updating employee={} - {}", IN, CONTROLLER, id, employee, trace());

        final var employeePutted = RDBMSService.updateEmployee(id, employee);

        log.info("{} {} [PUT] /v1/employees/{} - updated employeeId={}, status=200 - {}", OUT, CONTROLLER, id, employeePutted.id(), trace());
        return employeePutted;
    }

    @Validated
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable("id") @NotNull final UUID id){
        log.info("{} {} [DELETE] /v1/employees/{} - deleting employeeId={} - {}", IN, CONTROLLER, id, id, trace());

        RDBMSService.deleteEmployee(id);

        log.info("{} {} [DELETE] /v1/employees/{} - deleted employeeId={}, status=204 - {}", OUT, CONTROLLER, id, id, trace());
    }

}
