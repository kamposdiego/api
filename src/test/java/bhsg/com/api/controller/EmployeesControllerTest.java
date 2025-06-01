package bhsg.com.api.controller;

import bhsg.com.api.TestConfig;
import bhsg.com.api.dtos.Employee;
import bhsg.com.api.service.RDBMSService;
import bhsg.com.cache.IdempotentServiceGrpc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeesController.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
class EmployeesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RDBMSService rdbmsService;

    @Autowired
    @Qualifier("idempotentServiceBlockingStub")
    private IdempotentServiceGrpc.IdempotentServiceBlockingStub idempotentServiceBlockingStub;

    @Test
    @DisplayName("Should return all the employees")
    void shouldReturnAllRecords() throws Exception {
        given(rdbmsService.getAllEmployees()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/employees"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return all the employees")
    void shouldPostTheRecord() throws Exception {
        given(rdbmsService.createEmployee(any(), any())).willReturn(new Employee(UUID.randomUUID(), "Diego"));

        mockMvc.perform(post("/v1/employees").contentType(MediaType.APPLICATION_JSON)
                        .header("requestId", "3d1cc5a6-0670-4e04-a708-0014efca607d")
                        .header("X-Idempotency-ID", "1d1cc5a6-0670-4e04-a708-0014efca607d")
                        .content("{\"id\":\"9d1cc5a6-0670-4e04-a708-0014efca607d\", \"name\":\"diego\"}"))
                .andExpect(status().isCreated());
    }

}
