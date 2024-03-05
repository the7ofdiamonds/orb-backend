package tech.orbfin.api.gateway.services.firebase;

import tech.orbfin.api.gateway.configurations.ConfigFirebase;

import lombok.extern.slf4j.Slf4j;

import com.google.firebase.auth.FirebaseAuth;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServiceAuthFirebase {
    public final FirebaseAuth firebaseAuth;

    @Autowired
    public ServiceAuthFirebase(ConfigFirebase configFirebase) throws Exception {
        this.firebaseAuth = FirebaseAuth.getInstance(configFirebase.initializeFirebase());
    }
}
