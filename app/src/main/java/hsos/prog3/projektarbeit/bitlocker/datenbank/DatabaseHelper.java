package hsos.prog3.projektarbeit.bitlocker.datenbank;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import hsos.prog3.projektarbeit.bitlocker.logik.AES_Encryption;

/**
 * The DatabaseHelper class extends the SQLiteOpenHelper and implements the DatabaseInterface.
 * Purpose of this class is to be able to make and edit entries to the database
 * which is holding both websites and encrypted passwords.
 *
 * @author Andreas Morasch
 */

public class DatabaseHelper extends SQLiteOpenHelper implements DatabaseInterface {

    private static final String TABLE_NAME = "passwort_table";
    private static final String WEBSITE_COL = "website";
    private static final String PASSWORD_COL = "crypted_password";

    /**
     * Constructor for the DatabaseHelper class.
     *
     * @param context Context needs to be given to let the DatabaseHelper know, where the method call was made from.
     */

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    /**
     * At the start of the application this method will check whether a table (database file) already exists or not.
     * If not, creates one with the initialized static final class attributes.
     *
     * @param db SQLiteDatabase object in order to check the existence
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + WEBSITE_COL + " TEXT PRIMARY KEY, " +
                PASSWORD_COL + " TEXT)";
        db.execSQL(createTable);
    }

    /**
     * This method is only called when the corresponding table (database file) already exists, but the stored version number is lower
     * than requested in the constructor. It will drop the current table and create a new one with the correct version.
     *
     * @param db         SQLiteDatabase object in order to create the new table
     * @param oldVersion old version number
     * @param newVersion new version number
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor cursor = getDataCursor();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

        while (cursor.moveToNext()) {
            addData(cursor.getString(0), cursor.getString(1));
        }
    }

    /**
     * This method adds data (website + encrypted Password) to the current SQLite database table.
     *
     * @param website     website name to be stored (key attribute)
     * @param encryptedPW encrypted password to be stored
     * @return boolean value returning true if the storage was successful and false if not
     */

    public boolean addData(String website, String encryptedPW) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEBSITE_COL, website);
        contentValues.put(PASSWORD_COL, encryptedPW);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    /**
     * This method updates the website name based on the key attribute (oldWebsite).
     *
     * @param oldWebsite old website name to be found and replaced
     * @param newWebsite new website name which replaces the old website name
     */

    public void updateWebsite(String oldWebsite, String newWebsite) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET " + WEBSITE_COL + " = '" + newWebsite + "'" +
                " WHERE " + WEBSITE_COL + " = '" + oldWebsite + "'");
    }

    /**
     * This method updates the encrypted password based on the key attribute (website).
     *
     * @param website              website name as the key attribute to search for
     * @param newEncryptedPassword new encrypted password to replace the old one
     */

    public void updatePassword(String website, String newEncryptedPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET " + PASSWORD_COL + " = '" + newEncryptedPassword + "'" +
                " WHERE " + WEBSITE_COL + " = '" + website + "'");
    }

    /**
     * This method removes one specific database entry based on the key attribute (website).
     *
     * @param website website name to search for and remove the specific entry
     */

    public void removeOne(String website) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, WEBSITE_COL + "= '" + website + "'", null);
    }

    /**
     * This method removes all database entries.
     */

    public void removeAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    /**
     * This method returns a data cursor concerning the current database.
     * It can be used for iterations and to log data for example, since you
     * basically have a snapshot of the whole database in one variable.
     *
     * @return current tables data cursor
     */

    public Cursor getDataCursor() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        @SuppressLint("Recycle") Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * This method searches for a specific password based on the key attribute (website).
     *
     * @param website website name to search for
     * @return specific password of this website
     */

    public String getPassword(String website) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + PASSWORD_COL + " FROM " + TABLE_NAME + " WHERE " + WEBSITE_COL + "="
                + "'" + website + "'";
        @SuppressLint("Recycle") Cursor data = db.rawQuery(query, null);
        data.moveToFirst();
        return data.getString(0);
    }

    /**
     * This method checks if there is an existing key attribute (website) in the database.
     *
     * @param website website name to search for
     * @return boolean true if website was found (exists), false if it was not found (not exists)
     */

    public boolean checkExistence(String website) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + WEBSITE_COL + " FROM " + TABLE_NAME + " WHERE " + WEBSITE_COL + "="
                + "'" + website + "'";
        @SuppressLint("Recycle") Cursor data = db.rawQuery(query, null);
        return data.getCount() != 0;
    }

    /**
     * This method will log all entries in the Logcat window.
     */

    public void logAllEntries() {
        Cursor data = getDataCursor();
        while (data.moveToNext()) {
            Log.d("Your Data: ", data.getString(0) + "||" + data.getString(1));
        }
    }

    /**
     * This method will drop the current table if exists and create a new (empty) one.
     */

    public void dropCurrentTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * This method will update all encrypted passwords based on the given new parameter master password.
     * The only purpose of the method is that the user can update his master password which
     * is used for encrypting all website passwords without losing the relevant encryption information.
     *
     * @param aes_encryption    aes_encryption object required in order to perform this change
     * @param newMasterPassword new master password required which will be used to encrypt all decrypted passwords once again
     * @throws UnsupportedEncodingException The Character Encoding is not supported.
     * @throws NoSuchPaddingException       This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws IllegalBlockSizeException    This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws NoSuchAlgorithmException     This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws BadPaddingException          This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws InvalidKeyException          This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateAllPasswords(AES_Encryption aes_encryption, String newMasterPassword)
            throws UnsupportedEncodingException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        // Schritt 1: Entschlüssel das Passwort mit altem Schlüssel
        ArrayList<String> encryptedPasswords = new ArrayList<>();
        Cursor cursor1 = getDataCursor();
        while (cursor1.moveToNext()) {
            String encryptedPassword = aes_encryption.decryptPassword(cursor1.getString(1));
            // Schritt 1.1: Lagere die entschlüsselten Passwörter in einem Array
            encryptedPasswords.add(encryptedPassword);
        }
        // Schritt 2: Ändere den Schlüsseltext der AES_Encryption
        aes_encryption.changeKeyText(newMasterPassword);
        // Schritt 2.1: Verschlüssel das entschlüsselte Passwort mit neuem Schlüssel (also immer "encryptPassword" aufrufen mit Index aus dem Array)
        Cursor cursor2 = getDataCursor();
        int i = 0;
        while (cursor2.moveToNext()) {
            String cryptedPasswort = aes_encryption.encryptPassword(encryptedPasswords.get(i));
            Log.e("TEST: ", "website=" + cursor2.getString(0) + "crypted: " + cryptedPasswort + " || " + encryptedPasswords.get(i));
            // Schritt 3. UpdatePasswort Methode aufrufen und neues Passwort mitgeben um es zu aktualisieren
            this.updatePassword(cursor2.getString(0), cryptedPasswort);
            i++;
        }
    }
}
