package hsos.prog3.projektarbeit.bitlocker.logik;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * The aes encryption interface for the AES_Encryption class.
 * More information about the methods can be found in the AES_Encryption class.
 *
 * @author Andreas Morasch
 * @see AES_Encryption
 */

public interface AES_EncryptionInterface {
    void changeKeyText(String newMasterPassword);

    String encryptPassword(String passwort) throws NoSuchAlgorithmException, UnsupportedEncodingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException;

    String decryptPassword(String encryptedPassword) throws UnsupportedEncodingException, NoSuchPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;
}
