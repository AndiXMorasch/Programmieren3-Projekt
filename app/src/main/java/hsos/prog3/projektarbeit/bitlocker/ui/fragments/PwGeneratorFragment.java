package hsos.prog3.projektarbeit.bitlocker.ui.fragments;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import hsos.prog3.projektarbeit.bitlocker.R;
import hsos.prog3.projektarbeit.bitlocker.logik.PasswordGenerator;
import hsos.prog3.projektarbeit.bitlocker.ui.PasswordCreationScreen;

/**
 * The PwGeneratorFragment class is held by the MainMenuHolder Activity and is the logical user interface
 * when clicking on the Password Generator menu tab. Here the user can adjust several parameters
 * through seek bars and generate a specific password based on those parameters as well as copy it to
 * clipboard or automatically take it along to the PasswordCreationScreen.
 *
 * @see PasswordCreationScreen
 * @see hsos.prog3.projektarbeit.bitlocker.ui.MainMenuHolder
 */

public class PwGeneratorFragment extends Fragment {

    private SeekBar seekBarPasswordLength;
    private SeekBar seekBarSpecialCharacterPortion;
    private TextView textViewPasswortLengthRating;
    private TextView textViewSpecialCharacterPortion;
    private TextView generatedPasswordTextViewResult;
    private PasswordGenerator passwordGenerator;
    private String generatedPassword;
    private final Integer OFFSET_SEEKBAR = 10;

    /**
     * onCreate method for the PwGeneratorFragment, also contains several onClick methods such as
     * the onClickMethod for the generatePasswordButton, copyToClipBoardButton or goToPasswordCreationButton.
     * Also containing two seek bars with corresponding onClickListeners. One for the password length, the other
     * for the special character portion in percent.
     *
     * @param inflater           is used to instantiate the contents of layout XML files into their corresponding View objects
     * @param container          required parameter for the inflater
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState
     * @return a view with the corresponding contents of the fragment
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pw_generator, container, false);

        this.passwordGenerator = new PasswordGenerator();
        this.seekBarPasswordLength = (SeekBar) view.findViewById(R.id.seekBarPasswordLength);
        this.textViewPasswortLengthRating = (TextView) view.findViewById(R.id.textViewSeekBarPasswordLength);
        this.seekBarSpecialCharacterPortion = (SeekBar) view.findViewById(R.id.seekBarSpecialCharacterPortion);
        this.textViewSpecialCharacterPortion = (TextView) view.findViewById(R.id.textViewSeekBarSpecialCharacterPortion);
        this.generatedPasswordTextViewResult = (TextView) view.findViewById(R.id.generatedPasswordTextViewResult);

        Button generatePasswordButton = (Button) view.findViewById(R.id.generatePasswordButton);
        generatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePassword();
            }
        });

        Button copyToClipBoardButton = (Button) view.findViewById(R.id.copyPasswordButton);
        copyToClipBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyPassword();
            }
        });

        Button goToPasswordCreationButton = (Button) view.findViewById(R.id.goToPasswordCreationButton);
        goToPasswordCreationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPasswordCreation();
            }
        });

        this.seekBarPasswordLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                textViewPasswortLengthRating.setText(String.valueOf(progress + OFFSET_SEEKBAR) + " " + getString(R.string.characters)
                        + " (" + checkPasswordLengthRating(progress + OFFSET_SEEKBAR) + ")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        this.seekBarSpecialCharacterPortion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                textViewSpecialCharacterPortion.setText(String.valueOf(progress + OFFSET_SEEKBAR) + "% " + getString(R.string.special_character_portion)
                        + " (" + checkSpecialCharacterPortionRating(progress + OFFSET_SEEKBAR) + ")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }

    /**
     * This method will lead the user to the PasswordCreationScreen activity together with his
     * generated password. This password will automatically fill the password text inputs in the new activity.
     * It is called when the user pressed the goToPasswordCreationButton.
     *
     * @see PasswordCreationScreen
     */

    private void goToPasswordCreation() {
        if (this.generatedPassword == null) {
            Toast.makeText(getContext(), getString(R.string.you_havent_generated_a_pw), Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(getActivity(), PasswordCreationScreen.class);
        intent.putExtra("decryptedPassword", getActivity().getIntent().getStringExtra("decryptedPassword"));
        intent.putExtra("generatedPassword", this.generatedPassword);
        startActivity(intent);
    }

    /**
     * This method will copy the generated password (if exists)
     * to clipboard and output a toast message if successfully copied.
     * It is called when the user pressed the copyToClipboardButton.
     */

    private void copyPassword() {
        if (this.generatedPassword == null) {
            Toast.makeText(getContext(), getString(R.string.nothing_to_copy), Toast.LENGTH_LONG).show();
            return;
        }

        ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("CreatedPassword", this.generatedPassword);
        clipboardManager.setPrimaryClip(clip);

        Toast.makeText(getContext(), getString(R.string.password_copied), Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will call the PasswordGenerator method generatePassword with the corresponding
     * seek bar values chosen by the user when he pressed the generatePasswordButton.
     */

    private void generatePassword() {
        this.generatedPassword = passwordGenerator.generatePassword(seekBarPasswordLength.getProgress()
                + OFFSET_SEEKBAR, seekBarSpecialCharacterPortion.getProgress() + OFFSET_SEEKBAR);
        generatedPasswordTextViewResult.setText(this.generatedPassword);
    }

    /**
     * This method will check the password length and return a safety feedback as a String.
     * length < 18 = "very weak"
     * length < 26 = "weak"
     * length < 34 = "expandable"
     * length < 42 = "strong"
     * length >= 42 = "very strong"
     *
     * @param passwordLength password length as integer value
     * @return one of the above mentioned safety feedback result strings
     */

    private String checkPasswordLengthRating(int passwordLength) {
        if (passwordLength < 18) {
            return getString(R.string.very_weak);
        } else if (passwordLength < 26) {
            return getString(R.string.weak);
        } else if (passwordLength < 34) {
            return getString(R.string.expandable);
        } else if (passwordLength < 42) {
            return getString(R.string.strong);
        }
        return getString(R.string.very_strong);
    }

    /**
     * This method will check the special character portion in percent and return a safety feedback as a String.
     * length < 18 = "very weak"
     * length < 26 = "weak"
     * length < 34 = "expandable"
     * length < 42 = "strong"
     * length >= 42 = "very strong"
     *
     * @param specialCharacterPortion special character portion in percent as integer value
     * @return one of the above mentioned safety feedback result strings
     */

    private String checkSpecialCharacterPortionRating(int specialCharacterPortion) {
        if (specialCharacterPortion < 18) {
            return getString(R.string.very_weak);
        } else if (specialCharacterPortion < 26) {
            return getString(R.string.weak);
        } else if (specialCharacterPortion < 34) {
            return getString(R.string.expandable);
        } else if (specialCharacterPortion < 42) {
            return getString(R.string.strong);
        }
        return getString(R.string.very_strong);
    }
}