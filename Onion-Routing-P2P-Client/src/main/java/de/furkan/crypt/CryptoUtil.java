package de.furkan.crypt;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.Cipher;
import lombok.Getter;

@Getter
public class CryptoUtil {

  private PublicKey publicKey;
  private PrivateKey privateKey;
  private String publicKeyString;
  private String privateKeyString;
  private String keyAlgorithm;

  public void generateRSAKeyPair(int keySize, String algorithm) {
    try {
      this.keyAlgorithm = algorithm;
      KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
      kpg.initialize(keySize);
      KeyPair kp = kpg.generateKeyPair();
      publicKey = kp.getPublic();
      publicKeyString = Base64.getMimeEncoder().encodeToString(publicKey.getEncoded());
      privateKey = kp.getPrivate();
      privateKeyString = Base64.getMimeEncoder().encodeToString(privateKey.getEncoded());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String encrypt(String s) throws Exception {
    Cipher cipher = Cipher.getInstance(keyAlgorithm);
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    return Base64.getEncoder().encodeToString(cipher.doFinal(s.getBytes()));
  }

  public String decrypt(String s) throws Exception {
    Cipher cipher = Cipher.getInstance(keyAlgorithm);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    return new String(cipher.doFinal(Base64.getDecoder().decode(s)));
  }
}
