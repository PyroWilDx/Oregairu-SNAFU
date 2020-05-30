package aquaaxisorder.app.oregairusnafu;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import aquaaxisorder.app.oregairusnafu.lightNovelPackage.ChooseNovelFragment;
import aquaaxisorder.app.oregairusnafu.lightNovelPackage.ReadNovelFragment;
import aquaaxisorder.app.oregairusnafu.mangaPackage.MangaDexWebHostFragment;
import aquaaxisorder.app.oregairusnafu.musicPackage.BottomNavMusic;
import aquaaxisorder.app.oregairusnafu.quotesPackage.BottomNavQuotes;
import aquaaxisorder.app.oregairusnafu.tuturuPackage.TuturuFragment;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private AudioManager mAudioManager;
    private SharedPreferences prefs;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        prefs = getSharedPreferences(getString(R.string.prefsName), MODE_PRIVATE);

        doBindService();
        Intent music = new Intent(this, OregairuMusicService.class);
        startService(music);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String toolbarTitle = getString(R.string.app_name);
        SpannableString ssToolbarTitle = new SpannableString(toolbarTitle);
        int spanned = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        int colorBlue = getResources().getColor(R.color.colorOregairuBlue);
        int colorPink = getResources().getColor(R.color.colorOregairuPink);
        ssToolbarTitle.setSpan(new ForegroundColorSpan(colorPink), 0, 3, spanned);
        ssToolbarTitle.setSpan(new ForegroundColorSpan(colorBlue), 3, 5, spanned);
        ssToolbarTitle.setSpan(new ForegroundColorSpan(colorPink), 5, 8, spanned);
        ssToolbarTitle.setSpan(new ForegroundColorSpan(colorBlue), 9, 10, spanned);
        ssToolbarTitle.setSpan(new ForegroundColorSpan(colorPink), 10, 11, spanned);
        ssToolbarTitle.setSpan(new ForegroundColorSpan(colorBlue), 11, 12, spanned);
        ssToolbarTitle.setSpan(new ForegroundColorSpan(colorPink), 12, 13, spanned);
        ssToolbarTitle.setSpan(new ForegroundColorSpan(colorBlue), 13, 14, spanned);
        toolbar.setTitle(ssToolbarTitle);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navTitleTextView = headerView.findViewById(R.id.nav_header_animeTitle_textview);
        navTitleTextView.setText(ssToolbarTitle);
        navTitleTextView.setTypeface(null, Typeface.BOLD);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorOregairuBlue));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        audioFocusHelper = new AudioFocusHelper();
        requestAudioFocus();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        closeKeyboard();
        Fragment fragment = new HomeFragment();
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_music:
                fragment = new BottomNavMusic();
                break;
            case R.id.nav_lightNovel:
                fragment = new ChooseNovelFragment();
                break;
            case R.id.nav_manga:
                fragment = new MangaDexWebHostFragment();
                break;
            case R.id.nav_quotes:
                fragment = new BottomNavQuotes();
                break;
            case R.id.nav_tuturu:
                fragment = new TuturuFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private long backPressTime;
    private Toast backToast;
    public static boolean backPressInMusic = false;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof ReadNovelFragment) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChooseNovelFragment()).commit();
                toolbar.setVisibility(View.VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (backPressInMusic) {
                super.onBackPressed();
            } else {
                if (backPressTime + 2000 > System.currentTimeMillis()) {
                    backToast.cancel();
                    doUnbindService();
                    Intent music = new Intent();
                    music.setClass(this, OregairuMusicService.class);
                    stopService(music);
                    finish();
                    System.exit(0);
                } else {
                    backToast = Toast.makeText(getBaseContext(), R.string.pressBackAgain, Toast.LENGTH_SHORT);
                    backToast.show();
                }
                backPressTime = System.currentTimeMillis();
            }
        }
    }

    private AudioFocusHelper audioFocusHelper;
    private boolean wasPlaying = true;

    private final class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
        private void abandonAudioFocus() {
            mAudioManager.abandonAudioFocus(this);
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (mServ != null) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        if (!prefs.getBoolean(getString(R.string.prefsMusicPaused), false)) {
                            mServ.resumeMusic();
                            mServ.updateNotifPlayPauseButtons();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        if (!prefs.getBoolean(getString(R.string.prefsMusicPaused), false)) {
                            mServ.resumeMusic();
                            mServ.updateNotifPlayPauseButtons();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        if (!prefs.getBoolean(getString(R.string.prefsMusicPaused), false)) {
                            mServ.resumeMusic();
                            mServ.updateNotifPlayPauseButtons();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        abandonAudioFocus();
                        if (mServ != null && mServ.mPlayer != null) {
                            if (mServ.mPlayer.isPlaying()) {
                                mServ.pauseMusic();
                                wasPlaying = true;
                            } else {
                                wasPlaying = false;
                            }
                            mServ.updateNotifPlayPauseButtons();
                        }
                        break;
                }
            }
        }
    }

    private void requestAudioFocus() {
        mAudioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            int focusRequest = mAudioManager.requestAudioFocus(audioFocusHelper,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    private boolean mIsBound = false;
    public static OregairuMusicService mServ;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((OregairuMusicService.ServiceBinder) binder).getService();
            mServ.mPlayer.setVolume(prefs.getInt(getString(R.string.prefsVolumeLevel), 100) * 0.01f,
                    prefs.getInt(getString(R.string.prefsVolumeLevel), 100) * 0.01f);
            if (prefs.getBoolean(getString(R.string.prefsMusicPaused), false)) {
                mServ.pauseMusic();
                mServ.updateNotifPlayPauseButtons();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    private void doBindService() {
        bindService(new Intent(this, OregairuMusicService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {
            unbindService(serviceConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            requestAudioFocus();
            if (wasPlaying) {
                if (!prefs.getBoolean(getString(R.string.prefsMusicPaused), false)) {
                    mServ.resumeMusic();
                }
            }
            mServ.updateNotifPlayPauseButtons();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        Intent music = new Intent(this, OregairuMusicService.class);
        stopService(music);
    }

    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

}

