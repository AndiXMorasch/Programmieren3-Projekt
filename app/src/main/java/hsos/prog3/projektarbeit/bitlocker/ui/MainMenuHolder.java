package hsos.prog3.projektarbeit.bitlocker.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import hsos.prog3.projektarbeit.bitlocker.R;
import hsos.prog3.projektarbeit.bitlocker.ui.fragments.PwGeneratorFragment;
import hsos.prog3.projektarbeit.bitlocker.ui.fragments.SafeFragment;
import hsos.prog3.projektarbeit.bitlocker.ui.fragments.SettingsFragment;

/**
 * The MainMenuHolder activity holds the safe timeout variables as well as the startTimer() and closeTimer() methods.
 * Furthermore it is responsible for holding the main menu navigation and switch between fragments.
 *
 * @author Andreas Morasch
 */

public class MainMenuHolder extends AppCompatActivity {

    private final SafeFragment TRESOR_FRAGMENT = new SafeFragment();
    private final SettingsFragment SETTINGS_FRAGMENT = new SettingsFragment();
    private final PwGeneratorFragment PW_GENERATOR_FRAGMENT = new PwGeneratorFragment();

    private static boolean safeTimeoutSwitchOnOff;
    private static long startTimeInMillis;
    private static CountDownTimer countDownTimer;

    /**
     * onCreate method of MainMenuHolder, also contains setOnItemSelectedListener which
     * is responsible for switching between fragments.
     *
     * @param savedInstanceState if the activity is being re-initialized after
     *                           previously being shut down then this Bundle
     *                           contains the data it most recently supplied in #onSaveInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hauptmenue_screen);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, TRESOR_FRAGMENT).commit();

        startTimer(this);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tresor:
                        cancelTimer();
                        startTimer(getApplicationContext());
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, TRESOR_FRAGMENT).commit();
                        return true;
                    case R.id.pwGenerator:
                        cancelTimer();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, PW_GENERATOR_FRAGMENT).commit();
                        return true;
                    case R.id.einstellungen:
                        cancelTimer();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, SETTINGS_FRAGMENT).commit();
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * Getter for the safeTimeOutSwitchOnOff variable.
     *
     * @return true if switch is checked, false if not
     */

    public static boolean getSafeTimeoutSwitchOnOff() {
        return safeTimeoutSwitchOnOff;
    }

    /**
     * Setter for the safeTimeOutSwitchOnOff variable.
     *
     * @param newBoolean new safeTimeOutSwitchOnOff variable value
     */

    public static void setSafeTimeoutSwitchOnOff(boolean newBoolean) {
        safeTimeoutSwitchOnOff = newBoolean;
    }

    /**
     * Getter for the startTimeInMillis variable.
     *
     * @return startTimeInMillis variable
     */

    public static long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    /**
     * Setter for the startTimeInMillis variable.
     *
     * @param startTimeInMillis new startTimeInMillis variable
     */

    public static void setStartTimeInMillis(long startTimeInMillis) {
        MainMenuHolder.startTimeInMillis = startTimeInMillis;
    }

    /**
     * This method is counting from startTimeInMillis to zero.
     *
     * @param context context required since it is a static method
     */

    private static void safeTimeout(Context context) {
        countDownTimer = new CountDownTimer(startTimeInMillis, 1000) {

            /**
             * With every countDownInterval there will be a logcat message with the remaining Milliseconds.
             * @param millisLeftUntilFinished milliseconds left until finished
             */
            @Override
            public void onTick(long millisLeftUntilFinished) {
                Log.d("TIMER: ", String.valueOf(millisLeftUntilFinished));
            }

            /**
             * When the countdown is finished, a new intent is being started to throw the user into the login menu again.
             */
            @Override
            public void onFinish() {
                Intent intent = new Intent(context, LoginScreen.class);
                context.startActivity(intent);
            }
        }.start();
    }

    /**
     * This method is calling the loadTimeoutData(context) method from the
     * Settings Fragment in order to ensure that the required data is up to date.
     * After that, if the safeTimeoutSwitch is on (true), starting the timer.
     *
     * @param context context required since it is a static method
     */

    public static void startTimer(Context context) {
        SettingsFragment.loadTimeoutData(context);

        if (safeTimeoutSwitchOnOff) {
            safeTimeout(context);
        }
    }

    /**
     * This method will cancel the currently running timer.
     * It is needed when the user is leaving the safe-fragment.
     */

    public static void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}