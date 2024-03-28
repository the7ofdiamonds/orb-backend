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
public class RequestForgot {
    private String email;
    private String username;
}
