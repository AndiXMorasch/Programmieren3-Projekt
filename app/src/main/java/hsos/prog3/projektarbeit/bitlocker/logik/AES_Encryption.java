package hsos.prog3.projektarbeit.bitlocker.logik;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * The AES_Encryption class implements the AES_EncryptionInterface and its purpose is to encrypt
 * and decrypt given passwords in order to ensure that no one from outside can see the decrypted passwords in the database.
 *
 * @author Andreas Morasch
 */

// Source: http://blog.axxg.de/java-aes-verschluesselung-mit-beispiel/
public class AES_Encryption implements AES_EncryptionInterface {

    private static String MASTER_PASSWORD;

    /**
     * Constructor for the AES_Encryption class.
     *
     * @param masterPassword important encryption key, which is used for encrypting all given passwords
     * @throws UnsupportedEncodingException The Character Encoding is not supported.
     * @throws NoSuchAlgorithmException     This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     */

    public AES_Encryption(String masterPassword) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MASTER_PASSWORD = masterPassword;
    }

    /**
     * This Method changes the MASTER_PASSWORD key attribute used to encrypt all given passwords.
     * Important: This method should never be called alone, it is part of the DatabaseHelper class method "updateAllPasswords".
     *
     * @param newMasterPassword new master password to replace the old one
     * @see hsos.prog3.projektarbeit.bitlocker.datenbank.DatabaseHelper
     */

    public void changeKeyText(String newMasterPassword) {
        MASTER_PASSWORD = newMasterPassword;
    }

    /**
     * This is a helper method which is called by the methods encryptPassword and decryptPassword and
     * returns a SecretKeySpec in order to perform an encryption or decryption.
     *
     * @return SecretKeySpec used for encryption and decryption
     * @throws UnsupportedEncodingException The Character Encoding is not supported.
     * @throws NoSuchAlgorithmException     This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     */

    private SecretKeySpec getSecretKeySpec() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // byte-Array erzeugen
        byte[] key = (MASTER_PASSWORD).getBytes(StandardCharsets.UTF_8);
        // aus dem Array einen Hash-Wert erzeugen mit MD5 oder SHA
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        key = messageDigest.digest(key);
        // 32*8 Bit benutzt
        key = Arrays.copyOf(key, 32);
        return new SecretKeySpec(key, "AES");
    }

    /**
     * This method encrypts a given password by using the AES_Encryption Algorithm.
     *
     * @param decryptedPassword decrypted password to be encrypted
     * @return encrypted password as a String
     * @throws UnsupportedEncodingException The Character Encoding is not supported.
     * @throws NoSuchPaddingException       This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws IllegalBlockSizeException    This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws NoSuchAlgorithmException     This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws BadPaddingException          This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws InvalidKeyException          This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encryptPassword(String decryptedPassword) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec());
        byte[] encrypted = cipher.doFinal(decryptedPassword.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * This method decrypts an encrypted Password by using the AES_Encryption Algorithm.
     *
     * @param encryptedPassword encrypted password to be decrypted
     * @return decrypted password as a String
     * @throws UnsupportedEncodingException The Character Encoding is not supported.
     * @throws NoSuchPaddingException       This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws IllegalBlockSizeException    This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws NoSuchAlgorithmException     This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws BadPaddingException          This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws InvalidKeyException          This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decryptPassword(String encryptedPassword) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] encrypted = Base64.getDecoder().decode(encryptedPassword);
        Cipher cipher2 = Cipher.getInstance("AES");
        cipher2.init(Cipher.DECRYPT_MODE, getSecretKeySpec());
        byte[] cipherData2 = cipher2.doFinal(encrypted);
        return new String(cipherData2);
    }
}
