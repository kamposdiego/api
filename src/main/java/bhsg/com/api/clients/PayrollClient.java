package bhsg.com.api.clients;

import bhsg.com.api.dtos.Employee;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "/employees", accept = MediaType.APPLICATION_JSON_VALUE)
public interface PayrollClient {

    @PostExchange
    Employee postEmployee(@RequestBody Employee employee);

}
