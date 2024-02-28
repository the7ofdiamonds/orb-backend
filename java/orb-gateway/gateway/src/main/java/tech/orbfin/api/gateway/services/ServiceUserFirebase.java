package tech.orbfin.api.gateway.services;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import lombok.AllArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tech.orbfin.api.gateway.configurations.ConfigTopics;

@AllArgsConstructor
@Service
public class ServiceUserFirebase {
    private ServiceAuthFirebase auth;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public boolean userExistByPhone(String phone) throws FirebaseAuthException {
        try {
            auth.firebaseAuth.getUserByPhoneNumber(phone);

            return true;
        } catch (FirebaseAuthException e) {
            return false;
        }
    }

    public boolean userExistByEmail(String email) throws FirebaseAuthException {
        try {
            auth.firebaseAuth.getUserByEmail(email);

            return true;
        } catch (FirebaseAuthException e) {
            return false;
        }
    }

    public UserRecord createUser(String email, String username, String password, String phone) throws Exception {
        try {
            boolean firebaseUserByPhone = userExistByPhone(phone);

            if (firebaseUserByPhone) {
                kafkaTemplate.send(ConfigTopics.PASSWORD_RECOVERY, phone);
                throw new Exception("This Phone Number is already in our records. Check your phone for text messages from ORBFIN.");
            }

            boolean firebaseUser = userExistByEmail(email);

            if (firebaseUser) {
                kafkaTemplate.send(ConfigTopics.PASSWORD_RECOVERY, email);
                throw new Exception("This Email is already in our records. Check your email.");
            }

            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setDisplayName(username)
                    .setPassword(password)
                    .setPhoneNumber(phone);

            UserRecord userRecord = auth.firebaseAuth.createUser(request);
            System.out.println("Successfully created new user: " + userRecord.getUid());

            return userRecord;
        } catch (FirebaseAuthException e){
            throw new FirebaseAuthException(e);
        }
    }

    public UserRecord getUser(String uid) throws FirebaseAuthException {
        return auth.firebaseAuth.getUser(uid);
    }

    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        return auth.firebaseAuth.getUserByEmail(email);
    }

    public void changePassword(String uid, String newPassword) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setPassword(newPassword);

        auth.firebaseAuth.updateUser(request);
    }

    public void changeUsername(String uid, String displayName) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setDisplayName(displayName);

        auth.firebaseAuth.updateUser(request);
    }

    public void changePhone(String uid, String phone) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setPhoneNumber(phone);

        auth.firebaseAuth.updateUser(request);
        // Send Phone Number Changed Email
    }

    public void changeEmailVerified(String uid, Boolean emailVerified) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setEmailVerified(emailVerified);

        auth.firebaseAuth.updateUser(request);
//        Send Email Verified
    }

    public void deleteUser(String uid) throws FirebaseAuthException {
        auth.firebaseAuth.deleteUser(uid);
        // Send email
    }
}
