package tech.orbfin.api.gateway.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.orbfin.api.gateway.request.RequestRegister;
import tech.orbfin.api.gateway.user.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAuth {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
}