package tech.orbfin.api.gateway.model.request;

import lombok.*;

import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
@Component
public class RequestChangePassword {
    private String username;
    private String password;
    private String newPassword;
    private String confirmationPassword;
}
