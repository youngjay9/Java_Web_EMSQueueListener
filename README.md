package com.jay.encrypt;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Hex;

public class GenerateApimEncryptKey {

  //RSA 公鑰
  private static String b64RSAPublicKey = "MIIDWTCCAkECBF2oO24wDQYJKoZIhvcNAQEFBQAwcTELMAkGA1UEBhMCVFcxDzANBgNVBAgTBlRhaXBlaTEPMA0GA1UEBxMGVGFpcGVpMQ0wCwYDVQQKEwRDVENCMRYwFAYDVQQLEw1LZXlNYW5hZ2VtZW50MRkwFwYDVQQDExBTZWN1cml0eVByb3ZpZGVyMB4XDTE5MTAxNzA5NTkxMFoXDTI5MTAwNDA5NTkxMFowcTELMAkGA1UEBhMCVFcxDzANBgNVBAgTBlRhaXBlaTEPMA0GA1UEBxMGVGFpcGVpMQ0wCwYDVQQKEwRDVENCMRYwFAYDVQQLEw1LZXlNYW5hZ2VtZW50MRkwFwYDVQQDExBTZWN1cml0eVByb3ZpZGVyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxmkYYz/a87ccKIaBG75hRSBbiB+tQgGUx+8IpVvIKj2ik09ITXt67bEF6zV4YcNibozXVurVBsacQ0bTL3lH24olEfh2E/cXJk6IPsMtwdflPSy4KGNV+KpRNN3iv21/PBPoqAXwe0hvs4LM6P3/yjmwAyqwN12La6gewDQVh7f4GUz0MgS6NjmZgqIdQOmzxC4LScdpuYYVe+75YXlfGeBLbkCbeUEz+tJ+9QKBHovccTGyuU6fz9DJofB+e16+iQ45dTMT+vexY+T88mvZ5doTmQ3bwGH/YQLdzBXfnc5AMn28uoBS6johKA7fc1BWOFSoNUmKGzZTJDZzIp677QIDAQABMA0GCSqGSIb3DQEBBQUAA4IBAQCUc4e5wOUylbqi5eyIiYO/gcrthI2aJVrxh+1+aOL9o+SiSvQi3wYcHOBzZFvuG3XoaynSt78zZF3pI9nFzqN0bQeVbzWPiv6IQoyLkAWfTseiN6lUZY/U/Lr8uQqKCJVjfh+4W+YhGpBrk8JgL7DbICO79+H5E035nso5nXOkHFrf4+TumXX0El8rydO1JDPrf7wsas2YK9wQTRWUCSsz7Lj07GpPYwb+wS+QhO9FDv3C9Lm/Ll8Ij6PuYxIZAnNGHXJapCreRhzARmvEwFI3HMY550XTiy2PRuwH2+TIeM0Ves1qD2+bu0tOprzNaC+Hf3XgnJzQzZg944BXkdBe";

  private static byte[] encryptByRSA(byte[] in, RSAPublicKey pubkey) throws Exception {

    Cipher rsaCipher = Cipher.getInstance("RSA");
    rsaCipher.init(1, pubkey);

    byte[] byteCipherText = rsaCipher.doFinal(in);

    return byteCipherText;
  }

  private static X509Certificate getX509Cert(byte[] certData)
      throws CertificateException, NoSuchProviderException {
    if ((certData == null) || (certData.length == 0)) {
      throw new CertificateException("cert data is null or empty");
    }
    X509Certificate x509Rtn = null;

    CertificateFactory cf = CertificateFactory.getInstance("X509");
    try {
      x509Rtn = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certData));
      if (x509Rtn == null) {
        throw new CertificateException("parse no result");
      }
    } catch (Exception ex) {
      String strCertData = new String(certData);
      byte[] decodedCertData = (byte[]) null;
      try {
        decodedCertData = Base64.getDecoder().decode(strCertData);
        x509Rtn = (X509Certificate) cf
            .generateCertificate(new ByteArrayInputStream(decodedCertData));
      } catch (Exception ex2) {
        try {
          strCertData = strCertData.trim();
          String[] straCertData = strCertData.split("");
          decodedCertData = Base64.getDecoder().decode(straCertData[0]);
          x509Rtn = (X509Certificate) cf
              .generateCertificate(new ByteArrayInputStream(decodedCertData));
        } catch (Exception ex3) {
          throw new CertificateException("cert data invalid");
        }
      }
    }
    return x509Rtn;
  }

  public static byte[] makeAESKey(int aesSize) throws NoSuchAlgorithmException {

    KeyGenerator kgen = KeyGenerator.getInstance("AES");
    kgen.init(aesSize);
    SecretKey key = kgen.generateKey();
    byte[] aesKey = key.getEncoded();

    return aesKey;
  }


  public static void main(String[] args){
    try {
      // step1. 產生 AES key
      byte[] aesKeyByte = makeAESKey(256);

      // step2. 轉成 hex
      String aesKeyHexString = DatatypeConverter.printHexBinary(aesKeyByte);
      System.out.println("aesKeyHexString: "+ aesKeyHexString);

      // step3. 轉成 base64
      byte[] encodedHexB64 = Base64.getEncoder().encode(aesKeyHexString.getBytes());

      String base64EncodeAesKry = new String(encodedHexB64, StandardCharsets.UTF_8);
      System.out.println("base64EncodeAesKry: " + base64EncodeAesKry);

      // step4. 將 base64 AesKey 透過 RSA 加密
      X509Certificate destCert = getX509Cert(
          Base64.getDecoder().decode(b64RSAPublicKey.getBytes()));

      RSAPublicKey destPubKey = (RSAPublicKey) destCert.getPublicKey();

      byte[] encAESKey = encryptByRSA(base64EncodeAesKry.getBytes(), destPubKey);

      String encryptKey = Hex.encodeHexString(encAESKey);
      System.out.println("encryptKey: " + encryptKey);


    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
