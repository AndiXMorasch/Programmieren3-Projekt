package hsos.prog3.projektarbeit.bitlocker.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import hsos.prog3.projektarbeit.bitlocker.R;
import hsos.prog3.projektarbeit.bitlocker.datenbank.DatabaseHelper;

/**
 * The SplashScreen activity is the logical and graphical user interface
 * responsible for displaying the splash screen with the apps name as well as the logo.
 *
 * @author Andreas Morasch
 */

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private ImageView imageViewLogo;
    private TextView imageViewAppName;

    /**
     * onCreate method of the SplashScreen, also contains a runnable object to
     * perform the animation execution on another thread. After the animation
     * there will be a new handler in which the decision is made whether to start
     * the LoginScreen or the RegistrationScreen activity based on the fact if the
     * first database entry is empty (registration screen) or not (login screen).
     *
     * @param savedInstanceState if the activity is being re-initialized after previously
     *                           being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Cursor cursor = databaseHelper.getDataCursor();

        this.imageViewLogo = (ImageView) findViewById(R.id.lockLogo);
        this.imageViewAppName = (TextView) findViewById(R.id.appName);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                splashScreenAnimation();
            }
        };

        Thread animationThread = new Thread(runnable);
        animationThread.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cursor.moveToFirst()) {
                    startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                } else {
                    startActivity(new Intent(SplashScreen.this, RegistrationScreen.class));
                }
                finish();
            }
        }, 4000);
    }

    /**
     * This method will perform the animations of the splash screen.
     * The XML resources can be found in the res folder and in it under
     * the anim folder.
     */

    private void splashScreenAnimation() {
        Animation fadeInAnimationLogo = AnimationUtils.loadAnimation(this, R.anim.logo_fade_in);
        this.imageViewLogo.startAnimation(fadeInAnimationLogo);

        Animation fadeInAnimationAppName = AnimationUtils.loadAnimation(this, R.anim.appname_fade_in);
        this.imageViewAppName.startAnimation(fadeInAnimationAppName);

        Animation rotateAnimationLogo = AnimationUtils.loadAnimation(this, R.anim.logo_rotation);
        this.imageViewLogo.startAnimation(rotateAnimationLogo);
    }
}