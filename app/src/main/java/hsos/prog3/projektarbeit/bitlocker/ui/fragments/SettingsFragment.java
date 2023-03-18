package hsos.prog3.projektarbeit.bitlocker.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import hsos.prog3.projektarbeit.bitlocker.R;
import hsos.prog3.projektarbeit.bitlocker.datenbank.DatabaseHelper;
import hsos.prog3.projektarbeit.bitlocker.ui.LoginScreen;
import hsos.prog3.projektarbeit.bitlocker.ui.MainMenuHolder;
import hsos.prog3.projektarbeit.bitlocker.ui.RegistrationScreen;

/**
 * The SettingsFragment class is held by the MainMenuHolder Activity and is the logical user interface
 * when clicking on the Settings menu tab. Here the user can toggle the safe timeout as well as choose the
 * corresponding safe timeout time. This fragment is also holding a logout button and a delete account button.
 *
 * @see MainMenuHolder
 */

public class SettingsFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private SwitchMaterial safeTimeoutSwitch;
    private TextView timeOutSwitchText;
    private CheckBox deleteAccountConfirmationCheckbox;
    private RadioButton radioButton1Min;
    private RadioButton radioButton5Min;
    private RadioButton radioButton30Min;
    private long startTimeInMillis;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SAFE_TIMEOUT_SWITCH = "safeTimeoutSwitch";
    public static final String SAFE_TIMEOUT_MINUTES = "safeTimeoutMinutes";

    /**
     * onCreate method for the SettingsFragment, also contains several onClick methods such as
     * the onClick method for the logoutButton, deleteAccountButton as well as the safeTimeoutSwitch
     * and its corresponding radioButtons.
     *
     * @param inflater           is used to instantiate the contents of layout XML files into their corresponding View objects
     * @param container          required parameter for the inflater
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down then
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState
     * @return a view with the corresponding contents of the fragment
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        this.databaseHelper = new DatabaseHelper(getContext());
        this.timeOutSwitchText = view.findViewById(R.id.safeTimeoutSwitchText);
        this.safeTimeoutSwitch = (SwitchMaterial) view.findViewById(R.id.safeTimeoutSwitch);
        this.deleteAccountConfirmationCheckbox = view.findViewById(R.id.checkboxDeleteAccount);
        this.radioButton1Min = view.findViewById(R.id.radioButton1MinTimeout);
        this.radioButton5Min = view.findViewById(R.id.radioButton5MinTimeout);
        this.radioButton30Min = view.findViewById(R.id.radioButton30MinTimeout);
        this.startTimeInMillis = MainMenuHolder.getStartTimeInMillis();

        this.safeTimeoutSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimeoutData();
                setTimeOutSwitchTextOnClick();
            }
        });

        this.radioButton1Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimeoutDataMinutes((String) radioButton1Min.getText());
            }
        });

        this.radioButton5Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimeoutDataMinutes((String) radioButton5Min.getText());
            }
        });

        this.radioButton30Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimeoutDataMinutes((String) radioButton30Min.getText());
            }
        });

        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        Button deleteAccountButton = view.findViewById(R.id.deleteAccountButton);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });

        loadTimeoutData(requireContext());
        updateTimeoutViews();
        setTimeOutSwitchTextOnClick();

        return view;
    }

    /**
     * This method will check if the switch is enabled or not.
     * If enabled, it will call the setRadioButtonsEnabled() method,
     * else it will call the setRadioButtonsDisabled() method.
     */

    private void checkIfSwitchEnabled() {
        if (safeTimeoutSwitch.isChecked()) {
            setRadioButtonsEnabled();
        } else {
            setRadioButtonsDisabled();
        }
    }

    /**
     * This method is called by the checkIfSwitchEnabled() method.
     * It will enable (make clickable) all radio buttons and set
     * one of them checked based on the value of the startTimeInMillis
     * variable.
     */

    private void setRadioButtonsEnabled() {
        radioButton1Min.setEnabled(true);
        radioButton5Min.setEnabled(true);
        radioButton30Min.setEnabled(true);
        if (startTimeInMillis == 60000) {
            radioButton1Min.setChecked(true);
        } else if (startTimeInMillis == 300000) {
            radioButton5Min.setChecked(true);
        } else if (startTimeInMillis == 1800000) {
            radioButton30Min.setChecked(true);
        }
    }

    /**
     * This method is called by the checkIfSwitchEnabled() method.
     * It will disable (make not clickable) all radio buttons and set
     * one of them checked based on the value of the startTimeInMillis
     * variable.
     */

    private void setRadioButtonsDisabled() {
        radioButton1Min.setEnabled(false);
        radioButton5Min.setEnabled(false);
        radioButton30Min.setEnabled(false);
        if (startTimeInMillis == 60000) {
            radioButton1Min.setChecked(true);
        } else if (startTimeInMillis == 300000) {
            radioButton5Min.setChecked(true);
        } else if (startTimeInMillis == 1800000) {
            radioButton30Min.setChecked(true);
        }
    }

    /**
     * This method is called by the radioButtons onClickListener every time the user
     * will click on another radio button. It will update the sharedPreferences variable
     * (also used by MainMenuHolder) as well as update the startTimeInMillis variable to
     * the value selected by the user.
     *
     * @param radioBtnName name of the radio button to know the caller of the method
     * @see MainMenuHolder
     */

    private void saveTimeoutDataMinutes(String radioBtnName) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (radioBtnName.equals(getString(R.string.one_minute))) {
            editor.putLong(SAFE_TIMEOUT_MINUTES, 60000);
            startTimeInMillis = 60000;
        } else if (radioBtnName.equals(getString(R.string.five_minutes))) {
            editor.putLong(SAFE_TIMEOUT_MINUTES, 300000);
            startTimeInMillis = 300000;
        } else {
            editor.putLong(SAFE_TIMEOUT_MINUTES, 1800000);
            startTimeInMillis = 1800000;
        }
        editor.apply();
    }

    /**
     * This method is called by the safeTimeOutSwitch onClickListener every time the user
     * will toggle the switch. It will update the sharedPreferences variable which is also used
     * by the MainMenuHolder. Furthermore it will call the checkIfSwitchEnabled() method to decide
     * whether to enable or disable the radio button group.
     */

    private void saveTimeoutData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        checkIfSwitchEnabled();

        editor.putBoolean(SAFE_TIMEOUT_SWITCH, this.safeTimeoutSwitch.isChecked());
        editor.apply();
    }

    /**
     * This method is called by the onCreateView of the SettingsFragment to determine the last status of
     * both the safeTimeOutSwitch and the startTimeInMillis variables. It is also used by the MainMenuHolder
     * to determine the latest status of those variables.
     *
     * @param context context required since it is a static method
     */

    public static void loadTimeoutData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        MainMenuHolder.setSafeTimeoutSwitchOnOff(sharedPreferences.getBoolean(SAFE_TIMEOUT_SWITCH, false));
        MainMenuHolder.setStartTimeInMillis(sharedPreferences.getLong(SAFE_TIMEOUT_MINUTES, 60000));
    }

    /**
     * This method is called by the onCreateView of the SettingsFragment to always display the correct
     * last status of the safeTimeOutSwitch as well as its radio button group visibility when the user
     * restarts the app or logout from it.
     */

    private void updateTimeoutViews() {
        this.safeTimeoutSwitch.setChecked(MainMenuHolder.getSafeTimeoutSwitchOnOff());
        checkIfSwitchEnabled();
    }

    /**
     * This method is changing the corresponding safeTimeOutSwitch textView.
     * When the switch is checked: safe timeout on
     * When the switch is not checked: safe timeout off
     */

    private void setTimeOutSwitchTextOnClick() {
        if (this.safeTimeoutSwitch.isChecked()) {
            this.timeOutSwitchText.setText(getString(R.string.safe_timeout_on));
        } else {
            this.timeOutSwitchText.setText(R.string.safe_timeout_off);
        }
    }

    /**
     * This method will logout the user and drop him back to the LoginScreen activity.
     */

    private void logout() {
        Intent intent = new Intent(getContext(), LoginScreen.class);
        startActivity(intent);
    }

    /**
     * This method will delete the users account with all his corresponding
     * website and password data. Those changes are permanent and need to be
     * confirmed by the deleteAccountConfirmationCheckbox first before continue!
     * After the account has been deleted, the user will be dropped back to the
     * RegistrationScreen activity.
     */

    private void deleteAccount() {
        if (this.deleteAccountConfirmationCheckbox.isChecked()) {
            this.databaseHelper.removeAll();
            Intent intent = new Intent(getContext(), RegistrationScreen.class);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), getString(R.string.not_checked_warning), Toast.LENGTH_LONG).show();
        }
    }
}