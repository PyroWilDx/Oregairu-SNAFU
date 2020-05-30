package aquaaxisorder.app.oregairusnafu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mPlayer;
    private Intent HomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE);
        boolean splashScreenDisplay = prefs.getBoolean(getString(R.string.prefsSplashScreen), true);
        HomeActivity = new Intent(MainActivity.this, HomeActivity.class);

        if (splashScreenDisplay) {

            Random random = new Random();
            int randomNb = random.nextInt(2);

            if (randomNb == 0) {
                mPlayer = MediaPlayer.create(this, R.raw.nyaaa);
            } else if (randomNb == 1) {
                mPlayer = MediaPlayer.create(this, R.raw.yahallo);
            }
            mPlayer.start();

            Handler handler = new Handler();

            final ImageView hachimanChibi = findViewById(R.id.hachiman_chibi_image);
            final ImageView yukinoChibi = findViewById(R.id.yukino_chibi_image);
            final ImageView yuiChibi = findViewById(R.id.yui_chibi_image);
            AnimationSet asHachiman = new AnimationSet(false);
            AnimationSet asYukino = new AnimationSet(false);
            AnimationSet asYui = new AnimationSet(false);
            Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_fade_in);
            Animation hachimanAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_bottom_middle_hachiman);
            Animation yukinoAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_left_middle_yukino);
            Animation yuiAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_right_middle_yui);

            asHachiman.addAnimation(fadeIn);
            asHachiman.addAnimation(hachimanAnim);
            asYukino.addAnimation(fadeIn);
            asYukino.addAnimation(yukinoAnim);
            asYui.addAnimation(fadeIn);
            asYui.addAnimation(yuiAnim);

            hachimanChibi.startAnimation(asHachiman);
            yukinoChibi.startAnimation(asYukino);
            yuiChibi.startAnimation(asYui);

            final Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_fade_out);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hachimanChibi.startAnimation(fadeOut);
                    yukinoChibi.startAnimation(fadeOut);
                    yuiChibi.startAnimation(fadeOut);
                }
            }, asHachiman.getDuration());

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPlayer.stop();
                    mPlayer.reset();
                    mPlayer.release();
                    mPlayer = null;
                    startActivity(HomeActivity);
                    overridePendingTransition(R.anim.activity_trans_fade_in, R.anim.activity_trans_fade_out);
                    finish();
                }
            }, asHachiman.getDuration() + fadeOut.getDuration());

        } else {
            startActivity(HomeActivity);
            finish();
        }
    }

    private long backPressTime;
    private Toast backToast;

    @Override
    public void onBackPressed() {
        if (backPressTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            finish();
            System.exit(0);
        } else {
            backToast = Toast.makeText(getBaseContext(), R.string.pressBackAgain, Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressTime = System.currentTimeMillis();
    }

}
