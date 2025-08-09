package it.unipi.PROGETTO_SERVER.otherClasses;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashFunction {

    public static String hash(String p) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(p.getBytes(Charset.defaultCharset()));
        String encoded = Base64.getEncoder().encodeToString(hash);
        return encoded;
    }
}
