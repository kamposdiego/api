package bhsg.com.api.service;

import bhsg.com.api.dtos.Employee;
import bhsg.com.api.entity.EmployeeEntity;
import bhsg.com.api.exceptions.IllegalIdempotentStateException;
import bhsg.com.api.exceptions.NotFoundException;
import bhsg.com.api.logging.LoggingConstants;
import bhsg.com.api.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static bhsg.com.api.logging.LogContextUtils.trace;
import static bhsg.com.api.web.RequestAndCorrelationIdFilter.REQUEST_ID_HEADER;

@Slf4j
@Service
@RequiredArgsConstructor
public class RDBMSService {

    private final EmployeeRepository employeeRepository;
    private final CacheServiceGrpc cacheServiceGrpc;

    private EmployeeEntity findOrFail(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id.toString()));
    }

    public Employee get(final UUID id) {
        log.debug("{} {} Retrieving employee by ID: {} - {}", LoggingConstants.Symbols.IN, LoggingConstants.Tags.SERVICE, id, trace());

        final var entity = findOrFail(id);

        log.info("{} {} Found employee with ID: {} - {}", LoggingConstants.Symbols.OUT, LoggingConstants.Tags.SERVICE, entity.getId(), trace());
        return new Employee(entity.getId(), entity.getName());
    }

    public List<Employee> getAllEmployees() {
        log.debug("{} {} Retrieving all employees - {}", LoggingConstants.Symbols.IN, LoggingConstants.Tags.SERVICE, trace());

        final var list = employeeRepository.findAll();

        log.info("{} {} Retrieved {} employees - {}", LoggingConstants.Symbols.OUT, LoggingConstants.Tags.SERVICE, list.size(), trace());

        if (log.isDebugEnabled()) {
            log.debug("{} {} Employee list payload: {} - {}", LoggingConstants.Symbols.OUT, LoggingConstants.Tags.SERVICE, list, trace());
        }

        return list.stream()
                .map(e -> new Employee(e.getId(), e.getName()))
                .toList();
    }

    @Transactional
    public Employee createEmployee(final Employee employee) {
        log.debug("{} {} Creating employee: {} - {}", LoggingConstants.Symbols.IN, LoggingConstants.Tags.SERVICE, employee, trace());

        final var requestId = MDC.get(REQUEST_ID_HEADER);

        if (cacheServiceGrpc.existsById(requestId)) {
            log.warn("{} {} Request ID {} was already handled - {}", LoggingConstants.Symbols.IDEMPOTENT_REPLAY, LoggingConstants.Tags.IDEMPOTENT, requestId, trace());

            final var existing = employeeRepository.findById(employee.id())
                    .orElseThrow(() -> new IllegalIdempotentStateException(requestId));

            return new Employee(existing.getId(), existing.getName());
        }

        final var entity = employeeRepository.save(new EmployeeEntity(employee.id(), employee.name()));

        cacheServiceGrpc.createPostRequest(requestId);

        log.info("{} {} Employee created with ID {} - {}", LoggingConstants.Symbols.OUT, LoggingConstants.Tags.SERVICE, entity.getId(), trace());
        return new Employee(entity.getId(), entity.getName());
    }

    public Employee updateEmployee(final UUID id, final Employee employee) {
        log.debug("{} {} Updating employee ID {} - payload: {} - {}", LoggingConstants.Symbols.IN, LoggingConstants.Tags.SERVICE, id, employee, trace());

        final var entity = findOrFail(id);

        entity.setName(employee.name());
        final var updated = employeeRepository.save(entity);

        log.info("{} {} Employee ID {} updated - {}", LoggingConstants.Symbols.OUT, LoggingConstants.Tags.SERVICE, updated.getId(), trace());
        return new Employee(updated.getId(), updated.getName());
    }

    public void deleteEmployee(final UUID id) {
        log.debug("{} {} Deleting employee ID {} - {}", LoggingConstants.Symbols.IN, LoggingConstants.Tags.SERVICE, id, trace());

        employeeRepository.deleteById(id);

        log.info("{} {} Deleted employee ID {} - {}", LoggingConstants.Symbols.OUT, LoggingConstants.Tags.SERVICE, id, trace());
    }

}