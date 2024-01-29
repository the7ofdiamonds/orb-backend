package tech.orbfin.api.gateway.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.orbfin.api.gateway.configurations.ConfigFirebase;

@Service
public class ServiceUserFirebase {
    @Autowired
    private ServiceAuthFirebase auth;

    public UserRecord createUser(String email, String username, String password, String phone) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setDisplayName(username)
                .setPassword(password)
                .setPhoneNumber(phone);

        UserRecord userRecord = auth.getFirebaseAuth().createUser(request);
        System.out.println("Successfully created new user: " + userRecord.getUid());

        return userRecord;
        // Send email
    }

    public UserRecord getUser(String uid) throws FirebaseAuthException {
        return auth.getFirebaseAuth().getUser(uid);
    }

    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        return auth.getFirebaseAuth().getUserByEmail(email);
    }

    public void updateUser(String uid,
                           String displayName,
                           String password,
                           String phone,
                           Boolean emailVerified) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setDisplayName(displayName)
                .setPassword(password)
                .setPhoneNumber(phone)
                .setEmailVerified(emailVerified);

        auth.getFirebaseAuth().updateUser(request);
        // Send Email
    }

    public void deleteUser(String uid) throws FirebaseAuthException {
        auth.getFirebaseAuth().deleteUser(uid);
        // Send email
    }
}
