package tech.orbfin.api.gateway.services;

import com.google.firebase.auth.FirebaseAuth;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.orbfin.api.gateway.configurations.ConfigFirebase;

import java.io.IOException;

@Setter
@Getter
@Service
public class ServiceAuthFirebase {
    private final ConfigFirebase configFirebase;
    private final FirebaseAuth firebaseAuth;

    @Autowired
    public ServiceAuthFirebase(ConfigFirebase configFirebase) throws Exception {
        this.configFirebase = configFirebase;
        this.firebaseAuth = FirebaseAuth.getInstance(configFirebase.initializeFirebase());
    }
}
