package at.vcity.androidim;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
 

public class CryptoFileUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
 
    public static byte[] encrypt(String key, File inputFile, File outputFile)
            throws Exception {
        return doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
 
    public static byte[] decrypt(String key, File inputFile)
            throws Exception {
        return doCrypto2(Cipher.DECRYPT_MODE, key, inputFile);
    }
 
    private static byte[] doCrypto(int cipherMode, String key, File inputFile,
            File outputFile) throws Exception {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
             
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
            return outputBytes;
             
        } catch (Exception ex) {
            throw new Exception("Error encrypting file", ex);
        }
    }

    private static byte[] doCrypto2(int cipherMode, String key, File inputFile) throws Exception {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

             byte[] outputBytes = cipher.doFinal(inputBytes);
            inputStream.close();
            return outputBytes;


        } catch (Exception ex) {
            throw new Exception("Error decrypting file", ex);
        }
    }
}