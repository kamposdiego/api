package bhsg.com.api.controller;

import bhsg.com.api.ApiApplication;
import bhsg.com.api.dtos.Employee;
import bhsg.com.api.service.RDBMSService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EmployeesControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RDBMSService rdbmsService;

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
