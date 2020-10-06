package com.jay.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AESUtils {

    private static final String AES_KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String IV_VALUE = "1234567890123132";

//    protected static final int aesBlockSize = 256 / 8;
//
//    final protected static int dataBlockSize = 4096;

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * 根據key的內容儲存加密的Cipher
     */
    private static Map<String, Cipher> cipherEncryptMap = new HashMap<>();

    /**
     * 根據key的內容儲存解密的Cipher
     */
    private static Map<String, Cipher> cipherDecryptMap = new HashMap<>();


//    private static final String[] consult = new String[]{"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G"};

//    public static String genAesKey() throws NoSuchAlgorithmException {
//        //20190705 Ken: 調整成SecureRandom
//        SecureRandom sr = SecureRandom.getInstanceStrong();
//        byte[] key = new byte[aesBlockSize];
//        sr.nextBytes(key);
//        return byte2Hex(key);
//    }

    private static String byte2Hex(byte[] bin) {
        char[] hexChars = new char[bin.length * 2];
        for (int j = 0; j < bin.length; j++) {
            int v = bin[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] hex2Byte(String hex) throws DecoderException {
        return Hex.decodeHex(hex.toCharArray());
    }

    private static Key toKey(byte[] key) {
        return new SecretKeySpec(key, AES_KEY_ALGORITHM);
    }

    /**
     * 加密
     * @throws DecoderException encrypt error
     * @throws GeneralSecurityException encrypt error
     */
    public static String encrypt(String key, String data) throws DecoderException, GeneralSecurityException {
        if (cipherEncryptMap.get(key) == null) {
            Key k = toKey(hex2Byte(key));
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, k, createIV(IV_VALUE)); //加密模式
            cipherEncryptMap.put(key, cipher);
        }
        Cipher cipher = cipherEncryptMap.get(key);
        byte[] encryptData = cipher.doFinal(data.getBytes());
        String hexString = byte2Hex(encryptData);
        log.debug("decrypt hex data={}", byte2Hex(encryptData));
        log.debug("decrypt base64 data={}",  Base64.getEncoder().encodeToString(hexString.getBytes(StandardCharsets.UTF_8)));
        return Base64.getEncoder().encodeToString(hexString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解密
     *
     * @throws DecoderException encrypt error
     * @throws GeneralSecurityException encrypt error
     */
    public static String decrypt(String key, String data) throws DecoderException, GeneralSecurityException {
        log.debug("key={}", key);
        log.debug("data={}", data);
        if (cipherDecryptMap.get(key) == null) {
            Key k = toKey(hex2Byte(key));
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, k, createIV(IV_VALUE)); //解密模式
            cipherDecryptMap.put(key, cipher);
        }
        Cipher cipher = cipherDecryptMap.get(key);
        String hexString = new String(Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8)));
        byte[] original = cipher.doFinal(hex2Byte(hexString));
        log.debug("original text={}", new String(original, StandardCharsets.UTF_8));
        return new String(original, StandardCharsets.UTF_8);
    }

    private static IvParameterSpec createIV(String ivValue) {
        byte[] data;
        if (ivValue == null) {
            ivValue = "";
        }
        StringBuilder sb = new StringBuilder(16);
        sb.append(ivValue);
        while (sb.length() < 16) {
            sb.append("0");
        }
        if (sb.length() > 16) {
            sb.setLength(16);
        }
        data = sb.toString().getBytes(StandardCharsets.UTF_8);
        return new IvParameterSpec(data);
    }
    
    public static void main(String[] args){
        // ob module aes key
        String aesKey = "BAA3E6F4DE8BDD46C7099EF943965E7EAC53DE8CBC455C2E34093418E33D68E7";
        String str = "IdNo=A100000056&MobilePhone=0973178915&Birthday=19560225&MemberSeq=aec7265";

        try {
            String encryptStr= encrypt(aesKey, str);

            log.info("Encrypt str: {}", encryptStr);
            log.info("Decrypt str: {}", decrypt(aesKey, encryptStr));
        } catch (DecoderException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
