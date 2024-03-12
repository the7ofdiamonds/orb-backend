package tech.orbfin.api.gateway.model.orb;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Data
@Entity
public class Role {
    @Id
    private String name;
    private Object capabilities;
}
