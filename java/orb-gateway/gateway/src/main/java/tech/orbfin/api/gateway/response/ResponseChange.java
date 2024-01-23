package tech.orbfin.api.gateway.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class ResponseChange {
    private String success;
    private String error;
}
