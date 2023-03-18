package hsos.prog3.projektarbeit.bitlocker.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
 * The PasswordViewScreen activity is the logical and graphical user interface
 * responsible for showing the user its website name with the corresponding
 * password. This activity only gets called when clicked on a recyclerView
 * object in the SafeFragment.
 *
 * @author Andreas Morasch
 * @see hsos.prog3.projektarbeit.bitlocker.ui.fragments.SafeFragment
 */

public class PasswordViewScreen extends AppCompatActivity {

    private String website;
    private String password;
    private EditText viewScreenWebsiteEditText;
    private EditText viewScreenPasswordEditText;
    private CheckBox checkBox;
    private DatabaseHelper databaseHelper;
    private AES_Encryption aes_encryption;

    /**
     * onCreateMethod of the PasswordViewScreen, also contains several onClick methods
     * for the backToMenuButton, deleteEntryButton and modifyEntryButton.
     *
     * @param savedInstanceState if the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains
     *                           the data it most recently supplied in onSaveInstanceState
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_view_screen);

        this.checkBox = findViewById(R.id.checkBoxApplyChanges);
        this.databaseHelper = new DatabaseHelper(this);
        try {
            this.aes_encryption = new AES_Encryption(getIntent().getStringExtra("decryptedPassword"));
            initialize();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Button backToMenuButton = (Button) findViewById(R.id.goBackToMainMenuButton);
        backToMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMainMenu();
            }
        });

        Button deleteEntryButton = (Button) findViewById(R.id.deleteEntryButton);
        deleteEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEntryAndResumeToMenu();
            }
        });

        Button modifyEntryButton = (Button) findViewById(R.id.modifyEntryButton);
        modifyEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    modifyPasswordAndWebsite();
                } catch (UnsupportedEncodingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This method will set the websiteEditText as well as the passwordEditText
     * to the corresponding values from the database.
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initialize() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            website = extras.getString("website");
        }

        try {
            this.password = aes_encryption.decryptPassword(databaseHelper.getPassword(website));
        } catch (UnsupportedEncodingException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        this.viewScreenWebsiteEditText = findViewById(R.id.viewScreenWebsiteEditText);
        this.viewScreenWebsiteEditText.setText(this.website);
        this.viewScreenPasswordEditText = findViewById(R.id.viewScreenPasswordEditText);
        this.viewScreenPasswordEditText.setText(this.password);
    }

    /**
     * This method is called by the backToMenuButton (cancel Button) and
     * drop the user back to the main menu (SafeFragment).
     */

    private void backToMainMenu() {
        Intent intent = new Intent(this, MainMenuHolder.class);
        intent.putExtra("decryptedPassword", getIntent().getStringExtra("decryptedPassword"));
        startActivity(intent);
    }

    /**
     * This method is called by the deleteEntryButton and will delete the entry
     * corresponding to the website and its password the user is currently viewing.
     * Since the changes are permanent, the user needs to verify his decision by
     * clicking a checkbox to continue.
     */

    private void deleteEntryAndResumeToMenu() {
        if (!checkBox.isChecked()) {
            Toast.makeText(this, getString(R.string.not_checked_warning), Toast.LENGTH_LONG).show();
            return;
        } else if (!password.equals(viewScreenPasswordEditText.getText().toString()) || !website.equals(viewScreenWebsiteEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.unapplied_changes_warning), Toast.LENGTH_LONG).show();
            return;
        } else {
            databaseHelper.removeOne(website);
        }
        backToMainMenu();
    }

    /**
     * This method is called by the modifyEntryButton and will modify the entry
     * corresponding to the website and its password the user is currently viewing.
     * Since the changes are permanent, the user needs to verify his decision by
     * clicking a checkbox to continue. In this case several requirements must be
     * fulfilled in order to accept the changes.
     * Those requirements are:
     * required fields cannot be empty, passwordInit must equal passwordConfirm, passwordLength cannot be
     * less than 10 characters and greater then 50 characters, website length cannot be greater than
     * 20 characters, since the website name is key attribute in the database it cannot be already existing,
     * there must be changes in the website name; password or both
     *
     * @throws UnsupportedEncodingException The Character Encoding is not supported.
     * @throws NoSuchPaddingException       This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws IllegalBlockSizeException    This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws NoSuchAlgorithmException     This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws BadPaddingException          This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws InvalidKeyException          This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void modifyPasswordAndWebsite() throws UnsupportedEncodingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (!checkBox.isChecked()) {
            Toast.makeText(this, getString(R.string.not_checked_warning), Toast.LENGTH_LONG).show();
        } else if (viewScreenWebsiteEditText.getText().toString().isEmpty() || viewScreenPasswordEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.required_field_is_empty), Toast.LENGTH_LONG).show();
        } else if (viewScreenWebsiteEditText.getText().toString().length() > 25) {
            Toast.makeText(this, getString(R.string.website_too_long), Toast.LENGTH_LONG).show();
        } else if (viewScreenPasswordEditText.getText().toString().length() > 50) {
            Toast.makeText(this, getString(R.string.password_too_long), Toast.LENGTH_LONG).show();
        } else if (viewScreenPasswordEditText.getText().toString().length() < 10) {
            Toast.makeText(this, getString(R.string.password_too_short), Toast.LENGTH_LONG).show();
        } else if (viewScreenWebsiteEditText.getText().toString().equals(website) && viewScreenPasswordEditText.getText().toString().equals(password)) {
            Toast.makeText(this, getString(R.string.no_changes_made), Toast.LENGTH_LONG).show();
        } else if (databaseHelper.checkExistence(viewScreenWebsiteEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.website_already_exists), Toast.LENGTH_LONG).show();
        } else if (!password.equals(viewScreenPasswordEditText.getText().toString()) && !website.equals(viewScreenWebsiteEditText.getText().toString())) {
            updateWebsite();
            updatePassword();
            Toast.makeText(this, getString(R.string.password_and_website_changed), Toast.LENGTH_LONG).show();
        } else if (!password.equals(viewScreenPasswordEditText.getText().toString())) {
            updatePassword();
            Toast.makeText(this, getString(R.string.password_changed), Toast.LENGTH_LONG).show();
        } else {
            updateWebsite();
            Toast.makeText(this, getString(R.string.website_changed), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method is called by the modifyPasswordAndWebsite() method and will modify the
     * website based on the content of the websiteEditText.
     */

    private void updateWebsite() {
        databaseHelper.updateWebsite(website, viewScreenWebsiteEditText.getText().toString());
        this.website = viewScreenWebsiteEditText.getText().toString();
    }

    /**
     * This method is called by the modifyPasswordAndWebsite() method and will modify the
     * password based on the content of the passwordEditText.
     *
     * @throws UnsupportedEncodingException The Character Encoding is not supported.
     * @throws NoSuchPaddingException       This exception is thrown when a particular padding mechanism is requested but is not available in the environment.
     * @throws IllegalBlockSizeException    This exception is thrown when the length of data provided to a block cipher is incorrect, i.e., does not match the block size of the cipher.
     * @throws NoSuchAlgorithmException     This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     * @throws BadPaddingException          This exception is thrown when a particular padding mechanism is expected for the input data but the data is not padded properly.
     * @throws InvalidKeyException          This is the exception for invalid Keys (invalid encoding, wrong length, uninitialized, etc).
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updatePassword() throws UnsupportedEncodingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        databaseHelper.updatePassword(website, aes_encryption.encryptPassword(viewScreenPasswordEditText.getText().toString()));
        this.password = viewScreenPasswordEditText.getText().toString();
    }
}