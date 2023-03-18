package hsos.prog3.projektarbeit.bitlocker.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import hsos.prog3.projektarbeit.bitlocker.R;
import hsos.prog3.projektarbeit.bitlocker.datenbank.DatabaseHelper;
import hsos.prog3.projektarbeit.bitlocker.logik.AES_Encryption;

/**
 * The LoginScreen activity is the graphical and logical user interface responsible
 * for checking the users input password with his registration password.
 *
 * @author Andreas Morasch
 */

public class LoginScreen extends AppCompatActivity {

    private EditText masterpasswortConfirm;
    private DatabaseHelper databaseHelper;

    /**
     * onCreate method of the LoginScreen, also contains an onClickListener which is responsible
     * for starting a new MainMenuHolder activity when called.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously
     *                           being shut down then this Bundle contains the data it
     *                           most recently supplied in #onSaveInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                checkMasterPassword();
            }
        });

        this.masterpasswortConfirm = findViewById(R.id.masterpasswortFeldLoginEditText);
        this.databaseHelper = new DatabaseHelper(this);
    }

    /**
     * This method will check whether the entered password from the login screen
     * matches the password in the database previously created in the RegistrationScreen.
     * If it matches, then starting a new MainMenuHolder activity. If not outputs a toast
     * message saying the password is incorrect.
     *
     * @see RegistrationScreen
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkMasterPassword() {
        String masterpasswordToConfirm = this.masterpasswortConfirm.getText().toString();
        Cursor cursor = databaseHelper.getDataCursor();
        cursor.moveToFirst();
        String encryptedMasterPW = cursor.getString(1);
        String decryptedMasterPW;

        try {
            AES_Encryption aes_encryption = new AES_Encryption(masterpasswordToConfirm);
            decryptedMasterPW = aes_encryption.decryptPassword(encryptedMasterPW);
        } catch (Throwable e) {
            Toast.makeText(this, getString(R.string.wrong_masterpassword), Toast.LENGTH_LONG).show();
            return;
        }

        if (decryptedMasterPW.equals(masterpasswordToConfirm)) {
            Intent intent = new Intent(this, MainMenuHolder.class);
            intent.putExtra("decryptedPassword", decryptedMasterPW);
            startActivity(intent);
        }
    }
}