package bhsg.com.api.service;

import bhsg.com.api.TestConfig;
import bhsg.com.api.dtos.Employee;
import bhsg.com.api.entity.EmployeeEntity;
import bhsg.com.api.repository.EmployeeRepository;
import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static bhsg.com.api.web.RequestAndCorrelationIdFilter.REQUEST_ID_HEADER;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RDBMSServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CacheServiceGrpc cacheServiceGrpc;

    @Mock
    private Retry cacheRetry;

    @InjectMocks
    private RDBMSService rdbmsService;

    @Test
    @DisplayName("Should return a list of Employees when called")
    void shouldReturnAllEmployees(){
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        final List<Employee> employeeList = rdbmsService.getAllEmployees();

        assertThat(employeeList).isEmpty();
    }

}
