package org.r.base.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * @author casper
 * @date 19-10-18 上午10:29
 **/
public class XDigestUtils extends DigestUtils {
    public static final String KEY_SHA = "SHA";
    public static final String KEY_MD5 = "MD5";
    public static final String ALGORITHM = "DES";
    public static final String KEY_MAC = "HmacMD5";
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public XDigestUtils() {
    }

    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);

        for (int j = 0; j < len; ++j) {
            buf.append(HEX_DIGITS[bytes[j] >> 4 & 15]);
            buf.append(HEX_DIGITS[bytes[j] & 15]);
        }

        return buf.toString();
    }

    public static final String encryptSHA1(String data) {
        if (StringUtils.isEmpty(data)) {
            return null;
        } else {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
                messageDigest.update(data.getBytes());
                return getFormattedText(messageDigest.digest());
            } catch (Exception var2) {
                throw new RuntimeException(var2);
            }
        }
    }

    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    public static byte[] encryptMD5(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        return md5.digest();
    }

    public static byte[] encryptSHA(byte[] data) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA");
        sha.update(data);
        return sha.digest();
    }

    public static String initMacKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5");
        SecretKey secretKey = keyGenerator.generateKey();
        return encryptBASE64(secretKey.getEncoded());
    }

    public static byte[] encryptHMAC(byte[] data, String key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), "HmacMD5");
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return mac.doFinal(data);
    }

    private static Key toKey(byte[] key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        return secretKey;
    }

    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Key k = toKey(decryptBASE64(key));
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(2, k);
        return cipher.doFinal(data);
    }

    public static byte[] encrypt(byte[] data, String key) throws Exception {
        Key k = toKey(decryptBASE64(key));
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(1, k);
        return cipher.doFinal(data);
    }

    public static String initKey() throws Exception {
        return initKey((String) null);
    }

    public static String initKey(String seed) throws Exception {
        SecureRandom secureRandom = null;
        if (seed != null) {
            secureRandom = new SecureRandom(decryptBASE64(seed));
        } else {
            secureRandom = new SecureRandom();
        }

        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(secureRandom);
        SecretKey secretKey = kg.generateKey();
        return encryptBASE64(secretKey.getEncoded());
    }


    /**
     * aes-256-ecb-PKCS7Padding 解密
     *
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    public static String aes256EcbPKCS7PaddingDecrypt(String src, String key) throws Exception {

        byte[] bytes = aesDecrypt(src, key, 256, "ECB", "PKCS7Padding");
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * AES解密
     *
     * @param src         密文
     * @param key         秘钥
     * @param blockSize   块大小 128-192-256
     * @param blockAl     块解密算法
     * @param paddingMode 补齐模式
     * @return
     */
    public static byte[] aesDecrypt(String src, String key, Integer blockSize, String blockAl, String paddingMode) throws Exception {

        if (key == null || "".equals(key)) {
            throw new Exception("key can not be empty");
        }
        switch (blockSize) {
            case 128:
                if (key.length() != 16) {
                    throw new Exception(String.format("block size is %d, the key length must be 16", blockSize));
                }
                break;
            case 192:
                break;
            case 256:
                if (key.length() != 32) {
                    throw new Exception(String.format("block size is %d, the key length must be 32", blockSize));
                }
                break;
            default:
                throw new Exception("block size only can be 128,192,256");
        }
        KeyGenerator aesGen = KeyGenerator.getInstance("AES");
        aesGen.init(blockSize, new SecureRandom(src.getBytes()));
        SecretKey secretKey = aesGen.generateKey();
        byte[] encoded = secretKey.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(encoded, "AES");
        Cipher cipher = Cipher.getInstance(String.format("AES/%s/%s", blockAl, paddingMode));
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(src.getBytes());
    }


}
