package bhsg.com.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record Employee(@JsonProperty("id") UUID id, @NotBlank(message = "{error.employee.name.required}") @JsonProperty("name") String name){

}
