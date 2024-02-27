package tech.orbfin.api.gateway.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class RequestLogin {
    private String username;
    private String password;
    private Object location;

    @JsonCreator
    public RequestLogin(@JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("location") Object location) {
        this.username = username;
        this.password = password;
        this.location = location;
    }
}
