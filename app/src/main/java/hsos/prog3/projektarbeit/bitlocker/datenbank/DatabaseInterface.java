package hsos.prog3.projektarbeit.bitlocker.datenbank;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import hsos.prog3.projektarbeit.bitlocker.logik.AES_Encryption;

/**
 * The database interface for the DatabaseHelper class.
 * More information about the methods can be found in the DatabaseHelper class.
 *
 * @author Andreas Morasch
 * @see DatabaseHelper
 */

public interface DatabaseInterface {
    void onCreate(SQLiteDatabase sqLiteDatabase);

    void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1);

    boolean addData(String website, String encryptedPW);

    void updateWebsite(String oldWebsite, String newWebsite);

    void updatePassword(String website, String newPassword);

    public String getPassword(String website);

    void removeOne(String website);

    void removeAll();

    public boolean checkExistence(String website);

    Cursor getDataCursor();

    void logAllEntries();

    void dropCurrentTable();

    void updateAllPasswords(AES_Encryption aes_encryption, String neuesPasswort)
            throws UnsupportedEncodingException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;
}
