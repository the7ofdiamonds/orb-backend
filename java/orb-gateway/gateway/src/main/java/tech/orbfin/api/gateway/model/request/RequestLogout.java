package tech.orbfin.api.gateway.model.request;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.springframework.stereotype.Component;
import tech.orbfin.api.gateway.exceptions.LogoutException;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
@Component
public class RequestLogout {
    private String token;

}
