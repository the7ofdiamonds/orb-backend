package tech.orbfin.api.gateway.services;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ServiceUserFirebase {
    private ServiceAuthFirebase auth;

    public UserRecord createUser(String email, String username, String password, String phone) throws FirebaseAuthException {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setDisplayName(username)
                    .setPassword(password)
                    .setPhoneNumber(phone);

            UserRecord userRecord = auth.getFirebaseAuth().createUser(request);
            System.out.println("Successfully created new user: " + userRecord.getUid());

            return userRecord;
        } catch (FirebaseAuthException e){
            throw new FirebaseAuthException(e);
        }
        // Send email
    }

    public UserRecord getUser(String uid) throws FirebaseAuthException {
        return auth.getFirebaseAuth().getUser(uid);
    }

    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        return auth.getFirebaseAuth().getUserByEmail(email);
    }

    public void changePassword(String uid, String newPassword) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setPassword(newPassword);

        auth.getFirebaseAuth().updateUser(request);
        // Send Password changed Email
    }

    public void changeUsername(String uid, String displayName) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setDisplayName(displayName);

        auth.getFirebaseAuth().updateUser(request);
        // Send Username Changed Email
    }

    public void changePhone(String uid, String phone) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setPhoneNumber(phone);

        auth.getFirebaseAuth().updateUser(request);
        // Send Phone Number Changed Email
    }

    public void changeEmailVerified(String uid, Boolean emailVerified) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setEmailVerified(emailVerified);

        auth.getFirebaseAuth().updateUser(request);
//        Send Email Verified
    }

    public void deleteUser(String uid) throws FirebaseAuthException {
        auth.getFirebaseAuth().deleteUser(uid);
        // Send email
    }
}
