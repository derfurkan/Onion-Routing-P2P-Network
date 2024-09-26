package de.furkan.crypt;

import lombok.Getter;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Getter
public class CryptoUtil {
    private final String keyPairAlgorithm = "RSA";
    private final String keyAlgorithm = "AES";
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String publicKeyString;

    public void generateCryptoKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyPairAlgorithm);
            keyPairGenerator.initialize(4096);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
            publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encryptWithPublicKey(String s, PublicKey publicKey) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm);
        keyGenerator.init(256);
        SecretKey secretKey = keyGenerator.generateKey();

        Cipher aesCipher = Cipher.getInstance(keyAlgorithm);
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] aesEncrypted = aesCipher.doFinal(s.getBytes(StandardCharsets.UTF_8));

        Cipher rsaCipher = Cipher.getInstance(keyPairAlgorithm);
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] keyEncrypted = rsaCipher.doFinal(secretKey.getEncoded());

        return Base64.getEncoder().encodeToString(aesEncrypted) + "|" +
                Base64.getEncoder().encodeToString(keyEncrypted);
    }

    public String encrypt(String s) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm);
        keyGenerator.init(256);
        SecretKey secretKey = keyGenerator.generateKey();

        Cipher aesCipher = Cipher.getInstance(keyAlgorithm);
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] aesEncrypted = aesCipher.doFinal(s.getBytes(StandardCharsets.UTF_8));

        Cipher rsaCipher = Cipher.getInstance(keyPairAlgorithm);
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] keyEncrypted = rsaCipher.doFinal(secretKey.getEncoded());

        return Base64.getEncoder().encodeToString(aesEncrypted) + "|" +
                Base64.getEncoder().encodeToString(keyEncrypted);
    }

    public String decrypt(String s) throws Exception {
        if (!s.contains("|")) throw new RuntimeException();
        String[] parts = s.split("\\|");
        byte[] aesEncrypted = Base64.getDecoder().decode(parts[0]);
        byte[] keyEncrypted = Base64.getDecoder().decode(parts[1]);

        Cipher rsaCipher = Cipher.getInstance(keyPairAlgorithm);
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKeyBytes = rsaCipher.doFinal(keyEncrypted);
        SecretKey originalSecretKey = new SecretKeySpec(aesKeyBytes, keyAlgorithm);

        Cipher aesCipher = Cipher.getInstance(keyAlgorithm);
        aesCipher.init(Cipher.DECRYPT_MODE, originalSecretKey);
        return new String(aesCipher.doFinal(aesEncrypted), StandardCharsets.UTF_8);
    }

    public String decryptWithPrivateKey(String s, PrivateKey privateKey) throws Exception {
        if (!s.contains("|")) throw new RuntimeException();
        String[] parts = s.split("\\|");
        byte[] aesEncrypted = Base64.getDecoder().decode(parts[0]);
        byte[] keyEncrypted = Base64.getDecoder().decode(parts[1]);

        Cipher rsaCipher = Cipher.getInstance(keyPairAlgorithm);
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKeyBytes = rsaCipher.doFinal(keyEncrypted);
        SecretKey originalSecretKey = new SecretKeySpec(aesKeyBytes, keyAlgorithm);

        Cipher aesCipher = Cipher.getInstance(keyAlgorithm);
        aesCipher.init(Cipher.DECRYPT_MODE, originalSecretKey);
        return new String(aesCipher.doFinal(aesEncrypted), StandardCharsets.UTF_8);
    }

    public Key toKey(String key, KeyType keyType) throws Exception {
        byte[] publicBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(keyPairAlgorithm);
        return switch (keyType) {
            case PUBLIC -> keyFactory.generatePublic(keySpec);
            case PRIVATE -> keyFactory.generatePrivate(keySpec);
        };
    }

    public enum KeyType {
        PUBLIC,
        PRIVATE
    }

}
