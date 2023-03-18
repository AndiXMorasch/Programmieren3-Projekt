package hsos.prog3.projektarbeit.bitlocker.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
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
import hsos.prog3.projektarbeit.bitlocker.logik.PasswordGenerator;

/**
 * The PasswordCreationScreen activity is the logical and graphical user interface
 * responsible for creating a website and corresponding password dataset entry into
 * the database.
 *
 * @author Andreas Morasch
 */

public class PasswordCreationScreen extends AppCompatActivity {

    private EditText websiteInit;
    private EditText passwordInit;
    private EditText passwordConfirm;
    private SeekBar seekBarPasswordStrength;
    private TextView textViewSeekBarPasswordStrength;
    private DatabaseHelper databaseHelper;
    private AES_Encryption aes_encryption;
    private String decryptedMasterPW;
    private PasswordGenerator passwordGenerator;
    private final Integer offsetSeekBar = 10;

    /**
     * onCreate method of the PasswordCreationScreen, also contains several onClick methods,
     * for the backToMainMenuButton, savePasswordToDBButton, generatePasswordButtonPasswordCreation
     * and an onSeekbarChangeListener for the seekBarPasswortStrength seek bar variable.
     *
     * @param savedInstanceState if the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains
     *                           the data it most recently supplied in onSaveInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_creation_screen);

        this.websiteInit = findViewById(R.id.websiteFieldEditText);
        this.passwordInit = findViewById(R.id.passwordFieldEditText1);
        this.passwordConfirm = findViewById(R.id.passwordFieldEditText2);
        this.seekBarPasswordStrength = findViewById(R.id.seekBarPasswortStrength);
        this.textViewSeekBarPasswordStrength = findViewById(R.id.textViewSeekBarPasswordStrength);
        this.databaseHelper = new DatabaseHelper(this);
        this.passwordGenerator = new PasswordGenerator();
        Intent receiverIntent = getIntent();

        if (receiverIntent.getStringExtra("generatedPassword") != null) {
            fillPasswordFieldsWithGeneratedPassword(receiverIntent.getStringExtra("generatedPassword"));
        }

        Button backToMainMenuButton = (Button) findViewById(R.id.cancelButtonPasswordCreation);
        backToMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMainMenu();
            }
        });

        Button savePasswordToDBButton = (Button) findViewById(R.id.saveButtonPasswordCreation);
        savePasswordToDBButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                try {
                    savePasswordToDB();
                } catch (UnsupportedEncodingException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        });

        Button generatePasswordButtonPasswordCreation = (Button) findViewById(R.id.generatePasswordButtonPasswordCreation);
        generatePasswordButtonPasswordCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRandomPassword();
            }
        });

        seekBarPasswordStrength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                textViewSeekBarPasswordStrength.setText(String.valueOf(checkPasswordStrengthRating(progress + offsetSeekBar) + " ("
                        + (progress + offsetSeekBar) + " " + getString(R.string.characters_shorted) + ", " + (progress + offsetSeekBar) + "% " +
                        getString(R.string.sp_characters_shorted) + ")"));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        this.decryptedMasterPW = receiverIntent.getStringExtra("decryptedPassword");
        try {
            aes_encryption = new AES_Encryption(this.decryptedMasterPW);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will call the PasswordGenerator method generatePassword with the corresponding
     * seek bar value chosen by the user when he pressed the generatePasswordButtonPasswordCreation.
     * It also sets both text values of the passwordEditTexts (passwordInit and passwordConfirm)
     * automatically to the just generated password.
     */

    private void generateRandomPassword() {
        String generatedPassword = this.passwordGenerator.generatePassword(seekBarPasswordStrength.getProgress() + offsetSeekBar,
                seekBarPasswordStrength.getProgress() + offsetSeekBar);
        this.passwordInit.setText(generatedPassword);
        this.passwordConfirm.setText(generatedPassword);
    }

