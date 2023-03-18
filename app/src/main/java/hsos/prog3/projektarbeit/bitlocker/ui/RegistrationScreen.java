package hsos.prog3.projektarbeit.bitlocker.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import hsos.prog3.projektarbeit.bitlocker.R;
import hsos.prog3.projektarbeit.bitlocker.datenbank.DatabaseHelper;
import hsos.prog3.projektarbeit.bitlocker.logik.AES_Encryption;

/**
 * The RegistrationScreen activity is the graphical and logical user interface responsible
 * for creating and saving the master password based on the users input to the database.
 *
 * @author Andreas Morasch
 */

public class RegistrationScreen extends AppCompatActivity {

    private EditText masterpasswortInit;
    private EditText masterpasswortConfirm;
    private DatabaseHelper databaseHelper;

    /**
     * onCreate method of the RegistrationScreen, also contains an onClickListener which is responsible
     * for saving the master password to the database when saveMasterPWButton was clicked.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously
     *                           being shut down then this Bundle contains the data
     *                           it most recently supplied in #onSaveInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen);

        this.masterpasswortInit = findViewById(R.id.masterpasswortFeld1RegistrierungEditText);
        this.masterpasswortConfirm = findViewById(R.id.masterpasswortFeld2RegistrierungEditText);
        this.databaseHelper = new DatabaseHelper(this);

        Button saveMasterPWButton = findViewById(R.id.registrierenButton);
        saveMasterPWButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                try {
                    saveMasterPassword();
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException | InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This method is called by the onClickListener of the saveMasterPWButton and is saving
     * the entered master password to the database if it meets the appropriate requirements.
     * In this case, the fixed key (website name) is "BitLocker" since this method is only
     * used once namely when the user is creating his account.
     * Those requirements are:
     * required fields cannot be empty, passwordInit must equal passwordConfirm, passwordLength cannot be
     * less than 10 characters and greater then 50 characters
     *
     * @throws UnsupportedEncodingException The Character Encoding is not supported.
     * @throws NoSuchPaddingException       This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws IllegalBlockSizeException    This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws NoSuchAlgorithmException     This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws BadPaddingException          This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws InvalidKeyException          This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveMasterPassword() throws UnsupportedEncodingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException {
        String masterpasswortInit = this.masterpasswortInit.getText().toString();
        String masterpasswortConfirm = this.masterpasswortConfirm.getText().toString();

        if (masterpasswortInit.isEmpty() || masterpasswortConfirm.isEmpty()) {
            Toast.makeText(this, getString(R.string.required_field_is_empty), Toast.LENGTH_LONG).show();
            return;
        } else if (!masterpasswortInit.equals(masterpasswortConfirm)) {
            Toast.makeText(this, getString(R.string.passwords_unequal), Toast.LENGTH_LONG).show();
            return;
        } else if (masterpasswortInit.length() < 10) {
            Toast.makeText(this, getString(R.string.password_too_short), Toast.LENGTH_LONG).show();
            return;
        } else if (masterpasswortInit.length() > 50) {
            Toast.makeText(this, getString(R.string.password_too_long), Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(this, getString(R.string.password_created), Toast.LENGTH_LONG).show();
        }

        AES_Encryption aes_encryption = new AES_Encryption(masterpasswortInit);
        String encryptedPW = aes_encryption.encryptPassword(masterpasswortInit);
        databaseHelper.addData("BitLocker", encryptedPW);

        this.masterpasswortInit.getText().clear();
        this.masterpasswortConfirm.getText().clear();

        Intent intent = new Intent(this, LoginScreen.class);
        startActivity(intent);
    }

}