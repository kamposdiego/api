package bhsg.com.api.service;

import bhsg.com.api.dtos.Employee;
import bhsg.com.api.entity.EmployeeEntity;
import bhsg.com.api.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RDBMSServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private InMemoryService inMemoryService;

    @InjectMocks
    private RDBMSService rdbmsService;

    @Test
    @DisplayName("Should return a list of Employees when called")
    void shouldReturnAllEmployees(){
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        final List<Employee> employeeList = rdbmsService.getAllEmployees();

        assertThat(employeeList).isEmpty();
    }

    @Test
    @DisplayName("Should return a list of Employees when called")
    void shouldSaveEmployee(){
        final Employee employee = new Employee(UUID.randomUUID(), "Diego");

        when(inMemoryService.existsById(any())).thenReturn(false);
        when(employeeRepository.save(any())).thenReturn(new EmployeeEntity(UUID.randomUUID(), "diego"));

        final Employee employeeList = rdbmsService.createEmployee(employee);

        assertThat(employeeList).hasNoNullFieldsOrProperties();
    }

}
