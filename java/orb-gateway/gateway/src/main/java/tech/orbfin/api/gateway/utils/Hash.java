package tech.orbfin.api.gateway.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.mariadb.jdbc.plugin.authentication.standard.ed25519.Utils.bytesToHex;

public class Hash {

    public static String hash(String text) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashbytes = digest.digest(
                text.getBytes(StandardCharsets.UTF_8));
        String sha3Hex = bytesToHex(hashbytes);

        return sha3Hex;
    }
}