    /**
     * This method will check the password length as well as the special character
     * portion in percent and return a safety feedback as a String.
     * length < 18 = "very weak"
     * length < 26 = "weak"
     * length < 34 = "expandable"
     * length < 42 = "strong"
     * length >= 42 = "very strong"
     *
     * @param passwordStrength passwordStrength as integer value
     * @return one of the above mentioned safety feedback result strings
     */

    private String checkPasswordStrengthRating(int passwordStrength) {
        if (passwordStrength < 18) {
            return getString(R.string.very_weak);
        } else if (passwordStrength < 26) {
            return getString(R.string.weak);
        } else if (passwordStrength < 34) {
            return getString(R.string.expandable);
        } else if (passwordStrength < 42) {
            return getString(R.string.strong);
        }
        return getString(R.string.very_strong);
    }

    /**
     * This method is called by the onCreate method when there is a
     * receiver intent String named "generatedPassword". This means
     * that the PasswordCreationScreen has been started by the PwGeneratorFragment
     * which passed the generatedPassword to the PasswordCreationScreen activity.
     * The generated password will fill the editTexts passwordInit and passwordConfirm.
     *
     * @param generatedPW generated password from the PwGeneratorFragment as a String
     */

    private void fillPasswordFieldsWithGeneratedPassword(String generatedPW) {
        this.passwordInit.setText(generatedPW);
        this.passwordConfirm.setText(generatedPW);
    }

    /**
     * This method is called by the backToMainMenuButton (cancel Button) and
     * drop the user back to the main menu (SafeFragment).
     */

    private void backToMainMenu() {
        Intent intent = new Intent(this, MainMenuHolder.class);
        intent.putExtra("decryptedPassword", decryptedMasterPW);
        startActivity(intent);
    }

    /**
     * This method will save the user input to the database if they meet the appropriate requirements.
     * Those requirements are:
     * required fields cannot be empty, passwordInit must equal passwordConfirm, passwordLength cannot be
     * less than 10 characters and greater then 50 characters, website length cannot be greater than
     * 20 characters, since the website name is key attribute in the database it cannot be already existing.
     *
     * @throws UnsupportedEncodingException The Character Encoding is not supported.
     * @throws NoSuchPaddingException       This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws IllegalBlockSizeException    This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws NoSuchAlgorithmException     This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws BadPaddingException          This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws InvalidKeyException          This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void savePasswordToDB() throws UnsupportedEncodingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String websiteInit = this.websiteInit.getText().toString();
        String passwordInit = this.passwordInit.getText().toString();
        String passwordConfirm = this.passwordConfirm.getText().toString();

        if (websiteInit.isEmpty() || passwordInit.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, getString(R.string.required_field_is_empty), Toast.LENGTH_LONG).show();
            return;
        } else if (!passwordInit.equals(passwordConfirm)) {
            Toast.makeText(this, getString(R.string.passwords_unequal), Toast.LENGTH_LONG).show();
            return;
        } else if (passwordInit.length() < 10) {
            Toast.makeText(this, getString(R.string.password_too_short), Toast.LENGTH_LONG).show();
            return;
        } else if (passwordInit.length() > 50) {
            Toast.makeText(this, getString(R.string.password_too_long), Toast.LENGTH_LONG).show();
            return;
        } else if (websiteInit.length() > 20) {
            Toast.makeText(this, getString(R.string.website_too_long), Toast.LENGTH_LONG).show();
            return;
        } else if (databaseHelper.checkExistence(websiteInit)) {
            Toast.makeText(this, getString(R.string.website_already_exists), Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(this, getString(R.string.password_created), Toast.LENGTH_LONG).show();
        }

        String encryptedPW = this.aes_encryption.encryptPassword(passwordInit);
        databaseHelper.addData(websiteInit, encryptedPW);

        this.websiteInit.getText().clear();
        this.passwordInit.getText().clear();
        this.passwordConfirm.getText().clear();
    }
}