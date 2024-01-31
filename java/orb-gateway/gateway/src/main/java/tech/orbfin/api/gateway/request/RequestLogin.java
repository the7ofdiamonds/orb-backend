package tech.orbfin.api.gateway.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
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
